package cookbook;

import cookbook.accountcontrol.AccountLogin;
import cookbook.dbconnection.DatabaseControl;
import cookbook.webscraper.CookbookScraper;
import cookbook.accountcontrol.PasswordControl;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.Connection;

public class Demo
{
    /*todo (must haves):
        1. Functional web scraper that works on >=3 different websites
        2. Database for recipe storage and unique ID for each recipe
        3. Driver program with read and write control for the database, with input sanitation
        4. Version control integration
        5. Cloud database hosting
     */
    public static void main(String[]args)
    {
        CookbookScraper cookbookScraper = new CookbookScraper();
        PasswordControl passwordControl = new PasswordControl();
        DatabaseControl databaseControl = new DatabaseControl();

        AccountLogin accountLogin = new AccountLogin();

        ArrayList recipe = new ArrayList<>();

        //stage control
        boolean running = true;
        boolean stageOne = true; //recipe scraper, input link
        boolean stageTwo = false; //logging in and sign up
        boolean stageThree = false;

        boolean loggedIn = false;
        boolean savedRecipe = false;

        //database connection control
        Connection dbConnection = null;
        String url = "localhost:3306/cookbook";
        String user = "root";
        String pass = "pass";

        String userName;
        String userPassword;

        Scanner input = new Scanner(System.in);
        String kbInput;

        System.out.println("Welcome to Cookbook.");
        do
        {
            while(stageOne)
            {
                System.out.println("\nPlease input the recipe's link.\nFor additional options, please input 'other'.");
                System.out.print("\nInput: ");

                kbInput = input.next();
                if(kbInput.equalsIgnoreCase("exit"))
                {
                    running = false;
                    break;
                }
                else if(kbInput.contains("http"))
                {
                    recipe = cookbookScraper.scrapeLink(kbInput);
                    System.out.println("\nWould you like to save this recipe? (y/n)");
                    System.out.print("Input: ");
                    String saveInput = input.next();
                    if(saveInput.equalsIgnoreCase("y"))
                    {
                        if (!loggedIn)
                        {
                            savedRecipe = true;
                        }
                        else if(loggedIn)
                        {
                        }
                    }
                }
                else if(kbInput.equalsIgnoreCase("other") || savedRecipe)
                {
                    stageOne = false;
                    stageTwo = true;
                }
                else
                {
                    System.out.println("Input is invalid. Please try again.");
                }
            }
            while(stageTwo)
            {
                if(!loggedIn)
                {
                    System.out.println("If you would like to retrieve your existing recipes or save them, please log in. If you do not have an account, feel free to make one.\n");
                    System.out.println("Options:\n(0) Back\n(1) Log in\n(2) Sign up");

                    System.out.print("\nInput: ");
                    String optInput = input.next();
                    switch (optInput)
                    {
                        case("0"):
                            stageTwo=false;
                            stageOne=true;
                            break;
                        case("1"):
                            boolean attemptingLogin = true;
                            System.out.println("\nYou have opted to log in.\nInput '0' to cancel.");
                            try
                            {
                                dbConnection = databaseControl.establishConnection(url, user, pass);
                            }
                            catch (SQLException e)
                            {
                                System.out.println("Error: " + e);
                            }
                            while(attemptingLogin)
                            {
                                System.out.print("\nUsername: ");
                                userName = input.next();
                                if(userName.equals("0"))
                                {
                                    attemptingLogin=false;
                                }
                                else
                                {
                                    //System.out.println("Username: " + userName);
                                    userPassword = passwordControl.inputPassword();
                                    //System.out.println("Password: " + userPassword);

                                    int loginStatus = accountLogin.loginAttempt(dbConnection, userName, userPassword);
                                    switch (loginStatus)
                                    {
                                        case(0):
                                            System.out.println("Error: unknown error occurred.\nTry again.");
                                            break;
                                        case(1): //user not found
                                            System.out.println("Error: username not found.\nTry again.");
                                            break;
                                        case(2):
                                            System.out.println("Error: password is incorrect.\nTry again.");
                                            break;
                                        case(3):
                                            System.out.println("Success.");
                                            attemptingLogin=false;
                                            break;
                                    }
                                }
                            }
                            break;
                        default:
                            System.out.println("Error: invalid input. Try again.\n");
                    }
                }
            }
        }
        while(running);
    }
}

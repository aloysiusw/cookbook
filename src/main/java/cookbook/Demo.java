package cookbook;

import cookbook.accountcontrol.AccountLogin;
import cookbook.accountcontrol.AccountRegister;
import cookbook.dbconnection.DatabaseControl;
import cookbook.webscraper.CookbookScraper;
import cookbook.accountcontrol.PasswordControl;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.Connection;

public class Demo
{
    /*todo (must haves):
        1. Functional web scraper that works on >=3 different websites (ok)
        2. Database for recipe storage and unique ID for each recipe
        3. Driver program with read and write control for the database, with input sanitation (ok)
        4. Version control integration (ok)
        5. Cloud database hosting
        6. Configure db in app (ok)
        7. Admin access
     */
    public static void main(String[]args)
    {
        CookbookScraper cookbookScraper = new CookbookScraper();
        PasswordControl passwordControl = new PasswordControl();
        DatabaseControl databaseControl = new DatabaseControl();

        AccountLogin accountLogin = new AccountLogin();
        AccountRegister accountRegister = new AccountRegister();

        ArrayList recipe = new ArrayList<>();

        //stage control
        boolean running = true;
        boolean stageOne = true; //recipe scraper, input link
        boolean stageTwo = false; //logging in and sign up
        boolean stageThree = false; //saving and retrieving recipe

        boolean loggedIn = false;
        boolean savedRecipe = false;

        //database connection control
        Connection dbConnection;
        String dbURL = "localhost:3306/cookbook";
        String dbUser = "root";
        String dbPass = "pass";

        String userName;
        String userPassword;
        String accountType;

        Scanner input = new Scanner(System.in);
        String kbInput;

        Demo.printIntro();
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
                    System.out.println("Options:\n(0) Back\n(1) Log in\n(2) Sign up\n(-) Configure database connection");

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
                                dbConnection = databaseControl.establishConnection(dbURL, dbUser, dbPass);
                                while(attemptingLogin)
                                {
                                    System.out.print("\nPlease input your username.\nInput: ");
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
                                        String loginStatus = accountLogin.loginAttempt(dbConnection, userName, userPassword);
                                        switch (loginStatus)
                                        {
                                            case("USER_NOT_FOUND"): //user not found
                                                System.out.println("Error: username not found.\nTry again.");
                                                break;
                                            case("PASSWORD_INCORRECT"):
                                                System.out.println("Error: password is incorrect.\nTry again.");
                                                break;
                                            case("ADMIN"):
                                            case("USER"):
                                                accountType = loginStatus;
                                                System.out.println("Success.");
                                                attemptingLogin=false;
                                                break;
                                            default:
                                                System.out.println("Error: unknown error occurred.\nTry again.");
                                                break;
                                        }
                                    }
                                }
                                dbConnection.close();
                            }
                            catch (SQLException e)
                            {
                                System.out.println("Error: " + e);
                            }
                            break;
                        case("2"):
                            boolean attemptingRegister = true;
                            System.out.println("\nYou have opted to register.\nInput '0' to cancel.");
                            try
                            {
                                dbConnection = databaseControl.establishConnection(dbURL, dbUser, dbPass);
                                while(attemptingRegister)
                                {
                                    System.out.print("\nPlease input your username.\nInput: ");
                                    userName = input.next();
                                    if(userName.equals("0"))
                                    {
                                        attemptingRegister=false;
                                    }
                                    else
                                    {
                                        //System.out.println("Username: " + userName);
                                        userPassword = passwordControl.inputPassword();
                                        //System.out.println("Password: " + userPassword);
                                        String registerStatus = accountRegister.registerAttempt(dbConnection, userName, userPassword);
                                        switch (registerStatus)
                                        {
                                            case("USERNAME_TAKEN"): //user not found
                                                System.out.println("Error: username already taken.\nTry again.");
                                                break;
                                            case("ACCOUNT_CREATION_SUCCESS"):
                                                System.out.println("Success.");
                                                attemptingRegister=false;
                                                break;
                                            case("REGISTRATION_CANCELLED"):
                                                System.out.println("Account creation aborted.");
                                                break;
                                            default:
                                            {
                                                System.out.println("Error: unknown error occurred.\nTry again.");
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            catch (SQLException e)
                            {
                                System.out.println("Error: " + e);
                            }
                            break;
                        case("-"):
                            System.out.println("\nCurrent database configuration:\nURL: " + dbURL + "\nUser: " + dbUser + "\nPass: " + dbPass);
                            System.out.println("\nReconfigure? (y/n)");
                            System.out.print("Input: ");
                            String userReconfig = input.next();
                            String dbConfig;
                            if(userReconfig.equalsIgnoreCase("y"))
                            {
                                System.out.println("\nInput your new configurations: ");
                                
                                System.out.print("URL: ");
                                String newdbURL = input.next();

                                System.out.print("User: ");
                                String newdbUser = input.next();
                                
                                System.out.print("Pass: ");
                                String newdbPass = input.next();

                                System.out.println("\nNew database configuration:\nURL: " + newdbURL + "\nUser: " + newdbUser + "\nPass: " + newdbPass);
                                System.out.println("\nConfirm changes? (y/n)");
                                String userConfChanges = input.next();
                                if(userConfChanges.equalsIgnoreCase("y"))
                                {
                                    dbURL = newdbURL;
                                    dbUser = newdbUser;
                                    dbPass = newdbPass;
                                }
                                else
                                {
                                    System.out.println("Changes not saved.");
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
    private static String StageOne()
    {
        String stageOneTransition = null;

        return stageOneTransition;
    }
    private static void printIntro()
    {
        System.out.println("""
                     *@@,                       ,@@    \s
                     *@@@@@@@@@@         @@@@@@@@@@    \s
                 @@@ *@@@@@@@@@@@@@   @@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@ *@@@@@@@@@@@@@/ @@@@@@@@@@@@@@  @@@
                @@@@    %@@@@@@@@@@/ @@@@@@@@@@@#    @@@
                @@@@@@@@@.    @@@@@/ @@@@@%    /@@@@@@@@
                """);

        System.out.println("""
                Welcome to Cookbook.
                This project was brought to you by:

                Team leader:
                \t1. Aloysius Arno Wiputra
                Team members:
                \t1. Ruchira Bunga
                \t2. Hetul Patel

                Instructor\t: Dr. Frank Lee
                Class\t\t: CSCI 455 - Senior Project
                """);

        printSupported();

        System.out.println("\n------------------------------------------");
    }
    private static void printSupported()
    {
        Hashtable<Integer, String> supportedSites = new Hashtable<>();

        supportedSites.put(0, "gimmesomeoven"); //fully supported, such a nice easy site
        supportedSites.put(1, "playfulcooking"); //seems okay, after some tinkering
        supportedSites.put(2, "thespruceeats"); //not terribly effective, only works with some links

        System.out.println("Current supported sites: ");
        for(int i=0; i<supportedSites.size(); i++)
        {
            System.out.println(i+1 + ") " + supportedSites.get(i));
        }
    }
}

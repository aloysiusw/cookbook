package cookbook;

import cookbook.webscraper.CookbookScraper;
import cookbook.accountcontrol.PasswordControl;

import java.util.Scanner;
import java.util.ArrayList;

public class Demo
{

    public static void main(String[]args)
    {
        CookbookScraper cookbookScraper = new CookbookScraper();
        PasswordControl passwordControl = new PasswordControl();

        ArrayList recipe = new ArrayList<>();

        boolean running = true;
        boolean stageOne = true; //recipe scraper, input link
        boolean stageTwo = false; //logging in and sign up
        boolean stageThree = false;

        boolean loggedIn = false;
        boolean savedRecipe = false;

        String userName;
        String userPassword;
        boolean userVerified = false;

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

                    System.out.print("Input: ");
                    String optInput = input.next();
                    switch (optInput)
                    {
                        case("0"):
                            stageTwo=false;
                            stageOne=true;
                            break;
                        case("1"):
                            boolean attemptingLogin = true;
                            System.out.println("You have opted to log in.\nInput '0' to go cancel.");
                            while(attemptingLogin)
                            {
                                System.out.print("\nUsername: ");
                                userName = input.next();
                                System.out.println("Username: " + userName);
                                userPassword = passwordControl.inputPassword();
                                System.out.println("Password: " + userPassword);
                            }


                    }
                }
            }
        }
        while(running);
    }
}

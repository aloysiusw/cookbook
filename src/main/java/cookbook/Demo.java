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
        2. Database for recipe storage and unique ID for each recipe (ok)
        3. Driver program with read and write control for the database, with input sanitation (ok)
        4. Version control integration (ok)
        5. Cloud database hosting
        6. Configure db in app (ok)
        7. Admin access
     */
    static CookbookScraper cookbookScraper = new CookbookScraper();
    static PasswordControl passwordControl = new PasswordControl();
    static DatabaseControl databaseControl = new DatabaseControl();

    static AccountLogin accountLogin = new AccountLogin();
    static AccountRegister accountRegister = new AccountRegister();

    static Scanner input = new Scanner(System.in);
    static String kbInput;

    static String dbURL = "localhost:3306/cookbook";
    static String dbUser = "root";
    static String dbPass = "pass";

    static boolean loggedIn = false;

    static Hashtable<Integer, String> supportedSites = new Hashtable<>();

    static ArrayList recipeScraped = new ArrayList<>();
    static boolean savedRecipe = false;

    static Connection dbConnection;

    static String userName;
    static String userPassword;
    static String accountType;

    static String nextStage;
    static String currentStage = "STAGE_ONE";

    public static void main(String[]args)
    {
        Demo.printIntro();
        boolean appRunning = true;
        do
        {
            if(nextStage!=null)
            {
                currentStage=nextStage;
            }
            switch(currentStage)
            {
                case("STAGE_ONE"):
                {
                    Demo.StageOne();
                    break;
                }
                case("STAGE_TWO"):
                {
                    Demo.StageTwo();
                    break;
                }
                case("STAGE_THREE"):
                {
                    Demo.StageThree();
                    break;
                }
                case("STAGE_EXIT"):
                {
                    appRunning = false;
                    break;
                }
            }

        } while(appRunning);
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
        supportedSites.put(0, "gimmesomeoven"); //fully supported, such a nice easy site
        supportedSites.put(1, "playfulcooking"); //seems okay, after some tinkering
        supportedSites.put(2, "thespruceeats"); //not terribly effective, only works with some links

        System.out.println("Current supported sites: ");
        for(int i=0; i<supportedSites.size(); i++)
        {
            System.out.println(i+1 + ") " + supportedSites.get(i));
        }
    }
    private static String StageOne()
    {
        System.out.println("\nPlease input the recipe's link.\nFor additional options, please input 'other'.");
        System.out.print("\nInput: ");
        savedRecipe = false;
        kbInput = input.next();
        if(kbInput.equalsIgnoreCase("exit"))
        {
            nextStage = "STAGE_EXIT";
        }
        else if(kbInput.contains("http"))
        {
            boolean isSupported = false;
            for(int i=0; i<supportedSites.size(); i++)
            {
                if(kbInput.contains(supportedSites.get(i)))
                {
                    isSupported = true;
                }
            }
            if(isSupported)
            {
                recipeScraped = cookbookScraper.scrapeLink(kbInput);
                cookbookScraper.printRecipe(recipeScraped);
                System.out.println("\nWould you like to save this recipe? (y/n)");
                System.out.print("Input: ");
                String saveInput = input.next();
                if (saveInput.equalsIgnoreCase("y"))
                {
                    savedRecipe = true;
                    if(loggedIn)
                    {
                        sendRecipe();
                    }
                    //System.out.println(filedRecipeRaw);
                    else
                    {
                        System.out.println("\nYou are not logged in.");
                        nextStage = "STAGE_TWO";
                    }
                }
            }
            else
            {
                System.out.println("Site isn't supported.");
            }
        }
        else if(kbInput.equalsIgnoreCase("other") || savedRecipe)
        {
            nextStage = "STAGE_TWO";
        }
        else
        {
            System.out.println("Input is invalid. Please try again.");
        }
        return nextStage;
    }
    private static String StageTwo()
    {
        System.out.println("\nIf you would like to retrieve your existing recipes or save them, please log in. If you do not have an account, feel free to make one.\n");
        System.out.println("Options:\n(0) Back\n(1) Log in/out\n(2) Sign up\n(-) Configure database connection");

        System.out.print("\nInput: ");
        String optInput = input.next();
        switch (optInput)
        {
            case("0"):
                nextStage = "STAGE_ONE";
                break;
            case("1"):
                if(loggedIn)
                {
                    userName=null;
                    userPassword=null;
                    loggedIn=false;
                    try
                    {
                        dbConnection.close();
                    }
                    catch(SQLException e)
                    {
                        System.out.println(e);
                    }
                }
                else
                {
                    boolean attemptingLogin = true;
                    System.out.println("\nYou have opted to log in.\nInput '0' to cancel.");
                    try
                    {
                        dbConnection = databaseControl.establishConnection(dbURL, dbUser, dbPass);
                        while (attemptingLogin)
                        {
                            System.out.print("\nPlease input your username.\nInput: ");
                            userName = input.next();
                            if (userName.equals("0"))
                            {
                                attemptingLogin = false;
                            }
                            else
                            {
                                //System.out.println("Username: " + userName);
                                userPassword = passwordControl.inputPassword();
                                //System.out.println("Password: " + userPassword);
                                String loginStatus = accountLogin.loginAttempt(dbConnection, userName, userPassword);
                                switch (loginStatus)
                                {
                                    case ("USER_NOT_FOUND"): //user not found
                                        System.out.println("Error: username not found.\nTry again.");
                                        break;
                                    case ("PASSWORD_INCORRECT"):
                                        System.out.println("Error: password is incorrect.\nTry again.");
                                        break;
                                    case ("ADMIN"):
                                    case ("USER"):
                                        accountType = loginStatus;
                                        loggedIn = true;
                                        System.out.println("Successfully logged in, logged in as '" + userName + "'.");
                                        attemptingLogin = false;
                                        nextStage="STAGE_THREE";
                                        break;
                                    default:
                                        System.out.println("Error: unknown error occurred.\nTry again.");
                                        break;
                                }
                            }
                        }
                    }
                    catch (SQLException e)
                    {
                        System.out.println("Error: " + e);
                    }
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
                                    System.out.println("Successfully registered, logged in as '" + userName + "'.");
                                    loggedIn=true;
                                    attemptingRegister=false;
                                    nextStage="STAGE_THREE";
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
            {
                System.out.println("Error: invalid input. Try again.\n");
                break;
            }
        }
        if(loggedIn && savedRecipe)
        {
            try
            {
                sendRecipe();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        return nextStage;
    }
    private static String StageThree()
    {
        /*todo:
            1. retrieve recipe (go through resultset, see if it contains string)
            2. save recipe (go back to stage one)
            3. log out (nullify values, go back to stage two)
            4. if admin, see stats, user tables, promote to admin
         */
        System.out.println();
        return nextStage;
    }
    private static void sendRecipe()
    {
        String resultCode = cookbookScraper.fileRecipe(recipeScraped, userName, dbConnection);
        //System.out.println(resultCode);
        switch(resultCode)
        {
            case("RECIPE_ADDED"):
            {
                System.out.println("\nRecipe added.");
                break;
            }
            case("RECIPE_EXISTS"):
            {
                System.out.println("\nYou've already added this recipe.");
                break;
            }
            default:
            {
                System.out.println("Unknown error occurred");
                break;
            }
        }
        savedRecipe = false;
        recipeScraped = null;
    }
}

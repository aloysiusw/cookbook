package cookbook;

import cookbook.accountcontrol.AccountLogin;
import cookbook.accountcontrol.AccountRegister;
import cookbook.dbconnection.DatabaseControl;
import cookbook.webscraper.CookbookScraper;
import cookbook.accountcontrol.PasswordControl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.Connection;

import de.vandermeer.asciitable.AsciiTable;

public class Demo
{
    /*todo (must haves):
        1. Functional web scraper that works on >=3 different websites (ok)
        2. Database for recipe storage and unique ID for each recipe (ok)
        3. Driver program with read and write control for the database, with input sanitation (ok, partially)
        4. Version control integration (ok)
        5. Cloud database hosting
        6. Configure db in app (ok)
        7. Admin access (ok)
     */
    final static CookbookScraper cookbookScraper = new CookbookScraper();
    final static PasswordControl passwordControl = new PasswordControl();
    final static DatabaseControl databaseControl = new DatabaseControl();

    final static AccountLogin accountLogin = new AccountLogin();
    final static AccountRegister accountRegister = new AccountRegister();

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
        if(loggedIn)
        {
            nextStage="STAGE_THREE";
        }
        return nextStage;
    }
    private static String StageTwo()
    {
        System.out.println("\nIf you would like to retrieve your existing recipes or save them, please log in. If you do not have an account, feel free to make one.\n");
        System.out.println("Options:\n(0) Back\n(1) Log in\n(2) Sign up\n(-) Configure database connection");

        System.out.print("\nInput: ");
        String optInput = input.next();
        switch (optInput)
        {
            case("exit"):
            {
                nextStage = "STAGE_EXIT";
                break;
            }
            case("0"):
                nextStage = "STAGE_ONE";
                break;
            case("1"):
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
                                    accountType="USER";
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
            1. retrieve recipe (go through resultset, see if it contains string), print ascii table? (ok)
            2. print all recipes saved (id, title, domain source) (ok)
            2. save recipe (go back to stage one) (ok)
            3. log out (nullify values, go back to stage two) (ok)
            4. if admin, see user tables, promote to admin
         */

        System.out.println("\nWelcome, " + userName + ".");
        System.out.println("\nAvailable commands: \n(1) Search saved recipes\n(2) See all recipes\n(3) Add new recipes\n(4) Log out");
        if(accountType.equalsIgnoreCase("Admin"))
        {
            System.out.println("(5) See list of users\n(6) Switch user account privilege");
        }
        System.out.print("\nInput: ");
        String s3Input = input.next();
        switch(s3Input)
        {
            case("exit"):
            {
                nextStage = "STAGE_EXIT";
                break;
            }
            case("1"): //search recipes
            {
                System.out.println("\nYou can search by title or recipe ID.");
                System.out.print("\nInput: ");
                kbInput = input.next();
                if(kbInput.matches("^[0-9]*$")) //if it's number, then search by ID
                {
                    try
                    {
                        Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        String queryOptOneID = "SELECT RECIPE_TITLE, RECIPE_SOURCE, RECIPE_CONTENT FROM RECIPES WHERE SOURCE_USER = '" + userName + "' AND RECIPE_ID = '" + kbInput + "'";
                        ResultSet optOneIDResult = stm.executeQuery(queryOptOneID);
                        if(optOneIDResult.next())
                        {
                            optOneIDResult.first();
                            String recipeToParseRaw = optOneIDResult.getString("RECIPE_CONTENT"); //returned file is in raw format
                            recipeScraped = cookbookScraper.splitFiled(recipeToParseRaw); //splits the text into array list
                            cookbookScraper.printRecipe(recipeScraped);
                        }
                        else
                        {
                            System.out.println("\nNo result found.");
                        }
                    }
                    catch(SQLException e)
                    {
                        System.out.println(e);
                    }
                }
                else
                {
                    try
                    {
                        Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        String queryOptOneTitle = "SELECT RECIPE_TITLE, RECIPE_SOURCE, RECIPE_CONTENT FROM RECIPES WHERE RECIPE_TITLE LIKE '%" + kbInput + "%'";
                        String queryGetCount = "SELECT COUNT * FROM RECIPES WHERE RECIPE_TITLE LIKE '%" + kbInput + "%'";
                        ResultSet optOneCount = stm.executeQuery(queryGetCount);
                        int count = optOneCount.getInt(1);
                        System.out.println(count);
                        if(count!=0)
                        {
                            ResultSet optOneTitleResult = stm.executeQuery(queryOptOneTitle);
                            if (count == 1)
                            {
                                System.out.println("\nTitle: " + optOneTitleResult.getString("RECIPE_TITLE"));
                                System.out.println("\nSource: " + optOneTitleResult.getString("RECIPE_SOURCE"));
                                optOneTitleResult.first();
                                String recipeToParseRaw = optOneTitleResult.getString("RECIPE_CONTENT"); //returned file is in raw format
                                recipeScraped = cookbookScraper.splitFiled(recipeToParseRaw); //splits the text into array list
                                cookbookScraper.printRecipe(recipeScraped);
                            }
                            else if (count > 1)
                            {
                                AsciiTable optOneTable = new AsciiTable();
                                optOneTable.addRule();
                                optOneTable.addRow("ID", "TITLE","SOURCE");
                                optOneTable.addRule();
                                while(optOneTitleResult.next())
                                {
                                    optOneTable.addRow(optOneTitleResult.getString("RECIPE_ID"),optOneTitleResult.getString("RECIPE_TITLE"),optOneTitleResult.getString("RECIPE_SOURCE"));
                                    optOneTable.addRule();
                                }
                                String optOneTitleResultPrint = optOneTable.render();
                                System.out.println(optOneTitleResultPrint);
                            }
                        }
                        else
                        {
                            System.out.println("\nNo result found.");
                        }
                    }
                    catch(SQLException e)
                    {
                        System.out.println(e);
                    }
                }
                break;
            }
            case("2"): //see all
            {
                try
                {
                    Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String queryOptTwo = "SELECT RECIPE_ID, RECIPE_TITLE, RECIPE_SOURCE FROM RECIPES WHERE SOURCE_USER = '" + userName + "'";
                    ResultSet optTwoResult = stm.executeQuery(queryOptTwo);

                    AsciiTable optTwoTable = new AsciiTable();
                    optTwoTable.addRule();
                    optTwoTable.addRow("ID", "TITLE","SOURCE");
                    optTwoTable.addRule();
                    while(optTwoResult.next())
                    {
                        optTwoTable.addRow(optTwoResult.getString("RECIPE_ID"),optTwoResult.getString("RECIPE_TITLE"),optTwoResult.getString("RECIPE_SOURCE"));
                        optTwoTable.addRule();
                    }
                    String optTwoTablePrnt = optTwoTable.render();
                    System.out.println(optTwoTablePrnt);
                }
                catch(SQLException e)
                {
                    System.out.println(e);
                }
                break;
            }
            case("3"): //add new
            {
                nextStage = "STAGE_ONE";
                break;
            }
            case("4"): //log out
            {
                System.out.println("\nConfirm log out? (y/n)");
                System.out.print("Input: ");
                kbInput = input.next();
                if(kbInput.equalsIgnoreCase("y"))
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
                    nextStage="STAGE_TWO";
                }
                break;
            }
            case("5"): //admin, see user list
            {
                if(accountType.equalsIgnoreCase("Admin"))
                {
                    try
                    {
                        Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        String query = "SELECT * FROM USER_ACCOUNT";
                        ResultSet resultQuery = stm.executeQuery(query);

                        AsciiTable userList = new AsciiTable();
                        userList.addRule();
                        userList.addRow("USERS", "TYPE", "PASSWORD");
                        userList.addRule();
                        while (resultQuery.next())
                        {
                            userList.addRow(resultQuery.getString("USER_NAME"), resultQuery.getString("USER_TYPE"),resultQuery.getString("USER_PASSWORD"));
                            userList.addRule();
                        }
                        String userListTable = userList.render();
                        System.out.println(userListTable);
                    }
                    catch (SQLException e)
                    {
                        System.out.println(e);
                    }
                }
                break;
            }
            case("6"): //admin, promote userf
            {
                if(accountType.equalsIgnoreCase("Admin"))
                {
                    System.out.println("\nInput the user to switch account type.");
                    System.out.print("\nInput: ");
                    String userElevate = input.next();
                    try
                    {
                        Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        String query = "SELECT USER_TYPE FROM USER_ACCOUNT WHERE USER_NAME = '" + userElevate + "'";
                        ResultSet resultQuery = stm.executeQuery(query);
                        if(resultQuery.next())
                        {
                            resultQuery.first();
                            String resultType = resultQuery.getString("USER_TYPE");

                            System.out.println("User '" + userElevate + "' = " + resultType);
                            System.out.println("Switch types? (y/n)");
                            System.out.print("\nInput: ");
                            kbInput = input.next();
                            if(kbInput.equalsIgnoreCase("y"))
                            {
                                if(resultType.equalsIgnoreCase("admin"))
                                {
                                    resultType="USER";
                                }
                                else if(resultType.equalsIgnoreCase("user"))
                                {
                                    resultType="ADMIN";
                                }
                                String querySwitch = "UPDATE USER_ACCOUNT SET USER_TYPE='" + resultType + "' WHERE USER_NAME='" + userElevate + "'";
                                stm.executeUpdate(querySwitch);
                            }
                        }
                        else
                        {
                            System.out.println("User not found.");
                        }

                    }
                    catch (SQLException e)
                    {
                        System.out.println(e);
                    }
                }
                break;
            }
        }

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

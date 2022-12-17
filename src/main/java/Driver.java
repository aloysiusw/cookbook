import dbconnection.DatabaseControl;
import dbconnection.DatabaseQuery;

import accountcontrol.PasswordControl;
import webscraper.CookbookScraper;

import java.util.Scanner;

import java.sql.Connection;
public class Driver
{
    public static void main(String[]args) throws Exception
    {
        //create new instances, maybe put in individual tests?
        DatabaseControl databaseControl = new DatabaseControl();
        DatabaseQuery databaseQuery = new DatabaseQuery();
        PasswordControl passwordControl = new PasswordControl();
        CookbookScraper cookbookScraper = new CookbookScraper();

        String url = "localhost:3306/cookbook";
        String user = "root";
        String pass = "pass";
        Connection dbConn = null;
        boolean firstRun = true;

        Scanner input = new Scanner(System.in);

        boolean running = true;
        {
            while (running)
            {
                System.out.println("\nHere are the test functions.\n(0) Exit\n(1) Connect/Disconnect Database\n(2) Test query\n(3) Test password hashing\n(4) Test scraping\n");
                System.out.print("Input number: ");
                String textInput;
                String kbInput = input.next();
                switch (kbInput)
                {
                    case "0":    //exit
                        System.out.println("Closing program.");
                        running = false;
                        break;
                    case "1": //cycle connection, connects first, then after it checks connection status and cycles
                        if(firstRun)
                        {
                            dbConn = databaseControl.establishConnection(url, user, pass);
                            System.out.println("Connection: " + dbConn);
                            firstRun=false;
                        }
                        else
                        {
                            if (!dbConn.isClosed())
                            {
                                System.out.println("Closing connection.");
                                dbConn.close();
                            }
                            else
                            {
                                dbConn = databaseControl.establishConnection(url, user, pass);
                                System.out.println("Connection: " + dbConn);
                            }
                        }

                        break;
                    case "2": //test query
                        //dbConn = databaseControl.establishConnection(url, user, pass);
                        System.out.print("\nInput text string: ");
                        textInput = input.next();
                        String result = databaseQuery.queryTest(dbConn, textInput);
                        System.out.println("Result: " + result);
                        //dbConn.close();
                        break;
                    case "3": //test SHA-256
                        System.out.print("\nEnter password: ");
                        textInput = input.next();
                        String passwordEncoded = passwordControl.encodePassword(textInput);
                        System.out.println("Encoded hexadecimal: " + passwordEncoded);
                        break;
                    case "4": //test scrape
                        String recipeLink = "https://www.thespruceeats.com/toasted-pasta-soup-recipe-6823245";
                        cookbookScraper.scrapeLink(recipeLink);
                        break;
                    default:
                        System.out.println("Invalid input. Try again.");
                        break;
                }
            }
        }
    }
}

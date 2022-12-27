package cookbook.webscraper;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Connection;

public class CookbookScraper
{
    ArrayList<String> recipe = new ArrayList<>(); //chose ArrayList because faster to parse, only needs to be modified once.
    String recipeTitle;
    String urlDomain;

    public ArrayList<String> scrapeLink(String url)
    {
        int elem;
        /*todo:
            1. parse domain (ok)
            2. make individual ways to scrape (3/3) (ok)
            3. more and more testing
         */
        this.recipe.clear(); //clean the recipe array list for every run
        try
        {
            //System.out.println("A");

            WebClient webClient = new WebClient(BrowserVersion.CHROME); //simulate Firefox

            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            //test url: https://www.thespruceeats.com/classic-southern-fried-chicken-3056867
            HtmlPage htmlPage = webClient.getPage(url); //send request to link

            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();
            this.recipeTitle = htmlPage.getTitleText();
            System.out.println("\nTitle: " + this.recipeTitle);
            //System.out.println(htmlPage.asXml());
            /*
            String xPath = "//*[@id=\"mntl-sc-block_3-0-2\"]";
            DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
            String test = element.getTextContent();
            //String test = element.getAttribute("innerHTML");
            System.out.println(test);
            /*
                List<HtmlAnchor> links = htmlPage.getAnchors();
                for (HtmlAnchor link : links)
                {
                    String href = link.getHrefAttribute();
                    System.out.println("C");
                    System.out.println("Link: " + href);
                }
              */
            boolean elementNotNull = true;

            this.urlDomain = StringUtils.substringBetween(url, "https://www.", ".");
            if(urlDomain==null)
            {
                this.urlDomain = StringUtils.substringBetween(url,"https://", ".");
            }
            System.out.println("Domain: " + urlDomain);

            switch(urlDomain)
            {
                case "thespruceeats": //increments by 5, for some reason
                    if(htmlPage.getTitleText().toLowerCase().contains("recipe")) //check if title contains recipe, kinda basic way of doing it
                    {
                        elem = 2;
                        while (elementNotNull)
                        {
                            String xPath = "//*[@id=\"mntl-sc-block_3-0-" + elem + "\"]";
                            DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
                            if (element != null)
                            {
                                String test = element.getTextContent();
                                test = test.trim();
                                test = test.replace("\n","");
                                //System.out.println(test);
                                //String test = element.getAttribute("innerHTML");
                                if (!test.contains("Tips"))
                                {
                                    recipe.add(test);
                                }
                            }
                            else
                            {
                                elementNotNull = false;
                            }
                            elem = elem + 5;
                        }
                    }
                    else
                    {
                        System.out.println("Domain does not seem to contain a recipe.");
                    }
                    break;
                case "gimmesomeoven": //most straightforward one
                    elem = 1;
                    while (elementNotNull)
                    {
                        String xPath = "//*[@id=\"instruction-step-" + elem + "\"]";
                        DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
                        if (element != null)
                        {
                            String test = element.getTextContent();
                            test = test.trim();
                            test = test.replace("\n","");
                            this.recipe.add(test);
                        }
                        else
                        {
                            elementNotNull = false;
                        }
                        elem++;
                    }
                    break;
                case "playfulcooking": //this one has different structures for its recipe, requires unique ID
                    elem = 0;
                    while (elementNotNull)
                    {
                        ////*[@id="wprm-recipe-23597-step-0-0"]
                        // //*[@id="wprm-recipe-23513-step-0-0"] //*[@id="wprm-recipe-23545-step-0-0"]
                        // /html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[1]/div
                        // /html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[2]/div
                        // /html/body/div[1]/div/div/div/article/div[3]/div[4]/div/div[12]/div/ul/li[1]/div/span
                        //String xPath = "/html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[" + elem + "]/div";

                        //*[@id="wprm-recipe-container-23545"]
                        DomElement testElem = htmlPage.getFirstByXPath("//*[@data-recipe-id]");
                        String valueID = testElem.getAttribute("data-recipe-id");
                        //System.out.println(valueID); //this took so much time

                        String xPath = "//*[@id=\"wprm-recipe-" + valueID + "-step-0-" + elem + "\"]";
                        DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
                        //*[@id="wprm-recipe-23545-step-0-0"]
                        //*[@id="wprm-recipe-23545-step-0-0"]/div/span/text()

                        if (element != null)
                        {
                            String test = element.getTextContent();
                            test = test.trim();
                            test = test.replace("\n","");
                            this.recipe.add(test);
                        }
                        else
                        {
                            elementNotNull = false;
                        }
                        elem++;
                    }
                    break;
                case "thestayathomechef": //most straightforward one
                    elem = 0;
                    while (elementNotNull)
                    {
                        DomElement testElem = htmlPage.getFirstByXPath("//*[@data-recipe-id]");
                        String valueID = testElem.getAttribute("data-recipe-id");
                        String xPath = "//*[@id=\"wprm-recipe-" + valueID + "-step-0-" + elem + "\"]";
                        DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
                        if (element != null)
                        {
                            String test = element.getTextContent();
                            test = test.trim();
                            test = test.replace("\n","");
                            this.recipe.add(test);
                        }
                        else
                        {
                            elementNotNull = false;
                        }
                        elem++;
                    }
                    break;
                default:
                    System.out.println("Error: invalid input (domain not found or not in the list.)");
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e);
        }
        //printRecipe(recipe);
        return recipe;
    }
    public void printRecipe(ArrayList currRecipe)
    {
        if(!currRecipe.isEmpty())
        {
            System.out.println("\nRecipe:");
            int listLength = currRecipe.size();
            for (int i = 0; i < listLength; i++)
            {
                System.out.print(i + 1 + ") " + currRecipe.get(i) + "\n");
            }
        }
    }
    public String fileRecipe(ArrayList filedRecipe, String user, Connection dbConnection)
    {
        String resultCode = "";
        try
        {
            Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String queryCheckData = "SELECT RECIPE_ID FROM RECIPES WHERE RECIPE_TITLE='" + recipeTitle + "'";
            ResultSet queryCheckIfFilled = stm.executeQuery(queryCheckData);
            if (queryCheckIfFilled.next())
            {
                resultCode = "RECIPE_EXISTS";
            }
            else
            {
                int listLength = filedRecipe.size();
                String currentFiled = "";
                String nextFiled;
                for (int i = 0; i < listLength; i++)
                {
                    if (!currentFiled.equals(""))
                    {
                        nextFiled = filedRecipe.get(i).toString();
                        currentFiled = currentFiled + "@" + nextFiled;
                    }
                    else
                    {
                        currentFiled = filedRecipe.get(i).toString();
                    }
                }
                String queryFile = "INSERT INTO RECIPES(SOURCE_USER,RECIPE_TITLE,RECIPE_SOURCE,RECIPE_CONTENT) VALUES ('" + user + "','" + recipeTitle + "','" + urlDomain + "',\"" + currentFiled + "\")";
                stm.executeUpdate(queryFile);
                resultCode = "RECIPE_ADDED";
            }
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        return resultCode;
    }
    public ArrayList<String> splitFiled(String filedRecipe)
    {
        String splitRaw[] = filedRecipe.split("@");
        ArrayList<String> splitRecipe = new ArrayList<>(Arrays.asList(splitRaw));
        return splitRecipe;
    }
}

package cookbook.webscraper;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class CookbookScraper
{
    ArrayList<String> recipe = new ArrayList<>(); //chose ArrayList because faster to parse, only needs to be modified once.

    public ArrayList scrapeLink(String url)
    {
        /*todo:
            1. parse domain
            2. make individual ways to scrape (1/3)
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

            System.out.println("Title: " + htmlPage.getTitleText());
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

            String urlDomain = StringUtils.substringBetween(url, "https://www.", ".");
            System.out.println("Domain: " + urlDomain);

            switch(urlDomain)
            {
                case "thespruceeats":
                    if(htmlPage.getTitleText().toLowerCase().contains("recipe")) //check if title contains recipe, kinda basic way of doing it
                    {
                        int elem = 2;
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
                                if (!test.contains("Recipe Tips"))
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
                        int listLength = this.recipe.size();
                        for (int i = 0; i < listLength; i++)
                        {
                            System.out.print(i + 1 + ") " + this.recipe.get(i) + "\n");
                        }
                    }
                    else
                    {
                        System.out.println("Domain does not seem to contain a recipe.");
                    }
                    break;
                case "gimmesomeoven":
                    if(url.contains("recipe")) //check if url contains recipe
                    {
                        int elem = 1;
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
                        int listLength = this.recipe.size();
                        for (int i = 0; i < listLength; i++)
                        {
                            System.out.print(i + 1 + ") " + this.recipe.get(i) + "\n");
                        }
                    }
                    else
                    {
                        System.out.println("Domain does not seem to contain a recipe.");
                    }
                    break;
                case "playfulcooking":
                    int elem = 1;
                    while (elementNotNull)
                    {
                        // //*[@id="wprm-recipe-23513-step-0-0"]
                        // /html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[1]/div
                        // /html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[2]/div

                        String xPath = "/html/body/div[1]/div/div/div/article/div[3]/div[2]/div[2]/div/div[10]/div/ul/li[" + elem + "]/div";
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
                    int listLength = this.recipe.size();
                    for (int i = 0; i < listLength; i++)
                    {
                        System.out.print(i + 1 + ") " + this.recipe.get(i) + "\n");
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
        return recipe;
    }
}

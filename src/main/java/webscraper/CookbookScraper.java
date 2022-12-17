package webscraper;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class CookbookScraper
{
    public void scrapeLink(String url)
    {
        /*todo:
            1. parse domain
            2. make individual ways to scrape (1/3)
            3. more and more testing
         */
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
            System.out.println("Domain:" + urlDomain);

            switch(urlDomain)
            {
                case "thespruceeats":
                    if(htmlPage.getTitleText().toLowerCase().contains("recipe")) //check if title contains recipe, kinda basic way of doing it
                    {
                        int elem = 2;
                        ArrayList<String> recipe = new ArrayList<>();
                        while (elementNotNull)
                        {
                            String xPath = "//*[@id=\"mntl-sc-block_3-0-" + elem + "\"]";
                            DomElement element = htmlPage.getFirstByXPath(xPath); //use xpath
                            if (element != null)
                            {
                                String test = element.getTextContent();
                                //System.out.println(test);
                                //String test = element.getAttribute("innerHTML");
                                recipe.add(test);
                            }
                            else
                            {
                                elementNotNull = false;
                            }
                            elem = elem + 5;
                        }
                        int listLength = recipe.size();
                        for (int i = 0; i < listLength; i++)
                        {
                            System.out.print(i + 1 + ") " + recipe.get(i) + "\n");
                        }
                    }
                    else
                    {
                        System.out.println("Domain does not seem to be a recipe.");
                    }
                    break;
                default:
                    System.out.println("Invalid input (domain not found)");
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e);
        }
    }
}

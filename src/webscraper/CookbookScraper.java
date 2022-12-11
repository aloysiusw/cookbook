package webscraper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;

public class CookbookScraper
{
    public void scrapeLink(String url)
    {

        //todo: parse domain
        try
        {
            //System.out.println("A");
            WebClient webClient = new WebClient(BrowserVersion.CHROME); //simulate Firefox

            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

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
            */
            boolean elementNotNull = true;
            int elem=2;
            ArrayList<String> recipe = new ArrayList<>();
            while(elementNotNull)
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
                elem=elem+5;
            }
            int listLength = recipe.size();
            for(int i=0;i<listLength;i++)
            {
                System.out.print(i+1 + ") " + recipe.get(i) + "\n");
            }
            /*
            List<HtmlAnchor> links = htmlPage.getAnchors();
            for (HtmlAnchor link : links)
            {
                String href = link.getHrefAttribute();
                System.out.println("C");
                System.out.println("Link: " + href);
            }
            */
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e);
        }
    }
}

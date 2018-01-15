package org.lir.util;

import org.apache.lucene.index.IndexWriter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lir.main.Indexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class WebSpider {

    private HashSet<String> links;
    private int depth;
    private String index;
    private BufferedWriter writer = null;
    private HashMap<String,Integer> urlDepth= null;
    private String url;
    //Constructor for the class.
    public WebSpider(String seedUrl,int crawlDepth,String indexPath) {
    	this.depth =crawlDepth;
    	this.links = new HashSet<String>();
    	this.urlDepth = new HashMap<String,Integer>();
    	this.index = indexPath;
    	this.url = seedUrl;
    }
    
    public void init() {
    	//Crawl and index
    	Indexer index = new Indexer();
    	IndexWriter writer = index.initialize(true,this.index);
    	if(writer==null) {
    		return;
    	}
    
    	boolean flag = extractWebpageLink(writer,Normalization.normalizeUrl(this.url),0,this.index);
    	//Begin writing
    	if(flag) {
    		try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	String fileName = this.index + "\\pages.txt"; 
    	writeToFile(fileName,urlDepth);
    	}
    }

    
    private static String usage() {
    	return "Please use full url Example:www.example.com";
    }
    
    /**
     * 	Method for crawling through the webpages and extracting the Urls.
     * 	Storing the URL in ArrayList<> SavedLinks.
     * 	Param: URL to be crawled, Depth. 
     *	
     */
    
    public boolean extractWebpageLink(IndexWriter writer, String URL, int crawlDepth,String indexPath) {
    	org.jsoup.nodes.Document document = null;
    	
        if ((!links.contains(URL) && (URL != "" ))) {
           System.out.println("Depth: " + crawlDepth + " [" + URL + "]");
            urlDepth.put(URL, crawlDepth);
            try {
                links.add(URL);
                
                // Fetch the HTML code
                Connection con = Jsoup.connect(URL).timeout(50000).ignoreHttpErrors(true);
                
                Connection.Response response = con.execute();
    			if (response.statusCode() == 200) {
    				document = con.get();
    			}
                
                if(document!=null){
                //Initial indexing with create as default true
                	Indexer index = new Indexer();
                	index.indexDoc(writer, document);
                // Parse the HTML to extract links to other URLs
                if(crawlDepth<this.depth) {
                	Elements linksOnPage = document.select("a[href]");
                    // For each extracted URL... recursively call the method extractWebpageLink().
                    for (Element webpage : linksOnPage) {
                    	String urlNorm = Normalization.normalizeUrl(webpage.absUrl("href").toString());					
    					// recurse on the url if page is not already indexed
    					if (urlNorm != null && !this.links.contains(urlNorm)) {
                    	extractWebpageLink(writer,urlNorm, crawlDepth+1,indexPath);
                    }	
                }
                
                }
                }

            } catch (Exception e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    
    
    /**
     * 	Method for Writing the Urls stored in the arraylist to a .txt file.
     * 	Param: File name to be stored. 
     *	
     */
    
    public void writeToFile(String filename,HashMap<String,Integer> urlDepths) {
        FileWriter writer;
        
        try {
            writer = new FileWriter(filename);
            
            System.out.println("Writting URLS to " + filename + " ");
            
            //Iterating the URLs in the Array list and writting them to the file.   
            for (Map.Entry<String, Integer> entry : urlDepths.entrySet())
            {
                String url = entry.getKey();
                Integer depth = entry.getValue();
                //use key and value
                String urlDepth = url + "\t" + depth;
                try {
                    //save to file
                    writer.write(urlDepth);
                    writer.write("\n");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
           writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }   
}
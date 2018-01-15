package org.lir.util;

import org.apache.lucene.index.IndexWriter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lir.main.Indexer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class WebSpider {

	private HashSet<String> links;
	private int depth;
	private String index;
	private HashMap<String, Integer> urlDepth = null;
	private String url;

	// Constructor for the class.
	public WebSpider(String seedUrl, int crawlDepth, String indexPath) {
		this.depth = crawlDepth;
		this.links = new HashSet<String>();
		this.urlDepth = new HashMap<String, Integer>();
		this.index = indexPath;
		this.url = seedUrl;
	}

	// A init function to initialize values
	public void init() {
		// Crawl and index
		Indexer index = new Indexer();
		IndexWriter writer = index.initialize(true, this.index);
		if (writer == null) {
			return;
		}

		boolean flag = extractWebpageLink(normalizeUrl(this.url), 0, this.index, writer);
		// Begin writing
		if (flag) {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String fileName = this.index + "\\pages.txt";
			writeToFile(fileName, urlDepth);
		}
	}

	/**
	 * Method for crawling through the webpages and extracting the Urls. Map Stores
	 * the Link and its depth Param: URL to be crawled, Depth.
	 * 
	 */

	public boolean extractWebpageLink(String URL, int crawlDepth, String indexPath, IndexWriter writer) {
		org.jsoup.nodes.Document document = null;

		if ((!links.contains(URL) && (URL != ""))) {
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
				if (document != null) {
					// Initial indexing with create as default true
					Indexer index = new Indexer();
					index.indexDoc(writer, document);
					// Parse the HTML to extract links to other URLs
					if (crawlDepth < this.depth) {
						Elements linksOnPage = document.select("a[href]");
						// For each extracted URL... recursively call the method extractWebpageLink().
						for (Element webpage : linksOnPage) {
							String url = normalizeUrl(webpage.absUrl("href").toString());
							if (url != null && !this.links.contains(url)) {
								extractWebpageLink(url, crawlDepth + 1, indexPath, writer);
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
	 * Method for Writing the Urls stored in the arraylist to a .txt file. Param:
	 * File name to be stored.
	 * 
	 */

	public void writeToFile(String filename, HashMap<String, Integer> urlDepths) {
		FileWriter writer;

		try {
			writer = new FileWriter(filename);

			System.out.println("Writting URLS to " + filename + " ");

			// Iterating the URLs in the Array list and writing them to the file.
			for (Map.Entry<String, Integer> entry : urlDepths.entrySet()) {
				String url = entry.getKey();
				Integer depth = entry.getValue();
				// use key and value
				String urlDepth = url + "\t" + depth;
				try {
					// save to file
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

	/*
	 * Performs a simple normalization for the url removing (/,#) and adding https.
	 * 
	 */
	public static String normalizeUrl(String url) {

		if (!(url.startsWith("http") || url.startsWith("www")) || url == "") {
			return null;
		}

		// Lowercasing
		String urlNormalized = url.toLowerCase();

		// Remove anchor link by taking substring
		if (urlNormalized.contains("#")) {
			urlNormalized = urlNormalized.substring(0, urlNormalized.indexOf('#'));
		}

		if (urlNormalized.endsWith("/")) {
			urlNormalized = urlNormalized.substring(0, urlNormalized.length() - 1);
		}

		// Adding https
		if (urlNormalized.startsWith("www")) {
			urlNormalized = "https://" + urlNormalized;
		}
		return urlNormalized;
	}
}

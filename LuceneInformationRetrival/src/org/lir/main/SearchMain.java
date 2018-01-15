package org.lir.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lir.util.WebSpider;

/*

##############################Example###################################################
java -jar IR_P02.jar  [seed URL] [crawl depth] [path to index folder] [query] #
########################################################################################
*/


/*
 * 
 * 
 * Team Members:
 * SUBASH PRAKASH (220408)
 * NIKHIL MANDYA PARASHIVAMURTHY (220641)
 * ANSTUP DAS(220571)
 * 
 * Description:
 * Perform a crawling and put the urls into a text file
 * Perform a indexing and searching
 * 
 */
public class SearchMain {

	 private static void usage() {
	System.out.println("The program should be executed as:" + "java -jar IR_P02.jar  [seed URL] [crawl depth] [path to index folder] [query]");
	}

	
	// Lets take the values required as shown above as below values:
	// [seed URL] [crawl depth] [path to index folder] [query]
	public static void main(String args[]) {

		if (args.length == 0) {
			usage();
		} else if (args.length != 4) {
			usage();
		}
			
		else {
			// Actual execution of the code
			String seedUrl = args[0];
			
			if(!seedUrl.contains("www")) {
				usage();		
			}
			else {
			int crawlDepth = Integer.parseInt(args[1]);
			String indexFolder = args[2];
			String query = args[3];
			
			//Calling Crawler and indexing
			WebSpider crawl = new WebSpider(seedUrl, crawlDepth, indexFolder);
			crawl.init();
			IndexSearch search = new IndexSearch(query);

			try {
				search.initSearch(indexFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}
}

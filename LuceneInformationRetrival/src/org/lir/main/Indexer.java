package org.lir.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lir.util.HtmlParser;
import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexableFieldType;
import org.jsoup.*;
public class Indexer {

	   public IndexWriter initialize(boolean create,String indexPath) {
		IndexWriter writer = null;

		try {

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			// A folder check needs to added and then make the create to false if the
			// indexFolder exists
			boolean exists = DirectoryReader.indexExists(dir);

			if (exists) {
			//System.out.println("Already the index directory is present, so updating...");
				create = false;
			}

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				System.out.println("Creating Index");
				iwc.setOpenMode(OpenMode.CREATE);
			}
			else {
				return writer;
			}
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			
		}
		return writer;
	}
	
	/** Indexing the documents */
	public void indexDoc(IndexWriter writer,org.jsoup.nodes.Document document) throws IOException {
		
		String body = document.body().text();
		String url = document.location();
		String title = document.title();
		
		//Applied stemming and removed stop words
		String newBody = removeStopWords(body.toLowerCase());
		
		
		// apache lucene document
		org.apache.lucene.document.Document doc = new Document();
		
			//Adding body
			Field bodyField = new TextField("contents", newBody, Field.Store.YES);
			doc.add(bodyField);
			
			//Adding Title
			Field titleField = new StringField("title", title, Field.Store.YES);
			doc.add(titleField);
			
			//Adding URL
			Field URL = new StringField("url", url, Field.Store.YES);
			doc.add(URL);			
			
				writer.addDocument(doc);
	}

	//Porter Stemmer implementation using Snowball Library
	public static  String applyPorterStemmer(String input) throws IOException {
		String[] tokens = input.split("\\s+");
		PorterStemmer stemmer = new PorterStemmer();
		StringBuffer combine = new StringBuffer();
		combine.append("");
		
		for(String word:tokens) {
			stemmer.setCurrent(word);
	        stemmer.stem();
	        if(stemmer.getCurrent()!=null) {
	        	if(combine.toString().isEmpty()) {
	        		combine.append(stemmer.getCurrent());
	        	}
	        	else {
	        	combine.append(" " + stemmer.getCurrent());
	        	}
		}
		}
       
        return combine.toString();
    }
	
	//This is where we have implemented the method to allow removal of stopWords.We are currently using this method during the indexing process
	public static String removeStopWords(String body){
		//Default Set of stop words are being used
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        TokenStream tokenStream = new StandardAnalyzer().tokenStream(null, new StringReader(body));
        tokenStream = new StopFilter(tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try {
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term + " ");
        }
        tokenStream.close();
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        
        return sb.toString();
    }
}

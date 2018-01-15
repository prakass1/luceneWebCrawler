package org.lir.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.lir.model.Model;

public class HtmlParser {

	public Document parseDoc(String path) throws IOException {
		File input = new File(path);
		Document doc = Jsoup.parse(input, "UTF-8");
		return doc;
		
	}
	
	
	public void makeHTML(List<Model> model) throws FileNotFoundException {
		String html= "<html><head><title>Output Result</title></head><body>";
		html+="<table border='1'><tr><th>RANK</th><th>PATH</th><th>TITLE</th><th>RELSCORE</th><th>SUMMARY</th></tr>";
		PrintWriter pw = null;
		for(Model m:model) {
			
		if(m.getUrl()!=null)
		{
			File fileToWrite = new File("output.html");
			pw = new PrintWriter(fileToWrite);
			html+="<tr><td>" + m.getRank() + "</td><td>" + m.getUrl() + "</td><td>" + m.getTitle() + "</td><td>" + m.getRelScore()+ "</td><\tr>";
		}
		}
		
		html+="</table></body></html>";
		if(pw!=null) {
		pw.write(html);
		pw.close();
		}
		
	}
	
}

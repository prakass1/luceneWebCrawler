package org.lir.util;

public class Normalization {	
	public static String normalizeUrl(String url) {
		
		if ( !(url.startsWith("http")|| url.startsWith("www") ) || url == "") {
			return null;
		}
		
		//Lower casing the String 
		String urlNormalized = url.toLowerCase();
		
		//Truncate anchor link
		if (urlNormalized.contains("#")) {
			urlNormalized = urlNormalized.substring(0, urlNormalized.indexOf('#'));
		}		
		// remove trailing slash if it exists
		if (urlNormalized.endsWith("/")) {
			urlNormalized = urlNormalized.substring(0, urlNormalized.length() - 1);
		}
		
		// add https to the link 
		if (urlNormalized.startsWith("www")) {
			urlNormalized = "https://" + urlNormalized;
		}
		return urlNormalized;
	}
}
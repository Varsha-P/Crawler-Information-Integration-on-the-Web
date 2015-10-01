package source;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Iterator;


public class source {
	public static int count, sam_count;
	public static Set<String> pagesVisited = new HashSet<String>();
	public static Set<String> finalSet = new HashSet<String>();
	public static Set<String> samsung = new HashSet<String>();
    public static List<String> pagesToVisit = new LinkedList<String>();
    
    // Function returns proce/cost from a string
    public static Double price(String val) {
		String regExp = "\\$[0-9,\\.]+";
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(val);
		List<String> lstMatches = new ArrayList<String>(5);
		while (matcher.find()) {
		    lstMatches.add(matcher.group());
		}
		Double s = 0.0 ;
		for (String match : lstMatches) {
		    // Strip off the unrqeuired elements...
		    match = match.replaceAll("\\$", "");
		    match = match.replaceAll(",", "");
		    s = Double.parseDouble(match);
		}	
		return s;
	}
    
 // Function returns next URL too be scrapped avoiding pages already visited
 	public static String nextUrl() {
 		 if(pagesToVisit.size()==0) {
 			 return "";
 		 }
 		 String nextUrl = new String();
 	     int flag = 1;
         while(flag==1) {
         	nextUrl = pagesToVisit.remove(0);
         	if(nextUrl.contains("#")) {
 		      int l = nextUrl.indexOf('#');
 		      String newUrl3 = new String();
 		      newUrl3 = nextUrl.substring(0, l);
 		      if(pagesVisited.contains(newUrl3)) { 		    	  
 		      }
 		      else {
 		    	  flag = 0;
 		      }
 		      }
         	else if(pagesVisited.contains(nextUrl)) {	
         	}
         	else
         		flag = 0;
         }
         count++;
         if(nextUrl.isEmpty()) {
         	return nextUrl();
         }
         else if(nextUrl.equals("http://announcements.ebay.com")) {
         	return nextUrl();
         }
         else if(nextUrl.equals("http://www.ebay.com/sch/womens-clothing-/15724/i.html?_dcat=15724&Size%2520Type=Petites")) {
         	return nextUrl();
         }
         else if(nextUrl.equals("http://www.ebay.com/itm/GameStop-Gift-Card-50-and-100-mail-delivery-/121699339743")) {
         	return nextUrl();
         }
         else if(nextUrl.equals("http://www.stubhub.com")) {
         	return nextUrl();
         }
         	return nextUrl;
     }
    
    // Function to write html files into the folder
    public static void writeToFile() throws IOException {
    	int fgh = 0;
		FileWriter writ = new FileWriter("urlList.txt", true);
		Iterator itera = finalSet.iterator();
		while(itera.hasNext()) {
			String xx = itera.next().toString();
		    Document doc = Jsoup.connect(xx).timeout(100000000).get();
			fgh++;
			writ.write(fgh+" : "+xx);
		    writ.write("\r\n"); 
			String filePath = "HTML files/";
			filePath = filePath+fgh+".html";
			FileWriter writer = new FileWriter(filePath);
			writer.write(doc.toString());
			writer.close();
		}
        writ.close();
	}
    
    // Function returns domain-name/host of a URL
    public static String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    String domain = uri.getHost();
	    return domain.startsWith("www.") ? domain.substring(4) : domain;
	}
    
   
	
	// Scrapper function. 
	public static int scrape(String url) throws URISyntaxException {
		try {
			try {
			Document doc = Jsoup.connect(url).timeout(100000000).get();
			org.jsoup.select.Elements links = doc.select("a");
			pagesVisited.add(url);
			for (Element e: links) {
				if(e.toString().toLowerCase().contains("javascript")){	}
				else if (e.toString().contains("%2520")){ }
				else if(e.attr("href").toString().contains(" ")){ }
				else if(e.attr("href").toString().contains("|")){ }
				else if(e.attr("href").contains("mailto")){ }
				else {
					String urlOfE;
					if(!e.attr("href").equals("")) {
						if(e.attr("href").contains("&amp;")) {
							urlOfE = e.attr("href").toString().replace("/&amp;/g", "&");
						}
						else {
							urlOfE = e.attr("abs:href");
						}
						URL x= new URL(urlOfE);
						URL uu = new URI(x.getProtocol(), x.getHost(), x.getPath(), x.getQuery(), null).toURL();
						String xx = uu.toString();
						String url1 = getDomainName(urlOfE);
						if(url1.contains("ebay.com")) {
							pagesToVisit.add(xx);
							if(urlOfE.contains("ebay.com/itm/Samsung")||(urlOfE.contains("/itm/")&&urlOfE.contains("Samsung")&&urlOfE.contains("Galaxy"))) {
								if(pagesVisited.contains(urlOfE)) {
						        }
								else {
									if(finalSet.size()>1200) {
										
									}
									else {
										sam_count++;
										String content="";
										Document docu = Jsoup.connect(urlOfE).timeout(100000000).get();
										for(Element meta : docu.select("meta")) {
										    if(meta.attr("property").equals("og:description")) {
										    	content = meta.attr("content").toString();
										    }
										}
										Double dd = price(content);
										if(dd>=0 && dd<=400)
											finalSet.add(urlOfE);
									}
								}
							}
						}	
					}
				}
			}
			if(finalSet.size()>=1200) {
				return sam_count;
			}
			String check = nextUrl();
			if(check.equals("")) {
				return -1;
			}
			else {
			count = scrape(check);
			}
			if(count == -1) {
				return -1;
			}
			}
			catch (SocketException e) {
				// do nothing
			}
		}
		catch (IOException ex) {
				Logger.getLogger(source.class.getName()).log(Level.SEVERE, null, ex);
			}
		return count;
	}
	
	 // Main function
		public static void main(String[] args) throws URISyntaxException, IOException {
			count = 0;
			sam_count = 0;
			String url = "http://www.ebay.com/sch/samsung-galaxy";
			for(int fg = 2; fg<=20; fg++) {
				String build = "http://www.ebay.com/sch/i.html?_nkw=samsung+galaxy&_pgn="+fg+"&_skc=50&rt=nc";
				pagesToVisit.add(build);
			}
			count =  scrape(url);
			if(count == -1) {
				System.out.println("No more URLs in list");
			}
		    writeToFile();
		}
	
}
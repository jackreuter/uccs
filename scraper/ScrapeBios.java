import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements; 

public class ScrapeBios {

    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String URL = "http://www.s9.com/Browse/";
    
    public static void main(String[] args) throws Exception {
    
	for (int letterIndex = 0; letterIndex < 26; letterIndex++) {
	    URL s9 = new URL(URL+ALPHABET.charAt(letterIndex));
	    URLConnection sc = s9.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
	    String html = "";
	    String line = in.readLine();
	    while (line != null) {
		html += line;
		line = in.readLine();
	    }
	    Document doc = Jsoup.parse(html);
	    int pageCount;
	    if (letterIndex != 23) {
		pageCount = Integer.parseInt(doc.select("#pageLink").last().text());
	    }
	    else {
		pageCount = 1;
	    }
	    in.close();
      
	    for (int page = 0; page < pageCount; page++) {

		s9 = new URL(URL+ALPHABET.charAt(letterIndex)+"~"+(page*30)+",30");
		sc = s9.openConnection();
		in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
		html = "";
		line = in.readLine();

		while (line != null) {
		    html += line;
		    line = in.readLine();
		}

		doc = Jsoup.parse(html);
		Elements people = doc.select("table").last().select("tr");

		int count = 0;
		for (Element p : people) {
		    if (count==0) {}
		    else {System.out.println(p.text());}
		    count++;
		}

		in.close();
	    }
	}
    }
}

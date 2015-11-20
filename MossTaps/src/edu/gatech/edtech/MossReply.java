package edu.gatech.edtech;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MossReply {
	private URL mossURL;
	private String urlString;
	private SoftwareLanguage language;
	private Document doc;

	public MossReply(URL url, SoftwareLanguage sl) throws IOException{
		this.mossURL = url;
		this.urlString = url.toString();
		this.language = sl;
		this.doc = Jsoup.connect(url.toString()).get();
	}
	public MossReply() {
	}
	
	public List<MossRecord> extractMossLinks() throws IOException {
		List<MossRecord> records = new ArrayList<MossRecord>();
		System.out.println("Fetching "+this.urlString);
		Elements tables = doc.getElementsByTag("TABLE");
		for (Element table: tables){
			Elements rows = table.getElementsByTag("TR");
			for (Element row: rows){
				MossRecord record = new MossRecord();
				Elements links = row.getElementsByTag("a");
				if (links.size() == 2){
					record.setProjectStudentPercentA(links.get(0).text());
					record.setProjectStudentPercentB(links.get(1).text());
					record.setMossCompareLink(links.get(0).attr("href"));
				}
				Elements cells = row.getElementsByTag("TD");
				if (cells.size() == 3){
					record.setLinesMatched(endNumber(cells.get(2).text()));
				}
				// only add if there was data
				// and not the same student
				// and at least one current project
				if(!(record.getStudentA()== null)
						&& !(record.getStudentA().equals(record.getStudentB()))
						&& record.hasCurrentProject()		
						) {
					records.add(record);
				}
			}
		}
		return records;
	}

	private int endNumber(String text) {
		int matching;
		try {
			 matching = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			System.out.println("did not find a number of lines matched");
			matching = 0;
		}
		return matching;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public void setMossURL(URL mossURL) {
		this.mossURL = mossURL;
		this.urlString = mossURL.toString();
	}
	public void setLanguage(SoftwareLanguage language) {
		this.language = language;
	}
	public URL getMossURL() {
		return mossURL;
	}
	public SoftwareLanguage getLanguage() {
		return language;
	}
	public String getUrlString() {
		return urlString;
	}
	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}
}

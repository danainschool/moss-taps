package edu.gatech.edtech;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class Submission {
	String language;
	String server = "moss.stanford.edu";
	String port = "7690";
	String userID;
	String parentFolder;
	

	public Submission(String folder, String language, String userID) {
		this.language = language;
		this.userID = userID;
		this.parentFolder = folder;				
	}
	
	public String submit(){
		Collection<File> files = FileUtils.listFiles(new File(parentFolder), new String[] {language}, true);
		
		return null;
		
	}
}

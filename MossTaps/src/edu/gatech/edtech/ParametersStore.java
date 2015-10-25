package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class ParametersStore {
	// note that these structures taken from David Joyber's MOSSifier 1.1 script
	private List<SoftwareLanguage> languages = new ArrayList<SoftwareLanguage>();
	private String canonFolder;
	private String currentFolder;
	private String uploadFolder;
	private boolean collectionNeeded = true;
	private int linesThreshold = 100;

	public ParametersStore() {
		// TODO Auto-generated constructor stub
	}
	public ParametersStore(String[] args) {
		// TODO Auto-generated constructor stub
		// look for initialization file parameters, or use defaults
		if(isInitFile()){
			fetchInitValues();
		}
		// verify setup with user - include query
		verify();
			
	}


	private void verify() {
		// TODO Auto-generated method stub
		
	}

	private void fetchInitValues() {
		// TODO Auto-generated method stub
		
	}

	private boolean isInitFile() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<SoftwareLanguage> getLanguages() {
		return languages;
	}

	public void setLanguages(List<SoftwareLanguage> languages) {
		this.languages = languages;
	}

	public String getCanonFolder() {
		return canonFolder;
	}

	public void setCanonFolder(String canonFolder) {
		this.canonFolder = canonFolder;
	}

	public String getCurrentFolder() {
		return currentFolder;
	}

	public void setCurrentFolder(String originalFolder) {
		this.currentFolder = originalFolder;
	}

	public String getUploadFolder() {
		return uploadFolder;
	}

	public void setUploadFolder(String uploadFolder) {
		this.uploadFolder = uploadFolder;
	}

	public boolean isCollectionNeeded() {
		return collectionNeeded;
	}

	public void setCollectionNeeded(boolean collectionNeeded) {
		this.collectionNeeded = collectionNeeded;
	}

	public int getLinesThreshold() {
		return linesThreshold;
	}

	public void setLinesThreshold(int linesThreshold) {
		this.linesThreshold = linesThreshold;
	}

	public void loadParameters(String[] args) {
		// TODO Auto-generated method stub
		
	}

}

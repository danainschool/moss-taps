package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class ProjectCollection {
	
	String projectDir;
	List<StudentProject> studentProjects = new ArrayList<StudentProject>();

	public ProjectCollection(String folder, String uploadFolder) {
		// TODO Auto-generated constructor stub
		projectDir = folder;
		studentProjects = crawl();
		moveToUpload(uploadFolder);
	}

	private void moveToUpload(String uploadFolder) {
		// TODO Auto-generated method stub
		
	}

	private List<StudentProject> crawl() {
		// TODO Auto-generated method stub
		return null;
	}
	

}

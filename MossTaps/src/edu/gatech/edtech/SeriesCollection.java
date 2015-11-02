package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class SeriesCollection {
	ArrayList<ProjectCollection> groupOfProjects = new ArrayList<ProjectCollection>();
	String seriesFolder;
	String uploadFolder;
	List<SoftwareLanguage> languages;
	String prefix;
	
	public SeriesCollection(List<SoftwareLanguage> languages,String prefix, String seriesFolder, String uploadFolder) {
		this.languages = languages;
		this.prefix = prefix;
		this.seriesFolder = seriesFolder;
		this.uploadFolder = uploadFolder;
	}

	// removes files in upload folder
	public void collect() {
		// TODO implement
		// for each language,
		//     if no language directory in upload, create one
		//     for each project in seriesFolder,
		//         for each student in project folder
		//             crawl recursively thru entire contents
		//             for each file where the extension matches this language,
		//                  if no prefix-project-student folder, create one
		//                  copy file to prefix-project-student folder
	}

	public void consolidateStudents() {
		// TODO implement
		// for each language folder
		//     for each prefix-project-student folder in upload
		//         for each file in prefix-project-student folder
		//              if no prefix-student file, create one
		//              append file to folder 
		//         remove prefix-project-student folder
	}
}

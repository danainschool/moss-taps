package edu.gatech.edtech;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;

public class SeriesCollection {
	ArrayList<ProjectCollection> groupOfProjects = new ArrayList<ProjectCollection>();
	String seriesFolder;
	String uploadFolder;
	List<SoftwareLanguage> languages;
	String prefix;
	File[] projectFolders;
	
	public SeriesCollection(List<SoftwareLanguage> languages,String prefix, String seriesFolder, String uploadFolder) {
		this.languages = languages;
		this.prefix = prefix;
		this.seriesFolder = seriesFolder;
		this.uploadFolder = uploadFolder;
		this.projectFolders = new File(seriesFolder).listFiles((FilenameFilter) DirectoryFileFilter.DIRECTORY);
		showFiles(projectFolders);
	}

//	// removes files in upload folder
//	public void collect() {
//		// TODO implement
//		// for each language,
//		//     if no language directory in upload, create one
//		//     for each project in seriesFolder,
//		//         for each student in project folder
//		//             crawl recursively thru entire contents
//		//             for each file where the extension matches this language,
//		//                  if no prefix-project-student folder, create one
//		//                  copy file to prefix-project-student folder
//	}
//
//	public void consolidateStudents() {
//		// TODO implement
//		// for each language folder
//		//     for each prefix-project-student folder in upload
//		//         for each file in prefix-project-student folder
//		//              if no prefix-student file, create one
//		//              append file to folder 
//		//         remove prefix-project-student folder
//	}


	public void inflateZips() {
		Collection<File> files = FileUtils.listFiles(
				new File(seriesFolder),	new String[] { "zip" }, true);
		showFiles(files);
		for (File file : files) {
			unzip(file.getPath(),file.getParent());
		}

	}
	private static void unzip(String source, String destination) {
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
	}
	private void showFiles(File[] folders) {
		System.out.println("Showing Files");
		for (File folder : folders) {
			System.out.println(folder.getAbsolutePath());
		}
	}
		
	private static void showFiles(Collection<File> files) {
		System.out.println("Showing Files");
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}
		
	}
	public void cleanFileNames() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<ProjectCollection> getGroupOfProjects() {
		return groupOfProjects;
	}

	public String getSeriesFolder() {
		return seriesFolder;
	}

	public String getUploadFolder() {
		return uploadFolder;
	}

	public List<SoftwareLanguage> getLanguages() {
		return languages;
	}

	public String getPrefix() {
		return prefix;
	}

	public File[] getProjectFolders() {
		return projectFolders;
	}
}

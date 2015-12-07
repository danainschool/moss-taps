package edu.gatech.edtech;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class SeriesCollection {
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
		showProjects();
	}

	public int moveToUpload() throws IOException {
		// put language extensions into array
		String[] extensions = new String[languages.size()];
		for (int i = 0; i<languages.size(); i++) {
			extensions[i] = languages.get(i).getExtension();
		}
		// for each project, and for each student, collect all the files with extensions in all subdirectories
		for (File project : projectFolders) {
			File[] studentFolders = new File(project.getPath()).listFiles((FilenameFilter) DirectoryFileFilter.DIRECTORY);
			for (File student : studentFolders) {
				// make a directory in the Upload folder
				String copyDirectory = uploadFolder + File.separator + prependClean(project.getName()) + File.separator + clean(student.getName());
				File studentDirUpload = new File(copyDirectory);
				// find all the files to copy there and copy them
				Collection<File> files = FileUtils.listFiles(student, extensions, true);
				for (File file:files){
					FileUtils.copyFileToDirectory(file,studentDirUpload);
				}
			}
		}
		return 1;
	}

	public String clean(String name) {
		// replace the white space with underscore and prepend with prefix
		name = name.replaceAll("\\s","").replaceAll(",", "_");
		return name;
	}

	public String prependClean(String name) {
		// replace the white space with underscore and prepend with prefix
		return this.prefix + clean(name);
	}
	public void inflateZips() {
		Collection<File> files = FileUtils.listFiles(
				new File(seriesFolder),	new String[] { "zip" }, true);
//		showFiles(files);
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
	private void showProjects() {
		System.out.println("Showing "+this.prefix+" projects");
		for (File folder : projectFolders) {
			System.out.println(folder.getAbsolutePath());
		}
	}
		
//	private static void showFiles(Collection<File> files) {
//		System.out.println("Showing Files");
//		for (File file : files) {
//			System.out.println(file.getAbsolutePath());
//		}
//		
//	}
//
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

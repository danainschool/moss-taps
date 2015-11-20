package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SeriesCollectionTest {
	private List<SoftwareLanguage> sList = new ArrayList<SoftwareLanguage>();
	private String prefix = null;
	private String seriesFolderName = null;
	private String uploadFolderName = null;
	private String originalFolderName = null;
	private String currentFolderName = null;
	
	@Before
	public void setUp() throws Exception {
		String goldenFolderName = "golden-data";
		String testFolderName = "data"+File.separator+"testFolder";
		File goldenFolder = new File(goldenFolderName);
		File testFolder = new File(testFolderName);
		if (testFolder.exists() && testFolder.isDirectory()){
			FileUtils.cleanDirectory(testFolder);
		}
		FileUtils.copyDirectory(goldenFolder, testFolder);
		sList.add(new SoftwareLanguage("Java", "java", "java"));
		sList.add(new SoftwareLanguage("Python", "py", "python"));
		uploadFolderName = testFolderName + File.separator + "Upload";
		originalFolderName = testFolderName + File.separator + "Current";
		currentFolderName = testFolderName + File.separator + "Original";
		prefix = "C_";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMoveFilesToUpload() throws IOException {
		String comment = "test moveToUpload";
		int testValue = 1;
		SeriesCollection sc = new SeriesCollection(sList,prefix,currentFolderName,uploadFolderName);
		sc.inflateZips();
		int testResult = sc.moveToUpload();
		assertEquals(comment,testValue,testResult);
	}
	
	@Test
	public void testPrependCleanName() throws IOException {
		String comment = "test replace white space and prepend";
		String testValue = "C_squashed_name";
		SeriesCollection sc = new SeriesCollection(sList,prefix,currentFolderName,uploadFolderName);
		String testResult = sc.prependClean("squashed, name");
		assertEquals(comment,testValue,testResult);
	}

}

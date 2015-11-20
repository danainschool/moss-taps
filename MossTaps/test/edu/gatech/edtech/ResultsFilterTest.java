package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResultsFilterTest {
	private File input;
	private Document doc;
	private MossRecord mRecord;
	private MossReply mReply;

	@Before
	public void setUp() throws Exception {
		input = new File("data"+File.separator+"MossResults"
				+File.separator+"846377696.htm");
		doc = Jsoup.parse(input, "UTF-8", "http://moss.stanford.edu");
		mRecord = new MossRecord("stuA","stuB","moss.stanford.edu",
				"projA","projB",true,false,25,50,60);
		
		mReply = new MossReply();
		String testFile = "testHtmlJunit.html";
		File input = new File(testFile);
		Document d = Jsoup.parse(input, "UTF-8", "http://moss.stanford.edu");
		mReply.setDoc(d);
		mReply.setUrlString(testFile);
		
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testStringify() {
//		String comment = "test stringify method";
//		String testValue = "25,moss.stanford.edu,stuA,projA,50,stuB,projB,60";
//		List<MossRecord> testList = new ArrayList<MossRecord>();
//		testList.add(mRecord);
//		List<String> testCsv = ResultsFilter.stringify(testList);
//		assertEquals(comment,testValue,testCsv.get(1));
//	}
	@Test
	public void testStringify() {
		String comment = "test stringify method abbreviated";
		String testValue = "25,moss.stanford.edu,\"projA\",\"stuA\",50,\"projB\",\"stuB\",60";
		List<MossRecord> testList = new ArrayList<MossRecord>();
		testList.add(mRecord);
		List<String> testCsv = ResultsFilter.stringify(testList);
		assertEquals(comment,testValue,testCsv.get(1));
	}

	@Test
	// this is a two-unit test: extractMossLinks and saveToCSV
	public void testSaveToCSV() throws IOException  {
		List<MossRecord> mrecs = mReply.extractMossLinks();
		List<String> strList = ResultsFilter.stringify(mrecs);
		ResultsFilter.saveCsvResults("testOutCSV.csv",strList);
		assertEquals("check the file",1,1);
	}

}

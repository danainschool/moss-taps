package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MossReplyTest {
	MossReply mr = new MossReply();

	@Before
	public void setUp() throws Exception {
		String testFile = "testHtmlJunit.html";
		File input = new File(testFile);
		Document d = Jsoup.parse(input, "UTF-8", "http://moss.stanford.edu");
		mr.setDoc(d);
		mr.setUrlString(testFile);
		
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void test() {
//		fail("Not yet implemented"); // 
//	}
//
	@Test
	public void testExtractMossLinks() throws IOException {
		List<MossRecord> testRecords = mr.extractMossLinks();
		String comment = "test extractMossLinks";
		String testValue = "C:/Upload/P1-2015/Blacker,_Jordan(82d08df03494defb6b12f52319752fdc)/ (87%)";
		String testResult = testRecords.get(0).getStudentA();
		assertEquals(comment, testValue, testResult);
	}
}

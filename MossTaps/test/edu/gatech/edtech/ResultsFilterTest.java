package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;
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
	private MossRecord mr;

	@Before
	public void setUp() throws Exception {
		input = new File("data"+File.separator+"MossResults"
				+File.separator+"846377696.htm");
		doc = Jsoup.parse(input, "UTF-8", "moss.stanford.edu");
		mr = new MossRecord("stuA","stuB","moss.stanford.edu",
				"projA","projB",true,false,25,50,60);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStringify() {
		String comment = "test stringify method";
		String testValue = "25,moss.stanford.edu,stuA,projA,50,stuB,projB,60";
		List<MossRecord> testList = new ArrayList<MossRecord>();
		testList.add(mr);
		List<String> testCsv = ResultsFilter.stringify(testList);
		assertEquals(comment,testValue,testCsv.get(1));
	}

	@Test
	public void test2() {
		fail("Not yet implemented"); // TODO
	}

}

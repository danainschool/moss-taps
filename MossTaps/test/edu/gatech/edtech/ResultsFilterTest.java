package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResultsFilterTest {

	@Before
	public void setUp() throws Exception {
		File input = new File("data"+File.separator+"846377696.htm");
		Document doc = Jsoup.parse(input, "UTF-8", "http://moss.stanford.edu/");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void test2() {
		fail("Not yet implemented"); // TODO
	}

}

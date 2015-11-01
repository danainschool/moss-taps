package edu.gatech.edtech;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParametersStoreTest {
	
	private ParametersStore ps;
	
	@Before
	public void setUp() {
		ps = new ParametersStore();
	}
	
	@After
	public void tearDown() {
		ps = null;
	}	

	@Test
	public void testDefaultsSet() {
		String comment = "Testing Properties Default set";
		String testValue = "100";
		ps.getApplicationProps().remove("linesCommonThreshold");  // if overwritten from config
		assertEquals(comment, testValue, ps.getApplicationProps().getProperty("linesCommonThreshold"));
	}
	
	@Test
	public void testUpdateUIDcorrect() {
		String comment = "Testing Properties update set correctly";
		String testValue = "123456789";
		ps.getApplicationProps().setProperty("userID", testValue);
		assertEquals(comment,testValue,ps.getApplicationProps().getProperty("userID", "error"));
	}
	
	@Test
	public void testUpdateUIDwrong() {
		String comment = "Testing Properties update set incorrectly";
		String testValue = "abcdefghi";
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testMissingCanonDirectory(){
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testMissingCurrentDirectory(){
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testMissingUpdateDirectory(){
		fail("Not yet implemented"); // TODO
	}
}

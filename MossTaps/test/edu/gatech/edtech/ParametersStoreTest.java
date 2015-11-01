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
	public void testUpdateUID() {
		String comment = "Testing Properties update set";
		String testValue = "260284329";
		ps.getApplicationProps().setProperty("userID", testValue);
		assertEquals(comment,testValue,ps.getApplicationProps().getProperty("userID", "error"));
	}

}

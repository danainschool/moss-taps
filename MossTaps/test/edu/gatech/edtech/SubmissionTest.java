package edu.gatech.edtech;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubmissionTest {
	String parent;
	String base;
	SoftwareLanguage lang;
	Properties mossProps;
	

	@Before
	public void setUp() throws Exception {
		lang = new SoftwareLanguage("Java","java","java");
		mossProps = new Properties();
		mossProps.setProperty("userID","260284329");
		mossProps.setProperty("optM","10");
		mossProps.setProperty("optD","1");
		mossProps.setProperty("optX","0");
		mossProps.setProperty("optN","250");
		mossProps.setProperty("optC","");
		mossProps.setProperty("server","moss.stanford.edu");
		mossProps.setProperty("port","7690");
		mossProps.setProperty("language",lang.getParameter());
		base = "data"+File.separatorChar+"Base";
		parent = "data"+File.separatorChar+"UploadTest";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSubmitSuccess() throws Exception {
		String comment = "Testing submit success";
		Submission s = new Submission(parent,base,lang,mossProps);
		boolean result = s.submit();
		assertTrue(comment,result);
	}
	@Test
	public void testPropsSet() throws Exception {
		String comment = "Testing properties set";
		Submission s = new Submission(parent,base,lang,mossProps);
		String testValue = "250";
		assertEquals(comment,testValue,mossProps.getProperty("optN"));
	}
//	@Test
//	public void test2() throws Exception {
//		String comment = "Testing ...";
//		Submission s = new Submission(parent,base,lang,mossProps);
//		fail("Not yet implemented"); // TODO TEST
//	}
//
}

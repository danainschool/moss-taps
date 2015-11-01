package edu.gatech.edtech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 *  The object takes advantage of the Java Properties class to keep track of default
 *  and user specified parameters for MossTaps
 *  Note that the default values will be used if not explicit in the config
 *  file, but will not be written back out to the config file.  The config file only needs to 
 *  keep those parameters that are different from the default; The initial defaults are taken
 *  from both the Moss perl file and David Joyner's Mossifier use case
 *  
 * 	help at http://docs.oracle.com/javase/tutorial/essential/environment/properties.html
 * 	help at http://www.mkyong.com/java/java-properties-file-examples/
 */
public class ParametersStore {
	// note that these structures taken from David Joyner's MOSSifier 1.1 script
	
	private static final Map<String,String> initialProps;
	static {
		Map<String,String> tempMap = new HashMap<String,String>();
		tempMap.put("userID","");
		tempMap.put("baseFolder", "data"+File.separatorChar+"Base");
		tempMap.put("currentFolder", "data"+File.separatorChar+"Current");
		tempMap.put("originalFolder", "data"+File.separatorChar+"Original");
		tempMap.put("uploadFolder", "data"+File.separatorChar+"Upload");
		tempMap.put("linesCommonThreshold", "100");
		tempMap.put("maxTilIgnore", "10");
		tempMap.put("collectionNeeded", "true");
		tempMap.put("java", "true");
		tempMap.put("python", "true");
		tempMap.put("c", "false");
		tempMap.put("cc", "false");
		tempMap.put("ml", "false");
		tempMap.put("pascal", "false");
		tempMap.put("ada", "false");
		tempMap.put("lisp", "false");
		tempMap.put("scheme", "false");
		tempMap.put("haskell", "false");
		tempMap.put("fortran", "false");
		tempMap.put("ascii", "false");
		tempMap.put("ascii", "false");
		tempMap.put("vhdl", "false");
		tempMap.put("perl", "false");
		tempMap.put("matlab", "false");
		tempMap.put("mips", "false");
		tempMap.put("prolog", "false");
		tempMap.put("spice", "false");
		tempMap.put("vb", "false");
		tempMap.put("csharp", "false");
		tempMap.put("modula2", "false");
		tempMap.put("javascript", "false");
		tempMap.put("plsql", "false");
		initialProps = Collections.unmodifiableMap(tempMap);
	}
	
	private static final List<SoftwareLanguage> mossLanguages;
	static {
		List<SoftwareLanguage> tempLang = new ArrayList<SoftwareLanguage>();
		tempLang.add(new SoftwareLanguage("Java",".java","java"));
		tempLang.add(new SoftwareLanguage("Python",".py","python"));
		tempLang.add(new SoftwareLanguage("C",".c","c"));
		tempLang.add(new SoftwareLanguage("CPP",".cpp","cc"));
		tempLang.add(new SoftwareLanguage("MatLab",".m","matlab"));
		tempLang.add(new SoftwareLanguage("VisualBasic",".vb","vb"));
		tempLang.add(new SoftwareLanguage("CSharp",".cs","csharp"));
		tempLang.add(new SoftwareLanguage("JavaScript",".js","javascript"));
		//TODO add the others
		mossLanguages = Collections.unmodifiableList(tempLang);
	}
	private Properties defaultProps = new Properties();
	private Properties applicationProps;
	private List<SoftwareLanguage> languagesTested = new ArrayList<SoftwareLanguage>();
	private boolean validLanguages = false;
	private boolean validUserID = false;
	private boolean validSettings = false;	

	public ParametersStore() {
		// create and load default properties 
		for (String key:initialProps.keySet()){
			defaultProps.setProperty(key,initialProps.get(key));
		}
		
		// create application properties with default
		applicationProps = new Properties(defaultProps);
		
		// now load properties from user file
     	updateProperties();
    	
    	// check userid format, languages non-empty
     	loadLanguagesTested();
    	validateSettings();
    	
    	// Intro to user and option for quick changes
    	System.out.println("The current essential settings for your Moss submissions are as follows:\n"
    			+ "Original directory (past projects/students/../files): "+applicationProps.getProperty("originalFolder")+"\n"
    			+ "Current directory (current projects/students/../files): "+applicationProps.getProperty("currentFolder")+"\n"
    	    	+ "Base directory (contains starter code to be ignored; may be empty: "+applicationProps.getProperty("baseFolder")+"\n"
    	    	+ "Computer languages: "+ listLanguagesTested());
    	
    	saveIni(applicationProps);
	}

	private String listLanguagesTested() {
		StringBuilder sb = new StringBuilder();
		for (SoftwareLanguage sl:languagesTested) {
			sb.append(sl.getLanguageName());
			sb.append(",");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

	private void loadLanguagesTested() {
		for (SoftwareLanguage sl:mossLanguages) {
			if (applicationProps.getProperty(sl.getParameter()).equals("true")){
				this.languagesTested.add(sl);
			}
		}
	}

	private void updateProperties() {
		InputStream input = null;
    	try {
    		String filename = "config.txt";
    		input = new FileInputStream(filename);
    		applicationProps.load(input); 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }
	}

	private void validateSettings() {
    	// get the userid if not already loaded
    	String userID=applicationProps.getProperty("userID");
    	validUserID = true;
    	if (userID.isEmpty()){
     		userID = userInput("Enter your Moss UserID: ");
    		if (!validate(userID)){
   				System.out.println("Must have a 9-digit userid for Moss");
   				validUserID=false;
     		}
    		else {
    	    	applicationProps.setProperty("userID", userID);
    		}
    	}
    	// make sure languages non-empty
    	if (!languagesTested.isEmpty()) validLanguages = true;
    	if (validLanguages && validUserID) validSettings = true;
	}

	private String userInput(String userQuery) {
		System.out.println(userQuery);
   		Scanner scanIn = new Scanner(System.in);
   		String answer = scanIn.nextLine();
   		scanIn.close();
		return answer;
	}


	private boolean validate(String userID) {
		// should be an integer of length 9
		if (userID.length()!=9) return false;
		for (int i=0;i<userID.length();i++) {
			if (!Character.isDigit(userID.charAt(i)))
				return false;
		}
		return true;
	}


	/**
	 * @param props
	 * writes out properties to the config file 
	 * does NOT write out defaults, only changes added directly to the 
	 * applicationProps file
	 */
	private void saveIni(Properties props) {
		// save the old config file
		
		OutputStream output = null;
		try {
			output = new FileOutputStream("config.txt");
			props.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	public Properties getApplicationProps() {
		return applicationProps;
	}


	public boolean isValidSettings() {
		return validSettings;
	}

}

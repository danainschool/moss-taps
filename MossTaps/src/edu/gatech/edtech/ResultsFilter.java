package edu.gatech.edtech;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsFilter {
	private static final String CSV_HEADER = 
			"Lines Matched,Pct1,Pct2,Moss Link,"
					+"Project1,Student1,"
					+"Project2,Student2";
	private static final String COMMA = ",";
	private static final String QUOTE = "\"";

	public static void toCSV(List<MossReply> mossResults, SoftwareLanguage language) throws IOException {
		List<MossRecord> mDB = new ArrayList<MossRecord>();
		String outFileName = "MT_"+ MyUtils.getDateString()+ "_" + language.getLanguageName() + ".csv";
		for (MossReply reply : mossResults) {
			try {
				mDB.addAll(reply.extractMossLinks());
			} catch (Exception e) {
				System.out.println("Unable to extract data from "+reply.getMossURL().toString());
				e.printStackTrace();
				// for now create a dog and pony file for demo
			}
		}
		mDB = removeDuplicates(mDB);
		Collections.sort(mDB);  //if sorting by the lines num compareTo
		List<String> records = stringify(mDB);
		saveCsvResults(outFileName,records);
	}

	public static List<String> stringify(List<MossRecord> mDB) {
		List<String> records = new ArrayList<String>();
		records.add(CSV_HEADER);
		for (MossRecord mr:mDB){
			// put strings in quotes
			String str = mr.getLinesMatched() + COMMA 
					+ mr.getPercentA() + COMMA
					+ mr.getPercentB() + COMMA
					+ mr.getMossCompareLink() + COMMA
					+ QUOTE + mr.getProjectA() + QUOTE + COMMA 
					+ QUOTE + mr.getStudentA() + QUOTE + COMMA 
					+ QUOTE + mr.getProjectB() + QUOTE + COMMA 
					+ QUOTE + mr.getStudentB()+ QUOTE; 
			records.add(str);
		}
		return records;
	}

	private static List<MossRecord> removeDuplicates(List<MossRecord> mDB) {
		//TODO Implement removeDuplicates
		return mDB;
	}

	public static void saveCsvResults(String filename, List<String> recordList) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		for (String record : recordList) {
			pw.println(record);
		}
		pw.close();
		System.out.println("Results created in "+filename);
	}
}

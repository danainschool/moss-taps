package edu.gatech.edtech;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultsFilter {
	private static final String CSV_HEADER = 
			"Lines Matched,"
			+"Student1,Project1,Pct1,"
			+"Student2,Project2,Pct2";
	private static final String COMMA = ",";

	public static void toCSV(List<MossReply> mossResults, SoftwareLanguage language) throws IOException {
		List<MossRecord> mDB = new ArrayList<MossRecord>();
		String outFileName = "MT_"+ MyUtils.getDateString()+ "_" + language.getLanguageName();
		for (MossReply reply : mossResults) {
			try {
				mDB.addAll(extractRelevant(reply));
			} catch (Exception e) {
				System.out.println("Unable to extract data from "+reply.getMossURL().toString());
				e.printStackTrace();
			}
		}
		mDB = removeDuplicates(mDB);
		Collections.sort(mDB);  //if soring by the lines num compareTo
		List<String> records = stringify(mDB);
		saveCsvResults(outFileName,records);
	}

	private static List<MossRecord> extractRelevant(MossReply reply) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<String> stringify(List<MossRecord> mDB) {
		List<String> records = new ArrayList<String>();
		records.add(CSV_HEADER);
		for (MossRecord mr:mDB){
			String str = mr.getLinesMatched() + COMMA
					+mr.getStudentA() + COMMA + mr.getProjectA() + COMMA + mr.getPercentA() + COMMA
					+mr.getStudentB() + COMMA + mr.getProjectB() + COMMA + mr.getPercentB();
			records.add(str);
		}
		return records;
	}

	private static List<MossRecord> removeDuplicates(List<MossRecord> mDB) {
		//TODO Implement
		return mDB;
	}

	private static void saveCsvResults(String filename, List<String> recordList) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		for (String record : recordList) {
			pw.println(record);
		}
		pw.close();
	}
}

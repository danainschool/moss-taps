package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class MossTaps {

	private static SeriesCollection canon;
	private static SeriesCollection current;
	private static ParametersStore pStore = new ParametersStore();

	public static void main(String[] args) {
		// test to see if parameters file exists
		// update with args
		ParametersStore pStore = new ParametersStore();
		if (pStore.isValidSettings()) {
			System.out.println("settings valid - ready to go!");
		}
		// set up the canon
		canon = new SeriesCollection(pStore.getCanonFolder(),pStore.getUploadFolder());		
		// set up the current projects
		current = new SeriesCollection(pStore.getCurrentFolder(),pStore.getUploadFolder());
		//create the moji scripts
		SubmissionScripts ss = new SubmissionScripts(pStore, canon, current);
		//submit the script and get the results urls
		MossResults results = ss.submit();
		//filter the results per the parameters
		results.filter(pStore,canon, current);
		//output the results to a CSV file
		results.toCSV();
	}

}

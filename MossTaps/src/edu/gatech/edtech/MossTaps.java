package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class MossTaps {

	private static ParametersStore pStore = new ParametersStore();
	private static Canon canon;
	private static ProjectCollection current;

	public static void main(String[] args) {
		// test to see if parameters file exists
		// update with args
		pStore.loadParameters(args);
		// set up the canon
		canon = new Canon(pStore.getCanonFolder(),pStore.getUploadFolder());		
		//set up the current projects
		current = new ProjectCollection(pStore.getCurrentFolder(),pStore.getUploadFolder());
		//create the moji script
		SubmissionScripts ss = new SubmissionScripts(pStore, canon, current);
		//submit the script and get the results url
		MossResults results = ss.submit();
		//filter the results per the parameters
		results.filter(pStore,canon, current);
		//output the results to a CSV file
		results.toCSV();
	}

}

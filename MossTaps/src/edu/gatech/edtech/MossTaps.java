package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class MossTaps {

	private static ParametersStore pStore = new ParametersStore();
	private static multiProjectCollection past;
	private static multiProjectCollection current;

	public static void main(String[] args) {
		// test to see if parameters file exists
		// update with args
		pStore.loadParameters(args);
		// set up the canon of all past projects and past student subfolders
		past = new multiProjectCollection(pStore.getCanonFolder(),pStore.getUploadFolder());		
		//set up the current semester projects and student subfolders
		current = new multiProjectCollection(pStore.getCurrentFolder(),pStore.getUploadFolder());
		//create the moji script
		SubmissionScript ss = new SubmissionScript(pStore,past, current);
		//submit the script and get the results url
		MossResults results = ss.submit();
		//filter the results per the parameters
		results.filter(pStore,past, current);
		//output the results to a CSV file
		results.toCSV();
	}

}

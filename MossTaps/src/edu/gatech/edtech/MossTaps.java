package edu.gatech.edtech;

public class MossTaps {

	private static final String ORIGINAL_PREFIX = "O_";
	private static final String CURRENT_PREFIX = "C_";
	private static SeriesCollection canon;
	private static SeriesCollection current;
	private static ParametersStore pStore;

	public static void main(String[] args) {
		// test to see if parameters file exists
		// update with args
		pStore = new ParametersStore();
		if (pStore.isValidSettings()) {
			System.out.println("settings valid - ready to go!");
		}
		
		// set up the originals and current directories for submission
		canon = new SeriesCollection(pStore.getLanguagesTested(),ORIGINAL_PREFIX, pStore.getOriginalFolder(),pStore.getUploadFolder());
		canon.inflateZips();
		canon.cleanFileNames();
		
		current = new SeriesCollection(pStore.getLanguagesTested(),CURRENT_PREFIX,pStore.getCurrentFolder(),pStore.getUploadFolder());
//		current.inflateZips();
//		current.cleanFileNames();
//		current.consolidateStudents();
		
		//create the moji scripts
//		SubmissionScripts ss = new SubmissionScripts(pStore, canon, current);
//		//submit the script and get the results urls
//		MossResults results = ss.submit();
//		//filter the results per the parameters
//		results.filter(pStore,canon, current);
//		//output the results to a CSV file
//		results.toCSV();
	}

}

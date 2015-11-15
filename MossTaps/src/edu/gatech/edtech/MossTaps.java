package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class MossTaps {

	private static final String ORIGINAL_PREFIX = "O_";
	private static final String CURRENT_PREFIX = "C_";
	private static SeriesCollection canon;
	private static SeriesCollection current;
	private static ParametersStore pStore;

	public static void main(String[] args) throws Exception {
		// test to see if parameters file exists
		// update with args
		pStore = new ParametersStore();
		if (pStore.isValidSettings()) {
			System.out.println("settings valid - ready to go!");
		}
		
		// set up the originals and current directories for submission
		canon = new SeriesCollection(pStore.getLanguagesTested(),ORIGINAL_PREFIX, pStore.getOriginalFolder(),pStore.getUploadFolder());
		canon.inflateZips();
		canon.moveToUpload();
		
		current = new SeriesCollection(pStore.getLanguagesTested(),CURRENT_PREFIX,pStore.getCurrentFolder(),pStore.getUploadFolder());
		current.inflateZips();
		current.moveToUpload();
		
		// submit the queries for each language and collect the URLs from Moss
		for (SoftwareLanguage language:pStore.getLanguagesTested()){
			List<MossReply> mossResults = new ArrayList<MossReply>();
			Submission mossSub = new Submission(pStore.getUploadFolder(),pStore.getBaseFolder(),
					language,pStore.getMossProperties(language));
			mossSub.submit();
			if(mossSub.isSuccessful()){
				// success
				mossResults.addAll(mossSub.getReplies());
			}
			// convert the results to csv file form MT2015_1115_Python.csv
			ResultsFilter.toCSV(mossResults,language);		
		}
	}
}

package edu.gatech.edtech;

import java.util.ArrayList;
import java.util.List;

public class SubmissionScripts {
	
	List<Submission> submissions = new ArrayList<Submission>();
	ParametersStore pStore;
	SeriesCollection canon;
	SeriesCollection current;

	public SubmissionScripts(ParametersStore pStore, SeriesCollection canon,
			SeriesCollection current) {
		this.pStore = pStore;
		this.canon = canon;
		this.current = current;
		
		
		
		// for each
//		for (SoftwareLanguage lang:pStore.getLanguagesTested()){
//			for (St)
//			submissions.add(new Submission());
//		}
		
		// TODO Auto-generated constructor stub
	}

	public MossResults submit() {
		// TODO Auto-generated method stub
		return null;
	}

}

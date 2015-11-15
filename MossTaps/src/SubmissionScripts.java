

import java.util.ArrayList;
import java.util.List;

import edu.gatech.edtech.MossResults;
import edu.gatech.edtech.ParametersStore;
import edu.gatech.edtech.SeriesCollection;
import edu.gatech.edtech.Submission;

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

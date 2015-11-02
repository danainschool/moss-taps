package ravensproject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ravensproject.Agent.debugPrintType;

public class AgentDiagramComparison {

	//THIS STRING IS USED TO MAP OBJECTS IN ON FIGURE TO OBJECTS WHICH DON'T EXIST IN ANOTHER
	//FIGURE.  THAT MEANS THE OBJECT MAPPED TO THIS WAS CREATED OR DELETED
	static String dummyRavensObjectString = "NO OBJECT TO MAP";
	
	String comparisonName = "";
	String problemName = "";
	
	RavensFigure figure1 = null;
	RavensFigure figure2 = null;
	ArrayList<AgentShapeMapping> allPossibleMappings = new ArrayList<AgentShapeMapping>();
	
	//THESE HASHMAPS ARE THE SAME AS THOSE IN THE RavensFigure OBJECTS OTHER THAN
	//THEY ARE TWEAKED TO BE THE SAME SIZE AS EACH OTHER.  IF ONE OF THE HASHMAPS
	//WAS LARGER THAN THE OTHER, THE SMALLER WAS ADDED TO WITH DUMMY VALUES TO MAKE 
	//THEM THE SAME SIZE
	HashMap<String, RavensObject> figure1RevisedObjectList = new HashMap<String, RavensObject>();
	HashMap<String, RavensObject> figure2RevisedObjectList = new HashMap<String, RavensObject>();

	AgentMappingScore mapScore = null;

	debugPrintType debugPrinting = debugPrintType.NONE;
	
	public AgentDiagramComparison(String problem, String name, RavensFigure fig1, RavensFigure fig2, debugPrintType debugPrinting) {
		figure1 = fig1;
		figure2 = fig2;
		
		comparisonName = name;
		problemName = problem;
		this.debugPrinting = debugPrinting;
		
		int dummyCount = 0;
		
		//COPY THE OBJECT LISTS INTO THE INTERNAL HASHMAPS FOR THIS CLASS
		//SO WE CAN ADD DUMMY VALUES TO MAKE THEM THE SAME SIZE
		for(int i = 0; i < getObjectCount(1); ++i) {
			Map.Entry<String, RavensObject> temp = getFigureObjectByIndex(figure1, i);
			figure1RevisedObjectList.put(temp.getKey(), temp.getValue());
		}
		for(int i = 0; i < getObjectCount(2); ++i) {
			Map.Entry<String, RavensObject> temp = getFigureObjectByIndex(figure2, i);
			figure2RevisedObjectList.put(temp.getKey(), temp.getValue());
		}		
		
		//NOW MAKE THE TWO INTERNAL OBJECT ARRAYS BE THE SAME SIZE
		for(int i = figure1RevisedObjectList.size(); i < figure2RevisedObjectList.size(); ++i) {
			figure1RevisedObjectList.put(dummyRavensObjectString + "_" + i, new RavensObject(dummyRavensObjectString + "_" + i));
			dummyCount++;
		}
		for(int i = figure2RevisedObjectList.size(); i < figure1RevisedObjectList.size(); ++i) {
			figure2RevisedObjectList.put(dummyRavensObjectString + "_" + i, new RavensObject(dummyRavensObjectString + "_" + i));
			dummyCount++;
		}
			
		
		//NOW WE GENERATE ALL POSSIBLE MAPPINGS.  THE LEFT SIDE OF THE MAPPING WILL BE THE FIGURE1 LIST
		//OF OBJECTS IN ORDER.  THE RIGHT SIDE WILL BE ALL POSSIBLE PERMUTATIONS OF THE FIGURE2 LIST OF 
		//OBJECTS TO GET ALL POSSIBLE COMBINATIONS
		int initialIndex = 0;
		getAllPossibleMappings(initialIndex, null);

		
		
		//NOW WE NEED TO DETECT DUPLICATES BASED ON ANY VALUES WHICH USE THE dummyRavensObjectString
		//FOR EXAMPLE, IF FIGURE 1 HAS 2 OBJECTS AND FIGURE 2 HAS 4, FIGURE 1'S LIST WILL NOW CONTAIN
		//TWO DUMMY ENTRIES.  WE DON'T NEED TWO PERMUTATIONS LIKE THIS:
		/*
		 * 	FIG1	FIG2 (Map1)		FIG2 (Map2)
		 * 	a		1				1
		 * 	b		2				2
		 * 	dummy	3				4
		 * 	dummy	4				3
		 * 
		 * BECAUSE THIS MAY SCREW UP THE LEARNING OF THE MACHINE.  ANYWHERE WE HAVE TWO DIFFERENT MAPPINS
		 * AND THE ONLY DIFFERNCE BETWEEN THEM IS THAT THEY HAVE DIFFERENT MAPS TO DUMMY OBJECTS
		 * WE SHOULD NUKE THEM UNTIL WE HAVE ONLY ONE REMAINING */
		 //NOTE: THE ABOVE SCENARIO ONLY HAPPENS IF THERE ARE MORE THAN ONE DUMMY OBJECT
		if(dummyCount > 1)
			removeExtraDummyMappings();

		
		//NOW WE NEED TO IDENTIFY WHICH TRANSFORMATIONS TOOK PLACE FOR EACH MAPPING PERMUTATION
		for(int i = 0; i < allPossibleMappings.size(); ++i) {
			allPossibleMappings.get(i).identifyTransformationsForMap();
		}
		
		//USED FOR DEBUGGING ONLY - PRING THE MAPPINGS
    	if(debugPrinting == debugPrintType.ALL) {

    		System.out.println("-----COMPARISON " + comparisonName + " (" + problemName + ")----");
    		
			for(int i = 0; i < allPossibleMappings.size(); ++i) {
				allPossibleMappings.get(i).printMapping(Integer.toString(i));
			}
    	}

	}
	
	//NOW WE NEED TO DETECT DUPLICATES BASED ON ANY VALUES WHICH USE THE dummyRavensObjectString
	//FOR EXAMPLE, IF FIGURE 1 HAS 2 OBJECTS AND FIGURE 2 HAS 4, FIGURE 1'S LIST WILL NOW CONTAIN
	//TWO DUMMY ENTRIES.  WE DON'T NEED TWO PERMUTATIONS LIKE THIS:
	/*
	 * 	FIG1	FIG2 (Map1)		FIG2 (Map2)
	 * 	a		1				1
	 * 	b		2				2
	 * 	dummy	3				4
	 * 	dummy	4				3
	 * 
	 * BECAUSE THIS MAY SCREW UP THE LEARNING OF THE MACHINE.  ANYWHERE WE HAVE TWO DIFFERENT MAPPINS
	 * AND THE ONLY DIFFERNCE BETWEEN THEM IS THAT THEY HAVE DIFFERENT MAPS TO DUMMY OBJECTS
	 * WE SHOULD NUKE THEM UNTIL WE HAVE ONLY ONE REMAINING */	
	private void removeExtraDummyMappings() {
		for(int i =0; i < allPossibleMappings.size(); ++i) {
			for(int j = i + 1; j < allPossibleMappings.size(); ++j) {

				if(allPossibleMappings.get(i).hasSameMappingsIgnoringDummys(allPossibleMappings.get(j))) {
					allPossibleMappings.remove(j);
					j--;
				}
			}
		}			
	}
	
	public void getAllPossibleMappings(int index, AgentShapeMapping theMapping) {
		if(theMapping == null) {
			theMapping = new AgentShapeMapping(problemName, comparisonName);
			
			//FILL THE MAP WITH FIGURE1 OBJECTS IN ORDER
			for(int i = 0; i < figure1RevisedObjectList.size(); ++i) {
				//********************
				//THIS WAS KIND OF WORKING BUT FAILED ON DUMMY OBJECTS (getFigureObjectByIndex pulled
				//from the original figure array and would return null on dummy objects)
				//********************
				//Map.Entry<String, RavensObject> temp =getFigureObjectByIndex(figure1, i);
				Map.Entry<String, RavensObject> temp =getObjectByIndex(figure1RevisedObjectList, i);
				
				theMapping.figure1Objects.add(temp);
			}
		}

		//NOW FILL THE MAP WITH ALL POSSIBLE COMBINATIONS OF FIGURE2 STUFF
		for(int i = 0; i < figure2RevisedObjectList.size(); ++i) {
			
			Map.Entry<String, RavensObject> temp = getObjectByIndex(figure2RevisedObjectList, i);
			
			//HAVE WE ALREADY ADDED THIS OBJECT TO THE ARRAY? IF SO, DON'T ADD AGAIN, JUST 
			//USE A continue TO GO TO THE NEXT OBJECT
			if(theMapping.keyAlreadyUsed(theMapping.figure2Objects, temp.getKey())) {
				continue;
			}
			
			//IF SOMETHING IS ALREADY IN THIS SPOT, NUKE IT. THEN ADD INTO THAT INDEX
			if(theMapping.figure2Objects.size() > index) {
				theMapping.figure2Objects.remove(index);
			}
			theMapping.figure2Objects.add(index, temp);
			
			
			if(index == figure2RevisedObjectList.size() - 1) {
				//DONE! FOUND A MAP
				allPossibleMappings.add(theMapping);
			}
			else {
				//CALL SELF WITH AN INCREMENTED INDEX
				getAllPossibleMappings(index + 1, new AgentShapeMapping(problemName, comparisonName, theMapping));
			}
		}
	}
	
	
	public String getFigureName(int figure) {
		if(figure == 1)
			return figure1.getName();
		if(figure == 2)
			return figure2.getName();
		return "Invalid Figure Number";
	}
	
	public int getObjectCount(int figure) {
		if(figure == 1)
			return figure1.getObjects().size();
		if(figure == 2)
			return figure2.getObjects().size();
		return -1;
	}
	
	public Map.Entry<String, RavensObject> getObjectByIndex(HashMap<String, RavensObject> objects, int objectIndex) {
		
		int index = 0;
		for(HashMap.Entry<String, RavensObject> entry : objects.entrySet()){
			if(index == objectIndex) {
				return entry;
			}
			
			index++;
		}

		return null;
	}
	
	
	
	public Map.Entry<String, RavensObject> getFigureObjectByIndex(RavensFigure figure, int objectIndex) {
		
		HashMap<String, RavensObject> objects = figure.getObjects();
		
		int index = 0;
		for(HashMap.Entry<String, RavensObject> entry : objects.entrySet()){
			if(index == objectIndex) {
				return entry;
			}
			
			index++;
		}

		return null;
	}
	
	public Map.Entry<String, String> getObjectAttributeByIndex(RavensObject object, int attributeIndex) {
		
		int index = 0;
		for(HashMap.Entry<String, String> attribEntry: object.getAttributes().entrySet()) {
			
			if(index == attributeIndex) {
				return attribEntry;
			}
		}
		
		return null;
		
	}

	public AgentMappingScore calculateScores(AgentDiagramComparison compareTo, ArrayList<AgentSpecialHandling> specials) {

		AgentMappingScore bestScore = null;
		int bestIndex = -1;
		
		//CALCULATE SCORES OF EACH POSSIBLE MAPPING
		for(int i = 0; i < allPossibleMappings.size(); ++i) {
			AgentMappingScore theScore = allPossibleMappings.get(i).calculateScore(compareTo, specials);
			
			if(bestScore == null || theScore.whichScoreIsBetter(bestScore) == theScore) {
				bestScore = theScore;
				bestIndex = i;
			}
		}

		//USED FOR DEBUGGING ONLY - PRING THE MAPPINGS
    	if(debugPrinting == debugPrintType.ALL) {

    		System.out.println("****** BEST SCORE FOR COMPARISON " + comparisonName + " (" + problemName + ") **********");
    		System.out.println("delta Cost: " + bestScore.transformationDeltaCost);
    		System.out.println("total Cost: " + bestScore.transformationTotalCost);
			allPossibleMappings.get(bestIndex).printMapping(Integer.toString(bestIndex));
    	}		
		
		mapScore = bestScore;
		return bestScore;
	}


	
	
}

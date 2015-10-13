package ravensproject;

import java.util.ArrayList;

import javax.xml.crypto.dsig.TransformException;

import ravensproject.AgentShapeMapping.mappingTransformations;

public class AgentMappingScore {

	int transformationScore = -1;
	//WHAT'S THE COST OF THE DIFFERENCE BETWEEN THESE TRANSFORMATIONS AND THE COMPARED ONE?
	int transformationDeltaCost = -1;
	//WHAT'S THE TOTAL COST OF THE TRANSFORMATIONS FOR THIS MAP?
	int transformationTotalCost = -1;
	ArrayList<AgentTransformation> transformationDelta = null;
	ArrayList<AgentTransformation> totalTransformation = null;
	int correspondingMapIndex = -1;
	int agentIndex = -1;

	static int transformShapeChangeCost = 2; //DELETE AND CREATE - COUNT AS TWO
	static int transformSizeChangeCost = 2; //DELETE AND CREATE - COUNT AS TWO
	static int transformAboveChangeCost = 1;
	static int transformOverlapChangeCost = 1;
	static int transformAngleChangeCost = 1; //MAYBE 2???
	static int transformExpectedAngleChangeCost = 0; //SPECIAL CASES IN ROTATION/REFLECTION WHERE A SPECIFIC ANGLE CHANGE IS EXPECTED
	static int transformFillChangeCost = 1;
	static int transformInsideChangeCost = 1;
	static int transformAlignmentChangeCost = 2; //DELETE AND CREATE - COUNT AS TWO 
	static int transformCreatedCost = 1;
	static int transformDeletedCost = 1;	
	static int transformUndefinedChangeCost = 10;
	
	public AgentMappingScore(int correspondingMapIndex, ArrayList<AgentTransformation> transformationDelta, ArrayList<ArrayList<AgentTransformation>> transformationsForEachObject) {
		this.transformationDelta = transformationDelta;
		this.totalTransformation = getTotalTransformation(transformationsForEachObject);
		this.correspondingMapIndex = correspondingMapIndex;
		
		if(transformationDelta != null) {

			//HERE IS WHERE I WOULD ADD ANY KIND OF WEIGHTING OF TRANSFORMATIONS, LEARNING FROM CASES, ETC
			calculateScore();
			
		}
		
	}
	
	//ACCEPTS AN ARRAY OF TRANSFORMATION ARRAYS FOR EACH OBJECT IN A MAP BETWEEN FIGURES. 
	//RETURNS THE CUMULATIVE LIST OF ALL TRANSFORMATIONS USED IN THE MAP
	private ArrayList<AgentTransformation> getTotalTransformation(ArrayList<ArrayList<AgentTransformation>> list) {
		ArrayList<AgentTransformation> retval = new ArrayList<AgentTransformation>();
		
		for(int i = 0; i < list.size(); ++i) {
			for(int j = 0; j < list.get(i).size(); ++j) {
//				if(list.get(i).get(j) != mappingTransformations.NO_CHANGE)
					retval.add(new AgentTransformation(list.get(i).get(j).theTransformation, list.get(i).get(j).theValue));
			}
		}
		
//		if(retval.size() == 0)
//			retval.add(mappingTransformations.NO_CHANGE);
		
		return retval;
	}
		
	private void calculateScore() {
		
		transformationDeltaCost = getTransformationListWeight(transformationDelta);
		transformationScore = transformationDeltaCost * 100;
		

		transformationTotalCost = getTransformationListWeight(totalTransformation);
	
	}
	
	public static int getTransformationListWeight(ArrayList<AgentTransformation> theList) {
		int retval = 0;
		
		for(int i = 0; i < theList.size(); ++i) {
			retval += getTransformationWeight(theList.get(i));
		}
		
		return retval;
	}
	
	public static int getTransformationWeight(AgentTransformation transformation) {
		if(transformation.theTransformation == mappingTransformations.SHAPE_CHANGE)
			return transformShapeChangeCost;
		if(transformation.theTransformation == mappingTransformations.ABOVE_CHANGE)
			return transformAboveChangeCost;
		if(transformation.theTransformation == mappingTransformations.OVERLAP_CHANGE)
			return transformOverlapChangeCost;
		if(transformation.theTransformation == mappingTransformations.ANGLE_CHANGE)
			return transformAngleChangeCost;
		if(transformation.theTransformation == mappingTransformations.FILL_CHANGE)
			return transformFillChangeCost;
		if(transformation.theTransformation == mappingTransformations.INSIDE_CHANGE)
			return transformInsideChangeCost;
		if(transformation.theTransformation == mappingTransformations.ALIGNMENT_CHANGE)
			return transformAlignmentChangeCost;
		if(transformation.theTransformation == mappingTransformations.CREATED)
			return transformCreatedCost;
		if(transformation.theTransformation == mappingTransformations.DELETED)
			return transformDeletedCost;
		if(transformation.theTransformation == mappingTransformations.EXPECTEDANGLE_CHANGE)
			return transformExpectedAngleChangeCost;
		
		return transformUndefinedChangeCost;
	}
	
	public AgentMappingScore whichScoreIsBetter(AgentMappingScore compare) {
		if(whichScoreIsBetter("this", transformationDeltaCost, transformationTotalCost, "compare", compare.transformationDeltaCost, compare.transformationTotalCost) == "this")
			return this;
		
		return compare;
	}
	
	public static String whichScoreIsBetter(String A, int ADelta, int ATotal, String B, int BDelta, int BTotal) {
		//*********************************************************
		// THIS AND IN AgentShapeMapping.whichTransformListCostsLess ARE
		// THE PLACES TO PUT LEARNING LOGIC
		//*********************************************************
		
		if(ADelta == BDelta) {
			if(ATotal < BTotal)
				return A;
			return B;
		}
		else if(ADelta < BDelta)
			return A;
		
		return B;
	}
}

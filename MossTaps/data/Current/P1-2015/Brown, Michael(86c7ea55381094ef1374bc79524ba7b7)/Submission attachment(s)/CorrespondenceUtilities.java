package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

public final class CorrespondenceUtilities 
{
	private CorrespondenceUtilities(){}
	
	public static ArrayList<ObjectCorrespondence> GenerateObjectCorrespondence(RavensFigure from, 
			RavensFigure to, String spatialRelationship, HashMap<String, Integer> objectNesting)
	{
		ArrayList<ObjectCorrespondence> objectsCorrespondence = new ArrayList<ObjectCorrespondence>();
		
		int fromObjectsSize = from.getObjects().size();
		int toObjectsSize = to.getObjects().size();
		
		// Simple correspondence (0:0, 0:1, 1:0, 1:1) 
		if(fromObjectsSize < 2 && toObjectsSize < 2)
		{
			ObjectCorrespondence corr = new ObjectCorrespondence();
			
			if(fromObjectsSize == 0)
				corr.referenceObject = new RavensObject("NULL");
			else if (fromObjectsSize == 1)
				corr.referenceObject = from.getObjects().values().iterator().next();			
				
			if(toObjectsSize == 0)
				corr.correspondingObject = new RavensObject("NULL");
			
			else if (toObjectsSize == 1)
				corr.correspondingObject = to.getObjects().values().iterator().next(); 
					
			objectsCorrespondence.add(corr);
			return objectsCorrespondence;
		}
		
		// Complex Correspondence with addition / deletion (N:M, N!=M, N>1, M>1)
		else
		{
			// Working Lists of objects that can be added to and removed from
			ArrayList<RavensObject> fromObjects = new ArrayList<RavensObject>(from.getObjects().values());
			ArrayList<RavensObject> toObjects = new ArrayList<RavensObject>(to.getObjects().values());
			
			// Generate an ArrayList of all possible Object correspondences, then sort it by similarity value.
			ArrayList<ObjectCorrespondence> possibleMappings = GeneratePossibleMappings(fromObjects, toObjects, objectNesting);
			Collections.sort(possibleMappings, Collections.reverseOrder());
						
			// Iterate through the possibleMappings
			for(ObjectCorrespondence OC : possibleMappings)
			{
				// Break out of loop when either object pool gets to zero
				if(fromObjects.size() < 1 || toObjects.size() < 1)
					break;
				
				if(fromObjects.contains(OC.referenceObject) && toObjects.contains(OC.correspondingObject))
				{
					objectsCorrespondence.add(OC);
					fromObjects.remove(OC.referenceObject);
					toObjects.remove(OC.correspondingObject);					
				}			
			}						
			
			// When a pool empties, iterate through the leftovers to make creation / deletion correspondences
			for(RavensObject RO : fromObjects)
			{
				ObjectCorrespondence corr = new ObjectCorrespondence();
				corr.referenceObject = RO;
				corr.correspondingObject = new RavensObject("NULL");
				objectsCorrespondence.add(corr);
			}
			for(RavensObject RO : toObjects)
			{
				ObjectCorrespondence corr = new ObjectCorrespondence();
				corr.referenceObject = new RavensObject("NULL");
				corr.correspondingObject = RO;
				objectsCorrespondence.add(corr);
			}
			
			return objectsCorrespondence;			
		}		
	}
	
	public static int GetObjectSimilarity(RavensObject from, RavensObject to, HashMap<String, Integer> objectNesting)
	{
		// Object similarity is based on Nesting level, Shape, Size, Fill.  
		// Values and types of similarity are subject to change.
		
		int score = 0;
		
		if(objectNesting.get(from.getName()) == objectNesting.get(to.getName()))
			score += 5;
		if(from.getAttributes().get("shape").equals( to.getAttributes().get("shape")))
			score += 3;
		if(from.getAttributes().get("size").equals(to.getAttributes().get("size")))
			score += 2;
		if(from.getAttributes().get("fill").equals(to.getAttributes().get("fill")))
			score += 1;
		
		return score;
	}
	
	public static HashMap<String, Integer> DetermineFigureNesting(RavensProblem RP)
	{
		HashMap<String, Integer> nesting = new HashMap<>();
		
		for(RavensFigure RF : RP.getFigures().values())
		{
			for(RavensObject RO : RF.getObjects().values())
			{
				String containers = RO.getAttributes().get("inside");
				if( containers != null)
				{
					StringTokenizer st = new StringTokenizer(containers, ",");					
					nesting.put(RO.getName(), st.countTokens());
				}
				else
					nesting.put(RO.getName(), 0);				
			}
		}
		return nesting;
	}
	
	// Creates an ArrayList of all possible mappings (ObjectCorrespondence) between two sets of objects
	private static ArrayList<ObjectCorrespondence> GeneratePossibleMappings(ArrayList<RavensObject> fromObjects, ArrayList<RavensObject> toObjects, HashMap<String, Integer> objectNesting)
	{
		ArrayList<ObjectCorrespondence> possibleMappings = new ArrayList<ObjectCorrespondence>();
		
		for(RavensObject fromRO : fromObjects)
			for(RavensObject toRO : toObjects)
				possibleMappings.add(new ObjectCorrespondence(fromRO, toRO, 
					GetObjectSimilarity(fromRO, toRO, objectNesting)));		
	
		return possibleMappings;
	}	
}

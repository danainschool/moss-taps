package ravensproject.rpm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.RavensSolution;
import ravensproject.RavensObject;
import ravensproject.sngt.TransformationUtilities;

// Utility class for pattern matching helper functions
public final class PatternMatchingUtilities 
{
	private static ArrayList<String> referenceFigures = getReferenceFigures();
		
	private PatternMatchingUtilities(){}

	// Utility method for instantiating list of reference figures
	private static ArrayList<String> getReferenceFigures() 
	{
		ArrayList <String> refFigs = new ArrayList<String>(); 
		
		refFigs.add("A"); refFigs.add("B"); refFigs.add("C");
		refFigs.add("D"); refFigs.add("E"); refFigs.add("F");
		refFigs.add("G"); refFigs.add("H");
		
		return refFigs;
	}

	// Accepts a Ravens problem and a list of solutions that match the pattern as input
	// Returns a modified list of solutions adjusted for homogenous properties.
	// The reference figures are checked for common properties.  
	// Possible solution confidence is adjusted by how many properties the solution objects match. 
	public static ArrayList<RavensSolution> AdjustConfidenceHomogenousProperties(RavensProblem problem, 
												ArrayList<RavensSolution> solutions) 
	{
		ArrayList<RavensObject> referenceObjects = new ArrayList<RavensObject>();		
		
		// Generate List of objects from all reference figures A-H		
		for (String figureName : referenceFigures)
		{
			RavensFigure figure = problem.getFigures().get(figureName);
						
			// Check for null value
			if(figure == null)
				continue;
			
			referenceObjects.addAll(figure.getObjects().values());
		}	
		
		HashMap<String, String> referenceHomogenousProperties = GetHomogenousProperties(referenceObjects);	
		double totalReferenceProperties = referenceHomogenousProperties.size();
		
		// Iterate through possible solutions and determine degree of attribute matching
		for(RavensSolution solution: solutions)
		{
			RavensFigure figure = problem.getFigures().get(Integer.toString(solution.answer)); 
			
			// Check for null value
			if(figure == null)
				continue;
			
			HashMap <String, String> targetHomogenousProperties = GetHomogenousProperties(figure.getObjects().values());
			double matches = 0.0;
			
			for (String attribute : targetHomogenousProperties.keySet())
				if( referenceHomogenousProperties.containsKey(attribute) && 
				    referenceHomogenousProperties.get(attribute).equals( targetHomogenousProperties.get(attribute)))
					++matches;			
			
			solution.confidence = matches / totalReferenceProperties;
		}
		
		return solutions;
	}
	
	// Private helper function that returns a key value pair list of homogenous properties in a collection of Ravens Objects.
	private static HashMap<String, String> GetHomogenousProperties(Collection<RavensObject> collection)
	{
		HashMap <String, String> homogenousProperties = new HashMap<String, String>();
		boolean loaded = false;	
		
		for (RavensObject RO : collection)
		{
			if(!loaded)
			{
				homogenousProperties.putAll(RO.getAttributes());
				loaded = true;
			}
			else
			{
				// Iterate through the object properties and remove any unmatching properties  
				for (String attribute : RO.getAttributes().keySet())
				{
					if( homogenousProperties.containsKey(attribute) &&
					    !homogenousProperties.get(attribute).equals(RO.getAttributes().get(attribute)) ) 
						homogenousProperties.remove(attribute);
				}				
			}	
		}	
		
		return homogenousProperties;
	}

	// Returns true if the reference figures in problem all contain a single object.
	public static boolean SingleObjectProblem(RavensProblem problem) 
	{
		for (String figureName : referenceFigures)
		{
			RavensFigure RF = problem.getFigures().get(figureName);
			
			if(RF != null && RF.getObjects().values().size() != 1)
				return false;
		}
		
		return true;
	}

	public static boolean ExhibitsMitosis(RavensFigure first, RavensFigure second, RavensFigure third) 
	{
		// Get a list of objects in the second and third figures that can be modified
		ArrayList<RavensObject> secondObjects = new ArrayList<RavensObject>(second.getObjects().values());
		ArrayList<RavensObject> thirdObjects = new ArrayList<RavensObject>(third.getObjects().values());
		
		// For each object in the first figure
		for(RavensObject firstObject: first.getObjects().values())
		{
			// Look for shallow matches to that object in the second list of objects
			HashMap<String, RavensObject> secondShallowMatches = new HashMap<String, RavensObject>();			
			
			for(RavensObject secondObject : secondObjects)				
			{
				if(ShallowMatch(firstObject, secondObject))			
					secondShallowMatches.put(secondObject.getName(), secondObject);					
			}
			
			// If at least 2 shallow matches were found
			if(secondShallowMatches.size() >= 2)
			{
				// Look for a pair of overlapping objects and remove them from the second list of objects
				ArrayList<RavensObject> pair = GetOverlappingPair(secondShallowMatches);
				
				if(pair != null)
					secondObjects.removeAll(pair);
				else // Otherwise no mitosis exhibited
					return false;
			}
			else // This object was not doubled, no mitosis exhibited
				return false;
			
			// Now look for shallow matches to the original object in the third list of objects
			HashMap<String, RavensObject> thirdShallowMatches = new HashMap<String, RavensObject>();
			
			for(RavensObject thirdObject : thirdObjects)				
			{
				if(ShallowMatch(firstObject, thirdObject))			
					thirdShallowMatches.put(thirdObject.getName(), thirdObject);					
			}
			
			// If at least 2 shallow matches were found
			if(thirdShallowMatches.size() >= 2)
			{
				// Look for a pair of overlapping objects and remove them from the second list of objects
				ArrayList<RavensObject> pair = GetNonOverlappingPair(thirdShallowMatches);
				
				if(pair != null)
					thirdObjects.removeAll(pair);
				else // Otherwise no mitosis exhibited
					return false;
			}
			else // This object was not doubled throughout, no mitosis exhibited
				return false;			
		}
		
		// Final check: If execution reaches here then no conflicting conditions have been found.
		//              There should be no objects remaining in the second or third figure clones.
		
		if(secondObjects.size() == 0 && thirdObjects.size() == 0)
			return true;
		else // Extra objects floating around
			return false;
	}

	private static boolean ShallowMatch(RavensObject firstObject, RavensObject secondObject) 
	{
		// Get object clones that can be altered
		RavensObject first = TransformationUtilities.CloneRavensObject(firstObject);
		RavensObject second = TransformationUtilities.CloneRavensObject(secondObject);

		// Must pre process features that use object names, they prevent matches.		
		if(ContainsPositionalAttributes(first))
		{
			// If the first object contains positional attributes, the second should as well.
			boolean firstLeft = first.getAttributes().containsKey("left-of");
			boolean firstRight = first.getAttributes().containsKey("right-of");
			boolean firstAbove = first.getAttributes().containsKey("above");
			boolean firstBelow = first.getAttributes().containsKey("below");
			
			boolean secondLeft = second.getAttributes().containsKey("left-of");
			boolean secondRight = second.getAttributes().containsKey("right-of");
			boolean secondAbove = second.getAttributes().containsKey("above");
			boolean secondBelow = second.getAttributes().containsKey("below");
			
			
			// Enforce all counts equal between objects
			if ((firstLeft && !secondLeft)    ||
				(firstRight && !secondRight)  ||
				(firstAbove && !secondAbove)  || 
				(firstBelow && !secondBelow))
				return false;
		}
							
		// Remove these attributes, they are accounted fo above
		first.getAttributes().remove("inside");
		second.getAttributes().remove("inside");
		first.getAttributes().remove("above");
		second.getAttributes().remove("above");
		first.getAttributes().remove("below");
		second.getAttributes().remove("below");
		first.getAttributes().remove("left-of");
		second.getAttributes().remove("left-of");
		first.getAttributes().remove("right-of");
		second.getAttributes().remove("right-of");
		first.getAttributes().remove("overlaps");
		second.getAttributes().remove("overlaps");
		
		
		// Check for equal number of attributes
		if(first.getAttributes().keySet().size() == second.getAttributes().keySet().size())
		{
			for(String attributeName : first.getAttributes().keySet())
			{
				if(!first.getAttributes().get(attributeName).equals(second.getAttributes().get(attributeName))) 
					return false; 				
			}
			
			// No differences found
			return true;
		}
		
		// Not a shallow match
		return false;
	}
	
	// Returns a list of exactly two overlapping objects in the provided hash map, 
	// or null if no pairs could be found
	private static ArrayList<RavensObject> GetOverlappingPair(HashMap<String, RavensObject> shallowMatches) 
	{
		// Iterate through the matches
		for(RavensObject match : shallowMatches.values())
		{			
			// Get the list of object names that object overlaps with
			if(match.getAttributes().containsKey("overlaps"))
			{				
				String[] overlappingObjects = match.getAttributes().get("overlaps").split(",");
				
				// Iterate through the overlapping objects
				for(int i=0; i<overlappingObjects.length; ++i)
				{
					// Check to see if shallow matches contains this object.
					if(shallowMatches.containsKey(overlappingObjects[i]))
					{
						// If the match is found, return the two objects in a list
						ArrayList<RavensObject> pair = new ArrayList<RavensObject>();
						pair.add(match);
						pair.add(shallowMatches.get(overlappingObjects[i]));
						return pair;				
					}
				}
				
			}
		}
		
		// If the loops fail to return a value, return null
		return null;
	}
	
	private static ArrayList<RavensObject> GetNonOverlappingPair(HashMap<String, RavensObject> shallowMatches) 
	{
		// Iterate through the matches
		for(RavensObject match : shallowMatches.values())
		{			
			// If the object overlaps something
			if(match.getAttributes().containsKey("overlaps"))
			{				
				// Get the objects it overlaps with
				List<String> overlappingObjects = Arrays.asList(match.getAttributes().get("overlaps").split(","));
								
				// Iterate through the shallow matches looking for an object not in that list
				for(RavensObject otherMatch : shallowMatches.values())
				{
					// Skip if the object is being compared to itself
					if(match.getName().equals(otherMatch.getName()))
						continue;
					
					// If the overlapping objects does not contain this match
					if(!overlappingObjects.contains(otherMatch.getName()))
					{
						// Create and return the pair
						ArrayList<RavensObject> pair = new ArrayList<RavensObject>();
						pair.add(match);
						pair.add(otherMatch);
						return pair;	
					}
				}				
			}
			else 
			{
				// This object overlaps nothing, look for a match that does not overlap this object
				for(RavensObject otherMatch : shallowMatches.values())
				{
					// Skip if the object is being compared to itself
					if(match.getName().equals(otherMatch.getName()))
						continue;
					
					// if this match overlaps something
					if(otherMatch.getAttributes().containsKey("overlaps"))
					{
						// Get the objects it overlaps with
						List<String> overlappingObjects = Arrays.asList(otherMatch.getAttributes().get("overlaps").split(","));
						
						// if the original match is not in this list, return the pair.
						if(!overlappingObjects.contains(match.getName()))
						{
							ArrayList<RavensObject> pair = new ArrayList<RavensObject>();
							pair.add(match);
							pair.add(otherMatch);
							return pair;
						}
					}
					else // This object also overlaps nothing, return the pair
					{
						ArrayList<RavensObject> pair = new ArrayList<RavensObject>();
						pair.add(match);
						pair.add(otherMatch);
						return pair;
					}
				}
			}				
		}
		
		// If the loops fail to return a value, return null
		return null;
	}
	
	private static boolean ContainsPositionalAttributes(RavensObject obj)
	{
		if( obj.getAttributes().containsKey("overlaps") || 
			obj.getAttributes().containsKey("above")    ||
			obj.getAttributes().containsKey("below")    ||
			obj.getAttributes().containsKey("left-of")  ||
			obj.getAttributes().containsKey("right-of")    )
			return true;
		else
			return false;
	}
}

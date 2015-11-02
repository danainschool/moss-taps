package ravensproject.problemreduction;

import java.util.ArrayList;
import java.util.HashMap;
import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;

//Utility class for problem reduction helper functions.
public final class ProblemReductionUtilities 
{
	// Returns true if this problem mixes squares and rectangles.
	public static boolean MixedSquareRectangle(RavensProblem problem)
	{
		boolean squaresPresent = false;
		boolean rectanglesPresent = false;
		
		for (RavensFigure RF : problem.getFigures().values())
		{
			for (RavensObject RO : RF.getObjects().values())
			{
				if(RO.getAttributes().get("shape").equals("square"))
					squaresPresent = true;
				
				if(RO.getAttributes().get("shape").equals("rectangle"))
					rectanglesPresent = true;
				
				// Short circuit:  No need to iterate through every object if we find both
				if(squaresPresent && rectanglesPresent)
					return true;
			}
		}
		
		return false;
	}
	
	// Iterates through all Ravens Objects in the problem and rewrites any squares to rectangles.
	public static void RewriteSquaresAsRectangles(RavensProblem problem)
	{
		for (RavensFigure RF : problem.getFigures().values())
		{
			for (RavensObject RO : RF.getObjects().values())
			{
				if(RO.getAttributes().get("shape").equals("square"))
				{
					RO.getAttributes().replace("shape", "rectangle");
					
					String size = RO.getAttributes().get("size");
					
					RO.getAttributes().remove("size");
					RO.getAttributes().put("width", size);
					RO.getAttributes().put("height", size);				
				}				
			}
		}	
	}
	
	// Returns true if this problem has above and left-of attributes.
	public static boolean AboveLeftOf(RavensProblem problem) 
	{
		boolean abovePresent = false;
		boolean leftOfPresent = false;
		
		for (RavensFigure RF : problem.getFigures().values())
		{
			for (RavensObject RO : RF.getObjects().values())
			{
				if(RO.getAttributes().get("above") != null)
					abovePresent = true;
				
				if(RO.getAttributes().get("left-of") != null)
					leftOfPresent = true;
				
				// Short circuit:  No need to iterate through every object if we find both
				if(abovePresent || leftOfPresent)
					return true;
			}
		}
		
		return false;
	}
	
	// Iterates through all Ravens Objects in the problem and adds below and right-of attributes to objects
	public static void AddBelowRightOfAttributes(RavensProblem problem) 
	{
		HashMap<String, ArrayList<String>> belowsToWrite = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> rightOfsToWrite = new HashMap<String, ArrayList<String>>();
		
		// Iterate through each object and create hash maps of new attributes to be written
		for (RavensFigure RF : problem.getFigures().values())
		{
			for (RavensObject RO : RF.getObjects().values())
			{
				String above =  RO.getAttributes().get("above");
				String leftOf = RO.getAttributes().get("left-of");
				
				if(above != null)
				{
					for (String token : above.split(","))
					{
						if(belowsToWrite.containsKey(token))
						{
							belowsToWrite.get(token).add(RO.getName());
						}
						else
						{
							ArrayList<String> list = new ArrayList<String>();
							list.add(RO.getName());
							belowsToWrite.put(token, list);
						}
					}					
				}
				
				if(leftOf != null)
				{
					for (String token : leftOf.split(","))
					{
						if(rightOfsToWrite.containsKey(token))
						{
							rightOfsToWrite.get(token).add(RO.getName());
						}
						else
						{
							ArrayList<String> list = new ArrayList<String>();
							list.add(RO.getName());
							rightOfsToWrite.put(token, list);
						}
					}	
				}				
			}		
		}
		
		// Iterate through HashMaps and write new attributes
		for(String name : belowsToWrite.keySet())
		{
			// Create the attribute with all its targets, trim the extra comma at the end
			String attribute = "below";
			String value = "";
			
			for (String target : belowsToWrite.get(name))
				value = value + target + ",";
			
			value = value.substring(0, value.length()-1);
			
			// Add the attribute to the object
			for(RavensFigure RF : problem.getFigures().values())
			{
				if(RF.getObjects().get(name) != null)
				{
					RF.getObjects().get(name).getAttributes().put(attribute, value);
					break;
				}
			}
		}
		
		for(String name : rightOfsToWrite.keySet())
		{
			// Create the attribute with all its targets, trim the extra comma at the end
			String attribute = "right-of";
			String value = "";
			
			for (String target : rightOfsToWrite.get(name))
				value = value + target + ",";
			
			value = value.substring(0, value.length()-1);
			
			// Add the attribute to the object
			for(RavensFigure RF : problem.getFigures().values())
			{
				if(RF.getObjects().get(name) != null)
				{
					RF.getObjects().get(name).getAttributes().put(attribute, value);
					break;
				}
			}
		}		
	}

	// Iterates through all problem objects and returns true if any of the objects have the overlap property.
	public static boolean Overlaps(RavensProblem problem) 
	{
		// Iterate through objects looking for overlap attribute, return true if found.
		for (RavensFigure RF : problem.getFigures().values())
		{
			for (RavensObject RO : RF.getObjects().values())
			{
				if (RO.getAttributes().get("overlaps") != null)
					return true;
			}
		}
		
		return false;
	}
	
	// Iterates through all objects in the problem and records their overlapping properties, 
	// and writes a complementary attribute on the other object. 
	public static void AddOverlapsAttributes(RavensProblem problem) 
	{
		HashMap<String, String> overlapsToWrite = new HashMap<String, String>();
		
		//Populate hash map of values to write
		for(RavensFigure RF : problem.getFigures().values())
		{
			for(RavensObject RO : RF.getObjects().values())
			{
				String overlaps = RO.getAttributes().get("overlaps");
				
				if(overlaps != null)
				{				
					for(String target : overlaps.split(","))
					{
						if(overlapsToWrite.containsKey(target))
						{
							String prefix = overlapsToWrite.get(target);
							overlapsToWrite.remove(target);
							overlapsToWrite.put(target, prefix + "," + RO.getName());
						}
						else
							overlapsToWrite.put(target, RO.getName());
					}										
				}
			}
		}
		
		// Write values to objects
		for(String objectName : overlapsToWrite.keySet())
		{
			for(RavensFigure RF : problem.getFigures().values())
			{
				if(RF.getObjects().containsKey(objectName))
				{
					RavensObject RO = RF.getObjects().get(objectName);
					String currentOverlaps = RO.getAttributes().get("overlaps");
					
					if(currentOverlaps == null)
						RO.getAttributes().put("overlaps", overlapsToWrite.get(objectName));
					else
					{
						RO.getAttributes().remove("overlaps");
						RO.getAttributes().put("overlaps", currentOverlaps + "," + overlapsToWrite.get(objectName));
					}						
				}
			}
		}
	}
}

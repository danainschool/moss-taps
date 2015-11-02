package ravensproject;

import java.util.HashMap;

// Utility class for transformation helper functions.
public final class TransformationUtilities 
{
	// Constants
	public static final String HORIZONTAL = "horizontal";
	public static final String VERTICAL = "vertical";
	
	private TransformationUtilities(){}
	
	//Returns true if both objects have an equal number of attributes, and those attributes are equal.
	public static boolean ObjectsIdentical(RavensObject from, RavensObject to, HashMap<String, Integer> objectNesting)
	{
		// Check for equal number of attributes
		if(from.getAttributes().keySet().size() == to.getAttributes().keySet().size())
		{
			for(String attributeName : from.getAttributes().keySet())
			{
				// Special Case: inside attributes will never be exactly identical due to object names
				// 				 instead we must use object nesting values to determine if the objects are identical
				switch(attributeName)
				{
					case "inside":	if(objectNesting.get(from.getName()) != objectNesting.get(to.getName())) 
										return false; 
									break;
					default:		if(!from.getAttributes().get(attributeName).equals(to.getAttributes().get(attributeName))) 
										return false; 
									break;
				}
				
			}
			
			// No differences found
			return true;
		}
		
		return false;
	}
	
	// Returns the rotation between two ravens object as a positive integer only, between 0 and 360.
	public static int CalculateRotation(RavensObject from, RavensObject to)
	{
		// Get angles as integers
		int fromAngle, toAngle;
		
		if( from.getAttributes().get("angle") == null)
			return 0;
		else
			fromAngle = Integer.parseInt(from.getAttributes().get("angle"));
		
		if( to.getAttributes().get("angle") == null)
			return 0;
		else
			toAngle = Integer.parseInt(to.getAttributes().get("angle"));
		
		int rotation = toAngle - fromAngle;
		
		if(rotation >= 0)
			return rotation;
		else
			return rotation + 360;
	}
	
	// Checks if object alignment parameters result in horizontal or vertical reflections.
	// If alignment values are not found for objects, this function returns true.
	public static boolean AlignedForReflection(RavensObject from, RavensObject to, String spatialRelationship)
	{
		String fromAlignment = from.getAttributes().get("alignment");
		String toAlignment = to.getAttributes().get("alignment");

		if(fromAlignment == null || toAlignment == null)
			return true;
		
		if (spatialRelationship.equals(HORIZONTAL))
		{
			switch(fromAlignment)
			{
				case "bottom-right":if(toAlignment.equals("bottom-left")) return true; break;
				case "top-right":   if(toAlignment.equals("top-left")) return true; break;
				case "bottom-left": if(toAlignment.equals("bottom-right")) return true; break;
				case "top-left":    if(toAlignment.equals("top-right")) return true; break;
				default: return false;
			}
		}
		else if (spatialRelationship.equals(VERTICAL))
		{
			switch(fromAlignment)
			{
				case "bottom-right":if(toAlignment.equals("top-right")) return true; break;
				case "top-right":   if(toAlignment.equals("bottom-right")) return true; break;
				case "bottom-left": if(toAlignment.equals("top-left")) return true; break;
				case "top-left":    if(toAlignment.equals("bottom-left")) return true; break;
				default: return false;
			}
		}
		return false;
	}
	
	// Returns a string containing a whether a given shape is reflected horizontally, vertically, or both.
	// If there is no reflection CalculateReflections returns null. 
	public static String CalculateReflections(String shape, int rotation, int rootOrientation, String spatialRelationship)
	{
		switch(shape)
		{
			case "right triangle": return RightTriangleReflection(rotation, rootOrientation, spatialRelationship);
			case "pac-man": return PacManReflection(rotation, rootOrientation, spatialRelationship);
			case "circle":
			case "octagon": return RotationIndependentShapeReflection(spatialRelationship);
			case "plus":
			case "square":
			case "diamond": return XYZSymmetricShapeReflection(rotation, spatialRelationship);
			case "triangle":
			case "star":
			case "pentagon":
			case "heart":   return YSymmetricShapeReflection(rotation, rootOrientation, spatialRelationship);
			
			// No reflections observed
			default: return null;
		}		
	}
	
	private static String RotationIndependentShapeReflection(String spatialRelationship) 
	{
		if (spatialRelationship.equals(HORIZONTAL))
			return "reflected horizontally";
		else if (spatialRelationship.equals(VERTICAL))
			return "reflected vertically";
		else
			return null;
	}
	
	// All objects symmetric with respect to the Y axis 
	private static String YSymmetricShapeReflection(int rotation, int rootOrientation, String spatialRelationship) 
	{
		if (spatialRelationship.equals(HORIZONTAL))
		{
			switch(rootOrientation)
			{
				case 0:
				case 180:
					if(rotation == 0)   return "reflected horizontally"; break;				
				case 90:
				case 270:
					if(rotation == 180) return "reflected horizontally"; break;
				case 45:
				case 225:
					if(rotation == 270) return "reflected horizontally"; break;
				case 135:
				case 315:
					if(rotation == 90)  return "reflected horizontally"; break;
				default: return null; 
			}
		}
		else if(spatialRelationship.equals(VERTICAL))
		{
			switch(rootOrientation)
			{
				case 0: 
				case 180:
					if(rotation == 180) return "reflected vertically"; break;				
				case 90:
				case 270:
					if(rotation == 0) 	return "reflected vertically"; break;
				case 45:
				case 225:
					if(rotation == 90)  return "reflected vertically"; break;
				case 135:
				case 315:
					if(rotation == 270) return "reflected vertically"; break;
				default: return null;
			}
		}
		return null;
	}

	// All objects symmetric with respect to the X, Y, and Z axes 
	private static String XYZSymmetricShapeReflection(int rotation, String spatialRelationship) 
	{
		if (rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270) 
		{
			if (spatialRelationship.equals(HORIZONTAL))
				return "relfected horizontally";
			else if (spatialRelationship.equals(VERTICAL))
				return "reflected vertically";			
		}	
		return null;
	}

	private static String PacManReflection(int rotation, int rootOrientation, String spatialRelationship) 
	{
		if (spatialRelationship.equals(HORIZONTAL))
		{
			switch(rootOrientation)
			{
				case 0:
				case 180:
					if(rotation == 180) return "reflected horizontally"; break;					
				case 90:
				case 270:
					if(rotation == 0)   return "reflected horizontally"; break;
				case 45:
				case 225:
					if(rotation == 90)  return "reflected horizontally"; break;
				case 135:
				case 315:
					if(rotation == 270) return "reflected horizontally"; break;
				default: return null; 
			}
		}
		else if(spatialRelationship.equals(VERTICAL))
		{
			switch(rootOrientation)
			{
				case 0: 
				case 180:
					if(rotation == 0)   return "reflected vertically"; break;				
				case 90:
				case 270:
					if(rotation == 180) return "reflected vertically"; break;
				case 45:
				case 225:
					if(rotation == 270) return "reflected vertically"; break;
				case 135:
				case 315:
					if(rotation == 90)  return "reflected vertically"; break;
				default: return null;
			}
		}
		return null;
	}

	private static String RightTriangleReflection(int rotation, int rootOrientation, String spatialRelationship)
	{
		if (spatialRelationship.equals(HORIZONTAL))
		{
			switch(rootOrientation)
			{
				case 0: 
				case 180:
					if(rotation == 270) return "reflected horizontally"; break;					
				case 90:
				case 270:
					if(rotation == 90)  return "reflected horizontally"; break;
				case 45:
				case 225:
					if(rotation == 180) return "reflected horizontally"; break;
				case 135:
				case 315:
					if(rotation == 0)   return "reflected horizontally"; break;
				default: return null; 
			}
		}
		else if(spatialRelationship.equals(VERTICAL))
		{
			switch(rootOrientation)
			{
				case 0: 
				case 180:
					if(rotation == 90)  return "reflected vertically"; break;				
				case 90:
				case 270:
					if(rotation == 270) return "reflected vertically"; break;
				case 45:
				case 225:
					if(rotation == 0)   return "reflected vertically"; break;
				case 135:
				case 315:
					if(rotation == 180) return "reflected vertically"; break;
				default: return null;
			}
		}
		
		return null;
	}
	
	// Determines fill transformations between objects
	public static String CalculateFillTransformations(RavensObject from, RavensObject to) 
	{
		String fromFill = from.getAttributes().get("fill");
		String toFill = to.getAttributes().get("fill");
		
		if (fromFill == null)
			fromFill = "yes";
		if (toFill == null)
			toFill = "yes";
		
		switch (fromFill)
		{
			case "yes": if(toFill.equals("no"))  return "fill emptied"; break;
			case "no":  if(toFill.equals("yes")) return "filled"; break;
			case "right-half": if(toFill.equals("left-half"))   return "fill alternatedRL"; break;
			case "left-half":  if(toFill.equals("right-half"))  return "fill alternatedLR"; break;
			case "top-half":   if(toFill.equals("bottom-half")) return "fill alternatedTB"; break;
			case "bottom-half":if(toFill.equals("top-half"))    return "fill alternatedBT"; break;
			default: return null;
		}
		
		return null;
	}
	
	public static int GetSizeAsInt(String size)
	{
		switch (size)
		{
			case "very small": return 1;
			case "small":	   return 2;
			case "medium":	   return 3;
			case "large":	   return 4;
			case "very large": return 5;
			case "huge":	   return 6;
			default:		   return 0;
		}
	}
}

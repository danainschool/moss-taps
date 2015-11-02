package ravensproject.sngt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;

// Utility class for transformation helper functions.
public final class TransformationUtilities 
{
	// Constants
	public static final String HORIZONTAL = "horizontal";
	public static final String VERTICAL = "vertical";
	
	private TransformationUtilities(){}
	
	// Returns true if both objects have an equal number of attributes, and those attributes are equal.
	public static boolean ObjectsIdentical(RavensObject origFrom, RavensObject origTo, HashMap<String, Integer> objectNesting, boolean ignoreNesting)
	{
		// Get clones objects that can be altered
		RavensObject from = CloneRavensObject(origFrom);
		RavensObject to = CloneRavensObject(origTo);
		
		// If ignoreNesting is flagged, then the inside attributes are removed prior to execution
		if(ignoreNesting)
		{
			from.getAttributes().remove("inside");
			to.getAttributes().remove("inside");
		}
		
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
	
	public static RavensObject CloneRavensObject(RavensObject original) 
	{
		RavensObject clone = new RavensObject(original.getName());
		clone.getAttributes().putAll(original.getAttributes());
		return clone;
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

	public static boolean AllObjectsUnchanged(ArrayList<ObjectTransformation> objectTransformations) 
	{
		boolean objectsUnchanged = true;
		
		for(ObjectTransformation OT : objectTransformations)
		{
			if(!OT.getTransformationList().contains("unchanged"))
				objectsUnchanged = false;				
		}
				
		return objectsUnchanged;
	}
	
	// Returns string "spatial change: horizontal", "spatial change: vertical" or null based on the overall trend of additions or deletions
	// This is calculated by counting the above and left-of attributes for each object in each figure.
	public static String DetermineSpatialChange(RavensFigure from, RavensFigure to) 
	{
		int fromLefts = 0, fromAboves = 0, toLefts = 0, toAboves = 0; 
		
		// For each figure, iterate through each object and sum the total left-of and above attibutes.
		for(RavensObject RO : from.getObjects().values())
		{
			String leftOf = RO.getAttributes().get("left-of");
			String above = RO.getAttributes().get("above");
			
			if(leftOf != null)
			{
				StringTokenizer st = new StringTokenizer(leftOf, ",");
				fromLefts += st.countTokens();
			}
			if(above != null)
			{
				StringTokenizer st = new StringTokenizer(above, ",");
				fromAboves += st.countTokens();
			}			
		}
		
		for(RavensObject RO : to.getObjects().values())
		{
			String leftOf = RO.getAttributes().get("left-of");
			String above = RO.getAttributes().get("above");
			
			if(leftOf != null)
			{
				StringTokenizer st = new StringTokenizer(leftOf, ",");
				toLefts += st.countTokens();
			}
			if(above != null)
			{
				StringTokenizer st = new StringTokenizer(above, ",");
				toAboves += st.countTokens();
			}			
		}
		
		int leftChange = Math.abs(toLefts - fromLefts);
		int aboveChange = Math.abs(toAboves - fromAboves);
		
		if ( leftChange > aboveChange)
			return "spatial change: horizontal";
		else if ( aboveChange > leftChange)
			return "spatial change: vertical";
		else
			return null;
	}

	public static HashMap<String, String> GenerateObjectRelativePosition(RavensFigure RF) 
	{
		HashMap<String, String> relativePositions = new HashMap<String, String>(); 
		
		for (RavensObject RO : RF.getObjects().values())
		{
			int leftCount = GetCSVCount(RO.getAttributes().get("left-of"));
			int rightCount = GetCSVCount(RO.getAttributes().get("right-of"));
			int aboveCount = GetCSVCount(RO.getAttributes().get("above"));
			int belowCount = GetCSVCount(RO.getAttributes().get("below"));
			int overlapCount = GetCSVCount(RO.getAttributes().get("overlaps"));
			
			String objectMotion = "";
			
			if(leftCount > rightCount)
				objectMotion += "left,";
			else if(leftCount < rightCount)
				objectMotion += "right,";
			if(aboveCount > belowCount)
				objectMotion += "top,";
			else if(aboveCount < belowCount)
				objectMotion += "bottom,";
			objectMotion += "overlaps:" + overlapCount;
			
			relativePositions.put(RO.getName(), objectMotion);			
		}		
			
		return relativePositions;
	}

	public static ArrayList<String> GenerateRelativeMotionTransformations(
													HashMap<String, String> fromObjectRelativePosition,
													HashMap<String, String> toObjectRelativePosition,
													ArrayList<ObjectCorrespondence> objectsCorrespondence) 
	{
		ArrayList<String> relativeMotionTransformations = new ArrayList<String>();
		
		for(ObjectCorrespondence OC : objectsCorrespondence)
		{
			String fromPosition = fromObjectRelativePosition.get(OC.referenceObject.getName());
			String toPosition = toObjectRelativePosition.get(OC.correspondingObject.getName());
			
			if(fromPosition == null || toPosition == null)
				continue;
			
			boolean fromLeft = fromPosition.contains("left");
			boolean fromRight = fromPosition.contains("right");
			boolean fromTop = fromPosition.contains("top");
			boolean fromBottom = fromPosition.contains("bottom");
			//int fromOverlaps = fromPosition.charAt( fromPosition.lastIndexOf(":") + 1 );
			
			boolean toLeft = toPosition.contains("left");
			boolean toRight = toPosition.contains("right");
			boolean toTop = toPosition.contains("top");
			boolean toBottom = toPosition.contains("bottom");
			//int toOverlaps = toPosition.charAt( toPosition.lastIndexOf(":") + 1 );
			
			// Check for Lateral Motion
			if(fromLeft && !toLeft)
				relativeMotionTransformations.add("object moved right");
			if(fromRight && !toRight)
				relativeMotionTransformations.add("object moved left");
			if(!fromRight && !fromLeft)
			{
				if(toLeft)
					relativeMotionTransformations.add("object moved left");
				else if(toRight)
					relativeMotionTransformations.add("object moved right");
			}
			
			// Check for vertical motion
			if(fromTop && !toTop)
				relativeMotionTransformations.add("object moved down");
			if(fromBottom && !toBottom)
				relativeMotionTransformations.add("object moved up");
			if(!fromTop && !fromBottom)
			{
				if(toTop)
					relativeMotionTransformations.add("object moved up");
				if(toBottom)
					relativeMotionTransformations.add("object moved down");
			}
		}	
		return relativeMotionTransformations;
	}
	
	// Returns a count of the values in a comma separated string
	public static int GetCSVCount(String attribute) 
	{
		if(attribute == null)
			return 0;
		
		return attribute.split(",").length;
	}
}

package ravensproject.sngt;

import java.util.ArrayList;
import java.util.HashMap;

import ravensproject.RavensObject;

//Represents the transformation between two RavensObjects.
public class ObjectTransformation 
{
	// CONSTANTS
	private static final double REFLECTION_PREFERENCE = 0.25;
	
	private RavensObject from;
	private RavensObject to;
	private ArrayList<String> transformations;
	private String spatialRelationship;
	private HashMap<String, Integer> objectNesting;
	
	public ObjectTransformation(RavensObject from, RavensObject to, String spatialRelationship, HashMap<String, Integer> objectNesting)
	{
		this.from = from;
		this.to = to;
		this.spatialRelationship = spatialRelationship;
		this.objectNesting = objectNesting;
		transformations = new ArrayList<String>();
		
		// Detect Exclusive Transformations - return immediately
		if(this.to.getName().equals("NULL"))
		{
			transformations.add("deleted");
			return;
		}
		
		if(this.from.getName().equals("NULL"))
		{
			transformations.add("created");
			return;
		}
		
		if(TransformationUtilities.ObjectsIdentical(this.from, this.to, this.objectNesting, false))
		{
			transformations.add("unchanged");			
			return;
		} // End Exclusive transformations
		
		// Detect non-exclusive transformations - apply all
		String fromShape = this.from.getAttributes().get("shape");
		String toShape = this.to.getAttributes().get("shape");
		String fromAngle = this.from.getAttributes().get("angle");
		String fromSize = this.from.getAttributes().get("size");
		String toSize = this.to.getAttributes().get("size");
				
		if(fromAngle == null)
			fromAngle = "0";
		
		// Determine what reflections / rotations occurred.
		int rotation = TransformationUtilities.CalculateRotation(this.from, this.to);
		
		if(rotation != 0)
			transformations.add("rotation:" + rotation);
		
		// Only calculate reflections if shapes are the same and objects are aligned for reflections		
		if(fromShape.equals(toShape) &&  TransformationUtilities.AlignedForReflection(this.from, this.to, this.spatialRelationship))
		{			
			int rootOrientation = Integer.parseInt(fromAngle);
			String reflection = TransformationUtilities.CalculateReflections(fromShape, rotation, rootOrientation, this.spatialRelationship);
			
			if(reflection != null)
				transformations.add(reflection);			
		} 
		// End Reflections / Rotations
		
		// If the objects have fill properties,determine what fill / de-fill transformations occurred
		if( (this.from.getAttributes().get("fill") != null) || (this.to.getAttributes().get("fill") != null) )
		{
			String fillTransformation = TransformationUtilities.CalculateFillTransformations(this.from, this.to);			
			if(fillTransformation != null)
				transformations.add(fillTransformation);
		} // End Fills / De-Fills
		
		// If the objects have changed shapes, add shape morph transformations.
		if(!fromShape.equals(toShape))
			transformations.add("morph to:" + toShape);
		// End Shape Morphs
		
		// If shape sizes have changed, add shape sizing transformations (ignore if size is null)
		if(fromSize != null && toSize != null && !fromSize.equals(toSize))
		{
			int sizeChange = TransformationUtilities.GetSizeAsInt(toSize) - 
							TransformationUtilities.GetSizeAsInt(fromSize);
			transformations.add("size changed:" + sizeChange);			
		}
		// Special case:  rectangle
		// Only calculate size transformation if both objects are rectangles.
		else if(fromShape.equals("rectangle") && toShape.equals("rectangle"))
		{
			String fromWidth = from.getAttributes().get("width");
			String fromHeight = from.getAttributes().get("height");
			String toWidth = to.getAttributes().get("width");
			String toHeight = to.getAttributes().get("height");
			
			int fromRectSize = TransformationUtilities.GetSizeAsInt(fromWidth) * TransformationUtilities.GetSizeAsInt(fromHeight);
			int toRectSize = TransformationUtilities.GetSizeAsInt(toWidth) * TransformationUtilities.GetSizeAsInt(toHeight);
			int sizeChange = toRectSize - fromRectSize;
			
			transformations.add("size changed: " + sizeChange);
		}
		// End Size changes
	}	

	public String getTransformations()
	{
		return transformations.toString();
	}
	
	public ArrayList<String> getTransformationList()
	{
		return transformations;
	}
	
	// Generates a double value indicating degree of similarity between Object Transformations
	public double CompareTo(ObjectTransformation anotherOT)
	{
		// Make duplicate transformation list.  The comparison process removes list elements.
		ArrayList<String> anotherTransformations = new ArrayList<String>(anotherOT.transformations);
		
		double equal = 0, unique = transformations.size();
		boolean rotationsIncluded = false, morphsIncluded = false, sizeChangeIncluded = false;
		
		// Check for matching transformations
		for(String transformation : transformations)
		{
			if(transformation.startsWith("rotation:"))
				rotationsIncluded = true;
			if(transformation.startsWith("morph to:"))
				morphsIncluded = true;
			if(transformation.startsWith("size changed:"))
				sizeChangeIncluded = true;
			
			if(anotherTransformations.contains(transformation))
			{
				++equal;
				
				// Provide small preference for reflections versus rotations
				if(transformation.startsWith("reflected "))
				{
					equal += REFLECTION_PREFERENCE;
					unique += REFLECTION_PREFERENCE;
				}
				
				anotherTransformations.remove(transformation);
			}			
		}
		
		// Check for additional unique transformations
		for(String transformation : anotherTransformations)
		{
			// Prevents over penalizing a lack of rotation, morph or size change matches.
			if(rotationsIncluded && transformation.startsWith("rotation:"))
				continue;
			if(morphsIncluded && transformation.startsWith("morph to:"))
				continue;
			if(sizeChangeIncluded && transformation.startsWith("size changed:"))
				continue;
		
			++unique;
		}
		
		// check for divide by 0
		if (unique == 0)
			++unique;
		
		return equal / unique;
	}
	
	@Override 
	public String toString()
	{
		return from.getName() + ":" + to.getName() + " @ " + transformations;
	}

	public RavensObject getToObject() 
	{
		return to;
	}
	public RavensObject getFromObject() 
	{
		return from;
	}
}

package ravensproject.sngt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ravensproject.RavensFigure;

// Represents the transformation between two RavensFigures.  This class is a container for 3 main types of knowledge:
// 1. The correspondence of objects in the two figures
// 2. The object transformations between corresponding objects
// 3. Transformations at the figure level (i.e.  entire figure rotated vs. all objects rotated individually)
public class FigureTransformation 
{
	// Figures to be compared, their relationship, their transformations.
	private RavensFigure from;
	private RavensFigure to;
	private String spatialRelationship;
	private HashMap<String, Integer> objectNesting;	
	private ArrayList<String> transformations;
	
	// List of corresponding objects (supports one to one only)
	private ArrayList<ObjectCorrespondence> objectsCorrespondence;
	
	// List of object transformations
	private ArrayList<ObjectTransformation> objectTransformations;
	
	public FigureTransformation(RavensFigure from, RavensFigure to, String spatialRelationship, HashMap<String, Integer> problemNesting)
	{
		this.from = from;
		this.to = to;
		this.spatialRelationship = spatialRelationship;
		this.objectNesting = problemNesting;
		this.transformations = new ArrayList<String>();
		this.objectTransformations = new ArrayList<ObjectTransformation>();
		
		// Determine Object Correspondence
		this.objectsCorrespondence = CorrespondenceUtilities.GenerateObjectCorrespondence(this.from, this.to, this.spatialRelationship, this.objectNesting);
		
		// Generate Object Transformations based on accepted correspondence 
		for(ObjectCorrespondence OC : objectsCorrespondence)
			this.objectTransformations.add(new ObjectTransformation(OC.referenceObject, OC.correspondingObject, this.spatialRelationship, this.objectNesting));
		
		// Generate Transformations at the figure level		
		if(TransformationUtilities.AllObjectsUnchanged(this.objectTransformations))
			this.transformations.add("unchanged");
		else
		{
			// Iterate through object transformations and add generic figure transformations
			for (ObjectTransformation OT : this.objectTransformations)
			{
				ArrayList<String> transformationList = OT.getTransformationList();
				
				// First check for exclusive transformations - unchanged, created, deleted.  These can be short circuited.
				if(transformationList.contains("unchanged"))
					continue;
				if(transformationList.contains("deleted"))
				{
					String shape = OT.getFromObject().getAttributes().get("shape");				
					this.transformations.add("deleted:" + shape);
					
					String spatialChange = TransformationUtilities.DetermineSpatialChange(this.from, this.to);
					if(spatialChange != null)
						this.transformations.add(spatialChange);
					
					continue;
				}
				if(transformationList.contains("created"))
				{
					String shape = OT.getToObject().getAttributes().get("shape");				
					this.transformations.add("created:" + shape);
					
					String spatialChange = TransformationUtilities.DetermineSpatialChange(this.from, this.to);
					if(spatialChange != null)
						this.transformations.add(spatialChange);
					
					continue;
				}
				
				// Non exclusive transformations:  must iterate through object transformation list
				for (String transformation : transformationList)
				{
					if(transformation.startsWith("size changed:"))
					{
						this.transformations.add(transformation);
					}
					if(transformation.startsWith("morph to:"))
					{
						this.transformations.add(transformation);
					}
					if(transformation.startsWith("fill"))
					{
						this.transformations.add(transformation);
					}
					if(transformation.startsWith("rotation"))
					{
						this.transformations.add(transformation);
					}
				}			 
			}
			
			// Transformations not reliant on object level transformations
			
			// Check for general object movement - generate maps of object position per figure
			HashMap<String, String> fromObjectRelativePosition = TransformationUtilities.GenerateObjectRelativePosition(from);
			HashMap<String, String> toObjectRelativePosition = TransformationUtilities.GenerateObjectRelativePosition(to);
					
			// Compare these figures, determine motion
			this.transformations.addAll(
					TransformationUtilities.GenerateRelativeMotionTransformations(fromObjectRelativePosition, 
									toObjectRelativePosition, this.objectsCorrespondence));			
		}	
	}
	
	// Generates a double value indicating degree of similarity between Figure Transformations.
	public double CompareTo(FigureTransformation anotherFT)
	{
		int thisObjectTransformations = this.objectTransformations.size();
		int otherObjectTransformations = anotherFT.objectTransformations.size();
		
		// Check for simple object transformation correspondence (0:0, 0:1, 1:0, 1:1) 
		if(thisObjectTransformations == 0 || otherObjectTransformations == 0)
			return 0;
		else if(thisObjectTransformations == 1 && otherObjectTransformations == 1)
		{
			ObjectTransformation ref = this.objectTransformations.iterator().next();
			ObjectTransformation test = anotherFT.objectTransformations.iterator().next();
			return ref.CompareTo(test);
		}
		else  // Complex transformation correspondence (multiple and/or unequal numbers of transformations) 
		{
			// Working Lists of Object Transformations that can be modified
			ArrayList<ObjectTransformation> referenceOTs = new ArrayList<ObjectTransformation>(this.objectTransformations);
			ArrayList<ObjectTransformation> comparedOTs = new ArrayList<ObjectTransformation>(anotherFT.objectTransformations);
			
			// Generate a list of all possible FigureCorrespondences, and sort them
			ArrayList<FigureCorrespondence> possibleValues = GeneratePossibleValues(referenceOTs, comparedOTs);
			Collections.sort(possibleValues, Collections.reverseOrder());
			
			// Iterate through all of the Figure Correspondence Objects and collect the highest values
			ArrayList<FigureCorrespondence> figuresCorrespondence = new ArrayList<FigureCorrespondence>();
			for(FigureCorrespondence FC : possibleValues)
			{
				// Break out of loop when either object pool gets to zero
				if(referenceOTs.size() < 1 || comparedOTs.size() < 1)
					break;
				
				if(referenceOTs.contains(FC.referenceOT) && comparedOTs.contains(FC.comparedOT))
				{
					figuresCorrespondence.add(FC);
					referenceOTs.remove(FC.referenceOT);
					comparedOTs.remove(FC.comparedOT);
				}
			}
			
			// Use the established figure correspondence to calculate the ultimate return value
			double totalObjectTransformations = figuresCorrespondence.size();
			double sum = 0;
			
			for(FigureCorrespondence FC : figuresCorrespondence)
				sum += FC.similarity;
			
			return sum / totalObjectTransformations;
		}
	}
	
	private ArrayList<FigureCorrespondence> GeneratePossibleValues(	ArrayList<ObjectTransformation> referenceOTs,
																	ArrayList<ObjectTransformation> comparedOTs) 
	{
		ArrayList<FigureCorrespondence> possibleValues= new ArrayList<FigureCorrespondence>();
		
		for(ObjectTransformation refOT : referenceOTs)
			for(ObjectTransformation compOT : comparedOTs)
				possibleValues.add(new FigureCorrespondence(refOT, compOT, refOT.CompareTo(compOT)));
		
		return possibleValues;
	}

	public ArrayList<ObjectCorrespondence> getFigureCorrespondence()
	{
		return objectsCorrespondence;
	}
	public ArrayList<ObjectTransformation> getObjectTransformations()
	{
		return objectTransformations;
	}
	public ArrayList<String> getTransformations()
	{
		return transformations;
	}
}

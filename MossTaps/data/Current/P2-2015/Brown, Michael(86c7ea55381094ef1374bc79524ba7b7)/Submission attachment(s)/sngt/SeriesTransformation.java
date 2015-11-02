package ravensproject.sngt;

import java.util.ArrayList;
import java.util.HashMap;

import ravensproject.RavensFigure;

// Represents the transformation between two FigureTransformations, of three RavensFigures
// (1 -> 2) and (2 -> 3)
// This is used exclusively by 3x3 SNGT problems.
public class SeriesTransformation 
{
	// Series Figures, their transformations, their relationship
	private RavensFigure first;
	private RavensFigure second;
	private RavensFigure third;
	
	private String spatialRelationship;
	private HashMap<String, Integer> objectNesting;
	
	private FigureTransformation first_to_second;
	private FigureTransformation second_to_third;
	
	private ArrayList<String> transformations;
	private double transformationConfidence;
	
	public SeriesTransformation(RavensFigure first, RavensFigure second, RavensFigure third, String spatialRelationship, HashMap<String, Integer> problemNesting)
	{
		this.first = first;
		this.second = second;
		this.third = third;	
		
		this.spatialRelationship = spatialRelationship;
		this.objectNesting = problemNesting;
		
		// Build Figure Transformations
		this.first_to_second = new FigureTransformation(this.first, this.second, this.spatialRelationship, this.objectNesting);
		this.second_to_third = new FigureTransformation(this.second, this.third, this.spatialRelationship, this.objectNesting);
		
		this.transformations = new ArrayList<String>();
		double matchedTransformations = 0.0;
		double totalTransformations = first_to_second.getTransformations().size() + second_to_third.getTransformations().size();
		
		// Check for division by 0
		if(totalTransformations == 0.0)
			++totalTransformations;
		
		// Overall transformations for the series are any common generic figure transformations
		for(String transformation : first_to_second.getTransformations())
		{
			if(second_to_third.getTransformations().contains(transformation))
			{
				transformations.add(transformation);
				++matchedTransformations;
			}
			
			// Special Case:  Morphs.  Successive morphs include new shapes, so they must be coalesced.
			if(transformation.startsWith("morph to:"))
			{
				for (String otherTransformation : second_to_third.getTransformations())
				{
					if(otherTransformation.startsWith("morph to:"))
					{
						String shape = otherTransformation.substring(otherTransformation.indexOf(":")+1);
						transformations.add(transformation + "," + shape);						
						++matchedTransformations;
						
						// Only for one successive morph
						break;
					}
				}
			}
		}
		
		this.transformationConfidence = matchedTransformations / (totalTransformations - matchedTransformations);			
	}
	
	// Generates a double value indicating degree of similarity between three Figure Transformations.
	public double CompareTo(SeriesTransformation secondST, SeriesTransformation thirdST)
	{
		double equal = 0.0, unique = this.transformations.size();
		
		// Create duplicate transformation lists, this function deletes elements.
		ArrayList<String> secondTransformations = new ArrayList<String>(secondST.getTransformations());
		ArrayList<String> thirdTransformations = new ArrayList<String>(thirdST.getTransformations());
		
		// Search for matching transformations
		for(String transformation : this.transformations)
		{
			if(secondTransformations.contains(transformation) && thirdTransformations.contains(transformation))
			{
				++equal;
				
				secondTransformations.remove(transformation);
				thirdTransformations.remove(transformation);
			}
		}
		
		// Check for additional unique transformations
		for(String transformation : secondTransformations)
		{
			++unique;
			
			if(thirdTransformations.contains(transformation))
				thirdTransformations.remove(transformation);
		}
		unique += thirdTransformations.size();
		
		
		// check for divide by 0
		if (unique == 0)
			++unique;
		
		// Calculate reduction factor
		double reductionFactor = this.transformationConfidence * secondST.transformationConfidence * thirdST.transformationConfidence;
		return (equal / unique) * reductionFactor;
	}
	
	public ArrayList<String> getTransformations()
	{
		return transformations;
	}
}

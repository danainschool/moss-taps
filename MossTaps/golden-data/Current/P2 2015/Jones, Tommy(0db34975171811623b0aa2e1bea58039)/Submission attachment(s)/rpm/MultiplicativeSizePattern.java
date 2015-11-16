package ravensproject.rpm;

import java.util.ArrayList;
import java.util.HashMap;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;
import ravensproject.RavensSolution;
import ravensproject.sngt.TransformationUtilities;

public class MultiplicativeSizePattern extends Pattern 
{
	// Private Members
	private HashMap <String, Integer> template = populateTemplate();
	private int factor = 1;
	private boolean factorSet = false;
	
	public MultiplicativeSizePattern(RavensProblem problem)
	{
		this.problem = problem;
	}
	
	@Override
	public boolean matchesPattern() 
	{
		// This pattern requires only a single object per frame
		if(!PatternMatchingUtilities.SingleObjectProblem(problem))
			return false;
		
		// Iterate through template, looking for non matching conditions
		for(String figureName : template.keySet())
		{			
			// Check for null figure
			RavensFigure RF = problem.getFigures().get(figureName);
			if (RF == null)
				continue;
			
			//Get Object in Figure
			RavensObject RO = RF.getObjects().values().iterator().next();			
			
			// Get its size
			int objectSize;
			if(RO.getAttributes().get("shape").equals("rectangle"))
			{
				String width = RO.getAttributes().get("width");
				String height = RO.getAttributes().get("height");				
				objectSize = TransformationUtilities.GetSizeAsInt(width) * TransformationUtilities.GetSizeAsInt(height);
			}
			else
				objectSize = TransformationUtilities.GetSizeAsInt(RO.getAttributes().get("size"));
			
			// Set Factor			
			if(!factorSet && (objectSize % template.get(figureName) == 0 ))
				factor = objectSize / template.get(figureName);
			
			// Check to make sure object size is consistent with pattern
			if( objectSize/factor != template.get(figureName) ) 
				return false;						
		}
		
		// If loop completes, match was found
		return true;
	}

	@Override
	public ArrayList<RavensSolution> evaluateSolutions() 
	{
		ArrayList<RavensSolution> solutions = new ArrayList<RavensSolution>();
		int solution = template.get("solution") * factor;
				
		for (int i = 1; i<=NUM_SOLUTIONS; ++i)
		{
			RavensFigure answer = problem.getFigures().get(Integer.toString(i));
			
			//Get Object in Figure
			RavensObject RO = answer.getObjects().values().iterator().next();			
			
			// Get its size
			int answerSize;
			if(RO.getAttributes().get("shape").equals("rectangle"))
			{
				String width = RO.getAttributes().get("width");
				String height = RO.getAttributes().get("height");				
				answerSize = TransformationUtilities.GetSizeAsInt(width) * TransformationUtilities.GetSizeAsInt(height);
			}
			else
				answerSize = TransformationUtilities.GetSizeAsInt(RO.getAttributes().get("size"));			
			
			// Check if figure matches pattern
			if( answerSize == solution)
				solutions.add(new RavensSolution(i, 1.0));
		}		
		
		// Adjust the solutions that match the pattern based on homogenous properties
		return PatternMatchingUtilities.AdjustConfidenceHomogenousProperties(problem, solutions);
	}
	
	
	// Utility Method to statically populate pattern HashMap and desired solution
	private HashMap<String, Integer> populateTemplate()
	{
		HashMap<String, Integer> template = new HashMap<String, Integer>();
		
		template.put("A", 1);
		template.put("B", 2);
		template.put("C", 3);
		template.put("D", 2);
		template.put("E", 4);
		template.put("F", 6);
		template.put("G", 3);
		template.put("H", 6);
		template.put("solution", 9);
				
		return template;
	}
	
	@Override
	public String getPatternName() 
	{
		return "Multiplicative Size Patern";
	}
}

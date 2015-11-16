package ravensproject.rpm;

import java.util.ArrayList;
import java.util.HashMap;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.RavensSolution;

// Recognizes patterns in object counts where the object count is equal to the row index * column index
// Indices start at 1,  i.e. Figure A is 1x1, Figure B is 1x2, Figure E is 2x2, etc.
public class MultiplicativeObjectCountPattern extends Pattern 
{
	// Private Members
	private HashMap <String, Integer> template = populateTemplate();
	private int factor = 1;
	
	public MultiplicativeObjectCountPattern(RavensProblem problem)
	{
		this.problem = problem;
	}
		
	@Override
	public boolean matchesPattern() 
	{
		// Set factor based on number of objects in Figure A
		RavensFigure figA = problem.getFigures().get("A");		
		factor = figA.getObjects().values().size();		
		
		// Check for blank first figure
		if(factor == 0)
			return false;
		
		// Iterate through template, looking for non matching conditions
		for(String figureName : template.keySet())
		{			
			RavensFigure RF = problem.getFigures().get(figureName);			
			
			if( (RF != null) && (RF.getObjects().values().size()/factor != template.get(figureName)) )
				return false;						
		}
		
		// If loop completes, match was found
		return true;
	}

	@Override
	public ArrayList<RavensSolution> evaluateSolutions() 
	{
		ArrayList<RavensSolution> solutions = new ArrayList<RavensSolution>();
		int solution = template.get("solution");
				
		for (int i = 1; i<=NUM_SOLUTIONS; ++i)
		{
			RavensFigure answer = problem.getFigures().get(Integer.toString(i));
			
			// Check if figure matches pattern
			if( (answer != null) && (answer.getObjects().values().size() == solution) )
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
		return "Multiplicative Object Count Pattern";
	}
}
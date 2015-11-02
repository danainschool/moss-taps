package ravensproject.rpm;

import java.util.ArrayList;

import ravensproject.RavensProblem;
import ravensproject.RavensSolution;
import ravensproject.RavensStrategy;

// Implementation of Ravens Strategy that will solve 3 x 3 verbal problems by:
// 1. Rapidly applying the verbal problem knowledge to pattern recognition templates
// 2. Selecting a suitable answer by cascading scoring for matching.
public class RPM3x3Verbal extends RavensStrategy 
{
	public RPM3x3Verbal(RavensProblem problem) 
	{
		this.problem = problem;
		
		System.out.println("Agent has decided to use the Rapid Pattern Matching for 3x3 Verbal Problems Strategy.");		
	}

	public Boolean solve() 
	{
		// If a match occurs, the problem is evaluated and the solution(s) are added to the possible solutions.
		// The best answer is selected.
		
		ArrayList<Pattern> availablePatterns = populatePatterns();
		ArrayList<RavensSolution> possibleSolutions = new ArrayList<RavensSolution>();
		
		for (Pattern pattern : availablePatterns)
		{
			if(pattern.matchesPattern())
			{
				System.out.println(pattern.getPatternName() + " has matched this problem.");				
				possibleSolutions.addAll(pattern.evaluateSolutions());
			}
		}
		
		if(possibleSolutions.size() != 0)
		{
			// Select the best answer
			for(RavensSolution RS : possibleSolutions)
			{
				System.out.println("Solution " + RS.answer + " has confidence: " + RS.confidence); //DEBUG
				
				if((solution == null) || (solution.confidence < RS.confidence))
					solution = RS;				
			}		
			
			return true;
		}
		else // No solution found.
			return false;
	}
	
	// Utility method for populating the Patterns available to the strategy
	private ArrayList<Pattern> populatePatterns() 
	{
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		
		patterns.add(new MultiplicativeObjectCountPattern(problem));
		patterns.add(new MultiplicativeSizePattern(problem));
		patterns.add(new MitosisPattern(problem));
		
		return patterns;
	}
}

package ravensproject.rpm;

import java.util.ArrayList;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.RavensSolution;

public class MitosisPattern extends Pattern 
{
	public MitosisPattern(RavensProblem problem) 
	{
		this.problem = problem;
	}

	@Override
	public boolean matchesPattern()
	{
		// Get Figures
		RavensFigure figA = problem.getFigures().get("A");
		RavensFigure figB = problem.getFigures().get("B");
		RavensFigure figC = problem.getFigures().get("C");
		RavensFigure figD = problem.getFigures().get("D");
		RavensFigure figE = problem.getFigures().get("E");
		RavensFigure figF = problem.getFigures().get("F");
		RavensFigure figG = problem.getFigures().get("G");
		RavensFigure figH = problem.getFigures().get("H");
		
		if( PatternMatchingUtilities.ExhibitsMitosis(figA, figB, figC) &&
			PatternMatchingUtilities.ExhibitsMitosis(figD, figE, figF) &&
			PatternMatchingUtilities.ExhibitsMitosis(figA, figD, figG) &&
			PatternMatchingUtilities.ExhibitsMitosis(figB, figE, figH) )
			return true;
		else	
			return false;
	}

	@Override
	public ArrayList<RavensSolution> evaluateSolutions() 
	{
		ArrayList<RavensSolution> solutions = new ArrayList<RavensSolution>();
		
		// Get Figures		
		RavensFigure figC = problem.getFigures().get("C");
		RavensFigure figF = problem.getFigures().get("F");
		RavensFigure figG = problem.getFigures().get("G");
		RavensFigure figH = problem.getFigures().get("H");
		
		for(int i=1; i<=NUM_SOLUTIONS; ++i)
		{
			RavensFigure testFigure = problem.getFigures().get(Integer.toString(i));
			
			if( PatternMatchingUtilities.ExhibitsMitosis(figC, figF, testFigure) &&
				PatternMatchingUtilities.ExhibitsMitosis(figG, figH, testFigure) )
				solutions.add(new RavensSolution(i, 1.0));
		}		
		return solutions;
	}

	@Override
	public String getPatternName() 
	{
		return "Mitosis Pattern";
	}

}

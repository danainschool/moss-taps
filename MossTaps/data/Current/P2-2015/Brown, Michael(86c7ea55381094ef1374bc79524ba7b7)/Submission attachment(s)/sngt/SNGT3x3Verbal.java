package ravensproject.sngt;

import java.util.HashMap;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.RavensSolution;
import ravensproject.RavensStrategy;

//Implementation of Ravens Strategy that will solve 3 x 3 problems by:
//1. Representing the knowledge as a semantic network
//2. Using Generate and Test methodology to calculate a best answer
public class SNGT3x3Verbal extends RavensStrategy 
{
	// Constants
	public final int NUM_SOLUTIONS = 8;
	
	public SNGT3x3Verbal(RavensProblem problem) 
	{
		this.problem = problem;
		this.solution = new RavensSolution();
		
		System.out.println("Agent has decided to use the Semantic Network / Generate and Test for 3x3 Verbal Problems Strategy.");		
	}

	// Returns True if the algorithm was able to determine a solution.
	public Boolean solve() 
	{
		double[] solutionConfidence = new double[NUM_SOLUTIONS];
		SeriesTransformation[] horizontalTransformations = new SeriesTransformation[NUM_SOLUTIONS];
		SeriesTransformation[] verticalTransformations = new SeriesTransformation[NUM_SOLUTIONS];
		
		// Get Figures
		RavensFigure figA = problem.getFigures().get("A");
		RavensFigure figB = problem.getFigures().get("B");
		RavensFigure figC = problem.getFigures().get("C");
		RavensFigure figD = problem.getFigures().get("D");
		RavensFigure figE = problem.getFigures().get("E");
		RavensFigure figF = problem.getFigures().get("F");
		RavensFigure figG = problem.getFigures().get("G");
		RavensFigure figH = problem.getFigures().get("H");
		RavensFigure fig1 = problem.getFigures().get("1");
		RavensFigure fig2 = problem.getFigures().get("2");
		RavensFigure fig3 = problem.getFigures().get("3");
		RavensFigure fig4 = problem.getFigures().get("4");
		RavensFigure fig5 = problem.getFigures().get("5");
		RavensFigure fig6 = problem.getFigures().get("6");
		RavensFigure fig7 = problem.getFigures().get("7");
		RavensFigure fig8 = problem.getFigures().get("8");
		
		// Determine nesting levels for objects in this problem
		HashMap<String, Integer> objectNesting = CorrespondenceUtilities.DetermineFigureNesting(problem);
		
		// Set up reference figure transformations
		SeriesTransformation refHorizontalTop = new SeriesTransformation(figA, figB, figC, HORIZONTAL, objectNesting);
		SeriesTransformation refHorizontalMiddle = new SeriesTransformation(figD, figE, figF, HORIZONTAL, objectNesting);
		
		SeriesTransformation refVerticalLeft = new SeriesTransformation(figA, figD, figG, VERTICAL, objectNesting);
		SeriesTransformation refVerticalMiddle = new SeriesTransformation(figB, figE, figH, VERTICAL, objectNesting);
		
		// Set up solution series transformations
		horizontalTransformations[0] = new SeriesTransformation(figG, figH, fig1, HORIZONTAL, objectNesting);
		verticalTransformations[0]   = new SeriesTransformation(figC, figF, fig1, VERTICAL, objectNesting);
		horizontalTransformations[1] = new SeriesTransformation(figG, figH, fig2, HORIZONTAL, objectNesting);
		verticalTransformations[1]   = new SeriesTransformation(figC, figF, fig2, VERTICAL, objectNesting);
		horizontalTransformations[2] = new SeriesTransformation(figG, figH, fig3, HORIZONTAL, objectNesting);
		verticalTransformations[2]   = new SeriesTransformation(figC, figF, fig3, VERTICAL, objectNesting);
		horizontalTransformations[3] = new SeriesTransformation(figG, figH, fig4, HORIZONTAL, objectNesting);
		verticalTransformations[3]   = new SeriesTransformation(figC, figF, fig4, VERTICAL, objectNesting);
		horizontalTransformations[4] = new SeriesTransformation(figG, figH, fig5, HORIZONTAL, objectNesting);
		verticalTransformations[4]   = new SeriesTransformation(figC, figF, fig5, VERTICAL, objectNesting);
		horizontalTransformations[5] = new SeriesTransformation(figG, figH, fig6, HORIZONTAL, objectNesting);
		verticalTransformations[5]   = new SeriesTransformation(figC, figF, fig6, VERTICAL, objectNesting);
		horizontalTransformations[6] = new SeriesTransformation(figG, figH, fig7, HORIZONTAL, objectNesting);
		verticalTransformations[6]   = new SeriesTransformation(figC, figF, fig7, VERTICAL, objectNesting);
		horizontalTransformations[7] = new SeriesTransformation(figG, figH, fig8, HORIZONTAL, objectNesting);
		verticalTransformations[7]   = new SeriesTransformation(figC, figF, fig8, VERTICAL, objectNesting);
		
		// Calculate the confidence in each solution
		for(int i=0; i<NUM_SOLUTIONS; ++i)
		{
			double totalConfidence = refHorizontalTop.CompareTo(refHorizontalMiddle, horizontalTransformations[i]) +
									 refVerticalLeft.CompareTo(refVerticalMiddle, verticalTransformations[i]);
			solutionConfidence[i] = totalConfidence / 2.0;
			System.out.println("Solution " + (i+1) + " has confidence: " + solutionConfidence[i]); //DEBUG			
		}
		
		// Determine the best answer 		
		solution.answer = 1;
		solution.confidence = solutionConfidence[0];
		
		for(int i=1; i<NUM_SOLUTIONS; ++i)
		{
			if(solutionConfidence[i] > solution.confidence)
			{
				solution.answer = i+1; //array index offset
				solution.confidence = solutionConfidence[i];
			}
			else if(solutionConfidence[i] == solution.confidence)
			{
				//TODO: Break the tie				
			}
		}				
		
		return true;
	}

}

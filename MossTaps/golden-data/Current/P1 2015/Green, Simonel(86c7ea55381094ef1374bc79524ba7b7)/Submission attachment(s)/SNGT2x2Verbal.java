package ravensproject;

import java.util.HashMap;

// Implementation of Ravens Strategy that will solve 2 x 2 problems by:
// 1. Representing the knowledge as a semantic network
// 2. Using Generate and Test methodology to calculate a best answer
public class SNGT2x2Verbal extends RavensStrategy 
{
	// Constants
	public final int NUM_SOLUTIONS = 6;	
	
	public SNGT2x2Verbal(RavensProblem problem)
	{
		this.problem = problem;
		this.solution = new RavensSolution();
		
		System.out.println("Agent has decided to use the Semantic Network / Generate and Test for 2x2 Verbal Problems Strategy.");		
	}
	
	// Returns True if the algorithm was able to determine a solution.
	public Boolean solve() 
	{		
		double[] solutionConfidence = new double[NUM_SOLUTIONS];
		FigureTransformation[] horizontalTransformations = new FigureTransformation[NUM_SOLUTIONS];
		FigureTransformation[] verticalTransformations = new FigureTransformation[NUM_SOLUTIONS];		
		
		// Get figure transformations
		RavensFigure figA = problem.getFigures().get("A");
		RavensFigure figB = problem.getFigures().get("B");
		RavensFigure figC = problem.getFigures().get("C");
		RavensFigure fig1 = problem.getFigures().get("1");
		RavensFigure fig2 = problem.getFigures().get("2");
		RavensFigure fig3 = problem.getFigures().get("3");
		RavensFigure fig4 = problem.getFigures().get("4");
		RavensFigure fig5 = problem.getFigures().get("5");
		RavensFigure fig6 = problem.getFigures().get("6");
		
		// Determine nesting levels for objects in this problem
		HashMap<String, Integer> objectNesting = CorrespondenceUtilities.DetermineFigureNesting(problem);
		
		// Set up reference figure transformations
		FigureTransformation refHorizontal = new FigureTransformation(figA, figB, HORIZONTAL, objectNesting);
		FigureTransformation refVertical = new FigureTransformation(figA, figC, VERTICAL, objectNesting);
		
		// Set up solution figure transformations
		horizontalTransformations[0] = new FigureTransformation(figC, fig1, HORIZONTAL, objectNesting);
		verticalTransformations[0]   = new FigureTransformation(figB, fig1, VERTICAL, objectNesting);
		horizontalTransformations[1] = new FigureTransformation(figC, fig2, HORIZONTAL, objectNesting);
		verticalTransformations[1]   = new FigureTransformation(figB, fig2, VERTICAL, objectNesting);
		horizontalTransformations[2] = new FigureTransformation(figC, fig3, HORIZONTAL, objectNesting);
		verticalTransformations[2]   = new FigureTransformation(figB, fig3, VERTICAL, objectNesting);
		horizontalTransformations[3] = new FigureTransformation(figC, fig4, HORIZONTAL, objectNesting);
		verticalTransformations[3]   = new FigureTransformation(figB, fig4, VERTICAL, objectNesting);
		horizontalTransformations[4] = new FigureTransformation(figC, fig5, HORIZONTAL, objectNesting);
		verticalTransformations[4]   = new FigureTransformation(figB, fig5, VERTICAL, objectNesting);
		horizontalTransformations[5] = new FigureTransformation(figC, fig6, HORIZONTAL, objectNesting);
		verticalTransformations[5]   = new FigureTransformation(figB, fig6, VERTICAL, objectNesting);
		
		// Calculate the confidence in each solution
		for(int i=0; i<NUM_SOLUTIONS; ++i)
		{
			double totalConfidence = refHorizontal.CompareTo(horizontalTransformations[i]) +
									 refVertical.CompareTo(verticalTransformations[i]);
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

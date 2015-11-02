package ravensproject;

import java.util.ArrayList;

import ravensproject.problemreduction.ProblemReductionUtilities;
import ravensproject.rpm.RPM3x3Verbal;
import ravensproject.sngt.SNGT2x2Verbal;
import ravensproject.sngt.SNGT3x3Verbal;


// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() 
    {
    	System.out.println("Agent is online.");
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) 
    {
    	System.out.println("Agent is trying to solve a " + problem.getProblemType() + " problem named " + problem.getName());
    	
    	ArrayList<RavensSolution> possibleSolutions = new ArrayList<RavensSolution>();
    
    	// Apply appropriate problem reduction functions
    	if(problem.hasVerbal() && ProblemReductionUtilities.MixedSquareRectangle(problem))
    		ProblemReductionUtilities.RewriteSquaresAsRectangles(problem);
    	if(problem.hasVerbal() && ProblemReductionUtilities.AboveLeftOf(problem))
    		ProblemReductionUtilities.AddBelowRightOfAttributes(problem);
    	if(problem.hasVerbal() && ProblemReductionUtilities.Overlaps(problem))
    		ProblemReductionUtilities.AddOverlapsAttributes(problem);
    	
    	// Apply appropriate strategies
    	if(problem.getProblemType().equals("2x2") && problem.hasVerbal())
    	{    	
    		SNGT2x2Verbal sngt = new SNGT2x2Verbal(problem);
	    	
	    	if(sngt.solve())
	    		possibleSolutions.add(sngt.getSolution());
    	}
    	
    	if(problem.getProblemType().equals("3x3") && problem.hasVerbal())
    	{
    		SNGT3x3Verbal sngt = new SNGT3x3Verbal(problem);    		
    		if(sngt.solve())
    			possibleSolutions.add(sngt.getSolution());
    		
    		RPM3x3Verbal rpm = new RPM3x3Verbal(problem);
    		if(rpm.solve())
    			possibleSolutions.add(rpm.getSolution());    		
    	}
    	
    	// If no answer was found, skip the problem
    	if(possibleSolutions.size() < 1)
    	{
    		System.out.println("Agent is unable to find an answer.");
            return -1;
    	}
    	else  	// Check all provided answers to determine the best
    	{
    		RavensSolution solution = null;
    		
    		for(RavensSolution RS : possibleSolutions)
    		{
    			if(solution == null || solution.confidence < RS.confidence)
    				solution = RS;
    		}
    		
    		double guessThreshold = 0;
    		
    		switch(problem.getProblemType())
    		{
	    		case "2x2" : guessThreshold = .14;	break;
	    		case "3x3" : guessThreshold = .000001;	break;
    		}
    		
    		// If the best answer is below the guessing threshold, skip the problem
    		if(solution.confidence < guessThreshold)
    		{
    			System.out.println("I did not find a good answer.");
    			return -1;
    		}
    		else
    		{
    			System.out.println("I found an answer.  I am " + solution.confidence*100 + "% confident it is: " + solution.answer);
    			return solution.answer;
    		}    		
    	}    	    	
    }
    
    
    // For debugging purposes.
    @SuppressWarnings("unused")
	private void AttributeDump(RavensProblem problem)
    {
    	for (RavensFigure RF : problem.getFigures().values())
    	{
    		System.out.println("Figure " + RF.getName() + ":");
    		
    		for (RavensObject RO : RF.getObjects().values())
    		{
    			System.out.println("Object " + RO.getName() + ":");
    			
    			for (String attribute : RO.getAttributes().keySet())
    				System.out.println(attribute + ":" + RO.getAttributes().get(attribute));
    		}
    	}
    }
}

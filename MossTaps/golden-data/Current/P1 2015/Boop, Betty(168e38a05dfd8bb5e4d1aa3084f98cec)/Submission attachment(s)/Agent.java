package ravensproject;

import java.util.Hashtable;

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
    public Agent() {
        
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
    public int Solve(RavensProblem problem) {    	
    	int answer = -1;
    	
    	if(problem.hasVerbal())
    	{
	    	if(problem.getProblemType().equalsIgnoreCase("2x2"))
	    	{
	    		answer = this.Solve2x2(problem);
	    	}
//	    	else
//	    	{
//	    		answer = this.Solve3x3(problem);
//	    	}
    	}
    	
        return answer;
    }
    
    public int Solve2x2(RavensProblem problem)
    {	
//    	if(problem.getName().equals("Basic Problem B-10"))
//    	{
//    		//System.out.println("");
//    	}
    	
    	RavensFigure figureA = problem.getFigures().get("A");
    	Frame frameA = Frame.FromFigure(figureA);
    	
    	RavensFigure figureB = problem.getFigures().get("B");
    	Frame frameB = Frame.FromFigure(figureB);
    	
    	// Build template transformation
    	Hashtable<String, Change> template = Transformation.BuildTransformation(frameA, frameB);
    	
    	RavensFigure figureC = problem.getFigures().get("C");
    	Frame frameC = Frame.FromFigure(figureC);    	
    	
    	int[] scores = new int[6];
    	for(int index = 1; index < 7; index++)
    	{    		
	    	RavensFigure figureX = problem.getFigures().get(Integer.toString(index));
	    	Frame frameX = Frame.FromFigure(figureX);    	
    	
	    	Hashtable<String, Change> cToXTransformation = Transformation.BuildTransformation(frameC, frameX);	    	
	    	
	    	int points = 0;
	    	for(String key : template.keySet())
	    	{
	    		Change templateChange = template.get(key);
	    		Change change = cToXTransformation.get(key);
	    		if(change != null)
	    		{	    		
		    		if(templateChange.equals(change))
		    		{
		    			points++;
		    		}
	    		}
	    	}    	
	    	
	    	scores[index-1] = points;
    	}
    	
    	// Get the high score
    	int highValue = -1;
    	int highIndex = 1;
    	for(int index = 0; index < 6; index++)
    	{ 
    		if(scores[index] > highValue)
    		{
    			highValue = scores[index];
    			highIndex = index + 1;
    		}
    	}    	
    	
    	// Check for ties
    	int ties = -1;
    	for(int index = 0; index < 6; index++)
    	{ 
    		if(scores[index] == highValue)
    		{
    			ties++;    			
    		}
    	} 
    	
    	if(ties > 0)
    	{
    		//String text = "Problem [" + problem.getName() + "] has a tie.";
    		//System.out.println(text);
    		//highIndex = -1;
    	}
    	
    	return highIndex;
    }
    
    public int Solve3x3(RavensProblem problem)
    {
    	return 1;
    }
}

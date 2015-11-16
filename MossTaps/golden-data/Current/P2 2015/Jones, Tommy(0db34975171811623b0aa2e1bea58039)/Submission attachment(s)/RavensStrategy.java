package ravensproject;


// Superclass for Strategies for solving Ravens Problems
public abstract class RavensStrategy 
{
	// Constants
	public static final String HORIZONTAL = "horizontal";
	public static final String VERTICAL = "vertical";
	
	// Private members
	protected RavensProblem problem;
	protected RavensSolution solution;
		
	// Abstract method for solving the problem.  This method will return true if a solution was found.
	public abstract Boolean solve();
	
	// Getters for private members.  These values will only be set by constructor and solve method.
	public RavensSolution getSolution()
	{
		return solution;
	}
	
	public RavensProblem getProblem()
	{
		return problem;
	}
	
}

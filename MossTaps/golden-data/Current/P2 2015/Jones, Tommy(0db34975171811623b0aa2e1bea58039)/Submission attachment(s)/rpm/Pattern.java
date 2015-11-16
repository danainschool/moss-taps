package ravensproject.rpm;

import java.util.ArrayList;

import ravensproject.RavensProblem;
import ravensproject.RavensSolution;

// Superclass for Patterns. Each pattern matching algorithm is encapsulated in a static class 
// that can detect a pattern match, and then evaluate solutions if needed.
public abstract class Pattern 
{
	protected RavensProblem problem;
	protected final int NUM_SOLUTIONS = 8;
	
	// Abstract Pattern Functions to be implemented by inheritors
	public abstract boolean matchesPattern();	
	public abstract ArrayList<RavensSolution> evaluateSolutions();	
	public abstract String getPatternName();
}

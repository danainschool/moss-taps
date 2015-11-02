package ravensproject;

// Simple Structure for solutions to ravens problems
public class RavensSolution 
{
	// Public Members
	public int answer;
	public double confidence;
	
	public RavensSolution()
	{
		// Default solution is no answer with 0 confidence
		answer = 0;
		confidence = 0;
	}
	
	public RavensSolution(int answer, double confidence)
	{
		this.answer = answer;
		this.confidence = confidence;
	}
}

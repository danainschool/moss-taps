package ravensproject;

//Simple structure supporting correspondence of figures.
//Stores the reference object transformation, and the object transformation that corresponds to it, as well as a comparison value between the two.
public class FigureCorrespondence implements Comparable<FigureCorrespondence>
{
	public ObjectTransformation referenceOT;
	public ObjectTransformation comparedOT;
	public double similarity;
	
	// Default constructor supports simple creation
	public FigureCorrespondence() {}
	
	// Additional constructor for setting all member values
	public FigureCorrespondence(ObjectTransformation referenceOT, ObjectTransformation comparedOT, double similarity)
	{
		this.referenceOT = referenceOT;
		this.comparedOT = comparedOT;
		this.similarity = similarity;
	}
	
	@Override
	public int compareTo(FigureCorrespondence ft) 
	{
		return Double.compare(this.similarity, ft.similarity);
	}
	
	@Override
	public String toString()
	{
		return this.referenceOT.getTransformations() + ":" + comparedOT.getTransformations() + " @ " + this.similarity;
	}
}

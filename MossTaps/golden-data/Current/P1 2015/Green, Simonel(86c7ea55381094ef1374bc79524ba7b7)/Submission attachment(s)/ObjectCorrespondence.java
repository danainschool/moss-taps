package ravensproject;

// Simple structure supporting correspondence of objects.
// Stores the reference object and the object that corresponds to it, as well as a similarity value between the two. 
public class ObjectCorrespondence implements Comparable<ObjectCorrespondence>
{
	public RavensObject referenceObject;
	public RavensObject correspondingObject;
	public int similarity;
	
	// Default constructor supports simple creation
	public ObjectCorrespondence() {}
	
	// Additional constructor for setting all member values
	public ObjectCorrespondence(RavensObject referenceObject, RavensObject correspondingObject, int similarity)
	{
		this.referenceObject = referenceObject;
		this.correspondingObject = correspondingObject;
		this.similarity = similarity;
	}

	@Override
	public int compareTo(ObjectCorrespondence o) 
	{
		return Integer.compare(this.similarity, o.similarity);
	}
	
	@Override
	public String toString()
	{
		return this.referenceObject.getName() + ":" + this.correspondingObject.getName() + " @ " + this.similarity;
	}
}

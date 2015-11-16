package ravensproject;

/**
 * @author linh
 *	Conceptually this is like an aggregate of all network edges from and to this pair of objects.
 *  It allows us to store the total similarity of 2 objects, which will then be spread over potentially several network edges.
 */
public class ObjectPairing implements Comparable<ObjectPairing> {
	RavensObject srcObj;
	RavensObject destObj;
	int similarity;

	public RavensObject getSrcObj() {
		return srcObj;
	}

	public void setSrcObj(RavensObject srcObj) {
		this.srcObj = srcObj;
	}

	public RavensObject getDestObj() {
		return destObj;
	}

	public void setDestObj(RavensObject destObj) {
		this.destObj = destObj;
	}

	public int getSimilarity() {
		return similarity;
	}

	public void setSimilarity(int similarity) {
		this.similarity = similarity;
	}

	public ObjectPairing(RavensObject srcObj, RavensObject destObj, int similarity) {
		super();
		this.srcObj = srcObj;
		this.destObj = destObj;
		this.similarity = similarity;
	}

	@Override
	public int compareTo(ObjectPairing op) {
		return ((Integer) similarity).compareTo(op.similarity);
	}
	
	public String toString() {
		return "Pairing: source: " + srcObj.getName() 
			+ ", destination: " + destObj.getName()
			+ ", score: " + similarity;
	}
}

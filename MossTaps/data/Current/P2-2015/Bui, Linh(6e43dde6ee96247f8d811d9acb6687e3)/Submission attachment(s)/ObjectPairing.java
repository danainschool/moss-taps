package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linh
 *	Conceptually this is like an aggregate of all network edges from and to this pair of objects.
 *  It allows us to store the total similarity of 2 objects, which will then be spread over potentially several network edges.
 */
public class ObjectPairing implements Comparable<ObjectPairing> {
	private ObjectWrapper srcObj;
	private ObjectWrapper destObj;
	private int similarityScore;
	private List<TransformationNetworkEdge> networkEdges;


	public static final int UNCHANGING = 5;
	public static final int REFLECTED = 4;
	public static final int FILLED = 4;
	public static final int ROTATED = 3;
	public static final int SCALED = 2;
	public static final int SHAPE_CHANGED = 0;
	
	public int getSimilarityScore() {
		return similarityScore;
	}

	public List<TransformationNetworkEdge> getSimilarities() {
		return networkEdges;
	}

//	public ObjectWrapper getSrcAttrs() {
//		return srcObj;
//	}

//	public void setSrcObj(ObjectWrapper srcObj) {
//		this.srcObj = srcObj;
//	}

//	public HashMap<String, String> getDestAttrs() {
//		return destObj;
//	}
//
//	public void setDestAttrs(HashMap<String, String> destAttrs) {
//		this.destObj = destAttrs;
//	}

	public ObjectPairing(ObjectWrapper srcObj, ObjectWrapper destObj) {
		super();
		this.srcObj = srcObj;
		this.destObj = destObj;
		this.findSimilarities();
	}

	private void findSimilarities() {
		// save similarities
		// compute score
		// list all the changes, then return the score for the lowest change
		// (effectively, the most upsetting change wins)
		List<Integer> scores = new ArrayList<Integer>();
		this.networkEdges = new ArrayList<>();

		AttributeComparator comparator = new AttributeComparator(srcObj.getAttributes(), destObj.getAttributes());

		if (comparator.areUnchanged()) {
			this.networkEdges.add(new TransformationNetworkEdge(srcObj, destObj, new Unchanging()));
			scores.add(UNCHANGING);
		}
		else { 
			if (comparator.areScaled()) {
				this.networkEdges.add(new TransformationNetworkEdge(
						srcObj, destObj, 
						new Scaling(srcObj.getAttributes().get("size"), destObj.getAttributes().get("size")))
						);
				scores.add(SCALED);
			}
			if (comparator.areHorizontallyScaled()) {
				this.networkEdges.add(new TransformationNetworkEdge(
						srcObj, destObj, 
						new HorizontalScaling(srcObj.getAttributes().get("width"), destObj.getAttributes().get("width")))
						);
				scores.add(SCALED);
			}
			if (comparator.areOppositeFill()) {
				this.networkEdges.add(new TransformationNetworkEdge(srcObj, destObj, new OppositeFill()));
				scores.add(FILLED);
			}
			if (comparator.areHorizontallyReflected()) {
				this.networkEdges.add(new TransformationNetworkEdge(srcObj, destObj, new HorizontalReflection()));
				scores.add(REFLECTED);
			}
			else if (comparator.areVerticallyReflected()) {
				this.networkEdges.add(new TransformationNetworkEdge(srcObj, destObj, new VerticalReflection()));
				scores.add(REFLECTED);			
			}
			else if (comparator.areRotated()) {
				this.networkEdges.add(
						new TransformationNetworkEdge(
								srcObj, destObj, 
								new Rotation(
										Utilities.getAngleDifference(
												srcObj.getAttributes(), destObj.getAttributes()
												)
										)
								)
						);
				scores.add(ROTATED);
			}
			else if (comparator.areChangedShapes()) {
				this.networkEdges.add(new TransformationNetworkEdge(srcObj, destObj, new ShapeChanging(destObj.getAttributes().get("shape"))));
				scores.add(SHAPE_CHANGED);
			} 
		}
		if (comparator.bothAttrsHaveSameOutboundRelationship()) {
			scores = scores.stream().map(score -> score + 2).collect(Collectors.toList());
		}
		if (scores.size() > 0)
			this.similarityScore = Collections.min(scores);
		else
			this.similarityScore = 0;
	}
	
	public List<TransformationNetworkEdge> getNetworkEdges() {
		return networkEdges;
	}

	public ObjectWrapper getSrcObj() {
		return srcObj;
	}

	public ObjectWrapper getDestObj() {
		return destObj;
	}

	@Override
	public int compareTo(ObjectPairing op) {
		return ((Integer) similarityScore).compareTo(op.similarityScore);
	}
	
	public String toString() {
		return "Pairing:\n\tsource: " + srcObj.toString() 
			+ "\n\tdestination: " + destObj.toString()
			+ "\n\tedges: " + networkEdges
			+ "\n\tscore: " + similarityScore;
	}
}

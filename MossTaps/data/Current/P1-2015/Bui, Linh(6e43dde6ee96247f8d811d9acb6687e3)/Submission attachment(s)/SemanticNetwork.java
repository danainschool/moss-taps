package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SemanticNetwork {
	ArrayList<NetworkEdge> edges = new ArrayList<>();
	
	
	public void addEdge(NetworkEdge edge) {
		edges.add(edge);
	}

	
	public List<NetworkEdge> getEdgesOfObject(RavensObject object) {
		return edges.stream().filter((NetworkEdge edge) -> edge.getSource().equals(object)).collect(Collectors.toList());
	}
	
	public String toString() {
		StringBuilder networkString = new StringBuilder();
		for (NetworkEdge networkEdge : edges) {
			networkString.append(networkEdge.toString() + "\n");
		}
		return networkString.toString();
	}

	public List<RelationshipNetworkEdge> getRelationshipEdgesOfObject(
			RavensObject object) {
		return getEdgesOfObject(object).stream().
				filter(
					(NetworkEdge edge) -> 
						edge instanceof RelationshipNetworkEdge
				).map(
					(NetworkEdge edge) -> 
						(RelationshipNetworkEdge) edge).collect(Collectors.toList());
	}

	public List<TransformationNetworkEdge> getTransformationEdgesOfObject(
			RavensObject object) {
		return getTransformationEdgesInList(getEdgesOfObject(object));
	}
	
	public List<TransformationNetworkEdge> getTransformationEdges() {
		return getTransformationEdgesInList(edges);
	}
	
	private List<TransformationNetworkEdge> getTransformationEdgesInList(
			List<NetworkEdge> edgesOfObject) {
		return edgesOfObject.stream().
		filter(
			(NetworkEdge edge) -> 
				edge instanceof TransformationNetworkEdge
		).map((NetworkEdge edge) -> 
				(TransformationNetworkEdge) edge).collect(Collectors.toList());
		
	}
	
	private Set<RavensObject> getSources() {
		return edges.stream().map(edge -> edge.getSource()).collect(Collectors.toSet());
	}
	
	private Set<RavensObject> getTransformationSources() {
		return getTransformationEdges().stream().map(edge -> edge.getSource()).collect(Collectors.toSet());
	}

	public int getSimilarityScore() {
		// if there are multiple edges out of an object, take the lowest one
//		List<TransformationNetworkEdge> transEdges = getTransformationEdgesInList(edges);
		int score = 0;
		for (RavensObject srcObj : getTransformationSources()) {
			List<Integer> srcEdgeScores = getTransformationEdgesOfObject(srcObj).stream().map(edge -> edge.getTransformation().getScore()).collect(Collectors.toList());
			Integer srcScore = Collections.min(srcEdgeScores);
			// penalize 1p per additional transformation, in case there are multiple transformations with the same score; 
			// otherwise reflect + fill would be the same score as just reflect, when the former should be lower scored
			srcScore -= (srcEdgeScores.size() - 1);
			score += srcScore;
		}
//		for (TransformationNetworkEdge edge : transEdges) {
//			score += Utilities.getAttrSimilarityScore(edge.getSource().getAttributes(), edge.getDestination().getAttributes());
//		}
		return score;
	}
}

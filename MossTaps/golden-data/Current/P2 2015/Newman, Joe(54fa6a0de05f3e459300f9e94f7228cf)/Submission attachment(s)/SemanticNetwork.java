package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SemanticNetwork {
	private ArrayList<NetworkEdge> edges = new ArrayList<>();
	
	
	public ArrayList<NetworkEdge> getEdges() {
		return edges;
	}

	public SemanticNetwork(SemanticNetwork modelNetwork) {
		for (NetworkEdge originalEdge : modelNetwork.edges) {
			edges.add(originalEdge.deepCopy());
		}
	}

	public SemanticNetwork() {
	}

	public void addEdge(NetworkEdge edge) {
		edges.add(edge);
	}

	
	public List<NetworkEdge> getEdgesOfObject(ObjectWrapper object) {
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
			ObjectWrapper object) {
		return getEdgesOfObject(object).stream().
				filter(
					(NetworkEdge edge) -> 
						edge instanceof RelationshipNetworkEdge
				).map(
					(NetworkEdge edge) -> 
						(RelationshipNetworkEdge) edge).collect(Collectors.toList());
	}

	public List<TransformationNetworkEdge> getTransformationEdgesOfObject(
			ObjectWrapper object) {
		return getTransformationEdgesInList(getEdgesOfObject(object));
	}
	
	public List<TransformationNetworkEdge> getTransformationEdges() {
		return getTransformationEdgesInList(edges);
	}
	
	public List<TransformationNetworkEdge> getTransformationEdgesInList(
			List<NetworkEdge> edgesOfObject) {
		return edgesOfObject.stream().
		filter(
			(NetworkEdge edge) -> 
				edge instanceof TransformationNetworkEdge
		).map((NetworkEdge edge) -> 
				(TransformationNetworkEdge) edge).collect(Collectors.toList());
		
	}

	public List<RelationshipNetworkEdge> getRelationshipEdges() {
		return edges.stream().
				filter(
						(NetworkEdge edge) -> 
						edge instanceof RelationshipNetworkEdge
						).map((NetworkEdge edge) -> 
						(RelationshipNetworkEdge) edge).collect(Collectors.toList());
		
	}
	
//	private Set<ObjectWrapper> getSources() {
//		return edges.stream().map(edge -> edge.getSource()).collect(Collectors.toSet());
//	}
	
	public Set<ObjectWrapper> getTransformationSources() {
		return getTransformationEdges().stream().map(edge -> edge.getSource()).collect(Collectors.toSet());
	}
	public Set<ObjectWrapper> getTransformationDests() {
		return getTransformationEdges().stream().map(edge -> edge.getDestination()).collect(Collectors.toSet());
	}

	public int getSimilarityScore() {
		// if there are multiple edges out of an object, take the lowest one
		int score = 0;
		for (ObjectWrapper srcObj : getTransformationSources()) {
			List<Integer> srcEdgeScores = getTransformationEdgesOfObject(srcObj).stream().map(edge -> edge.getTransformation().getScore()).collect(Collectors.toList());
			Integer srcScore = Collections.min(srcEdgeScores);
			// penalize 1p per additional transformation, in case there are multiple transformations with the same score; 
			// otherwise reflect + fill would be the same score as just reflect, when the former should be lower scored
			srcScore -= (srcEdgeScores.size() - 1);
			score += srcScore;
		}
		// penalize 1p per object added/deleted
		List<TransformationNetworkEdge> addedOrDeletedEdges = 
				getTransformationEdges().stream().
					filter(edge -> edge.getTransformation() instanceof ObjectAdded || edge.getTransformation() instanceof ObjectDeleted).
					collect(Collectors.toList());
		score = score - addedOrDeletedEdges.size();

		return score;// - positionsOff;
	}
	
	public List<NetworkEdge> findEdgesByDest(ObjectWrapper destination) {
		return getTransformationEdges().stream().filter(edge -> edge.getDestination().equals(destination)).collect(Collectors.toList());
	}
	
	public List<NetworkEdge> findEdgesBySrc(ObjectWrapper source) {
		return getTransformationEdges().stream().filter(edge -> edge.getSource().equals(source)).collect(Collectors.toList());
	}

	public List<RelationshipNetworkEdge> getEdgesBySrcDestAndRel(ObjectWrapper srcObj, ObjectWrapper destObj, String relName) {
		return getRelationshipEdgesOfObject(srcObj).stream().
				filter(edge -> edge.relationship.equals(relName) && edge.getDestination().equals(destObj)).
				collect(Collectors.toList());
	}
	
	public int getPositionDelta() {
		int positionsOff = getTransformationEdges().stream().
			map(
				edge -> Math.abs(edge.getDestination().getRow() - edge.getSource().getRow()) + 
						Math.abs(edge.getDestination().getColumn() - edge.getSource().getColumn())
			).mapToInt(Integer::intValue).sum();
		return positionsOff;
	}

	public List<RelationshipNetworkEdge> getRelEdgesBySrcAndRel(ObjectWrapper obj, String relName) {
		return getRelationshipEdgesOfObject(obj).stream().
				filter( edge -> edge.relationship.equals(relName) ).
				collect(Collectors.toList());
	}
	
	public ObjectWrapper getTransDest(ObjectWrapper src) {
		List<TransformationNetworkEdge> transformationEdgesOfObject = getTransformationEdgesOfObject(src);
		if (transformationEdgesOfObject.size() > 0) {
			return transformationEdgesOfObject.get(0).getDestination();
		} else {
			return null;
		}
	}

	public ObjectWrapper getTransSrc(ObjectWrapper destination) {
		List<TransformationNetworkEdge> transformationEdgesOfObject = 
			getTransformationEdges().stream().filter(edge -> edge.getDestination().equals(destination)).collect(Collectors.toList());
		if (transformationEdgesOfObject.size() > 0) {
			return transformationEdgesOfObject.get(0).getSource();
		} else {
			return null;
		}
	}
}

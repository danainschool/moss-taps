package ravensproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;








import ravensproject.RelationshipNetworkEdge.Relationship;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
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
	 * The primary method for solving incoming Raven's Progressive Matrices. For
	 * each problem, your Agent's Solve() method will be called. At the
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
	 * @param problem
	 *            the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public int Solve(RavensProblem problem) {
//		if (!problem.getName().equals("Basic Problem B-10")) {
//			return -1;
//		}
		
		if (!problem.hasVerbal()) {
			return -1;
		}
		System.out.println("Now solving " + problem.getName());
		SemanticNetwork abNetwork = new SemanticNetwork();
		SemanticNetwork acNetwork = new SemanticNetwork();

		RavensFigure a = problem.getFigures().get("A");
		RavensFigure b = problem.getFigures().get("B");
		RavensFigure c = problem.getFigures().get("C");
		// figure = each square/image
		// object = each part/shape in the image
		// attribute: of each part/shape

		buildNetworkFromFigure(abNetwork, a);
		buildNetworkFromFigure(abNetwork, b);
		buildNetworkFromFigure(acNetwork, a);
		buildNetworkFromFigure(acNetwork, c);

		//
		buildNetworkFromFigurePair(abNetwork, a, b);
		buildNetworkFromFigurePair(acNetwork, a, c);

		List<HashMap<String, String>> expectedObjects;

		if (abNetwork.getSimilarityScore() >= acNetwork.getSimilarityScore()) {
			// A and B are more similar than A and C, so C should be the seed
			// for the missing figure
			expectedObjects = generateExpectedObjects(abNetwork, a, c);
		} else {
			expectedObjects = generateExpectedObjects(acNetwork, a, b);
		}

		String choiceIndexes[] = { "1", "2", "3", "4", "5", "6" };

		// TODO handle when nothing matches and/or the other match (e.g. AC vs AB) has the same similarity
		for (String choiceIndex : choiceIndexes) {
			RavensFigure choiceFigure = problem.getFigures().get(choiceIndex);
			// or, compare the expected attribute hashes to the choice's
			// attribute hashes
			List<HashMap<String, String>> choiceAttrs = choiceFigure.getObjects().values().stream()
					.map(o -> o.getAttributes()).collect(Collectors.toList());
			if (Utilities.areEqualAttributeLists(expectedObjects, choiceAttrs)) {
				int givenAnswer = Integer.parseInt(choiceIndex);
				int correctAnswer = problem.checkAnswer(givenAnswer);
				String isCorrect = givenAnswer == correctAnswer ? "RIGHT" : "WRONG";
				System.out.println(isCorrect + ". Agent answer: " + givenAnswer + ". Correct answer: " + correctAnswer);
			}
		}
//		System.out.println("Skipping " + problem.getName() + " because no answers found.");
		return -1;
	}

	private List<HashMap<String, String>> generateExpectedObjects(SemanticNetwork network, RavensFigure sourceFigure,
			RavensFigure destFigure) {
		List<HashMap<String, String>> expectedObjects = new ArrayList<>();
		// TODO saving the list of pairings when generating networks so we don't have to do it again here 
		HashMap<RavensObject, RavensObject> relatedObjectPairs = Utilities.pairRelatedObjectsInFigures(
				sourceFigure.getObjects(), destFigure.getObjects());
		
		HashMap<RavensObject, RavensObject> relatedObjectPairsIndexedByDest = 
			(HashMap<RavensObject, RavensObject>) relatedObjectPairs.entrySet().stream().
				collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		
		// iterate through C's objects
		for (Entry<String, RavensObject> objToTransformEntry : destFigure.getObjects().entrySet()) {
			RavensObject destObjectToTransform = objToTransformEntry.getValue();
			RavensObject srcObj = relatedObjectPairsIndexedByDest.get(destObjectToTransform);
			HashMap<String, String> attrsToTransform = destObjectToTransform.getAttributes();
			if (srcObj != null) {
				// if there's a paired object in A, apply the transformations done to that object
				List<TransformationNetworkEdge> transformationEdges = network.getTransformationEdgesOfObject(srcObj);
				
				boolean dontAdd = false;
				for (TransformationNetworkEdge edgeFromCurrentObj : transformationEdges) {
					if (edgeFromCurrentObj.getTransformation() instanceof ObjectDeleted)
						// if it's deleted from A to C, delete it here too
						dontAdd = true;
					else
						attrsToTransform = edgeFromCurrentObj.getTransformation().apply(attrsToTransform);
				}
				if (!dontAdd)
					expectedObjects.add(attrsToTransform);
			} else {
				// if there isn't, just carry it over untransformed
				expectedObjects.add(attrsToTransform);
			}
		}
		// if any object was added from A to B, add it to the expected object unchanged
		List<TransformationNetworkEdge> addedEdges = network.getTransformationEdges().stream().
				filter(edge -> edge.getTransformation() instanceof ObjectAdded).collect(Collectors.toList());
		List<RavensObject> addedObjs = addedEdges.stream().map(edge -> edge.getSource()).collect(Collectors.toList());
		for (RavensObject addedObj : addedObjs) {
			expectedObjects.add(addedObj.getAttributes());
		}

		return expectedObjects;
	}

	private void buildNetworkFromFigurePair(SemanticNetwork network, RavensFigure source, RavensFigure dest) {
		HashMap<RavensObject, RavensObject> objPairings = Utilities.pairRelatedObjectsInFigures(source.getObjects(), dest.getObjects());

		for (Entry<RavensObject, RavensObject> entry : objPairings.entrySet()) {
			RavensObject sourceObject = entry.getKey();
			buildNetworkFromObjectPair(network, sourceObject, objPairings.get(sourceObject));
		}
		
		handleAddingAndDeleting(network, source, dest, objPairings);
	}

	private void handleAddingAndDeleting(SemanticNetwork network, RavensFigure source, RavensFigure dest, HashMap<RavensObject, RavensObject> pairedObjs) {
		// remove the sources (keys) of paired objects from the source figure. what remain are the deleted objects.  
		// remove the destinations (values) of paired objects from the destination figure. what remain are the added objects.  
		Collection<RavensObject> deletedSrcObjs = new HashSet<>(source.getObjects().values());
		Collection<RavensObject> addedDestObjs = new HashSet<>(dest.getObjects().values());
		Collection<RavensObject> pairedSrcObjs = pairedObjs.keySet();
		Collection<RavensObject> pairedDestObjs = pairedObjs.values();
		// if there are objects in source that aren't in destination, create an OBJECT_ADDED edge
		deletedSrcObjs.removeAll(pairedSrcObjs);
		addedDestObjs.removeAll(pairedDestObjs);
		
		for (RavensObject addedObj : addedDestObjs) {
			network.addEdge(new TransformationNetworkEdge(addedObj, addedObj, new ObjectAdded()));
		}
		// if there are objects in destination that aren't in source, create an OBJECT_DELETED edge 
		for (RavensObject deletedObj : deletedSrcObjs) {
			network.addEdge(new TransformationNetworkEdge(deletedObj, deletedObj, new ObjectDeleted()));
		}
	}

	private void buildNetworkFromObjectPair(SemanticNetwork network, RavensObject sourceObject, RavensObject destObject) {
		HashMap<String, String> srcAttributes = sourceObject.getAttributes();
		HashMap<String, String> destAttributes = destObject.getAttributes();
		if (Utilities.areUnchanged(srcAttributes, destAttributes)) {
			network.addEdge(new TransformationNetworkEdge(sourceObject, destObject, new Unchanging()));
		} else {
			if (Utilities.areOppositeFill(srcAttributes, destAttributes)) {
				network.addEdge(new TransformationNetworkEdge(sourceObject, destObject, new OppositeFill()));
			}
			if (Utilities.areHorizontallyReflected(srcAttributes, destAttributes)) {
				network.addEdge(new TransformationNetworkEdge(sourceObject, destObject, new HorizontalReflection()));
			}
			if (Utilities.areVerticallyReflected(srcAttributes, destAttributes)) {
				network.addEdge(new TransformationNetworkEdge(sourceObject, destObject, new VerticalReflection()));
			}
			if (Utilities.areScaled(srcAttributes, destAttributes)) {
				network.addEdge(new TransformationNetworkEdge(
					sourceObject, destObject, 
					new Scaling(srcAttributes.get("size"), destAttributes.get("size"))
				));
			}
		}
	}

	private void buildNetworkFromFigure(SemanticNetwork network, RavensFigure figure) {
		for (String aObjectName : figure.getObjects().keySet()) {
			RavensObject aObject = figure.getObjects().get(aObjectName);

			// if the object has a 'inside' or 'above' attribute, create a
			// network edge from the object
			// to the other object in the figure with that key
			List<String> relationships = Arrays
					.asList(new String[] { "inside", "above", "overlaps" });
			for (String attr : aObject.getAttributes().keySet()) {
				if (relationships.contains(attr)) {
					// if a is inside b,c,d,e, split up, then add 1 edge per destination 
					String destinations = aObject.getAttributes().get(attr);
					for (String destination : destinations.split(",")) {
						RavensObject destObject = figure.getObjects().get(destination);
						RelationshipNetworkEdge edge = null;
						switch (attr) {
						case "inside":
							edge = new RelationshipNetworkEdge(aObject, destObject, Relationship.INSIDE);
							break;
						case "above":
							new RelationshipNetworkEdge(aObject, destObject, Relationship.ABOVE);
							break;
						case "overlaps":
							new RelationshipNetworkEdge(aObject, destObject, Relationship.OVERLAPS);
							break;
						default:
							break;
						}
						if (edge != null)
							network.addEdge(edge);
					}
				}
			}
		}
	}
}

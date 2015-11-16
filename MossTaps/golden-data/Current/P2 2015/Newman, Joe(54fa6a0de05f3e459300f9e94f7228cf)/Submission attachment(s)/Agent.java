package ravensproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

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
	private final class ReverseComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return -o1.compareTo(o2);
		}
	}

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
//		if (!(problem.getName().equals("Basic Problem B-05") || problem.getName().equals("Basic Problem B-06"))) {
//		if (!problem.getName().equals("Basic Problem B-10")) {
//			return -1;
//		}
		// TODO fix wrong test problems
		
		if (!problem.hasVerbal()) {
			return -1;
		}
		System.out.print("Now solving " + problem.getName() + ".\t\t");
		Collection<ObjectWrapper> a;
		Collection<ObjectWrapper> b;
		Collection<ObjectWrapper> c;

		try {
			Collection<ObjectWrapper> expectedObjs = null;
			
			if (problem.getProblemType().equals("2x2")) {
				a = convertObjsInFigureToWrappers(problem.getFigures().get("A"));
				b = convertObjsInFigureToWrappers(problem.getFigures().get("B"));
				c = convertObjsInFigureToWrappers(problem.getFigures().get("C"));

				expectedObjs = solve2x2(a, b, c);
			} else {
				a = convertObjsInFigureToWrappers(problem.getFigures().get("G"));
				b = convertObjsInFigureToWrappers(problem.getFigures().get("H"));
				c = convertObjsInFigureToWrappers(problem.getFigures().get("H"));
				
				expectedObjs = solve3x3(a, b, c);
			}
			
			List<String> choiceIndexes;
			if (problem.getProblemType().equals("2x2")) {
				choiceIndexes = Arrays.asList("1", "2", "3", "4", "5", "6");
			} else {
				choiceIndexes = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
			}
			
			TreeMap<Integer, List<String>> choiceScores = new TreeMap<>(new ReverseComparator());
			HashMap<String, SemanticNetwork> choiceNetworks = new HashMap<>();
			
			for (String choiceIndex : choiceIndexes) {
				RavensFigure choiceFigure = problem.getFigures().get(choiceIndex);
				SemanticNetwork networkFromExpectedToChoice = buildNetwork(expectedObjs, convertObjsInFigureToWrappers(choiceFigure)); 
				choiceNetworks.put(choiceIndex, networkFromExpectedToChoice);
//				System.out.println("choice " + choiceIndex + ", network: " + networkFromExpectedToChoice);
				int choiceNetworkSimilarityScore = networkFromExpectedToChoice.getSimilarityScore();
				if (choiceScores.get(choiceNetworkSimilarityScore) == null) {
					choiceScores.put(choiceNetworkSimilarityScore, new ArrayList<>());
				}
				choiceScores.get(choiceNetworkSimilarityScore).add(choiceIndex);
			}

			String maxScoreChoiceIndex = "-1";

			// if more than 1 choice have the highest score, take positions into account
			List<String> highestScoreChoices = choiceScores.firstEntry().getValue();
			int currentLowestPositionDelta = 9999; 
			if (highestScoreChoices.size() > 1) {
				for (String choiceIndex : highestScoreChoices) {
					SemanticNetwork choiceNetwork = choiceNetworks.get(choiceIndex);
					int choiceNetworkDelta = choiceNetwork.getPositionDelta();
					if (choiceNetworkDelta < currentLowestPositionDelta) {
						currentLowestPositionDelta = choiceNetworkDelta; 
						maxScoreChoiceIndex = choiceIndex;
					}
				}
			} else {
				maxScoreChoiceIndex = highestScoreChoices.get(0); 
			}
			int givenAnswer = Integer.parseInt(maxScoreChoiceIndex);
			int correctAnswer = problem.checkAnswer(givenAnswer);
			String isCorrect = givenAnswer == correctAnswer ? "RIGHT" : "WRONG";
			System.out.println(isCorrect + ". Agent answer: " + givenAnswer + ". Correct answer: " + correctAnswer);
			return givenAnswer;
		} catch(Exception ex) {
			System.out.println("");
			System.out.println(ex);
			for (StackTraceElement stack : ex.getStackTrace()) {
				System.out.println(stack);
			}
		}
		System.out.println("Skipping " + problem.getName() + " because no answers found.");
		return -1;
	}

	private Collection<ObjectWrapper> solve3x3(Collection<ObjectWrapper> a, Collection<ObjectWrapper> b,
			Collection<ObjectWrapper> c) {
		SemanticNetwork abNetwork = buildNetwork(a, b);
		SemanticNetwork acNetwork = buildNetwork(a, c);
		
		return generateExpectedObjects(abNetwork, acNetwork, a, c);
	}

	private Collection<ObjectWrapper> solve2x2(Collection<ObjectWrapper> a, Collection<ObjectWrapper> b, Collection<ObjectWrapper> c) {
		// figure = each square/image
		// object = each part/shape in the image
		// attribute: of each part/shape
		
		SemanticNetwork abNetwork = buildNetwork(a, b);
		SemanticNetwork acNetwork = buildNetwork(a, c);
		
		Collection<ObjectWrapper> expectedObjs;
		
		// figure out whether to generate expected objects from B or C depending on which network contains more similar figures
		if (abNetwork.getSimilarityScore() >= acNetwork.getSimilarityScore()) {
			// A and B are more similar than A and C, so C should be the seed
			// for the missing figure
			expectedObjs = generateExpectedObjects(abNetwork, acNetwork, a, c);
		} else {
			expectedObjs = generateExpectedObjects(acNetwork, abNetwork, a, b);
		}
		return expectedObjs;
	}

	private SemanticNetwork buildNetwork(Collection<ObjectWrapper> srcObjs, Collection<ObjectWrapper> destObjs) {
		SemanticNetwork network = new SemanticNetwork();
		
		buildNetworkFromFigure(network, srcObjs);
		populatePositions(network, srcObjs);
		
		buildNetworkFromFigure(network, destObjs);
		populatePositions(network, destObjs);

		buildNetworkFromFigurePair(network, srcObjs, destObjs);
		
		return network;
	}

	@SuppressWarnings("unchecked")
	private Collection<ObjectWrapper> generateExpectedObjects(SemanticNetwork modelNetwork, SemanticNetwork correspondenceNetwork, 
			Collection<ObjectWrapper> a, Collection<ObjectWrapper> c) {
		// suppose A-B is the 'model' pair
		Collection<ObjectWrapper> expectedObjs = new ArrayList<>();
		for (ObjectWrapper objToTransform : c) {
			// find the corresponding object in A
			// the network should never have 2 edges from 2 different sources to the same destination, so `get(0)` is safe
			List<TransformationNetworkEdge> correspondingEdges = 
				correspondenceNetwork.getTransformationEdgesInList(correspondenceNetwork.findEdgesByDest(objToTransform));
			HashMap<String, String> expectedAttrs = (HashMap<String, String>) objToTransform.getAttributes().clone();
			boolean dontAdd = false;
			if (correspondingEdges.size() > 0) {
				ObjectWrapper correspondingObj = correspondingEdges.get(0).getSource();
				
				// find the transformation done to it
				List<TransformationNetworkEdge> edgesWithTransfToApply = modelNetwork.getTransformationEdgesOfObject(correspondingObj); 
				// apply the same transformation to objToTransform
				for (TransformationNetworkEdge edgeWithTransfToApply : edgesWithTransfToApply) {
					Transformation transformation = edgeWithTransfToApply.getTransformation();
					if (transformation instanceof ObjectDeleted) {
						dontAdd = true;
						break;
					}
					expectedAttrs = transformation.apply(expectedAttrs);
				}
			} 
			if (!dontAdd) {				
				// if no corresponding object found, just add as-is
				ObjectWrapper expectedObj = new ObjectWrapper(objToTransform);
				expectedObj.setAttributes(expectedAttrs);
				expectedObjs.add(expectedObj);
			}
		}
		
		// add any added object in the model network
		List<ObjectWrapper> addedObjs = modelNetwork.getTransformationEdges().stream().
				filter(edge -> edge.getTransformation() instanceof ObjectAdded).
				map(edge -> edge.getSource()).
				collect(Collectors.toList());
//		for (ObjectWrapper addedObj : addedObjs) {
//			// must be an added object whose relationship attributes are still pointing to their old parents
//			for (String relName : addedObj.getAttributes()) {
//				if (AttributeComparator.relationshipAttrs.contains(relName)) {
//					String origDests = addedObj.getAttributes().get(relName);
//					for (String dest : origDests.split(",")) {
//						// look in the model network first, if found, set attribute to corresponding objects
//						Optional<ObjectWrapper> origDest = modelNetwork.getTransformationDests().stream().filter(obj -> obj.getName().equals(dest)).findFirst();
//						if (origDest.isPresent()) {
//							modelNetwork.getTransDest(origDest.get());
//						}
//						// else, look in corr. network
//						
//					}
//					
//				}
//			}
//		}
		expectedObjs.addAll(addedObjs);
		
		// for each transformation from A to B, find the position delta between source and destination 
		for (ObjectWrapper srcObj : a) {
			List<TransformationNetworkEdge> transEdges = modelNetwork.getTransformationEdgesOfObject(srcObj);
			if (transEdges.size() > 0) {
				ObjectWrapper destObj = transEdges.get(0).getDestination();
				int rowDelta = destObj.getRow() - srcObj.getRow();
				int columnDelta = destObj.getColumn() - srcObj.getColumn();
				// apply the delta to the expected object generated from the object that corresponds to objToTransform 
				ObjectWrapper srcOfExpectedObj = correspondenceNetwork.getTransDest(srcObj);
				ObjectWrapper generatedObj = expectedObjs.stream().filter(object -> object.getName().equals(srcOfExpectedObj.getName())).findFirst().get();
				generatedObj.setRow(generatedObj.getRow() + rowDelta);
				generatedObj.setColumn(generatedObj.getColumn() + columnDelta);
			}
		}
		
		return expectedObjs;
	}

	private SemanticNetwork buildNetworkFromFigurePair(SemanticNetwork network, Collection<ObjectWrapper> srcObjs, Collection<ObjectWrapper> destObjs) {
		// this method should return a network with all edges filled in
		// to do that, while figuring out similarities, it needs to save all found similarities along with the total similarity score
		return buildNetworkFromObjLists(network, srcObjs, destObjs);
	}
	
	private SemanticNetwork buildNetworkFromObjLists(SemanticNetwork network, Collection<ObjectWrapper> srcObjList, Collection<ObjectWrapper> destObjList) {
		List<ObjectPairing> objPairings = Utilities.pairRelatedObjectsInFigures(network, srcObjList, destObjList);
		
		for (ObjectPairing pair : objPairings) {
			network.getEdges().addAll(pair.getNetworkEdges());
		}
		
		handleAddingAndDeleting(network, srcObjList, destObjList, objPairings);
		return network;
	}

	private Collection<ObjectWrapper> convertObjsInFigureToWrappers(RavensFigure source) {
		return source.getObjects().values().stream().map(object -> new ObjectWrapper(object)).collect(Collectors.toSet());
	}

	private void handleAddingAndDeleting(SemanticNetwork network, Collection<ObjectWrapper> srcObjList, Collection<ObjectWrapper> destObjList, List<ObjectPairing> objPairings) {
		// remove the sources (keys) of paired objects from the source figure. what remain are the deleted objects.  
		// remove the destinations (values) of paired objects from the destination figure. what remain are the added objects.  
		Collection<ObjectWrapper> deletedSrcObjs = new ArrayList<>(srcObjList);
		Collection<ObjectWrapper> addedDestObjs = new ArrayList<>(destObjList);
		Collection<ObjectWrapper> pairedSrcObjs = objPairings.stream().map(pair -> pair.getSrcObj()).collect(Collectors.toList());
		Collection<ObjectWrapper> pairedDestObjs = objPairings.stream().map(pair -> pair.getDestObj()).collect(Collectors.toList());
		// if there are objects in source that aren't in destination, create an OBJECT_ADDED edge
		deletedSrcObjs.removeAll(pairedSrcObjs);
		addedDestObjs.removeAll(pairedDestObjs);
		
		for (ObjectWrapper addedObj : addedDestObjs) {
			network.addEdge(new TransformationNetworkEdge(addedObj, addedObj, new ObjectAdded()));
		}
		// if there are objects in destination that aren't in source, create an OBJECT_DELETED edge 
		for (ObjectWrapper deletedObj : deletedSrcObjs) {
			network.addEdge(new TransformationNetworkEdge(deletedObj, deletedObj, new ObjectDeleted()));
		}
	}

	private void buildNetworkFromFigure(SemanticNetwork network, Collection<ObjectWrapper> objs) {
		// my end goal is to have a list of edges involving all objects in an island, in the order they're referred to, 
		// but not including non-adjacent edges. 
		// For example, if the edges are "a above b", "b above c", "a above c", I only want "a above b" and "b above c"
		// so, once I've figured out a is the starting object, since I have a list of objects ranked by connectedness, 
		// the next most connected object is b, so I search for an a-b edge and find it, so I add it to the 'linked list'
		// continue until the ranked list runs out

		for (ObjectWrapper obj : objs) {
			// if the object has a 'inside', 'above', etc attribute, create a
			// network edge from the object
			// to the other object in the figure with that key
			List<String> relAttrs = obj.getAttributes().keySet().stream().filter(attribute -> AttributeComparator.relationshipAttrs.contains(attribute)).collect(Collectors.toList());
			for (String relName : relAttrs) {
				// if a is inside b,c,d,e, split up, then add 1 edge per destination 
				String destinationsStr = obj.getAttributes().get(relName);
				String[] destinations = destinationsStr.split(",");
				for (String destination : destinations) {
					Optional<ObjectWrapper> destObjOptional = objs.stream().
						filter(object -> object.getName().equals(destination)).findFirst();
					ObjectWrapper destObject = destObjOptional.get();
					RelationshipNetworkEdge edge = new RelationshipNetworkEdge(obj, destObject, relName);
					network.addEdge(edge);
				}
			}
		}
	}

	private void populatePositions(SemanticNetwork network, Collection<ObjectWrapper> objs) {
		HashMap<String, TreeMap<Integer, ObjectWrapper>> numOfOutboundDests = new HashMap<>(); 

		for (ObjectWrapper obj : objs) {
			List<String> relAttrs = obj.getAttributes().keySet().stream().filter(attribute -> AttributeComparator.relationshipAttrs.contains(attribute)).collect(Collectors.toList());
			for (String relName : relAttrs) {
				if (numOfOutboundDests.get(relName) == null) {
					numOfOutboundDests.put(relName, new TreeMap<>(new ReverseComparator()));
				}
				// if a is inside b,c,d,e, split up, then add 1 edge per destination 
				String destinationsStr = obj.getAttributes().get(relName);
				String[] destinations = destinationsStr.split(",");
				numOfOutboundDests.get(relName).put(destinations.length, obj);
			}
		}
		// populate each object's row and column positions
		// for each relationship attribute found, find the starting object (e.g. for the 'above' attribute, the top object in 3 stacked objects)
		// which is the object with the most destinations
		// disclaimer: will fail if there are multiple 'islands' in the figure, e.g. a above b, c above d but no link between a/b and c/d. 
		// It'll never get to any island after the first one. 
		// compile a list of edges
		for (String relName : numOfOutboundDests.keySet()) {
			List<RelationshipNetworkEdge> linkedEdges = new LinkedList<>();
			TreeMap<Integer, ObjectWrapper> objsSortedByDestCount = numOfOutboundDests.get(relName);
			Entry<Integer, ObjectWrapper> startingObjEntry = objsSortedByDestCount.firstEntry();
			ObjectWrapper startingObj = startingObjEntry.getValue();
			// there are more than 1 object with this relationship, so we have a nested relationship on our hands
			// in which case we look at them 2 at a time to make the linked list of edges
			if (objsSortedByDestCount.size() > 1) {
				for (int i = 0; i < objsSortedByDestCount.size() - 1; i++) {
					Entry<Integer, ObjectWrapper> nextEntry = objsSortedByDestCount.higherEntry(startingObjEntry.getKey());
					ObjectWrapper nextObj = nextEntry.getValue();
					// find a relationship edge from the starting object to the next object in the list, having the current relationship
					List<RelationshipNetworkEdge> forwardEdges = network.getEdgesBySrcDestAndRel(startingObj, nextObj, relName);
					if (forwardEdges.size() > 0) {					
						linkedEdges.add(forwardEdges.get(0));
					} else {
						// chain is broken for some reason, probably shouldn't happen
						break;
					}
					startingObjEntry = nextEntry;
					startingObj = startingObjEntry.getValue();
				}
			}
			linkedEdges.add(
				network.getRelEdgesBySrcAndRel(startingObj, relName).get(0)
			);
			// then walk the edges 
			for (RelationshipNetworkEdge edge : linkedEdges) {
				ObjectWrapper srcObj = edge.getSource();
				ObjectWrapper destObj = edge.getDestination();
				
				switch (relName) {
				case RelationshipNetworkEdge.ABOVE:
					if (srcObj.getRow() == ObjectWrapper.INVALID_POSITION && destObj.getRow() == ObjectWrapper.INVALID_POSITION) {
						srcObj.setRow(0);
						destObj.setRow(1);
					} else if (srcObj.getRow() == ObjectWrapper.INVALID_POSITION) {
						srcObj.setRow(destObj.getRow() - 1);
					} else if (destObj.getRow() == ObjectWrapper.INVALID_POSITION)
						destObj.setRow(srcObj.getRow() + 1);
					break;
				case RelationshipNetworkEdge.LEFT_OF:
					if (srcObj.getColumn() == ObjectWrapper.INVALID_POSITION && destObj.getColumn() == ObjectWrapper.INVALID_POSITION) {
						srcObj.setColumn(0);
						destObj.setColumn(1);
					} else if (srcObj.getColumn() == ObjectWrapper.INVALID_POSITION) {
						srcObj.setColumn(destObj.getColumn() - 1);
					} else if (destObj.getColumn() == ObjectWrapper.INVALID_POSITION) {
						destObj.setColumn(srcObj.getColumn() + 1);
					}
					break;
				case RelationshipNetworkEdge.OVERLAPS:
					if (srcObj.getColumn() == ObjectWrapper.INVALID_POSITION && destObj.getColumn() == ObjectWrapper.INVALID_POSITION &&
						srcObj.getRow() == ObjectWrapper.INVALID_POSITION && destObj.getRow() == ObjectWrapper.INVALID_POSITION) {
						srcObj.setRow(0);
						srcObj.setColumn(0);
						destObj.setRow(0);
						destObj.setColumn(0);
					} 
					else {
						if (srcObj.getRow() == ObjectWrapper.INVALID_POSITION && destObj.getRow() != ObjectWrapper.INVALID_POSITION) {
							srcObj.setRow(destObj.getRow());
						} 
						if (srcObj.getRow() != ObjectWrapper.INVALID_POSITION && destObj.getRow() == ObjectWrapper.INVALID_POSITION) {
							destObj.setRow(srcObj.getRow());
						}
						if (srcObj.getColumn() == ObjectWrapper.INVALID_POSITION && destObj.getColumn() != ObjectWrapper.INVALID_POSITION) {
							srcObj.setColumn(destObj.getColumn());
						} 
						if (srcObj.getColumn() != ObjectWrapper.INVALID_POSITION && destObj.getColumn() == ObjectWrapper.INVALID_POSITION) {
							destObj.setColumn(srcObj.getColumn());
						}
					}
					break;
				default:
					break;
				}
			}
		}
		
		for (ObjectWrapper obj : objs) {
			if (obj.getRow() == ObjectWrapper.INVALID_POSITION) {
				obj.setRow(0);
			}
			if (obj.getColumn() == ObjectWrapper.INVALID_POSITION) {
				obj.setColumn(0);
			}
		}
	}
}

package ravensproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Utilities {
	// static boolean isCorresponding(HashMap<String, String> srcAttributes,
	// HashMap<String, String> destAttributes) {
	// return isUnchanged(srcAttributes, destAttributes)
	// || isHorizontallyReflected(srcAttributes, destAttributes)
	// || isRotated(srcAttributes, destAttributes);
	// }

	// @SuppressWarnings("unchecked")
	// static boolean isRotated(HashMap<String, String> srcAttributes,
	// HashMap<String, String> destAttributes) {
	// if (srcAttributes.get("angle") != null
	// && destAttributes.get("angle") != null
	// && !isHorizontallyReflected(srcAttributes, destAttributes)) {
	// // if all attributes are the same, except angle
	// // check by cloning attributes without angles and checking they're
	// // unchanged
	// HashMap<String, String> srcAttrCopy = (HashMap<String, String>)
	// srcAttributes
	// .clone();
	// srcAttrCopy.remove("angle");
	// HashMap<String, String> destAttrCopy = (HashMap<String, String>)
	// destAttributes
	// .clone();
	// destAttrCopy.remove("angle");
	// return isUnchanged(srcAttrCopy, destAttrCopy);
	// }
	// return false;
	// }

	static boolean areEqualAttributeLists(List<HashMap<String, String>> list1, List<HashMap<String, String>> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		list1Loop: for (HashMap<String, String> list1map : list1) {
			for (HashMap<String, String> list2map : list2) {
				// if the pair match, check the next list1 entry
				// if not, check the next list2 entry
				// if list2 loop ends without having been broken,
				// there's been a list1 entry that didn't match any list2 entry,
				// so return false.
				if (areUnchanged(list1map, list2map)) {
					continue list1Loop;
				}
			}
			return false;
		}
		return true;
	}

	static boolean areEqualAttributeHashes(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs,
			String ignoredAttributes[]) {
		boolean unchanged = true;
		// if all attributes match, relationship = 'unchanged'
		for (String attr : sourceAttrs.keySet()) {
			// ignore attributes that refer to other objects, because they don't
			// help with determining transformations
			if (attr.equals("inside") || attr.equals("above") || Arrays.asList(ignoredAttributes).contains(attr)) {
				continue;
			} else if (attr.equals("angle") && destAttrs.get("angle") != null) {
				// all attributes have to match, except angles, which have to be
				// equal mod 360
				unchanged = getAngleDifference(sourceAttrs, destAttrs) % 360 == 0;
			} else if (!sourceAttrs.get(attr).equals(destAttrs.get(attr))) {
				unchanged = false;
			}
		}
		return unchanged;

	}

	static int getAngleDifference(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		int sourceAngle = Integer.parseInt(sourceAttrs.get("angle"));
		int destAngle = Integer.parseInt(destAttrs.get("angle"));

		return destAngle - sourceAngle;
	}

	static int getAngleSum(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		int sourceAngle = Integer.parseInt(sourceAttrs.get("angle"));
		int destAngle = Integer.parseInt(destAttrs.get("angle"));

		return destAngle + sourceAngle;
	}

	static HashMap<String, String> generateReflectedAttributes(HashMap<String, String> sourceAttrs) {
		switch (sourceAttrs.get("angle")) {
		case "pac-man":
			sourceAttrs.put("angle", advanceAngleStr(sourceAttrs.get("angle"), 270));
			break;
		// for a right triangle, right angle in bottom left = 0º; angles are
		// calculated clockwise
		case "right triangle":
			sourceAttrs.put("angle", advanceAngleStr(sourceAttrs.get("angle"), 90));
			break;
		default:
			break;
		}
		return sourceAttrs;
	}

	static String advanceAngleStr(String angleString, int addend) {
		int angle = Integer.parseInt(angleString);
		return new Integer(angle + addend).toString();
	}

	static List<String> shapesUnchangedByAngle = Arrays
			.asList(new String[] { "circle", "square", "diamond", "octagon" });

	static boolean areUnchanged(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		// some shapes are the same no matter the angle
		if (shapesUnchangedByAngle.contains(sourceAttrs.get("shape"))) {
			return areEqualAttributeHashes(sourceAttrs, destAttrs, new String[] { "angle" });
		} else {
			return areEqualAttributeHashes(sourceAttrs, destAttrs, new String[] {});
		}
	}

	static boolean areHorizontallyReflected(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		if (sourceAttrs.get("angle") == null || destAttrs.get("angle") == null
				|| !sourceAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		// TODO handle alignment
		String shape = sourceAttrs.get("shape");
		if (shape.equals("right triangle")) {
			return getAngleSum(destAttrs, sourceAttrs) == 270;
		} else if (shape.equals("pac-man")) {
			int angle = new Integer(sourceAttrs.get("angle"));
			if (angle <= 180) {
				return getAngleSum(destAttrs, sourceAttrs) == 180;
			} else {
				return getAngleSum(destAttrs, sourceAttrs) == 540;
			}
		} else if (shape.equals("circle") || shape.equals("square")) {
			return true;
		} else {
			return getAngleDifference(destAttrs, sourceAttrs) % 180 == 0;
		}
	}

	static boolean areVerticallyReflected(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		if (sourceAttrs.get("angle") == null || destAttrs.get("angle") == null
				|| !sourceAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}

		String srcAlignment = sourceAttrs.get("alignment");
		String destAlignment = destAttrs.get("alignment");
		// only way to pass is for one of these 4 conditions to apply
		if (srcAlignment != null
				&& destAlignment != null
				&& (!(srcAlignment.equals("top-left") && destAlignment.equals("bottom-left"))
						&& !(srcAlignment.equals("bottom-left") && destAlignment.equals("top-left"))
						&& !(srcAlignment.equals("top-right") && destAlignment.equals("bottom-right")) && !(srcAlignment
						.equals("bottom-right") && destAlignment.equals("top-right")))) {
			return false;
		}

		String shape = sourceAttrs.get("shape");
		if (shape.equals("right triangle")) {
			return getAngleSum(destAttrs, sourceAttrs) % 360 == 90;
		} else if (shape.equals("pac-man")) {
			int angle = new Integer(sourceAttrs.get("angle"));
			return getAngleSum(destAttrs, sourceAttrs) % 360 == 0;
		} else if (shape.equals("circle") || shape.equals("square")) {
			return true;
		} else {
			return getAngleDifference(destAttrs, sourceAttrs) % 180 == 0;
		}
		// }
		// return false;
	}

	public static boolean areOppositeFill(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		String sourceFill = sourceAttrs.get("fill");
		if (sourceFill == null || destAttrs.get("fill") == null
				|| !sourceAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		return !sourceFill.equals(destAttrs.get("fill"));
	}

	private static boolean isRotated(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		return sourceAttrs.get("angle") != null && destAttrs.get("angle") != null
				&& sourceAttrs.get("shape").equals(destAttrs.get("shape"))
				&& !sourceAttrs.get("angle").equals(destAttrs.get("angle"));
	}

	public static boolean areScaled(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		String srcSize = sourceAttrs.get("size");
		String destSize = destAttrs.get("size");
		if (srcSize == null || destSize == null || !sourceAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		return !srcSize.equals(destSize);
	}

	// public static List<RavensObject> getSameShapeObjectsInFigure(
	// RavensObject srcObj, RavensFigure destFigure) {
	// List<RavensObject> foundObjectList = new ArrayList<RavensObject>();
	// for (Entry<String, RavensObject> destObjectEntry : destFigure
	// .getObjects().entrySet()) {
	// RavensObject destObj = destObjectEntry.getValue();
	// if (isCorresponding(srcObj.getAttributes(), destObj.getAttributes())) {
	// foundObjectList.add(destObj);
	// }
	// }
	// return foundObjectList;
	// }

//	public static List<RavensObject> getSameNetworkPositionObjects(RavensObject srcObject, RavensFigure destFigure,
//			SemanticNetwork sampleNetwork, SemanticNetwork cNetwork) {
//		List<RavensObject> sameNetworkPositionObjects = new ArrayList<>();
//
//		if (sampleNetwork.getRelationshipEdgesOfObject(srcObject).size() > 0) {
//			for (Entry<String, RavensObject> destObjectEntry : destFigure.getObjects().entrySet()) {
//				RavensObject destObject = destObjectEntry.getValue();
//				if (isSameNetworkPosition(srcObject, destObject, sampleNetwork, cNetwork)) {
//					sameNetworkPositionObjects.add(destObject);
//				}
//			}
//		}
//		return sameNetworkPositionObjects;
//	}

	// help determine if, say, given a is above b, b is inside c and d is above
	// e, e is inside f, whether eg a and d are equivalent (true)
//	public static boolean isSameNetworkPosition(RavensObject obj1, RavensObject obj2, SemanticNetwork sampleNetwork,
//			SemanticNetwork cNetwork) {
//		// so when src is a, destination is d, follow the relationship edges for
//		// both at the same time
//		// until one has more edges and the other doesn't, then return false.
//		// recursive function: given a src and a destination object, if they
//		// both have 1 relationship edge going out of them
//		// that are the same type, rerun with the destination objects; if
//		// neither does; return true; else return false
//
//		// assuming each object only has 1 edge going out of it (TODO support
//		// multiple outbound edges)
//		List<RelationshipNetworkEdge> obj1OutboundEdges = sampleNetwork.getRelationshipEdgesOfObject(obj1);
//		List<RelationshipNetworkEdge> obj2OutboundEdges = cNetwork.getRelationshipEdgesOfObject(obj2);
//		List<RelationshipNetworkEdge> obj1DestOutboundEdges = sampleNetwork
//				.getRelationshipEdgesOfObject(obj1OutboundEdges.get(0).getDestination());
//		List<RelationshipNetworkEdge> obj2DestOutboundEdges = cNetwork.getRelationshipEdgesOfObject(obj2OutboundEdges
//				.get(0).getDestination());
//		if (obj1DestOutboundEdges.size() == 0 && obj2DestOutboundEdges.size() == 0) {
//			return true;
//		} else if (obj1DestOutboundEdges.get(0).relationship == obj2DestOutboundEdges.get(0).relationship) {
//			return isSameNetworkPosition(obj1DestOutboundEdges.get(0).getDestination(), obj2DestOutboundEdges.get(0)
//					.getDestination(), sampleNetwork, cNetwork);
//		} else {
//			return false;
//		}
//	}

	public static final int UNCHANGING = 5;
	public static final int REFLECTED_OR_FILLED = 4;
	public static final int ROTATED = 3;
	public static final int SCALED = 2;
	public static final int SHAPE_CHANGED = 0;

	@SuppressWarnings("unchecked")
	public static HashMap<RavensObject, RavensObject> pairRelatedObjectsInFigures(
			HashMap<String, RavensObject> sourceObjs, HashMap<String, RavensObject> destObjs) {
		// for each object in source figure, compute similarity score with all objects in destination figure
		// do this for all pairs
		// sort pairs in descending order of similarity
		// form network edges until running out of objects

		// clone object maps so I can delete from them
		sourceObjs = (HashMap<String, RavensObject>) sourceObjs.clone();
		destObjs = (HashMap<String, RavensObject>) destObjs.clone();

		HashMap<RavensObject, RavensObject> relatedObjectPairs = new HashMap<>();

		List<ObjectPairing> pairings = new ArrayList<>();
		for (Entry<String, RavensObject> srcObjEntry : sourceObjs.entrySet()) {
			RavensObject srcObj = srcObjEntry.getValue();
			// I want a data structure to keep all potential pairings and their scores in
			// such that I can sort by the score
			for (Entry<String, RavensObject> destObjEntry : destObjs.entrySet()) {
				RavensObject destObj = destObjEntry.getValue();
				HashMap<String, String> sourceAttrs = srcObj.getAttributes();
				HashMap<String, String> destAttrs = destObj.getAttributes();

				int similarityScore = getAttrSimilarityScore(sourceAttrs, destAttrs);
				pairings.add(new ObjectPairing(srcObj, destObj, similarityScore));
			}
		}
		Collections.sort(pairings);
		for (int i = pairings.size() - 1; i >= 0; i--) {
			if (!sourceObjs.isEmpty() && !destObjs.isEmpty()) {
				ObjectPairing pairing = pairings.get(i);
				RavensObject srcObj = pairing.getSrcObj();
				RavensObject destObj = pairing.getDestObj();
				// only add a pairing if the source and destination haven't already been paired
				if (sourceObjs.containsKey(srcObj.getName()) && destObjs.containsKey(destObj.getName())) {
					relatedObjectPairs.put(srcObj, destObj);
					sourceObjs.remove(srcObj.getName());
					destObjs.remove(destObj.getName());
				}
			}
		}

		return relatedObjectPairs;
	}

	static int getAttrSimilarityScore(HashMap<String, String> sourceAttrs, HashMap<String, String> destAttrs) {
		// TODO refactor duplicated logic in Agent.buildNetworkFromObjectPair
		// list all the changes, then return the score for the lowest change
		// (effectively, the most upsetting change wins)
		List<Integer> scores = new ArrayList<Integer>();
		if (sourceAttrs.get("shape") != null && destAttrs.get("shape") != null
				&& !sourceAttrs.get("shape").equals(destAttrs.get("shape"))) {
			scores.add(SHAPE_CHANGED);
		} else if (sourceAttrs.get("size") != null && destAttrs.get("size") != null
				&& !sourceAttrs.get("size").equals(destAttrs.get("size"))) {
			scores.add(SCALED);
		} else if (isRotated(sourceAttrs, destAttrs) && !areHorizontallyReflected(sourceAttrs, destAttrs)) {
			scores.add(ROTATED);
		} else if (sourceAttrs.get("fill") != null && destAttrs.get("fill") != null
				&& !sourceAttrs.get("fill").equals(destAttrs.get("fill"))
				|| areHorizontallyReflected(sourceAttrs, destAttrs)) {
			scores.add(REFLECTED_OR_FILLED);
		} else if (areUnchanged(sourceAttrs, destAttrs)) {
			scores.add(UNCHANGING);
		}
		if (bothAttrsHaveSameOutboundRelationship(sourceAttrs, destAttrs)) {
			scores = scores.stream().map(score -> score + 3).collect(Collectors.toList());
		}
		return Collections.min(scores);
	}

	private static boolean bothAttrsHaveSameOutboundRelationship(HashMap<String, String> sourceAttrs,
			HashMap<String, String> destAttrs) {
		return sourceAttrs.containsKey("inside") && destAttrs.containsKey("inside") || sourceAttrs.containsKey("above")
				&& destAttrs.containsKey("above");
	}

}

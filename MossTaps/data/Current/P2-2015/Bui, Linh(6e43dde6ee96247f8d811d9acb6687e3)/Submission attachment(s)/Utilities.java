package ravensproject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Utilities {
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


	public static List<ObjectPairing> pairRelatedObjectsInFigures(
			SemanticNetwork network, Collection<ObjectWrapper> sourceObjs, Collection<ObjectWrapper> destObjs) {
		// for each object in source figure, compute similarity score with all objects in destination figure
		// do this for all pairs
		// sort pairs in descending order of similarity
		// form network edges until running out of objects

		// clone object collections so I can delete from them
		List<ObjectWrapper> sourceObjsCopy = new ArrayList<>();
		sourceObjsCopy.addAll(sourceObjs);
		List<ObjectWrapper> destObjsCopy = new ArrayList<>();
		destObjsCopy.addAll(destObjs);

		List<ObjectPairing> relatedObjectPairs = new ArrayList<>();

		List<ObjectPairing> potentialPairs = new ArrayList<>();
		for (ObjectWrapper srcObj : sourceObjs) {
			for (ObjectWrapper destObj : destObjs) {
				potentialPairs.add(new ObjectPairing(srcObj, destObj));
			}
		}
		
		Collections.sort(potentialPairs);
		for (int i = potentialPairs.size() - 1; i >= 0; i--) {
			if (!sourceObjsCopy.isEmpty() && !destObjsCopy.isEmpty()) {
				ObjectPairing pairing = potentialPairs.get(i);
				ObjectWrapper sortedSrcObj = pairing.getSrcObj();
				ObjectWrapper sortedDestObj = pairing.getDestObj();
				// only add a pairing if the source and destination haven't already been paired
				if (sourceObjsCopy.contains(sortedSrcObj) && destObjsCopy.contains(sortedDestObj)) {
					relatedObjectPairs.add(pairing);
					sourceObjsCopy.remove(sortedSrcObj);
					destObjsCopy.remove(sortedDestObj);
				}
			}
		}

		return relatedObjectPairs;
	}
}

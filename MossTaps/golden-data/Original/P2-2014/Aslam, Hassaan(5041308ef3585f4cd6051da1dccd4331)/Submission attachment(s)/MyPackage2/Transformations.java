package ravensproject.MyPackage2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;

public class Transformations {

	static List<String> ATTRIB_VALUES_SIZE = Arrays.asList("very small", "small", "medium", "large", "very large",
			"huge");

	static ArrayList getAttributeTransformations(RavensFigure figA, RavensFigure figB) {
		ArrayList transformations = new ArrayList();

		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();

		SortedSet<String> keysA = new TreeSet<String>(entriesA.keySet());
		SortedSet<String> keysB = new TreeSet<String>(entriesB.keySet());

		Iterator iteratorKeysA = keysA.iterator();
		Iterator iteratorKeysB = keysB.iterator();

		String isLastKeyUsedStillValid = null;

		while (iteratorKeysA.hasNext()) {
			HashMap<String, String> attributeDiff = new HashMap<>();

			String keyA = (String) iteratorKeysA.next();
			HashMap attributesObjectA = entriesA.get(keyA).getAttributes();

			if (!iteratorKeysB.hasNext()) {
				// object is absent in B
				if (isLastKeyUsedStillValid == null)
					continue;
			}

			String keyB;
			if (isLastKeyUsedStillValid == null)
				keyB = (String) iteratorKeysB.next();
			else {
				keyB = isLastKeyUsedStillValid;
				isLastKeyUsedStillValid = null;
			}

			HashMap attributesObjectB = entriesB.get(keyB).getAttributes();

			for (Object key : attributesObjectA.keySet()) {
				Object valueA = attributesObjectA.get((String) key);

				if (!attributesObjectB.containsKey(key)) {
					attributeDiff.put((String) key, "absent");
					continue;
				}
				Object valueB = attributesObjectB.get(key);

				if (valueA.equals(valueB)) {
					attributeDiff.put((String) key, valueA + "->same");
					continue;
				}
				if ((keysA.size() != keysB.size()) && key.equals("size") && !valueA.equals(valueB)) {
					/*
					 * if number of objects in figA are not equal to number of
					 * objects in figB AND current key in figA is "size" AND key
					 * value in figA is not matching figB then there is a chance
					 * that object in figA is missing in figB
					 */
					attributeDiff = null;
					isLastKeyUsedStillValid = keyB;
					break;
				}

				attributeDiff.put((String) key, valueA + "->" + valueB);
			}

			transformations.add(attributeDiff);
		}
		/*
		 * If during transformation from A->B, B has more objects than A: Add
		 * new objects to transformations list.
		 */
		// while (iteratorKeysB.hasNext()) {
		// String keyB = (String) iteratorKeysB.next();
		// HashMap<String, String> attributesObjectB =
		// entriesB.get(keyB).getAttributes();
		// transformations.add(attributesObjectB);
		// }

		return transformations;
	}

	public static void applyAttributeTransformations(RavensFigure fig, ArrayList transformations) {
		HashMap<String, RavensObject> entriesFig = fig.getObjects();
		SortedSet<String> ravenObjectKeys = new TreeSet<String>(entriesFig.keySet());

		Iterator iteratorRavenObjectKeys = ravenObjectKeys.iterator();
		Iterator iteratorTransformations = transformations.iterator();

		while (iteratorRavenObjectKeys.hasNext()) {
			String ravenObjectKey = (String) iteratorRavenObjectKeys.next();
			RavensObject currentRavenObject = (entriesFig.get(ravenObjectKey));
			HashMap currentRavenObjectAttributes = currentRavenObject.getAttributes();

			if (!iteratorTransformations.hasNext()) {
				entriesFig.remove(ravenObjectKey);
				continue;
			}

			HashMap currentTransformation = (HashMap) iteratorTransformations.next();

			if (currentTransformation == null) {
				entriesFig.remove(ravenObjectKey);
				continue;
			}

			ArrayList<String> missingAttributes = new ArrayList<String>();

			for (Iterator<Map.Entry<String, String>> it = currentRavenObjectAttributes.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry<String, String> attribute = it.next();
				String attributeKey = attribute.getKey();
				String attributeValue = attribute.getValue();

				if (!currentTransformation.containsKey(attributeKey)) {
					missingAttributes.add(attributeKey);
					continue;
				}

				String transformationValue = (String) currentTransformation.get(attributeKey);

				if (transformationValue.contains("->same"))
					continue;
				else if (transformationValue.equals("absent")) {
					missingAttributes.add(attributeKey);
				} else {
					String currentValue = (String) currentRavenObjectAttributes.get(attributeKey);
					String newValue = Transformations.getAdjustedValue(attributeKey, currentValue, transformationValue);
					currentRavenObjectAttributes.put(attributeKey, newValue);
				}
			}

			// remove attributes that were found missing
			for (String attrib : missingAttributes) {
				currentRavenObjectAttributes.remove(attrib);
			}
		}
		
		// Add remaining objects from transformations 
		while (iteratorTransformations.hasNext()) {
			HashMap currentTransformation = (HashMap) iteratorTransformations.next();
			RavensObject newObject = new RavensObject("Added");
			HashMap<String, String> attr = (HashMap<String, String>) currentTransformation.clone();
			for (String key2 : attr.keySet()) {
				newObject.getAttributes().put(key2, attr.get(key2));
			}
			String newObjKey = fig.getObjects().size() + 1 + "Added";
			fig.getObjects().put(newObjKey, newObject);
		}
	}

	private static String getAdjustedValue(String key, String currentValue, String transformValue) {
		String newValue = currentValue;
		switch (key) {
		case "size":
			String tranformValueA = transformValue.split("->")[0];
			String tranformValueB = transformValue.split("->")[1];
			int valueA = ATTRIB_VALUES_SIZE.indexOf(tranformValueA);
			int valueB = ATTRIB_VALUES_SIZE.indexOf(tranformValueB);
			int current = ATTRIB_VALUES_SIZE.indexOf(currentValue);
			newValue = ATTRIB_VALUES_SIZE.get(valueB - valueA + current);
			break;
		}
		return newValue;
	}

	public static HashMap<String, Integer> getShapeCountTransformations(RavensFigure fA, RavensFigure fB) {
		HashMap shapeCountA = RavensFigureUtilities.getShapeCount(fA);
		HashMap shapeCountB = RavensFigureUtilities.getShapeCount(fB);
		HashMap<String, Integer> shapeCountDiff = new HashMap<String, Integer>();
		for (Object keyA : shapeCountA.keySet()) {
			if (shapeCountB.containsKey(keyA)) {
				Integer valueA = (Integer) shapeCountA.get(keyA);
				Integer valueB = (Integer) shapeCountB.get(keyA);
				shapeCountDiff.put((String) keyA, valueB - valueA);
				shapeCountB.remove(keyA);
			} else
				shapeCountDiff.put((String) keyA, -1);
		}

		for (Object keyB : shapeCountB.keySet()) {
			shapeCountDiff.put((String) keyB, 1);
		}

		return shapeCountDiff;
	}
	
	public static HashMap<String, Integer> getObjectsPositionTransformations(RavensFigure fA, RavensFigure fB) {
		HashMap<String, Integer> transformations = new HashMap<String, Integer>();
		Map<String, Integer> pA = RavensFigureUtilities.getObjectsPosition(fA);
		Map<String, Integer> pB = RavensFigureUtilities.getObjectsPosition(fB);
		int valueA = pB.get("above") - pA.get("above");
		valueA = (valueA > 0) ? valueA : 0;
		transformations.put("above", valueA);

		int valueB = pB.get("left-of") - pA.get("left-of");
		valueB = (valueB > 0) ? valueB : 0;
		transformations.put("left-of", pB.get("left-of") - pA.get("left-of"));

		return transformations;
	}
}

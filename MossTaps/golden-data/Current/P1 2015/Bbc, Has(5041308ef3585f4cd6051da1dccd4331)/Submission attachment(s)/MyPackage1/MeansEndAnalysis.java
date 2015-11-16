package ravensproject.MyPackage1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;

public class MeansEndAnalysis {

	public static int solve(RavensProblem problem) {
		HashMap<String, RavensFigure> figures = problem.getFigures();
		RavensFigure fA = figures.get("A");
		RavensFigure fB = figures.get("B");
		RavensFigure fC = figures.get("C");

		List<RavensFigure> answersList = new ArrayList<RavensFigure>();
		answersList.add(figures.get("1"));
		answersList.add(figures.get("2"));
		answersList.add(figures.get("3"));
		answersList.add(figures.get("4"));
		answersList.add(figures.get("5"));
		answersList.add(figures.get("6"));

		ArrayList transformations = getTransformations(fA, fB);
		//printAttributes(fA);
		//printAttributes(fB);
		//printAttributes(fC);
		//System.out.println(transformations);
		//System.out.println("*after*");
		applyTransformations(fC, transformations);
		//printAttributes(fC);
		for (RavensFigure fAns : answersList) {
			if (ProductionSystem.caseEqual(fC, fAns)) {
				//System.out.println("found: " + fAns.getName());
				return Integer.parseInt(fAns.getName());
			}
		}
		//printAttributes(figures.get("1"));
		return -1;
	}

	public static void printAttributes(RavensFigure r) {
		String attributes = "";
		HashMap<String, RavensObject> entries = r.getObjects();
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
			RavensObject currentObject = entries.get(key);
			attributes += currentObject.getAttributes().toString();
		}

		System.out.println(r.getName() + ": " + attributes);
	}

	private static void applyTransformations(RavensFigure fig, ArrayList transformations) {
		HashMap<String, RavensObject> entriesFig = fig.getObjects();
		SortedSet<String> ravenObjectKeys = new TreeSet<String>(entriesFig.keySet());

		Iterator iteratorRavenObjectKeys = ravenObjectKeys.iterator();
		Iterator iteratorTransformations = transformations.iterator();

		while (iteratorRavenObjectKeys.hasNext()) {
			String ravenObjectKey = (String) iteratorRavenObjectKeys.next();
			RavensObject currentRavenObject = ((RavensObject) entriesFig.get(ravenObjectKey));
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
				String attributeKey = (String) attribute.getKey();
				String attributeValue = (String) attribute.getValue();

				if (!currentTransformation.containsKey(attributeKey)) {
					missingAttributes.add(attributeKey);
					continue;
				}

				String transformationValue = (String) currentTransformation.get(attributeKey);

				if (transformationValue.equals("same"))
					continue;
				else if (transformationValue.equals("absent")) {
					missingAttributes.add(attributeKey);
				} else {
					currentRavenObjectAttributes.put(attributeKey, transformationValue);
				}
			}
			
			// remove attributes that were found missing
			for( String attrib : missingAttributes){
				currentRavenObjectAttributes.remove(attrib);
			}
		}
	}

	private static HashMap<String, RavensObject> applyTransformations(HashMap<String, RavensObject> tempFig,
			ArrayList transformations) {

		return null;
	}

	public static RavensFigure clone(RavensFigure fig) {
		HashMap<String, RavensObject> entries = fig.getObjects();
		HashMap<String, RavensObject> temp = ProductionSystem.clone(entries);
		RavensFigure newFig = new RavensFigure("", "", "");
		// newFig.getObjects().put("", value)
		return newFig;
	}

	private static ArrayList getTransformations(RavensFigure figA, RavensFigure figB) {
		ArrayList transformations = new ArrayList();

		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();

		SortedSet<String> keysA = new TreeSet<String>(entriesA.keySet());
		SortedSet<String> keysB = new TreeSet<String>(entriesB.keySet());

		Iterator iteratorKeysA = keysA.iterator();
		Iterator iteratorKeysB = keysB.iterator();

		String isLastKeyUsedStillValid = null;

		while (iteratorKeysA.hasNext()) {
			HashMap<Object, Object> attributeDiff = new HashMap<>();

			String keyA = (String) iteratorKeysA.next();
			HashMap attributesObjectA = ((RavensObject) entriesA.get(keyA)).getAttributes();

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

			HashMap attributesObjectB = ((RavensObject) entriesB.get(keyB)).getAttributes();

			for (Object key : attributesObjectA.keySet()) {
				Object valueA = attributesObjectA.get(key);

				if (!attributesObjectB.containsKey(key)) {
					attributeDiff.put(key, "absent");
					continue;
				}
				Object valueB = attributesObjectB.get(key);

				if (valueA.equals(valueB)) {
					attributeDiff.put(key, "same");
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

				attributeDiff.put(key, valueB);
			}

			transformations.add(attributeDiff);
		}

		return transformations;
	}
}

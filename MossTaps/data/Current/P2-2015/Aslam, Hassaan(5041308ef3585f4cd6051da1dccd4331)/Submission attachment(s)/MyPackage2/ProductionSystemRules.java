package ravensproject.MyPackage2;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import ravensproject.RavensFigure;
import ravensproject.RavensObject;

public class ProductionSystemRules {

	public static boolean isEqual(RavensFigure figA, RavensFigure figB) {
		// Rule 1: Match count of objects in each figure
		if (figA.getObjects().size() != figB.getObjects().size())
			return false;

		// Rule 2: Get objects in each figure and match their attributes.
		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();
		if (isRavenObjectsEqual(entriesA, entriesB))
			return true;

		return false;
	}

	public static boolean isVerticalMirrorEffect(RavensFigure figA, RavensFigure figB) {
		// Is fig B a vertical mirror effect of fig A?
		// Step 1: Match count of objects in each figure
		if (figA.getObjects().size() != figB.getObjects().size())
			return false;

		// Step 2: Clone both figures
		RavensFigure tempA = RavensFigureUtilities.getClone(figA);
		RavensFigure tempB = RavensFigureUtilities.getClone(figB);

		// Step 3: Get object of each figure
		HashMap<String, RavensObject> objectsOfA = tempA.getObjects();
		HashMap<String, RavensObject> objectsOfB = tempB.getObjects();

		// Step 4: Iterate objects and remove "left-of" property
		for (String key : objectsOfA.keySet()) {
			RavensObject obj = objectsOfA.get(key);
			if (obj.getAttributes().containsKey("left-of"))
				obj.getAttributes().remove("left-of");
		}

		for (String key : objectsOfB.keySet()) {
			RavensObject obj = objectsOfB.get(key);
			if (obj.getAttributes().containsKey("left-of"))
				obj.getAttributes().remove("left-of");
		}

		// Step 5: Compare
		if (isRavenObjectsEqual(objectsOfA, objectsOfB))
			return true;

		return false;
	}

	private static boolean isRavenObjectsEqual(HashMap<String, RavensObject> objA, HashMap<String, RavensObject> objB) {
		String attributesA = "";
		String attributesB = "";

		SortedSet<String> keysA = new TreeSet<String>(objA.keySet());
		for (String key : keysA) {
			RavensObject currentObject = objA.get(key);
			attributesA += currentObject.getAttributes().toString();
		}

		SortedSet<String> keysB = new TreeSet<String>(objB.keySet());
		for (String key : keysB) {
			RavensObject currentObject = objB.get(key);
			attributesB += currentObject.getAttributes().toString();
		}

		// change inside attribute independent of figure name
		attributesA = attributesA.replaceAll("inside=[a-z0-9,]+", "inside=[*]");
		attributesB = attributesB.replaceAll("inside=[a-z0-9,]+", "inside=[*]");

		// change above attribute independent of figure name
		attributesA = attributesA.replaceAll("above=[a-z0-9,]+", "above=[*]");
		attributesB = attributesB.replaceAll("above=[a-z0-9,]+", "above=[*]");

		// change left-of attribute independent of figure name
		attributesA = attributesA.replaceAll("left-of=[a-z0-9,]+", "left-of=[*]");
		attributesB = attributesB.replaceAll("left-of=[a-z0-9,]+", "left-of=[*]");

		// System.out.println("Attributes A: " + attributesA);
		// System.out.println("Attributes B: " + attributesB);

		if (attributesA.equals(attributesB))
			return true;
		return false;
	}

	public static boolean isMiddleMirrorEffect(RavensFigure figA, RavensFigure figB) {
		// Rule 1: Match count of objects in each figure
		if (figA.getObjects().size() != figB.getObjects().size())
			return false;

		// Step 2: Clone figure A and change its angle
		RavensFigure tempA = RavensFigureUtilities.getClone(figA);

		// Get object of each figure
		HashMap<String, RavensObject> objectsOfA = tempA.getObjects();
		HashMap<String, RavensObject> objectsOfB = figB.getObjects();

		// Iterate objects and change "angle" property: 90->270 or 270->90
		for (String key : objectsOfA.keySet()) {
			RavensObject obj = objectsOfA.get(key);
			if (obj.getAttributes().containsKey("angle")) {
				String angle = obj.getAttributes().get("angle");
				obj.getAttributes().remove("angle");

				if (angle.equals("90"))
					angle = "270";
				else if (angle.equals("270"))
					angle = "90";

				obj.getAttributes().put("angle", angle);
			}
		}

		// Step 4: Compare
		if (isRavenObjectsEqual(objectsOfA, objectsOfB))
			return true;
		return false;
	}

	public static boolean isDouble(RavensFigure figA, RavensFigure figB) {
		// Mitosis
		// Step 1: number of objects in B should be twice the number of objects
		// in A
		if (figB.getObjects().size() != 2 * figA.getObjects().size())
			return false;

		// Clone figA and filter its attributes
		RavensFigure tmpA = RavensFigureUtilities.getClone(figA);
		HashMap<String, RavensObject> objectsOfA = tmpA.getObjects();
		for (String key : objectsOfA.keySet()) {
			RavensObject obj = objectsOfA.get(key);
			if (obj.getAttributes().containsKey("left-of")) {
				obj.getAttributes().remove("left-of");
			}
			if (obj.getAttributes().containsKey("above")) {
				obj.getAttributes().remove("above");
			}
		}

		// Clone figB and filter its attributes
		RavensFigure tmpB = RavensFigureUtilities.getClone(figB);
		HashMap<String, RavensObject> objectsOfB = tmpB.getObjects();
		for (String key : objectsOfB.keySet()) {
			RavensObject obj = objectsOfB.get(key);
			if (obj.getAttributes().containsKey("left-of")) {
				obj.getAttributes().remove("left-of");
			}
			if (obj.getAttributes().containsKey("above")) {
				obj.getAttributes().remove("above");
			}
		}

		// Step 2: Get attributes of A and B as a string
		String attributesA = RavensFigureUtilities.getAttributesToString(tmpA);
		String attributesB = RavensFigureUtilities.getAttributesToString(tmpB);

		// System.out.println(attributesA);
		// System.out.println(attributesB);

		// Step 3: Convert attribute of figA into a pattern and find its
		// occurrences in figB attributes
		// http://rosettacode.org/wiki/Count_occurrences_of_a_substring#Java
		int matchCount = (attributesB.length() - attributesB.replace(attributesA, "").length()) / attributesA.length();

		// Step 4: if matchCount is found twice then we have found a double
		if (matchCount == 2)
			return true;

		return false;
	}

	public static boolean isTripple(RavensFigure figA, RavensFigure figB) {
		// Step 1: number of objects in B should be twice the number of objects
		// in A
		if (figB.getObjects().size() != 3 * figA.getObjects().size())
			return false;

		// Clone figA and filter its attributes
		RavensFigure tmpA = RavensFigureUtilities.getClone(figA);
		HashMap<String, RavensObject> objectsOfA = tmpA.getObjects();
		for (String key : objectsOfA.keySet()) {
			RavensObject obj = objectsOfA.get(key);
			if (obj.getAttributes().containsKey("left-of")) {
				obj.getAttributes().remove("left-of");
			}
			if (obj.getAttributes().containsKey("above")) {
				obj.getAttributes().remove("above");
			}
			if (obj.getAttributes().containsKey("overlaps")) {
				obj.getAttributes().remove("overlaps");
			}
		}

		// Clone figB and filter its attributes
		RavensFigure tmpB = RavensFigureUtilities.getClone(figB);
		HashMap<String, RavensObject> objectsOfB = tmpB.getObjects();
		for (String key : objectsOfB.keySet()) {
			RavensObject obj = objectsOfB.get(key);
			if (obj.getAttributes().containsKey("left-of")) {
				obj.getAttributes().remove("left-of");
			}
			if (obj.getAttributes().containsKey("above")) {
				obj.getAttributes().remove("above");
			}
			if (obj.getAttributes().containsKey("overlaps")) {
				obj.getAttributes().remove("overlaps");
			}
		}

		// Step 2: Get attributes of A and B as a string
		String attributesA = RavensFigureUtilities.getAttributesToString(tmpA);
		String attributesB = RavensFigureUtilities.getAttributesToString(tmpB);

		// System.out.println(attributesA);
		// System.out.println(attributesB);

		// Step 3: Convert attribute of figA into a pattern and find its
		// occurrences in figB attributes
		// http://rosettacode.org/wiki/Count_occurrences_of_a_substring#Java
		int matchCount = (attributesB.length() - attributesB.replace(attributesA, "").length()) / attributesA.length();

		// Step 4: if matchCount is found twice then we have found a double
		if (matchCount == 3)
			return true;

		return false;
	}
}

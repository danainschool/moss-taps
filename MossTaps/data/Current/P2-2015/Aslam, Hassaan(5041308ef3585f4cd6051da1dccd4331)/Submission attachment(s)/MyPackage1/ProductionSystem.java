package ravensproject.MyPackage1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;

public class ProductionSystem {

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

		// compare FigA to FigB. if comparison does not work try compare FigA to
		// FigC
		int rule1 = applyRules(fA, fB);
		if (rule1 != -1) {
			for (RavensFigure fAns : answersList) {
				if (applyRules(fC, fAns) == rule1)
					return Integer.parseInt(fAns.getName());
			}
		} else {
			int rule2 = applyRules(fA, fC);
			if (rule2 != -1) {
				for (RavensFigure fAns : answersList) {
					if (applyRules(fB, fAns) == rule2)
						return Integer.parseInt(fAns.getName());
				}
			}
		}

		return -1;
	}

	public static int applyRules(RavensFigure figA, RavensFigure figB) {
		int ruleApplied = -1; // Default = -1

		if (caseEqual(figA, figB))
			ruleApplied = 1;
		else if (caseMirrorHorizontal(figA, figB))
			ruleApplied = 2;
		else if (caseFlipColor(figA, figB))
			ruleApplied = 3;
		else if (caseMirrorVertical(figA, figB))
			ruleApplied = 4;

		/*
		 * B-06 -> better implement means-end analysis lower half filled upper
		 * half filled
		 * 
		 * B-05 -> flip alignment
		 */

		return ruleApplied;
	}

	public static boolean caseMirrorVertical(RavensFigure figA, RavensFigure figB) {
		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();

		HashMap<String, RavensObject> tempA = new HashMap<String, RavensObject>(clone(entriesA));

		for (Map.Entry<String, RavensObject> entry : tempA.entrySet()) {
			RavensObject currentObject = entry.getValue();
			if (currentObject.getAttributes().containsKey("alignment")) {
				String alignmentValue = currentObject.getAttributes().get("alignment");
				if (alignmentValue.contains("top"))
					alignmentValue = alignmentValue.replace("top", "bottom");
				else
					alignmentValue = alignmentValue.replace("bottom", "top");

				currentObject.getAttributes().remove("alignment");
				currentObject.getAttributes().put("alignment", alignmentValue);
			}
			if (currentObject.getAttributes().containsKey("angle")) {
				int angleValue = Integer.parseInt(currentObject.getAttributes().get("angle"));
				if (angleValue < 180)
					angleValue += 90;
				else
					angleValue -= 90;

				currentObject.getAttributes().remove("angle");
				currentObject.getAttributes().put("angle", angleValue + "");
			}
		}

		if (isRavenObjectsEqual(tempA, entriesB))
			return true;
		return false;
	}

	public static boolean caseFlipColor(RavensFigure figA, RavensFigure figB) {
		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();

		HashMap<String, RavensObject> tempA = new HashMap<String, RavensObject>(clone(entriesA));

		for (Map.Entry<String, RavensObject> entry : tempA.entrySet()) {
			RavensObject currentObject = entry.getValue();
			if (!currentObject.getAttributes().containsKey("fill"))
				continue;
			String fillValue = currentObject.getAttributes().get("fill");
			if (fillValue.equals("yes"))
				fillValue = "no";
			else
				fillValue = "yes";

			currentObject.getAttributes().remove("fill");
			currentObject.getAttributes().put("fill", fillValue);
		}

		if (isRavenObjectsEqual(tempA, entriesB))
			return true;
		return false;
	}

	public static boolean caseMirrorHorizontal(RavensFigure figA, RavensFigure figB) {
		HashMap<String, RavensObject> entriesA = figA.getObjects();
		HashMap<String, RavensObject> entriesB = figB.getObjects();

		HashMap<String, RavensObject> tempA = new HashMap<String, RavensObject>(clone(entriesA));

		for (Map.Entry<String, RavensObject> entry : tempA.entrySet()) {
			RavensObject currentObject = entry.getValue();
			if (!currentObject.getAttributes().containsKey("angle"))
				continue;
			int angleValue = Integer.parseInt(currentObject.getAttributes().get("angle"));
			if (angleValue < 180)
				angleValue += 90;
			else
				angleValue -= 90;

			currentObject.getAttributes().remove("angle");
			currentObject.getAttributes().put("angle", angleValue + "");
		}

		if (isRavenObjectsEqual(tempA, entriesB))
			return true;
		return false;
	}

	public static boolean caseEqual(RavensFigure figA, RavensFigure figB) {
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
		attributesA = attributesA.replaceAll("inside=[a-z0-9]", "inside=[*]");
		attributesB = attributesB.replaceAll("inside=[a-z0-9]", "inside=[*]");

		// change above attribute independent of figure name
		attributesA = attributesA.replaceAll("above=[a-z0-9]", "above=[*]");
		attributesB = attributesB.replaceAll("above=[a-z0-9]", "above=[*]");

		// System.out.println(attributesA);
		// System.out.println(attributesB);

		if (attributesA.equals(attributesB))
			return true;
		return false;
	}

	public static HashMap<String, RavensObject> clone(HashMap<String, RavensObject> input) {
		HashMap<String, RavensObject> output = new HashMap<String, RavensObject>();
		for (String key : input.keySet()) {
			String name = input.get(key).getName();
			RavensObject newObject = new RavensObject(name);
			HashMap<String, String> attr = (HashMap<String, String>) input.get(key).getAttributes().clone();
			for (String key2 : attr.keySet()) {
				newObject.getAttributes().put(key2, attr.get(key2));
			}
			output.put(key, newObject);
		}
		return output;
	}
}

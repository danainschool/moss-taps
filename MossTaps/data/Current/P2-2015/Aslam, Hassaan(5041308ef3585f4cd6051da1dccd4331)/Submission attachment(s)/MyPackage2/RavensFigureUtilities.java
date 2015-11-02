package ravensproject.MyPackage2;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;

public class RavensFigureUtilities {

	public static RavensFigure getClone(RavensFigure fig) {
		RavensFigure figClone = new RavensFigure(fig.getName(), null, null);
		HashMap<String, RavensObject> output = new HashMap<String, RavensObject>();

		HashMap<String, RavensObject> figObjects = fig.getObjects();
		SortedSet<String> figKeys = new TreeSet<String>(figObjects.keySet());

		for (String key : figKeys) {
			String name = figObjects.get(key).getName();
			RavensObject newObject = new RavensObject(name);
			HashMap<String, String> attr = (HashMap<String, String>) figObjects.get(key).getAttributes().clone();
			for (String key2 : attr.keySet()) {
				newObject.getAttributes().put(key2, attr.get(key2));
			}
			// output.put(key, newObject);
			figClone.getObjects().put(key, newObject);
		}

		return figClone;
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

	public static String getAttributesToString(RavensFigure r) {
		String attributes = "";
		HashMap<String, RavensObject> entries = r.getObjects();
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
			RavensObject currentObject = entries.get(key);
			attributes += currentObject.getAttributes().toString();
		}

		return attributes;
	}

	public static HashMap<String, Integer> getShapeCount(RavensFigure r) {
		HashMap<String, Integer> shapeCount = new HashMap<String, Integer>();
		HashMap<String, RavensObject> entries = r.getObjects();
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
			RavensObject currentObject = entries.get(key);
			String currentShape = currentObject.getAttributes().get("shape");
			if (shapeCount.containsKey(currentShape)) {
				shapeCount.put(currentShape, shapeCount.get(currentShape) + 1);
			} else
				shapeCount.put(currentShape, 1);
		}
		return shapeCount;
	}

	public static void makeTransparent(RavensFigure fig) {
		HashMap<String, RavensObject> entries = fig.getObjects();
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
			if (entries.get(key).getAttributes().containsKey("fill")
					&& entries.get(key).getAttributes().get("fill").equals("yes")) {
				entries.get(key).getAttributes().put("fill", "no");
			}
		}
	}

	public static HashMap<String, Integer> getObjectsPosition(RavensFigure fig) {
		// this function is used for figures with single shape objects
		HashMap<String, Integer> positions = new HashMap<String, Integer>();
		positions.put("above", 0);
		positions.put("left-of", 0);

		HashMap<String, RavensObject> entries = fig.getObjects();
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
			if (entries.get(key).getAttributes().containsKey("above")) {
				positions.put("above", positions.get("above") + 1);
			}
			if (entries.get(key).getAttributes().containsKey("left-of")) {
				positions.put("left-of", positions.get("left-of") + 1);
			}
		}
		return positions;
	}
}

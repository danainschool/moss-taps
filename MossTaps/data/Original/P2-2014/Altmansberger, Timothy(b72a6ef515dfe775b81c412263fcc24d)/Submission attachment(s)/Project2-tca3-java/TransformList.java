package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TransformList {

	ArrayList<Transform> mList;

	public TransformList() {
		mList = new ArrayList<Transform>();
	}

	// adds a single transform to the TransformList
	public void AddTransform(String diffString) {
		Transform newTransform = new Transform();

		String[] tokens = diffString.split(":-");

		newTransform.mAttribute = tokens[0];
		newTransform.mOriginalValue = tokens[1];
		newTransform.mNewValue = tokens[2];

		mList.add(newTransform);
	}

	public void AddTransform(Transform transform) {
		mList.add(transform);
	}

	public void AddTransforms(TransformList transformList) {
		for (Transform transform : transformList.mList) {
			mList.add(transform);
		}
	}

	public Iterator<Transform> GetIterator() {
		return mList.iterator();
	}

	public String toString() {
		String result = "";

		for (Transform transform : mList) {
			result += transform.toString();
		}

		return result;
	}

	// TODO: Much room for improvement..
	public int GetDifferenceScore() {

		int result = 0;
		
		for (Transform t : mList)
		{
			if (t.isCreation() || t.isDeletion())
			{
				result++;
			}
			else if (t.mAttribute.equals("inside") || t.mAttribute.equals("above") || t.mAttribute.equals("left-of") || t.mAttribute.equals("overlaps"))
			{
				// do nothing
			}
			else
			{
				result++;
			}
		}
		
		return result;
	}
}

// STRUCT TO HOLD INDIVIDUAL TRANSFORMS
class Transform {

	public String mAttribute;
	public String mOriginalObjectName;
	public String mNewObjectName;
	public String mOriginalValue;
	public String mNewValue;

	public RavensObject mCreationDeletionObject;

	public Transform() {
		// do nothing
	}

	public Transform(String attribute, String originalObjectName,
			String newObjectName, String originalValue, String newValue) {
		mAttribute = attribute;
		mOriginalObjectName = originalObjectName;
		mNewObjectName = newObjectName;
		mOriginalValue = originalValue;
		mNewValue = newValue;
	}

	public Transform(String attribute, String originalObjectName,
			String newObjectName, String originalValue, String newValue,
			RavensObject creationDeletionObject) {
		this(attribute, originalObjectName, newObjectName, originalValue,
				newValue);
		mCreationDeletionObject = creationDeletionObject;

	}

	public boolean isNumeric() {
		return (tryParse(mOriginalValue) && tryParse(mNewValue));
	}

	public boolean isCreation() {
		return (mOriginalObjectName == null);
	}

	public boolean isDeletion() {
		return (mNewObjectName == null);
	}

	public Double GetReflectionLine() {
		if (isNumeric()) {
			// try splitting 360 degrees in different ways
			for (int i = 2; i <= Agent.MAX_REFLECTION_SPLITS; i++) {
				// try values which there could be a reflection across
				for (int j = 0; j < i; j++) {
					double reflectionValueCandidate = 1.0 * Agent.MAX_ANGLE / i
							* j;
					if (IsReflectionBetween(Integer.parseInt(mOriginalValue),
							Integer.parseInt(mNewValue),
							reflectionValueCandidate)) {
						return reflectionValueCandidate;
					}
				}
			}
			return null;
		} else {
			return null;
		}
	}

	// returns key if there appears to be a text reflection going on (i.e.
	// bottom-left -> bottom-right)
	public String[] getBinaryTextTransform() {
		for (Map.Entry<String, String> entry : Agent.BINARY_PAIRS.entrySet()) {
			if (mOriginalValue.contains(entry.getKey())
					&& mNewValue.contains(entry.getValue())) {
				String[] result = { entry.getKey(), entry.getValue() };
				return result;
			}
		}
		return null;
	}

	public boolean IsReflectionBetween(int originalValue, int newValue,
			double reflectionValue) {
		double diff1 = newValue - reflectionValue;
		double diff2 = reflectionValue - originalValue;

		if (diff1 < 0) {
			diff1 += Agent.MAX_ANGLE;
		}

		if (diff2 < 0) {
			diff2 += Agent.MAX_ANGLE;
		}

		return (diff1 == diff2);
	}

	public int GetNumericDifference() {
		int value1 = Integer.parseInt(mOriginalValue);
		int value2 = Integer.parseInt(mNewValue);

		return value2 - value1;
	}

	public boolean tryParse(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String toString() {

		String result = "";
		if (isCreation()) {
			result += "Created object " + mNewObjectName;
		} else if (isDeletion()) {
			result += "Deleted object " + mOriginalObjectName;
		} else {
			result += (mOriginalObjectName + "." + mAttribute + " = "
					+ mOriginalValue + "   ->   " + mNewObjectName + "."
					+ mAttribute + " = " + mNewValue);
		}

		result += "\n"; // end with newline character
		return result;
	}
}
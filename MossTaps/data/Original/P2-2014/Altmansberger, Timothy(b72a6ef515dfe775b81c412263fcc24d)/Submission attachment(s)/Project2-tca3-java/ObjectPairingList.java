package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ObjectPairingList {

	public ArrayList<ObjectPairing> mObjectPairingList;
	public HashMap<String, RavensObject> mObjectsInList1;
	public HashMap<String, RavensObject> mObjectsInList2;

	public ObjectPairingList() {
		mObjectPairingList = new ArrayList<ObjectPairing>();
		mObjectsInList1 = new HashMap<String, RavensObject>();
		mObjectsInList2 = new HashMap<String, RavensObject>();
	}

	public void AddObjectPairing(RavensObject object1, RavensObject object2,
			int difference)

	{
		mObjectPairingList.add(new ObjectPairing(object1, object2, difference));
		mObjectsInList1.put(object1.getName(), object1);
		mObjectsInList2.put(object2.getName(), object2);
	}

	// returns best pairings, will return objects that did not pair as
	// ObjectPairing with single object and null difference
	public ArrayList<ObjectPairing> GetBestPairings() {
		ArrayList<ObjectPairing> bestPairs = new ArrayList<ObjectPairing>();

		ObjectPairing currentBestPairing = null;

		// make copy of own object pairing list
		ArrayList<ObjectPairing> ObjectPairingListCopy = new ArrayList<ObjectPairing>();
		for (ObjectPairing op : mObjectPairingList) {
			ObjectPairingListCopy.add(op);
		}

		while (!(ObjectPairingListCopy.isEmpty())) {
			// find best pairing in list
			for (ObjectPairing objectPairing : ObjectPairingListCopy) {

				// if a better pair is found, replace current best pairing with
				// that
				if ((currentBestPairing == null)
						|| (currentBestPairing.mDifference > objectPairing.mDifference)) {
					currentBestPairing = objectPairing;
				}
			}

			// add best pairing to bestpairs list
			bestPairs.add(currentBestPairing);

			// remove object pairings from current list so no object is
			// double-counted
			for (Iterator<ObjectPairing> it = ObjectPairingListCopy.iterator(); it
					.hasNext();) {
				ObjectPairing removalCandidate = it.next();

				// if an object pairing is composed of one of the objects
				// involved in the best pairing, remove that pairing from the
				// list
				if (removalCandidate
						.InvolvesObject(currentBestPairing.mObject1)
						|| removalCandidate
								.InvolvesObject(currentBestPairing.mObject2)) {
					it.remove();
				}
			}

			// alter strings to indicate objects have been counted
			mObjectsInList1.remove(currentBestPairing.mObject1.getName());
			mObjectsInList2.remove(currentBestPairing.mObject2.getName());

			// set to null for next go around
			currentBestPairing = null;
		}

		// Detect objects which are not captured in pairings
		for (Map.Entry<String, RavensObject> entry : mObjectsInList1.entrySet()) {
			bestPairs.add(new ObjectPairing(entry.getValue(), null, 0));
		}

		for (Map.Entry<String, RavensObject> entry : mObjectsInList2.entrySet()) {
			bestPairs.add(new ObjectPairing(null, entry.getValue(), 0));
		}

		return bestPairs;
	}

}

class ObjectPairing {
	RavensObject mObject1;
	RavensObject mObject2;
	int mDifference;

	public ObjectPairing(RavensObject object1, RavensObject object2,
			int difference) {
		mObject1 = object1;
		mObject2 = object2;
		mDifference = difference;
	}

	public boolean InvolvesObject(RavensObject object) {
		String name = object.getName();

		return (mObject1.getName().equals(name) || mObject2.getName().equals(
				name));
	}

	public TransformList toTransformList() {

		TransformList result = new TransformList();

		// if one of the objects is null, an object has been created or deleted
		if (mObject1 == null) { // creation
			// add transform for creation/deletion
			result.AddTransform(new Transform(null, null, mObject2.getName(),
					null, null, mObject2));
		} else if (mObject2 == null) {
			result.AddTransform(new Transform(null, mObject1.getName(), null,
					null, null, mObject1));
		} else {
			// both objects are non-null
			HashMap<String, String> hashmap1 = mObject1.getAttributes();
			HashMap<String, String> hashmap2 = mObject2.getAttributes();

			for (Map.Entry<String, String> entry : hashmap1.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				// check if hashmap contains key
				if (hashmap2.containsKey(key)) {
					// check if values are different
					if (!(value.equals(hashmap2.get(key)))) {

						result.AddTransform(new Transform(key, mObject1
								.getName(), mObject2.getName(), value, hashmap2
								.get(key)));
					} else {
						// TODO: if other hashmap does not contain key
					}
				}
			}
		}
		return result;
	}

	public String toString() {
		return (mObject1.getName() + " " + mObject2.getName() + " " + Integer
				.toString(mDifference));
	}

}

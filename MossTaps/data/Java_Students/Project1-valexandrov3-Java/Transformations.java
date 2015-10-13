package ravensproject;


import java.util.HashMap;

public class Transformations {

    /* Constructor */
    public Transformations () {}

    // Look for transformations
    public HashMap<String, Integer> compareAngles (HashMap<String, HashMap<String, String>> first, HashMap<String, HashMap<String, String>> second) {

        int angleDifference = 0;
        HashMap<String, Integer> objectAngleChange = new HashMap<String, Integer>();
        //Go through objects in first
        for (String thisObjectInFirst : first.keySet()) {
            HashMap<String, String> objectsInFirst = first.get(thisObjectInFirst);

            // Go through objects in second
            for (String thisObjectInSecond : second.keySet()) {
                HashMap<String, String> objectsInSecond = second.get(thisObjectInSecond);

                // Go through attributes in first
                for (String attributeNameInFirst : objectsInFirst.keySet()) {
                    String attributeNameFirst = objectsInFirst.get(attributeNameInFirst);

                    // Go through attributes in second
                    for (String attributeNameInSecond : objectsInSecond.keySet()) {
                        String attributeNameSecond = objectsInSecond.get(attributeNameInSecond);

                        // Add angle comparison
                        if (attributeNameInFirst.equals("angle") && attributeNameSecond.equals("angle") &&
                                thisObjectInFirst.equals(thisObjectInSecond)) {
                            int angle1 = Integer.valueOf(attributeNameFirst);
                            int angle2 = Integer.valueOf(attributeNameSecond);
                            angleDifference = Math.abs(angle2 - angle1);
                            objectAngleChange.put(thisObjectInFirst, angleDifference);
                        }

                    }
                }
            }
        }
        return objectAngleChange;
    }

}

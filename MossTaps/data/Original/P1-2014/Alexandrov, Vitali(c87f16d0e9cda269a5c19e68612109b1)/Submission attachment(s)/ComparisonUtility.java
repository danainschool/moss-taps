package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;


public class ComparisonUtility {

    /* Constructor */
    public ComparisonUtility () {}

    /*
     * Create a HashMap for the name of the object (key) and a HashMap for name of the attribute and its value
     */
    public HashMap<String, HashMap<String, String>> getComparisonHashMap(RavensFigure figure) {

        HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
        for (String objectName : figure.getObjects().keySet()) {
            RavensObject thisObject = figure.getObjects().get(objectName);
            String thisObjectName = thisObject.getName();
            //System.out.println(thisObject.getAttributes());

            HashMap<String, String> valuesHashMap = new HashMap<String, String>();
            for (String attributeName : thisObject.getAttributes().keySet()) {
                String attributeValue = thisObject.getAttributes().get(attributeName);
                valuesHashMap.put(attributeName, attributeValue);
            }
            map.put(thisObjectName, valuesHashMap);
        }
        return map;
    }

    /*
     * Compare objects in HashMaps of two RavensFigures figure1 and figure2 using analogical reasoning
     */
    //public void compareObjects(HashMap<String, HashMap<String, String>> first, HashMap<String, HashMap<String, String>> second) {
    public HashMap<String, HashMap<String, Integer>> compareObjects(HashMap<String, HashMap<String, String>> first, HashMap<String, HashMap<String, String>> second) {

        HashMap<String, HashMap<String, Integer>> objectComparisonHashMap = new HashMap<String, HashMap<String, Integer>>();

        // Generate a HashMap with the key for first object

        //Go through objects in first
        for (String thisObjectInFirst : first.keySet()) {
            HashMap<String, String> objectsInFirst = first.get(thisObjectInFirst);
            HashMap<String, Integer> objectSimilarityScores = new HashMap<String, Integer>();

            // Go through objects in second
            for (String thisObjectInSecond : second.keySet()) {
                HashMap<String, String> objectsInSecond = second.get(thisObjectInSecond);
                int similarityScore = 0;

                // Go through attributes in first
                for (String attributeNameInFirst : objectsInFirst.keySet()) {
                    String attributeNameFirst = objectsInFirst.get(attributeNameInFirst);

                    // Go through attributes in second
                    for (String attributeNameInSecond : objectsInSecond.keySet()) {
                        String attributeNameSecond = objectsInSecond.get(attributeNameInSecond);


                        // Weight different attributes giving different scores
                        if (attributeNameInSecond.equals(attributeNameInFirst)) {
                            if (attributeNameSecond.equals(attributeNameFirst)) {
                                switch (attributeNameInFirst) {
                                    case "shape":
                                        similarityScore += 5;
                                        break;
                                    case "size":
                                        similarityScore += 4;
                                        break;
                                    case "fill":
                                        similarityScore += 3;
                                        break;
                                    case "angle":
                                        similarityScore += 2;
                                        break;
                                    case "allignment":
                                        similarityScore += 1;
                                        break;
                                    case "inside":
                                        similarityScore += 1;
                                        break;
                                }
                            }
                        }
                    }
                }
                //objectSimilarityScores.put(thisObjectInSecond, Integer.valueOf(similarityScore));
                objectSimilarityScores.put(thisObjectInSecond, similarityScore);
                objectComparisonHashMap.put(thisObjectInFirst, objectSimilarityScores);
            }
        }
        return objectComparisonHashMap;
    }

    public int getScore (HashMap<String, HashMap<String, Integer>> map) {

        int score = 0;
        for (HashMap<String, Integer> pairs : map.values()) {
            System.out.println(pairs);
            for (String object : pairs.keySet()) {
                score += pairs.get(object);
            }
        }
        return score;
    }

    public int getClosestScore (int score, HashMap<Integer, Integer> scoreMap) {
        int dx = 0;
        int distance = 0;
        for (int k = 1; k < scoreMap.get(k); k++) {
            int k_distance = Math.abs(scoreMap.get(k) - score);
            if (k_distance < distance) {
                dx = k;
                distance = k_distance;
            }
        }
        int closestScore = scoreMap.get(dx);
        return closestScore;
    }




    // check for duplicates if scoreCD == scoreAB, return random score
    public HashMap<Integer, Integer> giveRandomDuplicate (int referenceScore, HashMap<Integer, Integer> scoreMap) {

        HashMap<Integer, Integer> mapOfDuplicates = new HashMap<Integer, Integer>();
        int numSameScores = 0;
        for (int score : scoreMap.values()) {
            if (score == referenceScore) {

            }
        }
        return null;
    }

    }

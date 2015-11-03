package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ravensproject.ComparisonUtility;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {

    // Instance variables
    private ComparisonUtility comparisonUtility;
    private Transformations transformations;

    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {

        comparisonUtility = new ComparisonUtility();
        transformations = new Transformations();

    }


    /*
    public static void main(String[] args) {
        System.out.println("TEST");
        RavensProject problem = new RavensProject();
    }
    */

    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */

    /* Originally:
    public int Solve(RavensProblem problem) {
        return -1;
    }
    */

    public int Solve(RavensProblem problem) {
        int bestChoice = 0;
        if (problem.getProblemType().equals("2x2")) {
            bestChoice = Solve2x2(problem);
        } else if(problem.getProblemType().equals("3x3")) {
            bestChoice = Solve3x3(problem);
        }
        return bestChoice;
    }

    public int Solve2x2(RavensProblem problem) {

        //constants for figures
        RavensFigure figureA = problem.getFigures().get("A");
        RavensFigure figureB = problem.getFigures().get("B");
        RavensFigure figureC = problem.getFigures().get("C");

        HashMap<String, HashMap<String, String>> figureAValuesHashMap = comparisonUtility.getComparisonHashMap(figureA);
        //System.out.println(figureAValuesHashMap.keySet());
        //System.out.println(figureAValuesHashMap.values());

        HashMap<String, HashMap<String, String>> figureBValuesHashMap = comparisonUtility.getComparisonHashMap(figureB);
        //System.out.println(figureBValuesHashMap.keySet());

        HashMap<String, HashMap<String, String>> figureCValuesHashMap = comparisonUtility.getComparisonHashMap(figureC);

        // Compare objects
        HashMap<String, HashMap<String, Integer>> map = comparisonUtility.compareObjects(figureAValuesHashMap, figureBValuesHashMap);
        //System.out.println(map);
        //System.out.println(map.values());
        //System.out.println(map.keySet());
        //System.out.println(comparisonUtility.getScore(map));
        int scoreAB = comparisonUtility.getScore(map);
        System.out.println(scoreAB);
        //HashMap<String, Integer> angleDifference = transformations.compareAngles(figureAValuesHashMap, figureBValuesHashMap);
        //System.out.println(angleDifference);


        // Iterate over all choices and find the best choice answer
        int bestChoice = 3; // default
        HashMap<Integer, Integer> choiceScoresMap = new HashMap<Integer, Integer>();
        for (int choice = 1; choice <=6; choice++) {
            RavensFigure figureChoice = problem.getFigures().get(String.valueOf(choice));
            HashMap<String, HashMap<String, String>> figureChoiceValuesHashMap = comparisonUtility.getComparisonHashMap(figureChoice);

            // Use ComparisonUtility to compare objects
            HashMap<String, HashMap<String, Integer>> map2 = comparisonUtility.compareObjects(figureCValuesHashMap, figureChoiceValuesHashMap);
            //System.out.println(map2);
            int scoreCD = comparisonUtility.getScore(map2);
            //choiceScoresMap.put(choice, scoreCD);
            if (scoreCD == scoreAB) {
                bestChoice = choice;
            }
        }
        //List<Integer> keysInArray = new ArrayList<Integer>(choiceScoresMap.keySet());
        //Random rnd = new Random();
        //bestChoice = choiceScoresMap.get(keysInArray.get(rnd.nextInt(keysInArray.size())));

        //int bestScore = comparisonUtility.getClosestScore(scoreAB, choiceScoresMap);
        //bestChoice = choiceScoresMap.get(bestScore);

        //return -1;
        return bestChoice;
    }

    //
    // 3x3 Problems
    public int Solve3x3(RavensProblem problem) {
        int bestChoice = 4; // default

        //constants for figures
        RavensFigure figureA = problem.getFigures().get("A");
        RavensFigure figureB = problem.getFigures().get("B");
        RavensFigure figureC = problem.getFigures().get("C");
        RavensFigure figureD = problem.getFigures().get("D");
        RavensFigure figureE = problem.getFigures().get("E");
        RavensFigure figureF = problem.getFigures().get("F");
        RavensFigure figureG = problem.getFigures().get("G");
        RavensFigure figureH = problem.getFigures().get("H");


        HashMap<String, HashMap<String, String>> figureAValuesHashMap = comparisonUtility.getComparisonHashMap(figureA);
        HashMap<String, HashMap<String, String>> figureBValuesHashMap = comparisonUtility.getComparisonHashMap(figureB);
        HashMap<String, HashMap<String, String>> figureCValuesHashMap = comparisonUtility.getComparisonHashMap(figureC);
        HashMap<String, HashMap<String, String>> figureDValuesHashMap = comparisonUtility.getComparisonHashMap(figureD);
        HashMap<String, HashMap<String, String>> figureEValuesHashMap = comparisonUtility.getComparisonHashMap(figureE);
        HashMap<String, HashMap<String, String>> figureFValuesHashMap = comparisonUtility.getComparisonHashMap(figureF);
        HashMap<String, HashMap<String, String>> figureGValuesHashMap = comparisonUtility.getComparisonHashMap(figureG);
        HashMap<String, HashMap<String, String>> figureHValuesHashMap = comparisonUtility.getComparisonHashMap(figureH);

        int numObjA = comparisonUtility.numObjects(figureA);
        int numObjC = comparisonUtility.numObjects(figureC);
        int diffNumObjCA = numObjC - numObjA;
        //System.out.println(numObjA);
        //System.out.println(diffNumObjCA);
        //System.out.println(diffNumObjCA);
        int numObjG = comparisonUtility.numObjects(figureG);
        int diffNumObjGA = numObjG - numObjA;

        HashMap<String, Integer> objA = comparisonUtility.objectsQuantity(figureA);
        HashMap<String, Integer> objC = comparisonUtility.objectsQuantity(figureC);
        HashMap<String, Integer> objG = comparisonUtility.objectsQuantity(figureG);

        //System.out.println(objA);
        //System.out.println(objC);

        HashMap<String, Integer> objDifferenceAC = comparisonUtility.objectsDifference(objA, objC);
        HashMap<String, Integer> objDifferenceAG = comparisonUtility.objectsDifference(objA, objG);
        //System.out.println(objDifferenceAC);


        // Compare objects
        // First row
        HashMap<String, HashMap<String, Integer>> mapAB = comparisonUtility.compareObjects(figureAValuesHashMap, figureBValuesHashMap);
        HashMap<String, HashMap<String, Integer>> mapBC = comparisonUtility.compareObjects(figureBValuesHashMap, figureCValuesHashMap);
        int scoreAB = comparisonUtility.getScore(mapAB);
        int scoreBC = comparisonUtility.getScore(mapBC);
        int scoreABC = scoreAB + scoreBC;
        // First column
        HashMap<String, HashMap<String, Integer>> mapAD = comparisonUtility.compareObjects(figureAValuesHashMap, figureDValuesHashMap);
        HashMap<String, HashMap<String, Integer>> mapDG = comparisonUtility.compareObjects(figureDValuesHashMap, figureGValuesHashMap);
        int scoreAD = comparisonUtility.getScore(mapAD);
        int scoreDG = comparisonUtility.getScore(mapDG);
        int scoreADG = scoreAD + scoreDG;

        int scoreFirst = scoreABC = scoreADG;

        // Second row
        HashMap<String, HashMap<String, Integer>> mapDE = comparisonUtility.compareObjects(figureDValuesHashMap, figureEValuesHashMap);
        HashMap<String, HashMap<String, Integer>> mapEF = comparisonUtility.compareObjects(figureEValuesHashMap, figureFValuesHashMap);
        int scoreDE = comparisonUtility.getScore(mapDE);
        int scoreEF = comparisonUtility.getScore(mapEF);
        int scoreDEF = scoreDE + scoreEF;
        // Second column
        HashMap<String, HashMap<String, Integer>> mapBE = comparisonUtility.compareObjects(figureBValuesHashMap, figureEValuesHashMap);
        HashMap<String, HashMap<String, Integer>> mapEH = comparisonUtility.compareObjects(figureEValuesHashMap, figureHValuesHashMap);
        int scoreBE = comparisonUtility.getScore(mapBE);
        int scoreEH = comparisonUtility.getScore(mapEH);
        int scoreBEH = scoreBE + scoreEH;

        int scoreSecond = scoreDEF + scoreBEH;


        // Iterate over all choices and find the best choice answer

        // Default is random number
        //Random randomChoice = new Random();
        //bestChoice = randomChoice.nextInt(8) + 1;

        HashMap<Integer, Integer> choiceScoresMap = new HashMap<Integer, Integer>();
        for (int choice = 1; choice <=8; choice++) {
            RavensFigure figureChoice = problem.getFigures().get(String.valueOf(choice));
            HashMap<String, HashMap<String, String>> figureChoiceValuesHashMap = comparisonUtility.getComparisonHashMap(figureChoice);

            // Use ComparisonUtility to compare objects
            // Third row
            HashMap<String, HashMap<String, Integer>> mapGH = comparisonUtility.compareObjects(figureGValuesHashMap, figureHValuesHashMap);
            HashMap<String, HashMap<String, Integer>> mapHChoice = comparisonUtility.compareObjects(figureHValuesHashMap, figureChoiceValuesHashMap);
            int scoreGH = comparisonUtility.getScore(mapGH);
            int scoreHChoice = comparisonUtility.getScore(mapHChoice);
            int scoreGHChoice = scoreGH + scoreHChoice;

            // Third column
            HashMap<String, HashMap<String, Integer>> mapCF = comparisonUtility.compareObjects(figureCValuesHashMap, figureFValuesHashMap);
            HashMap<String, HashMap<String, Integer>> mapFChoice = comparisonUtility.compareObjects(figureFValuesHashMap, figureChoiceValuesHashMap);
            int scoreCF = comparisonUtility.getScore(mapCF);
            int scoreFChoice = comparisonUtility.getScore(mapFChoice);
            int scoreCFChoice = scoreCF + scoreFChoice;

            int scoreThird = scoreGHChoice + scoreCFChoice;
            // Calculate differences in number of objects to be compared
            int numObjChoice = comparisonUtility.numObjects(figureChoice);
            int diffNumObjGChoice = numObjG - numObjChoice;
            int diffNumObjAChoice = numObjA - numObjChoice;


            HashMap<String, Integer> objChoice = comparisonUtility.objectsQuantity(figureChoice);
            HashMap<String, Integer> objDifferenceCChoice = comparisonUtility.objectsDifference(objC, objChoice);
            HashMap<String, Integer> objDifferenceGChoice = comparisonUtility.objectsDifference(objG, objChoice);
            System.out.println(objDifferenceCChoice.keySet());
            //System.out.println(objChoice.keySet());

            /*
            for (String keyInChoice : objChoice.keySet()) {
                for (String keyInCChoice : objDifferenceCChoice.keySet()) {
                    for (String keyInGChoice : objDifferenceGChoice.keySet()) {
                        if (keyInChoice.equals(keyInCChoice) && keyInChoice.equals(keyInGChoice)) {
                            //int m = objChoice.get(keyInChoice) - objDifferenceCChoice.get(keyInCChoice);
                            //String key = keyInChoice;
                            //Integer value = objChoice.get(keyInChoice);
                            //System.out.println(m);
                            //System.out.println(key);
                            //System.out.println(value);
                            if ((objDifferenceAG.get(keyInChoice) == objDifferenceCChoice.get(keyInCChoice))
                                    && (objDifferenceAC.get(keyInChoice) == objDifferenceGChoice.get(keyInCChoice))) {
                                bestChoice = choice;
                            }
                        }
                    }

                }
            }
            */
            outerloop:
            for (String keyInChoice : objChoice.keySet()) {
                for (String keyInDiffAC : objDifferenceAC.keySet()) {
                    if (keyInChoice.equals(keyInDiffAC)) {
                        if ((objDifferenceAC.get(keyInChoice) == objDifferenceGChoice.get(keyInChoice))
                                || (objDifferenceAG.get(keyInChoice) == objDifferenceGChoice.get(keyInChoice))) {
                            bestChoice = choice;
                        } else if (scoreFirst == scoreThird || scoreSecond == scoreThird) {
                            bestChoice = choice;
                        }

                    } else break outerloop;
                }
                //bestChoice = choice;
            }

            //if (scoreFirst == scoreThird || scoreSecond == scoreThird) {
            //    bestChoice = choice;
            //}
            //else {
            //    bestChoice = randomChoice.nextInt(8) + 1;
            //}

            //if (scoreFirst == scoreThird || scoreSecond == scoreThird) {
            //    bestChoice = choice;
            //} else {
            //   bestChoice = randomChoice.nextInt(8) + 1;
            //}

            //if (diffNumObjCA == diffNumObjAChoice && diffNumObjGA == diffNumObjGChoice) {
            //    bestChoice = choice;
            //}
            //if ( numObjG +  (diffNumObjGA + 1) == numObjChoice) {
            //    bestChoice = choice;
            //}

        }

        return bestChoice;

    }

}

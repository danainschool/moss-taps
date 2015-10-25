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
}

package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import ravensproject.solvers.EquivalenceChecker;
import ravensproject.solvers.ObjectMatcher;
import ravensproject.solvers.ProblemSolver;
import ravensproject.transformations.CostComparator;
import ravensproject.transformations.FigureTransformation;

import java.util.List;
import java.util.Map;

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
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
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
    public int Solve(final RavensProblem problem) {
        final int guess = getGuess(problem);
        if(guess > 0) {
            final int actual = problem.checkAnswer(guess);
            if (guess == actual) {
                System.out.println("Guessed correct answer of " + actual);
            } else {
                System.out.println("Guessed " + guess + " but actually was " + actual);
            }
        } else {
            System.out.println("Uncertain about this problem; skipping");
        }
        return guess;
    }

    private int getGuess(final RavensProblem problem) {
        switch(problem.getProblemType()) {
            case "2x2":
                return solve2x2(problem);
            case "3x3":
                return solve3x3(problem);
            default:
                return -1;
        }
    }

    private int solve2x2(final RavensProblem problem) {
        /*if(!problem.getName().equals("Basic Problem B-11")) {
            //Use this for debugging a single problem
            return -1;
        }*/
        if(!problem.hasVerbal()) {
            return -1;
        }
        final RavensFigure figA = problem.getFigures().get("A");
        final RavensFigure figB = problem.getFigures().get("B");
        final RavensFigure figC = problem.getFigures().get("C");
        final Map<String, String> matchesAC = ObjectMatcher.match(figA.getObjects(), figC.getObjects());
        final List<FigureTransformation> transformations = ProblemSolver.getPossibleTransformationsFor2x2(figA, figB);
        transformations.sort(new CostComparator());
        for(final FigureTransformation transformation : transformations) {
            final Map<String, Map<String, String>> possibleAnswer = transformation.forFigure(matchesAC).transform(figC);
            for(int i = 1; i <= 6; ++i) {
                final RavensFigure choice = problem.getFigures().get(Integer.toString(i));
                if(EquivalenceChecker.areFiguresEquivalent(possibleAnswer, choice)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int solve3x3(final RavensProblem problem) {
        return -1;
    }
}

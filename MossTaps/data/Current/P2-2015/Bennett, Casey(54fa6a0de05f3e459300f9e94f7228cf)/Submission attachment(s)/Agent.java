package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import ravensproject.solvers.ObjectMatcher;
import ravensproject.solvers.OrderFinder;
import ravensproject.solvers.ProblemSolver;
import ravensproject.transformations.CostComparator;
import ravensproject.transformations.FigureTransformation;

import java.util.*;

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
        /*if(!problem.getName().equals("Basic Problem C-07")) {
            //Use this for debugging a single problem
            return -1;
        }*/
        if(!problem.hasVerbal()) {
            return -1;
        }
        final SortedMap<String, PotentialSolution> transformed = transform(problem);
        switch(problem.getProblemType()) {
            case "2x2":
                return solve2x2(transformed);
            case "3x3":
                return solve3x3(transformed);
            default:
                return -1;
        }
    }

    private int findAnswer(final PotentialSolution possibleAnswer, final SortedMap<String, PotentialSolution> figures) {
        int bestAnswer = -1;
        int bestDistance = Integer.MAX_VALUE;
        final List<Integer> distances = new ArrayList<>(8);
        for(int i = 1; i <= 8; ++i) {
            if(!figures.containsKey(Integer.toString(i))) {
                continue;
            }
            final PotentialSolution choice = figures.get(Integer.toString(i));
            final int distance = possibleAnswer.distanceHeuristic(choice);
            distances.add(distance);
            if(distance < bestDistance) {
                bestAnswer = i;
                bestDistance = distance;
            }
        }
        if(bestDistance == 0) {
            return bestAnswer;
        }
        final List<Integer> sorted = new ArrayList<>(distances);
        sorted.sort(Comparator.<Integer>naturalOrder());
        if(2 * bestDistance < sorted.get(1)) {
            return bestAnswer;
        }
        return -1;
    }

    private int transformAndGetAnswer(final List<FigureTransformation> transformations, final Map<String, String> matches, final PotentialSolution fig, final SortedMap<String, PotentialSolution> figures) {
        for(final FigureTransformation transformation : transformations) {
            final PotentialSolution possibleAnswer = OrderFinder.rebalance(transformation.forFigure(matches).transform(fig));
            final int answer = findAnswer(possibleAnswer, figures);
            if(answer > 0) {
                return answer;
            }
        }
        return -1;
    }

    private static SortedMap<String, PotentialSolution> transform(final RavensProblem problem) {
        final SortedMap<String, PotentialSolution> toRet = new TreeMap<>();
        for(final Map.Entry<String, RavensFigure> entry : problem.getFigures().entrySet()) {
            toRet.put(entry.getKey(), OrderFinder.replaceOrdering(entry.getValue().getObjects()));
        }
        return Collections.unmodifiableSortedMap(toRet);
    }

    private int solve2x2(final SortedMap<String, PotentialSolution> figures) {
        final PotentialSolution figA = figures.get("A");
        final PotentialSolution figB = figures.get("B");
        final PotentialSolution figC = figures.get("C");
        final Map<String, String> matchesAC = ObjectMatcher.match(figA, figC);
        final List<FigureTransformation> transforms = getPossibleTransformations(new FigurePair(figA, figB));
        return transformAndGetAnswer(transforms, matchesAC, figC, figures);
    }

    private int solve3x3(final SortedMap<String, PotentialSolution> figures) {
        final PotentialSolution figA = figures.get("A");
        final PotentialSolution figB = figures.get("B");
        final PotentialSolution figC = figures.get("C");
        final PotentialSolution figD = figures.get("D");
        final PotentialSolution figE = figures.get("E");
        final PotentialSolution figF = figures.get("F");
        final PotentialSolution figG = figures.get("G");
        final PotentialSolution figH = figures.get("H");

        final Map<String, String> matchesEH = ObjectMatcher.match(figE, figH);

        final List<FigureTransformation> secondTfms = getPossibleTransformations(new FigurePair(figE, figF));
        final int firstTry = transformAndGetAnswer(secondTfms, matchesEH, figH, figures);
        if(firstTry > 0) {
            return firstTry;
        }

        final Map<String, String> matchesGH = ObjectMatcher.match(figG, figH);
        final List<FigureTransformation> bottomRowTfms = getPossibleTransformations(new FigurePair(figG, figH));
        return transformAndGetAnswer(bottomRowTfms, matchesGH, figH, figures);
    }

    private List<FigureTransformation> getPossibleTransformations(final FigurePair start, final FigurePair... next) {
        final Set<FigureTransformation> tSet = new HashSet<>(ProblemSolver.getPossibleTransformationsFor2x2(start.a, start.b));
        for(final FigurePair pair : next) {
            //tSet.retainAll(ProblemSolver.getPossibleTransformationsFor2x2(pair.a, pair.b));
        }
        final List<FigureTransformation> tList = new ArrayList<>(tSet);
        tList.sort(new CostComparator());
        return tList;
    }

    private static final class FigurePair {
        public final PotentialSolution a, b;

        public FigurePair(final PotentialSolution a, final PotentialSolution b) {
            this.a = a;
            this.b = b;
        }
    }
}

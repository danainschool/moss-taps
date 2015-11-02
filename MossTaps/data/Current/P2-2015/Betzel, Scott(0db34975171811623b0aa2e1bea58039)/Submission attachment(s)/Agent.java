package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import ravensproject.betzel.agents.*;
import ravensproject.betzel.helpers.LoggingHelper;

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
    private List<AgentBase> agents;

    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     *
     * Do not add any variables to this signature; they will not be used by
     * main().
     *
     */
    public Agent() {
        boolean verbose = false;

        this.agents = new ArrayList<>();
        this.agents.add(new PixelCountAgent("Pixel Count Agent"));
        this.agents.add(new GeneralShapeCountAgent("General Shape Count Agent"));
        this.agents.add(new DistinctShapeCountAgent("Distinct Shape Count Agent"));
        this.agents.add(new FillCountAgent("Fill Count Agent"));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (D, G, F)", "D", "G", "F", verbose));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (B, C, H)", "B", "C", "H", verbose));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (G, H, H)", "G", "H", "H", verbose));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (C, F, F)", "C", "F", "F", verbose));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (E, F, H)", "E", "F", "H", verbose));
        this.agents.add(new TransitionComparisonAgent("Transition Comparison Agent (E, H, F)", "E", "H", "F", verbose));

        // Specific Shapes
        this.agents.add(new SpecificShapeCountAgent("Square Specific Shape Count Agent", "square"));
        this.agents.add(new SpecificShapeCountAgent("Rectangle Specific Shape Count Agent", "rectangle"));
        this.agents.add(new SpecificShapeCountAgent("Diamond Specific Shape Count Agent", "diamond"));
        this.agents.add(new SpecificShapeCountAgent("Star Specific Shape Count Agent", "star"));
        this.agents.add(new SpecificShapeCountAgent("Right Triangle Specific Shape Count Agent", "right triangle"));
        this.agents.add(new SpecificShapeCountAgent("Circle Specific Shape Count Agent", "circle"));
        this.agents.add(new SpecificShapeCountAgent("Triangle Specific Shape Count Agent", "triangle"));
        this.agents.add(new SpecificShapeCountAgent("Octagon Specific Shape Count Agent", "octagon"));
    }

    private void merge(Map<Integer, Integer> map, Set<Integer> toMerge) {
        assert map != null;
        assert toMerge != null;

        for (Integer i: toMerge) {
            if (map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
            } else {
                map.put(i, 1);
            }
        }

    }

    private void outputOccurrenceMap(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder("[");
        boolean hasOne = false;

        for (Integer i: map.keySet()) {
            sb.append(String.format("%d = %dx, ", i, map.get(i)));
            hasOne = true;
        }

        if (hasOne) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("]");
        LoggingHelper.outputString(sb.toString());
    }

    private int getMaxOccurrenceCount(Map<Integer, Integer> map) {
        int max = 0;
        int value;

        for (Integer key: map.keySet()) {
            value = map.get(key);

            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    private Set<Integer> getAllWithOccurrenceCount(Map<Integer, Integer> map, int occurrenceCount) {
        HashSet<Integer> toRet = new HashSet<>();

        for (Integer key: map.keySet()) {
            if (map.get(key) == occurrenceCount) {
                toRet.add(key);
            }
        }

        return toRet;
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
    public int Solve(RavensProblem problem) {
        if (!problem.hasVerbal() || !problem.hasVisual()) {
            LoggingHelper.outputString(String.format("Skipping problem '%s'. Verbal and Visual required.",
                    problem.getName()));
            return -1;
        }

        if (problem.getProblemType().equals("2x2")) {
            LoggingHelper.outputString(String.format("Only 3x3 supported.  Skipping problem '%s'", problem.getName()));
            return -1;
        }

        try {
            LoggingHelper.setErrorLoggingEnabled(false);
            LoggingHelper.outputString(String.format("Solving %s\n--------------------------------", problem.getName()));

            Map<Integer, Integer> occurrenceCountMap = processAgents(problem);

            Set<Integer> validAnswers = getValidAnswers(occurrenceCountMap);

            int correctAnswer;

            if (validAnswers.size() == 1) {
                int toSet = (int)validAnswers.toArray()[0];
                correctAnswer = problem.checkAnswer(toSet);
            } else {
                correctAnswer = problem.checkAnswer(-1);
            }

            if (validAnswers.contains(correctAnswer)) {
                LoggingHelper.outputString(String.format("%s contains %d\n\n", validAnswers, correctAnswer));
            } else {
                LoggingHelper.outputString(String.format("%s does not contain %d\n\n", validAnswers, correctAnswer));
            }

        } catch (Exception ex) {
            LoggingHelper.logError(ex.getMessage());
        }

        return -1;
    }

    private Set<Integer> getValidAnswers(Map<Integer, Integer> occurrenceCountMap) {
        LoggingHelper.outputString("Merged Occurrence Counts = ");
        this.outputOccurrenceMap(occurrenceCountMap);
        LoggingHelper.outputString("\n");

        int maxOccurrence = this.getMaxOccurrenceCount(occurrenceCountMap);

        return this.getAllWithOccurrenceCount(occurrenceCountMap, maxOccurrence);
    }

    private Map<Integer, Integer> processAgents(RavensProblem problem) throws Exception {
        HashMap<Integer, Integer> toRet = new HashMap<>();

        for (AgentBase ba: this.agents) {
            this.mergeOccurrenceCountWithAgent(problem, toRet, ba);
        }

        return toRet;
    }

    private void mergeOccurrenceCountWithAgent(RavensProblem problem,
                                               Map<Integer, Integer> occurrenceCountMap,
                                               AgentBase betzelAgent) throws Exception {
        Set<Integer> pcaAnswers = betzelAgent.solve(problem);
        this.merge(occurrenceCountMap, pcaAnswers);
        LoggingHelper.outputString(String.format("%s = %s", betzelAgent.getName(), pcaAnswers));
    }
}

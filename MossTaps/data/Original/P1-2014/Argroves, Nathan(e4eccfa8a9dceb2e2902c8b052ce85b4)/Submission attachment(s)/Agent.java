package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import java.util.*;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * <p>
 * You may also create and submit new files in addition to modifying this file.
 * <p>
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * <p>
 * These methods will be necessary for the project's main method to run.
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * <p>
     * Do not add any variables to this signature; they will not be used by
     * main().
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
     * <p>
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * <p>
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     *
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {


//        System.out.println("\n\nSTART PROBLEM");
        /**
         *  A --- B
         *  |     |
         *  |     |
         *  C --- ?
         */
        RavensFigure[][] values = new RavensFigure[2][2];
        Set<String> attributes = new HashSet<>();
        List<RavensFigure> answers = new ArrayList<>();
        /**
         * sets up hardcoded ravens objects A, B, and C
         * builds out attribute list
         */
        for (String figureName : problem.getFigures().keySet()) {
            RavensFigure thisFigure = problem.getFigures().get(figureName);
            if (figureName.equals("A")) {
                values[0][0] = thisFigure;
                //pick a random figure that will always exist to fill the attributes from
                for (String objectName : thisFigure.getObjects().keySet()) {
                    RavensObject thisObject = thisFigure.getObjects().get(objectName);
                    for (String attributeName : thisObject.getAttributes().keySet()) {
                        attributes.add(attributeName);
                    }
                }
            } else if (figureName.equals("B")) {
                values[0][1] = thisFigure;
            } else if (figureName.equals("C")) {
                values[1][0] = thisFigure;
            } else {
                answers.add(thisFigure);
            }
        }

//        Map<String, String> attributeMapOfTopLeft = getAttributeMapForRavensFigure(values[0][0]);
//        Map<String, String> attributeMapOfTopRight = getAttributeMapForRavensFigure(values[0][1]);
//        Map<String, String> attributeMapOfBottomLeft = getAttributeMapForRavensFigure(values[1][0]);
        List<RavensObject> listOfTopLeftObjects = getRavensObjectsForRavensFigure(values[0][0]);
        List<RavensObject> listOfTopRightObjects = getRavensObjectsForRavensFigure(values[0][1]);
        List<RavensObject> listOfBottomLeftObjects = getRavensObjectsForRavensFigure(values[1][0]);

        int priority = 0;
        int maxPriority = 0;
        int answerNumber = -1;
        for (RavensFigure answer : answers) {

            for (int i = 0; i < answer.getObjects().values().size(); i++) {
                //will store the shape to help figure out which symmetry check to run
                String shape = "";

                List<RavensObject> listOfAnswerObjects = getRavensObjectsForRavensFigure(answer);
                Map<String, String> attributeMapOfTopLeft = new HashMap<>();
                Map<String, String> attributeMapOfTopRight = new HashMap<>();
                Map<String, String> attributeMapOfBottomLeft = new HashMap<>();
                Map<String, String> attributeMapOfAnswer = new HashMap<>();
                if (i < listOfTopLeftObjects.size() && listOfTopLeftObjects != null && listOfTopLeftObjects.get(i) != null) {
                    attributeMapOfTopLeft = getAttributeMapForRavensObject(listOfTopLeftObjects.get(i));
                }
                if (i < listOfTopRightObjects.size() && listOfTopRightObjects != null && listOfTopRightObjects.get(i) != null) {
                    attributeMapOfTopRight = getAttributeMapForRavensObject(listOfTopRightObjects.get(i));
                }
                if (i < listOfBottomLeftObjects.size() && listOfBottomLeftObjects != null && listOfBottomLeftObjects.get(i) != null) {
                    attributeMapOfBottomLeft = getAttributeMapForRavensObject(listOfBottomLeftObjects.get(i));
                }
                if (i < listOfAnswerObjects.size() && listOfAnswerObjects != null && listOfAnswerObjects.get(i) != null) {
                    attributeMapOfAnswer = getAttributeMapForRavensObject(listOfAnswerObjects.get(i));
                }

                for (String attribute : attributes) {
                    boolean isDirectMatch = false;
                    String topLeft = attributeMapOfTopLeft.get(attribute);
                    String topRight = attributeMapOfTopRight.get(attribute);
                    String bottomLeft = attributeMapOfBottomLeft.get(attribute);
                    String answerValueAttribute = attributeMapOfAnswer.get(attribute);

                    /**
                     * DO NULL PROCESSING
                     */
                    boolean somethingIsNull = false;
                    boolean allAreNull = topLeft == null && topRight == null && bottomLeft == null && answerValueAttribute == null;
                    //replace null value with "null" string
                    if (topLeft == null) {
                        topLeft = "null";
                        somethingIsNull = true;
                    }
                    if (topRight == null) {
                        topRight = "null";
                        somethingIsNull = true;
                    }
                    if (bottomLeft == null) {
                        bottomLeft = "null";
                        somethingIsNull = true;
                    }
                    if (answerValueAttribute == null) {
                        answerValueAttribute = "null";
                        somethingIsNull = true;
                    }

//                    System.out.println("topLeft = " + topLeft + ", topRight = " + topRight + ", bottomLeft = " + bottomLeft + ", answer = " + answerValueAttribute);

                    /**
                     * HORIZONTAL COMPARISON
                     */
                    if (topLeft.equals(topRight) && bottomLeft.equals(answerValueAttribute)) {
                        isDirectMatch = true;
                    }

                    /**
                     * VERTICAL COMPARISON
                     */
                    if (topLeft.equals(bottomLeft) && topRight.equals(answerValueAttribute)) {
                        isDirectMatch = true;
                    }

                    if (bottomLeft.equals(topRight) && topLeft.equals(answerValueAttribute)) {
                        isDirectMatch = true;
                    }

                    //if you're testing angles, test symmetry
                    if (attribute.equals("angle") && !somethingIsNull) {
                        int topLeftInt = Integer.parseInt(topLeft);
                        int topRightInt = Integer.parseInt(topRight);
                        int bottomLeftInt = Integer.parseInt(bottomLeft);
                        int answerAttributeValueInt = Integer.parseInt(answerValueAttribute);

                        if (checkHorizontalSymmetry(topLeftInt, topRightInt) && checkHorizontalSymmetry(bottomLeftInt, answerAttributeValueInt)) {
                            isDirectMatch = true;
                        }

                        if (checkVerticalSymmetry(topLeftInt, bottomLeftInt) && checkVerticalSymmetry(topRightInt, answerAttributeValueInt)) {
                            isDirectMatch = true;
                        }
                    }

                    //if it's not a direct match and you're testing alignment, see if they're all different
                    if (attribute.equals("alignment") && !somethingIsNull && !isDirectMatch) {
                        if (!topLeft.equals(topRight) && !topLeft.equals(bottomLeft) && !topLeft.equals(answerValueAttribute)
                                && !topRight.equals(bottomLeft) && !topRight.equals(answerValueAttribute) &&
                                !bottomLeft.equals(answerValueAttribute)) {
                            priority = priority + 2;
                        }
                    }

                    if (isDirectMatch && !allAreNull) {
                        switch (attribute) {
                            case "angle":
                            case "shape":
                                priority = priority + 3;
                                break;
                            case "fill":
                            case "size":
                                priority = priority + 2;
                                break;
                            default:
                                priority++;
                                break;
                        }
                    }
                }
            }
            if (priority > maxPriority) {
                maxPriority = priority;
                answerNumber = Integer.parseInt(answer.getName());
            }
//            System.out.println("answer of " + answer.getName() + " has priority " + priority);
            priority = 0;

        }
//        System.out.println("END PROBLEM\n\n");

        return answerNumber;
    }

    private Map<String, String> getAttributeMapForRavensFigure(RavensFigure figure) {
        Map<String, String> attributeMap = new HashMap<>();
        for (String objectName : figure.getObjects().keySet()) {
            RavensObject thisObject = figure.getObjects().get(objectName);
            attributeMap = thisObject.getAttributes();
        }
        return attributeMap;
    }

    private Map<String, String> getAttributeMapForRavensObject(RavensObject object) {
        return object.getAttributes();
    }

    private List<RavensObject> getRavensObjectsForRavensFigure(RavensFigure figure) {
        List<RavensObject> objects = new ArrayList<>();
        SortedSet<String> keys = new TreeSet<>(figure.getObjects().keySet());
        for (String key : keys) {
            objects.add(figure.getObjects().get(key));
        }
        return objects;
    }

    private boolean checkHorizontalSymmetry(int left, int right) {
        switch (left) {
            case 0:
                if (right == 180 || right == 360) {
                    return true;
                }
                break;
            case 45:
                if (right == 135) {
                    return true;
                }
                break;
            case 135:
                if (right == 45) {
                    return true;
                }
                break;
            case 180:
                if (right == 180 || right == 360) {
                    return true;
                }
                break;
            case 225:
                if (right == 315) {
                    return true;
                }
                break;
            case 315:
                if (right == 225) {
                    return true;
                }
                break;
            case 360:
                if (right == 0 || right == 180) {
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean checkVerticalSymmetry(int top, int bottom) {
        switch (top) {
            case 0:
                if (bottom == 360) {
                    return true;
                }
                break;
            case 45:
                if (bottom == 315) {
                    return true;
                }
                break;
            case 90:
                if (bottom == 270) {
                    return true;
                }
                break;
            case 135:
                if (bottom == 225) {
                    return true;
                }
                break;
            case 180:
                if (bottom == 180 || bottom == 360) {
                    return true;
                }
                break;
            case 225:
                if (bottom == 135) {
                    return true;
                }
                break;
            case 270:
                if (bottom == 90) {
                    return true;
                }
                break;
            case 315:
                if (bottom == 45) {
                    return true;
                }
                break;
            case 360:
                if (bottom == 0) {
                    return true;
                }
                break;
        }
        return false;
    }
}

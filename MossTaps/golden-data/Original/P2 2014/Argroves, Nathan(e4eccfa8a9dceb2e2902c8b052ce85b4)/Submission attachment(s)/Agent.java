package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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


        System.out.println("\n\nSTART PROBLEM " + problem.getName());
        /**
         *  A --- B
         *  |     |
         *  |     |
         *  C --- ?
         */
        RavensFigure[][] values = new RavensFigure[3][3];
        Set<String> attributes = new HashSet<>();
        List<RavensFigure> answers = new ArrayList<>();
        int answerNumber = -1;

        if (problem.getProblemType().equals("2x2")) {
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

            List<RavensObject> listOfTopLeftObjects = getRavensObjectsForRavensFigure(values[0][0]);
            List<RavensObject> listOfTopRightObjects = getRavensObjectsForRavensFigure(values[0][1]);
            List<RavensObject> listOfBottomLeftObjects = getRavensObjectsForRavensFigure(values[1][0]);

            int priority = 0;
            int maxPriority = 0;
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
                System.out.println("answer of " + answer.getName() + " has priority " + priority);
                priority = 0;
            }
        } else {
//            BufferedImage stitchedImage = buildImageThroughVisualProcessing(problem.getName(), false);
//            checkSymmetryOfVisualImage(stitchedImage);
            /**
             * sets up hardcoded ravens objects A, B, C, D, E, F, G, H
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
                    values[0][2] = thisFigure;
                } else if (figureName.equals("D")) {
                    values[1][0] = thisFigure;
                } else if (figureName.equals("E")) {
                    values[1][1] = thisFigure;
                } else if (figureName.equals("F")) {
                    values[1][2] = thisFigure;
                } else if (figureName.equals("G")) {
                    values[2][0] = thisFigure;
                } else if (figureName.equals("H")) {
                    values[2][1] = thisFigure;
                } else {
                    answers.add(thisFigure);
                }
            }

            List<RavensObject> topLeftList = getRavensObjectsForRavensFigure(values[0][0]);
            List<RavensObject> topCenterList = getRavensObjectsForRavensFigure(values[0][1]);
            List<RavensObject> topRightList = getRavensObjectsForRavensFigure(values[0][2]);
            List<RavensObject> middleLeftList = getRavensObjectsForRavensFigure(values[1][0]);
            List<RavensObject> middleCenterList = getRavensObjectsForRavensFigure(values[1][1]);
            List<RavensObject> middleRightList = getRavensObjectsForRavensFigure(values[1][2]);
            List<RavensObject> bottomLeftList = getRavensObjectsForRavensFigure(values[2][0]);
            List<RavensObject> bottomCenterList = getRavensObjectsForRavensFigure(values[2][1]);

            int priority = 0;
            int maxPriority = 0;
            for (RavensFigure answer : answers) {

                for (int i = 0; i < answer.getObjects().values().size(); i++) {
                    //will store the shape to help figure out which symmetry check to run
                    String shape = "";

                    List<RavensObject> answerList = getRavensObjectsForRavensFigure(answer);
                    Map<String, String> topLeftMap = new HashMap<>();
                    Map<String, String> topCenterMap = new HashMap<>();
                    Map<String, String> topRightMap = new HashMap<>();
                    Map<String, String> middleLeftMap = new HashMap<>();
                    Map<String, String> middleCenterMap = new HashMap<>();
                    Map<String, String> middleRightMap = new HashMap<>();
                    Map<String, String> bottomLeftMap = new HashMap<>();
                    Map<String, String> bottomCenterMap = new HashMap<>();
                    Map<String, String> answerMap = new HashMap<>();
                    if (i < topLeftList.size() && topLeftList != null && topLeftList.get(i) != null) {
                        topLeftMap = getAttributeMapForRavensObject(topLeftList.get(i));
                    }
                    if (i < topCenterList.size() && topCenterList != null && topCenterList.get(i) != null) {
                        topCenterMap = getAttributeMapForRavensObject(topCenterList.get(i));
                    }
                    if (i < topRightList.size() && topRightList != null && topRightList.get(i) != null) {
                        topRightMap = getAttributeMapForRavensObject(topRightList.get(i));
                    }
                    if (i < middleLeftList.size() && middleLeftList != null && middleLeftList.get(i) != null) {
                        middleLeftMap = getAttributeMapForRavensObject(middleLeftList.get(i));
                    }
                    if (i < middleCenterList.size() && middleCenterList != null && middleCenterList.get(i) != null) {
                        middleCenterMap = getAttributeMapForRavensObject(middleCenterList.get(i));
                    }
                    if (i < middleRightList.size() && middleRightList != null && middleRightList.get(i) != null) {
                        middleRightMap = getAttributeMapForRavensObject(middleRightList.get(i));
                    }
                    if (i < bottomLeftList.size() && bottomLeftList != null && bottomLeftList.get(i) != null) {
                        bottomLeftMap = getAttributeMapForRavensObject(bottomLeftList.get(i));
                    }
                    if (i < bottomCenterList.size() && bottomCenterList != null && bottomCenterList.get(i) != null) {
                        bottomCenterMap = getAttributeMapForRavensObject(bottomCenterList.get(i));
                    }
                    if (i < answerList.size() && answerList != null && answerList.get(i) != null) {
                        answerMap = getAttributeMapForRavensObject(answerList.get(i));
                    }

                    for (String attribute : attributes) {
                        boolean isDirectMatch = false;
                        String topLeft = topLeftMap.get(attribute);
                        String topCenter = topCenterMap.get(attribute);
                        String topRight = topRightMap.get(attribute);
                        String middleLeft = middleLeftMap.get(attribute);
                        String middleCenter = middleCenterMap.get(attribute);
                        String middleRight = middleRightMap.get(attribute);
                        String bottomLeft = bottomLeftMap.get(attribute);
                        String bottomCenter = bottomCenterMap.get(attribute);
                        String answerValue = answerMap.get(attribute);

                        /**
                         * DO NULL PROCESSING
                         */
                        boolean somethingIsNull = false;
                        boolean allAreNull = topLeft == null && topCenter == null && topRight == null &&
                                middleLeft == null && middleCenter == null && middleRight == null &&
                                bottomLeft == null && bottomCenter == null && answerValue == null;
                        //replace null value with "null" string
                        if (topLeft == null) {
                            topLeft = "null";
                            somethingIsNull = true;
                        }
                        if (topCenter == null) {
                            topCenter = "null";
                            somethingIsNull = true;
                        }
                        if (topRight == null) {
                            topRight = "null";
                            somethingIsNull = true;
                        }
                        if (middleLeft == null) {
                            middleLeft = "null";
                            somethingIsNull = true;
                        }
                        if (middleCenter == null) {
                            middleCenter = "null";
                            somethingIsNull = true;
                        }
                        if (middleRight == null) {
                            middleRight = "null";
                            somethingIsNull = true;
                        }
                        if (bottomLeft == null) {
                            bottomLeft = "null";
                            somethingIsNull = true;
                        }
                        if (bottomCenter == null) {
                            bottomCenter = "null";
                            somethingIsNull = true;
                        }
                        if (answerValue == null) {
                            answerValue = "null";
                            somethingIsNull = true;
                        }

//                    System.out.println("topLeft = " + topLeft + ", topRight = " + topRight + ", bottomLeft = " + bottomLeft + ", answer = " + answerValueAttribute);

                        /**
                         * HORIZONTAL COMPARISON
                         */
                        if (topLeft.equals(topCenter) && topLeft.equals(topRight) && topCenter.equals(topRight) &&
                                middleLeft.equals(middleCenter) && middleLeft.equals(middleRight) && middleCenter.equals(middleRight) &&
                                bottomLeft.equals(bottomCenter) && bottomLeft.equals(answerValue) && bottomCenter.equals(answerValue)) {
                            isDirectMatch = true;
                        }

                        /**
                         * VERTICAL COMPARISON
                         */
                        if (topLeft.equals(middleLeft) && topLeft.equals(bottomLeft) && middleLeft.equals(bottomLeft) &&
                                topCenter.equals(middleCenter) && topCenter.equals(bottomCenter) && middleCenter.equals(bottomCenter) &&
                                topRight.equals(middleRight) && topRight.equals(answerValue) && middleRight.equals(answerValue)) {
                            isDirectMatch = true;
                        }

                        if (bottomLeft.equals(topRight) && topLeft.equals(answerValue)) {
                            isDirectMatch = true;
                        }

//                        System.out.println("size top left = " + attributeMapOfTopLeft.size());
//                        System.out.println("size top center = " + attributeMapOfTopCenter.size());
//                        System.out.println("size middle left = " + attributeMapOfMiddleLeft.size());

                        //if you're testing angles, test symmetry
                        if (attribute.equals("angle") && !somethingIsNull) {
                            int topLeftInt = Integer.parseInt(topLeft);
                            int topRightInt = Integer.parseInt(topRight);
                            int bottomLeftInt = Integer.parseInt(bottomLeft);
                            int answerAttributeValueInt = Integer.parseInt(answerValue);

                            if (checkHorizontalSymmetry(topLeftInt, topRightInt) && checkHorizontalSymmetry(bottomLeftInt, answerAttributeValueInt)) {
                                isDirectMatch = true;
                            }

                            if (checkVerticalSymmetry(topLeftInt, bottomLeftInt) && checkVerticalSymmetry(topRightInt, answerAttributeValueInt)) {
                                isDirectMatch = true;
                            }
                        }

                        //if it's not a direct match and you're testing alignment, see if they're all different
                        if (attribute.equals("alignment") && !somethingIsNull && !isDirectMatch) {
                            if (!topLeft.equals(topRight) && !topLeft.equals(bottomLeft) && !topLeft.equals(answerValue)
                                    && !topRight.equals(bottomLeft) && !topRight.equals(answerValue) &&
                                    !bottomLeft.equals(answerValue)) {
                                priority = priority + 2;
                            }
                        }

                        if (attribute.equals("shape") && !somethingIsNull && !isDirectMatch) {
                            if (topLeft.equals(middleCenter) && middleCenter.equals(answerValue)) {
                                priority = priority + 5;
                            }
                        }

                        if (attribute.equals("size") && !isDirectMatch) {
                            if (!somethingIsNull) {
                                if (topLeft.equals("small") && topCenter.equals("medium") && topRight.equals("large")
                                        && middleRight.equals("very large") && answerValue.equals("huge")) {
                                    priority = priority + 5;
                                } else if (topLeft.equals("huge") && topCenter.equals("very large") && topRight.equals("large")
                                        && middleRight.equals("medium") && answerValue.equals("small")) {
                                    priority = priority + 5;
                                } else if (topLeft.equals("very small") && topCenter.equals("small") && topRight.equals("medium")
                                        && middleRight.equals("large") && answerValue.equals("very large")) {
                                    priority = priority + 5;
                                } else if (topLeft.equals("very large") && topCenter.equals("large") && topRight.equals("medium")
                                        && middleRight.equals("small") && answerValue.equals("very small")) {
                                    priority = priority + 5;
                                }
                            } else {
                                if (topLeft != null && middleCenter != null && answerValue != null) {
                                    if (topLeft.equals("small") && middleCenter.equals("large") && answerValue.equals("huge")) {
                                        priority = priority + 5;
                                    }
                                }
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

                    if (sizesMatch(topLeftMap, topCenterMap, topRightMap) &&
                            sizesMatch(middleLeftMap, middleCenterMap, middleRightMap) &&
                            sizesMatch(bottomLeftMap, bottomCenterMap, answerMap)) {
                        priority = priority + 5;
                    }

                    if (topLeftList.size() < topCenterList.size() && topLeftList.size() < middleLeftList.size() &&
                            middleLeftList.size() < bottomLeftList.size() && topLeftList.size() < topRightList.size() &&
                            bottomLeftList.size() < bottomCenterList.size() && topRightList.size() < middleRightList.size() &&
                            bottomCenterList.size() < answerList.size() && middleRightList.size() < answerList.size()) {
                        priority = priority + 5;
                    }

                    if (bottomLeftList.size() < bottomCenterList.size() && bottomCenterList.size() < answerList.size() &&
                            answerList.size() < middleRightList.size() && middleRightList.size() < topRightList.size()) {
                        priority = priority + 5;
                    }
                }
                if (priority > maxPriority) {
                    maxPriority = priority;
                    answerNumber = Integer.parseInt(answer.getName());
                }
                System.out.println("answer of " + answer.getName() + " has priority " + priority);
                priority = 0;
            }
        }


        System.out.println("END PROBLEM\n\n");

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

    private boolean sizesMatch(Map<String, String> map1, Map<String, String> map2, Map<String, String> map3) {
        return map1.size() == map2.size() && map1.size() == map3.size() && map2.size() == map3.size();
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

    private BufferedImage buildImageThroughVisualProcessing(String name, boolean isTwoByTwo) {
        try {
            String path;
            if (name.contains("Challenge Problem")) {
                path = "Problems/Challenge Problems " + name.substring(name.length() - 4, name.length() - 3) + "/" + name + "/";
            } else {
                path = "Problems/Basic Problems " + name.substring(name.length() - 4, name.length() - 3) + "/" + name + "/";
            }
            System.out.println("path = " + path);
            int rows, cols;
            BufferedImage[] imgFiles;
            if (isTwoByTwo) {
                imgFiles = new BufferedImage[4];
                rows = 2;
                cols = 2;
                imgFiles[0] = ImageIO.read(new File(path + "A.png"));
                imgFiles[1] = ImageIO.read(new File(path + "B.png"));
                imgFiles[2] = ImageIO.read(new File(path + "C.png"));
                imgFiles[3] = ImageIO.read(new File(path + "1.png"));
            } else {
                imgFiles = new BufferedImage[9];
                rows = 3;
                cols = 3;
                imgFiles[0] = ImageIO.read(new File(path + "A.png"));
                imgFiles[1] = ImageIO.read(new File(path + "B.png"));
                imgFiles[2] = ImageIO.read(new File(path + "C.png"));
                imgFiles[3] = ImageIO.read(new File(path + "D.png"));
                imgFiles[4] = ImageIO.read(new File(path + "E.png"));
                imgFiles[5] = ImageIO.read(new File(path + "F.png"));
                imgFiles[6] = ImageIO.read(new File(path + "G.png"));
                imgFiles[7] = ImageIO.read(new File(path + "H.png"));
                imgFiles[8] = ImageIO.read(new File(path + "1.png"));
            }
            int chunkWidth, chunkHeight, type;
            type = imgFiles[0].getType();
            chunkWidth = imgFiles[0].getWidth();
            chunkHeight = imgFiles[0].getHeight();

            BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);
            int num = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    finalImg.createGraphics().drawImage(imgFiles[num], chunkWidth * j, chunkHeight * i, null);
                    num++;
                }
            }
            System.out.println("Image concatenated.....");
            ImageIO.write(finalImg, "png", new File("finalImg.png"));
            return finalImg;
        } catch (IOException ioe) {
            System.out.println("Couldn't find file");
            return null;
        }
    }

    public void checkSymmetryOfVisualImage(BufferedImage stitchedImage) {
        int width = stitchedImage.getWidth();
        int height = stitchedImage.getHeight();
        System.out.println("width = " + width + ", height = " + height);
        int startingPointForLeftSide = (width / 2) - 1;
        int startingPointForRightSide = width / 2;
        int priority = 0;
        for (int i = 0; i < width / 2; i++) {
            for (int j = 0; j < height; j++) {
                if (stitchedImage.getRGB(j, startingPointForLeftSide) == stitchedImage.getRGB(j, startingPointForRightSide)) {
                    priority++;
                }
            }
            startingPointForLeftSide--;
            startingPointForRightSide++;
        }

        System.out.println("priority = " + priority);
    }
}

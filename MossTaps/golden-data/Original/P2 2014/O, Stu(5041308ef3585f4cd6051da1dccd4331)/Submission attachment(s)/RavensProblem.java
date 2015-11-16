/*
 * DO NOT MODIFY THIS FILE.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */
package ravensproject;

import java.util.HashMap;

/**
 * A single Raven's Progressive Matrix problem, represented by a type (2x2 or
 * or 3x3), a String name, and a HashMap of figures. Information is also given
 * regarding whether visual or verbal representations are provided for the
 * problem.
 * 
 */
public class RavensProblem {
    private String name;
    private String problemType; //2x2 or 3x3
    private boolean hasVisualRepresentation; //true if visual representations are included
    private boolean hasVerbalRepresentation; //true if verbal representations are included
    
    private int correctAnswer;
    private int givenAnswer;
    private boolean answerReceived;
    
    private HashMap<String, RavensFigure> figures;
    
    /**
     * Initializes a new Raven's Progressive Matrix problem given a name, a
     * type, and a correct answer to the problem. Also initializes a blank
     * HashMap representing the figures in the problem.
     * 
     * Your agent does not need to use this method. 
     * 
     * @param name the name of the problem, typically a number
     * @param problemType the type of problem: 2x1, 2x2, or 3x3
     * @param correctAnswer the correct answer to the problem, a number 1 to 6
     */
    public RavensProblem(String name, String problemType, int correctAnswer, boolean hasVisual, boolean hasVerbal) {
        this.name = name;
        this.problemType = problemType;
        this.correctAnswer = correctAnswer;
        this.hasVisualRepresentation = hasVisual;
        this.hasVerbalRepresentation = hasVerbal;
        
        figures = new HashMap<>();
        givenAnswer = -1;
        answerReceived = false;
    }
    
    /**
     * Returns the correct answer to the problem.
     * 
     * In order to receive the correct answer to the problem, your Agent must
     * supply a guess (givenAnswer). Once it has supplied its guess, it will NOT
     * be able to change its answer to the problem; the answer passed as
     * givenAnswer will be stored as the answer to this problem.
     * 
     * This method is provided to enable your Agent to participate in meta-
     * reasoning by reflecting on its past incorrect answers. Using this method
     * is completely optional.
     * 
     * @param givenAnswer your Agent's answer to the problem
     * @return the correct answer to the problem
     */
    public int checkAnswer(int givenAnswer) {
        setAnswerReceived(givenAnswer);
        return correctAnswer;
    }
    /**
     * Sets your Agent's answer to this problem. This method can only be used
     * once; the first answer that is received will be stored. This method is
     * called by either checkAnswer or by the main() method.
     * 
     * Your agent does not need to use this method.
     * 
     * @param givenAnswer your Agent's answer to the problem
     */
    public void setAnswerReceived(int givenAnswer) {
        if(!answerReceived) {
            answerReceived=true;
            this.givenAnswer=givenAnswer;
        }
    }
    /**
     * Returns your Agent's answer to this problem.
     * @return your Agent's answer
     */
    public int getGivenAnswer() {
        return givenAnswer;
    }
    /**
     * Returns whether your Agent's answer is the correct answer. Your agent
     * does not need to use this method; it is used to identify whether each
     * problem is correct in main().
     * 
     * Your agent does not need to use this method.
     * 
     * @return "Correct" if your agent's answer is correct, "Skipped" if your
     * agent did not answer the problem, and "Incorrect" if your agent answered
     * the problem incorrectly.
     */
    public String getCorrect() {
        if(givenAnswer == correctAnswer) {
            return "Correct";
        } else if(givenAnswer < 0) {
            return "Skipped";
        } else {
            return "Incorrect";
        }
    }

    /**
     * Returns the type of problem: 2x2 or 3x3. 2x2 problems have 6 answer
     * choices; 3x3 problems have 8 answer choices.
     * 
     * @return a String of the problem type: "2x2" or "3x3".
     */
    public String getProblemType() {
        return problemType;
    }
    /**
     * Returns the HashMap representing the RavensFigures of the problem. The
     * key for each figure is the name of the figure. For example:
     * 
     * getFigures().get("A") would return the first frame in a problem.
     * getFigures().get("3") would return the third answer choice in a problem.
     * getFigures().get("G") would return the third row, first column of a 3x3
     * problem.
     * 
     * The value for each key is the RavensFigure corresponding to that figure
     * of the problem.
     * 
     * In 2x2, the figures are named A, B, and C. A is to B as C is to one of
     * the answer choices. Similarly, A is to C as B is to one of the answer
     * choices. In 2x2 problems, relationships are present both across the rows
     * and down the columns. The answer choices are named 1 through 6.
     * 
     * In 3x3, the figures in the first row are named from left to right A, B, 
     * and C. The figures in the second row are named from left to right D, E, 
     * and F. The figures in the third row are named from left to right G and H.
     * Relationships are present across rows and down columns: A is to B is to
     * C as D is to E is to F as G is to H is to one of the answer choices. A is
     * to D is G as B is to E is to H as C is to F is to one of the answer
     * choices. The answer choices are named 1 through 8.
     * 
     * @return a HashMap of the RavensFigures in this problem
     */
    public HashMap<String, RavensFigure> getFigures() {
        return figures;
    }
    /**
     * Returns the name of this problem.
     * 
     * Your agent does not need to use this method.
     * 
     * @return the name of the problem
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns whether a verbal representation is supplied for this problem. If
     * this problem has no verbal representation, then the HashMap of
     * RavensObject in RavensFigure will be empty.
     * 
     * Your agent may need to use this method.
     * 
     * @return true if a verbal representation is available; false if not.
     */
    public boolean hasVerbal() {
        return hasVerbalRepresentation;
    }
    
    /**
     * Returns whether a visual representation is supplied for this problem.
     * 
     * Your agent may need to use this method.
     * 
     * @return true if a verbal representation is available; false if not.
     */
    public boolean hasVisual() {
        return hasVisualRepresentation;
    }
}

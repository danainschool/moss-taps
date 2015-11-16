/**
 * DO NOT MODIFY THIS FILE.
 * 
 * When you submit your project, an alternate version of this file will be used
 * to test your code against the sample Raven's problems in this zip file, as
 * well as other problems from the Raven's Test and former students.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */

package ravensproject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The main driver file for the project. You may edit this file while debugging
 * and designing, but you should not depend on changes to this file for final
 * execution of your project. Your project will be graded using our own version
 * of this file. * 
 */

public class RavensProject {
    /**
     * The main method of the project code.
     */
    public static void main(String[] args) {
        //Loading problems from files
        ArrayList<ProblemSet> sets = new ArrayList<ProblemSet>();       // The variable 'sets' stores multiple problem sets.
                                                                        // Each problem set comes from a different folder in /Problems/
                                                                        // Additional sets of problems will be used when grading projects.
                                                                        // You may also write your own problems.
        
        Scanner r = null;
        try {                                                           // ProblemSetList.txt lists the sets to solve.
            r = new Scanner(new File("Problems" + File.separator + "ProblemSetList.txt"));
        } catch(Exception ex) {                                         // Sets will be solved in the order they appear in ProblemSetList.txt.
            System.out.println(ex);                                     // You may modify ProblemSetList.txt to toggle what sets your agent addresses.
                                                                        // Note that we will use a fresh copy of all problem sets and of ProblemSetList.txt when grading.
                                                                        // Note also that grading will use some problem sets not given in advance.
        }
        
        while(r.hasNext()) {                                            // Load each set in order.
            String line = r.nextLine();
            sets.add(new ProblemSet(line));
        }
        
        // Initializing problem-solving agent from Agent.java
        Agent agent = new Agent();                                      // Your agent will be initialized with its default constructor.
                                                                        // You may modify the default constructor in Agent.java
        
        // Running agent against each problem set
        try {
            PrintWriter results = new PrintWriter("ProblemResults.csv");    // Results will be written to ProblemResults.txt.
            PrintWriter setResults = new PrintWriter("SetResults.csv");     // Note that each run of the program will overwrite the previous results.
                                                                            // Do not write anything else to ProblemResults.txt during execution of the program.
                                                                            
            results.println("Problem,Agent's Answer,Correct?,Correct Answer");
            setResults.println("Set,Correct,Incorrect,Skipped");
            for(ProblemSet set : sets) {
                for(RavensProblem problem : set.getProblems()) {            // Your agent will solve one problem at a time.
                    try {
                        problem.setAnswerReceived(agent.Solve(problem));    // The problem will be passed to your agent as a RavensProblem object as a parameter to the Solve method
                                                                            // Your agent should return its answer at the conclusion of the execution of Solve.
                                                                            // Note that if your agent makes use of RavensProblem.checkAnswer to check its answer, the answer passed to checkAnswer() will be used.
                                                                            // Your agent cannot change its answer once it has checked its answer.
                                                                            // If your agent encounters an error before giving an answer, the question will be counted as Skipped.
                        
                        results.println(problem.getName() + "," + problem.getGivenAnswer() + "," + problem.getCorrect() + "," + problem.checkAnswer(0));
                    } catch(Exception ex) {
                        System.out.println("Error encountered in " + problem.getName());
                        results.println(problem.getName() + "," + problem.getGivenAnswer() + ",Error," + problem.checkAnswer(0));
                    }
                }
                setResults.println(set.getName() + "," + set.getTotal("Correct") + "," + set.getTotal("Incorrect") + "," + set.getTotal("Skipped"));
            }
            results.close();
            setResults.close();
        } catch(IOException ex) {
            System.out.println("Unable to create results file:");
            System.out.println(ex);
        }
    }
}

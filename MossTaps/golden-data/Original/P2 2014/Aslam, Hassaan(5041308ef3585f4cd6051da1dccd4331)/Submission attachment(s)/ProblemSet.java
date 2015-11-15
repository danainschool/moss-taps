/*
 * DO NOT MODIFY THIS FILE.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */

package ravensproject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A list of RavensProblems within one set.
 * 
 * Your agent does not need to use this class explicitly.
 * 
 */

public class ProblemSet {
    private String name;
    private ArrayList<RavensProblem> problems;
    
    /**
     * Initializes a new ProblemSet with the given name and an empty set of
     * problems.
     * 
     * Your agent does not need to use this method.
     * 
     * @param name The name of the problem set.
     */
    public ProblemSet(String name) {
        this.name=name;
        problems=new ArrayList<>();
        loadProblemSet();
    }
    
    /**
     * Returns the name of the problem set.
     * 
     * Your agent does not need to use this method. 
     * 
     * @return the name of the problem set as a String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns an ArrayList of the RavensProblems in this problem set.
     * 
     * Your agent does not need to use this method. 
     * 
     * @return the RavensProblems in this set as an ArrayList.
     */
    public ArrayList<RavensProblem> getProblems() {
        return problems;
    }
    
    /**
     * Loads the problem set from the folder whose name matches that of this
     * problem set.
     * 
     * Your agent does not need to use this method.
     */
    private void loadProblemSet() {
        Scanner r = null;
        try {
            r = new Scanner(new File("Problems" + File.separator + getName() + File.separator + "ProblemList.txt"));
        } catch(Exception ex) {
            System.out.println(ex);
        }
        
        while(r.hasNext()) {
            String line = r.nextLine();
            loadProblem(line);
        }
    }
    
    /**
     * Loads the problem from the folder whose name is given in 'problemName'.
     * 
     * Your agent does not need to use this method.     * 
     */
    private void loadProblem(String problemName) {
        Scanner r = null;
        try {
            r = new Scanner(new File("Problems" + File.separator + getName() + File.separator + problemName + File.separator + "ProblemData.txt"));
        } catch(Exception ex) {
            System.out.println(ex);
        }
        
        String problemType = r.nextLine();
        int correctAnswer = Integer.parseInt(r.nextLine());
        boolean hasVisual = Boolean.parseBoolean(r.nextLine());
        boolean hasVerbal = Boolean.parseBoolean(r.nextLine());
        
        RavensProblem newProblem = new RavensProblem(problemName, problemType, correctAnswer, hasVisual, hasVerbal);
        if(hasVerbal) {
            HashMap<String, RavensFigure> figures=new HashMap<>();
            RavensFigure currentFigure=null;
            RavensObject currentObject=null;
            while(r.hasNext()) {
                String line=r.nextLine();
                if(!line.startsWith("\t")) {
                    RavensFigure newFigure=new RavensFigure(line, problemName, getName());
                    figures.put(line, newFigure);
                    currentFigure=newFigure;
                }
                else if(!line.startsWith("\t\t")) {
                    line=line.replace("\t", "");
                    RavensObject newObject=new RavensObject(line);
                    currentFigure.getObjects().put(line, newObject);
                    currentObject=newObject;
                }
                else if(line.startsWith("\t\t")) {
                    line=line.replace("\t", "");
                    String[] split=line.split(":");
                    currentObject.getAttributes().put(split[0],split[1]);
                }
            }
            newProblem.getFigures().putAll(figures);
        }
        else {
            newProblem.getFigures().put("A", new RavensFigure("A", problemName, getName()));
            newProblem.getFigures().put("B", new RavensFigure("B", problemName, getName()));
            newProblem.getFigures().put("C", new RavensFigure("C", problemName, getName()));
            newProblem.getFigures().put("1", new RavensFigure("1", problemName, getName()));
            newProblem.getFigures().put("2", new RavensFigure("2", problemName, getName()));
            newProblem.getFigures().put("3", new RavensFigure("3", problemName, getName()));
            newProblem.getFigures().put("4", new RavensFigure("4", problemName, getName()));
            newProblem.getFigures().put("5", new RavensFigure("5", problemName, getName()));
            newProblem.getFigures().put("6", new RavensFigure("6", problemName, getName()));

            if(problemType.equals("3x3")) {
                newProblem.getFigures().put("D", new RavensFigure("D", problemName, getName()));
                newProblem.getFigures().put("E", new RavensFigure("E", problemName, getName()));
                newProblem.getFigures().put("F", new RavensFigure("F", problemName, getName()));
                newProblem.getFigures().put("G", new RavensFigure("G", problemName, getName()));
                newProblem.getFigures().put("H", new RavensFigure("H", problemName, getName()));
                newProblem.getFigures().put("7", new RavensFigure("7", problemName, getName()));
                newProblem.getFigures().put("8", new RavensFigure("8", problemName, getName()));
            }
        }
        problems.add(newProblem);
    }
    
    /**
     * Returns the total number of problems answered in this set in a certain
     * type.
     * 
     * @return the total number of problems answered in this set that match the
     * given type.
     */
    public int getTotal(String result) {
        int count = 0;
        for(RavensProblem problem : problems) {
            if(problem.getCorrect().equals(result)) {
                count++;
            }
        }
        return count;
    }
}

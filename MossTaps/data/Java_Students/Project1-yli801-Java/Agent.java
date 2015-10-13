package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This agent is designed to solve Raven's Progressive matrices problems
 * @author: Yue Li
 */
public class Agent {
	
    public Agent() {        
    }
    
    /** Main method for solving Raven's Progressive matrices problems
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
    	int anwser = -1;
    	if (problem.getProblemType().equals("2x2")){
    		anwser = solve2x2(problem);
    	}    	
        return anwser;
    }
    
    //Method for solving 2x2 problems
    public int solve2x2(RavensProblem problem){
    	int bestanswer = -1;
    	int bestscore = 0;
    	boolean tie = false;
    	List<Integer> tiedanswers = new ArrayList<Integer>();
    	
    	HashMap<String, RavensFigure> figures = problem.getFigures();
    	RavensFigure figureA = figures.get("A");
    	RavensFigure figureB = figures.get("B");
    	RavensFigure figureC = figures.get("C");
    	
    	if (problem.hasVerbal()){  
    		RavensFigure calculated = new RavensFigure("calculated", "", "");
    		TransitionList transitions = new TransitionList();

    		// match objects between Figure A and B
    		int matchrowscore = Match.matchFigures(figureA, figureB,false);
    		// match objects between Figure A and C
    		int matchcolscore = Match.matchFigures(figureA, figureC,false);
    		
    		// find patterns in a row
    		if (matchrowscore >= matchcolscore){
    			// calculate all the transitions from Figure A to Figure B
    			transitions.addAll(TransitionList.calcTransitions(figureA,figureB));
    			// apply the same transformation to Figure C and generate potential answer
    			TransitionList.transform(figureC,transitions,calculated);
    		}
    		// find patterns in a column
    		else{
    			// calculate all the transitions from Figure A to Figure C
    			transitions.addAll(TransitionList.calcTransitions(figureA,figureC));
    			// apply the same transformation to Figure B and generate potential answer
    			TransitionList.transform(figureB,transitions,calculated);
    		}
    		
    		// compare each answer to potential answer and select the best one
    		for (int i = 1; i <= 6; i++){
    			RavensFigure anwserfig = figures.get(String.valueOf(i));
    			int score = Match.matchFigures(calculated,anwserfig,true);
    			if (score > bestscore){
    				bestscore = score;
    				bestanswer = i;
    				tie = false;
    			}else if (score == bestscore){
    				tie = true;
    				tiedanswers.add(i);
    			}
    		}
    		// if multiple answers have the same score, guess the answer
    		if (tie){
    			Random random = new Random();
    			bestanswer = tiedanswers.get(random.nextInt((tiedanswers.size())));
    		}
    	}
    	return bestanswer;
    }
    
}

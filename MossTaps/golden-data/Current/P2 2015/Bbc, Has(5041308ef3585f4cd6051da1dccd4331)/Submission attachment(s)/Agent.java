package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

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
	
	private Random randomGenerator;
	
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
    	this.randomGenerator = new Random();
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
    	int answer = -1;
    	
    	if(problem.hasVerbal())
    	{
	    	if(problem.getProblemType().equalsIgnoreCase("2x2"))
	    	{
	    		answer = this.Solve2x2(problem);
	    	}
	    	else
	    	{
	    		answer = this.Solve3x3(problem);
	    	}
    	}
    	
        return answer;
    }
    
    public int Solve2x2(RavensProblem problem)
    {	    	
    	RavensFigure figureA = problem.getFigures().get("A");
    	Frame frameA = Frame.FromFigure(figureA);
    	
    	RavensFigure figureB = problem.getFigures().get("B");
    	Frame frameB = Frame.FromFigure(figureB);
    	
    	// Build template transformation
    	Hashtable<String, Change> template = Transformation.BuildTransformation(frameA, frameB);
    	
    	RavensFigure figureC = problem.getFigures().get("C");
    	Frame frameC = Frame.FromFigure(figureC);    	
    	
    	int[] scores = new int[6];
    	for(int index = 1; index < 7; index++)
    	{    		
	    	RavensFigure figureX = problem.getFigures().get(Integer.toString(index));
	    	Frame frameX = Frame.FromFigure(figureX);    	
    	
	    	Hashtable<String, Change> cToXTransformation = Transformation.BuildTransformation(frameC, frameX);	    	
	    	
	    	int points = 0;
	    	for(String key : template.keySet())
	    	{
	    		Change templateChange = template.get(key);
	    		Change change = cToXTransformation.get(key);
	    		if(change != null)
	    		{	    		
		    		if(templateChange.equals(change))
		    		{
		    			points++;
		    		}
	    		}
	    	}    	
	    	
	    	scores[index-1] = points;
    	}
    	
    	// Get the high score
    	int highValue = -1;
    	int highIndex = 1;
    	for(int index = 0; index < 6; index++)
    	{ 
    		if(scores[index] > highValue)
    		{
    			highValue = scores[index];
    			highIndex = index + 1;
    		}
    	}    	
    	
    	// Check for ties
    	int ties = -1;
    	for(int index = 0; index < 6; index++)
    	{ 
    		if(scores[index] == highValue)
    		{
    			ties++;    			
    		}
    	} 
    	
    	if(ties > 0)
    	{
    		//String text = "Problem [" + problem.getName() + "] has a tie.";
    		//System.out.println(text);
    		//highIndex = -1;
    	}
    	
    	return highIndex;
    }
    
    public int Solve3x3(RavensProblem problem)
    {   
    	System.out.println("==============================");
    	System.out.format("%s %n", problem.getName());
    	
    	int answer = -1;
    	
    	answer = this.CheckCellRowColumnConstant(problem);
    	if(answer > 0)
    	{
    		return answer;
    	}
    	
    	answer = this.CheckCellNumericProgression(problem);
    	if(answer > 0)
    	{
    		return answer;
    	}
    	
    	answer = this.CheckCellObjectPattern(problem);
    	if(answer > 0)
    	{
    		return answer;
    	}
    	
    	if(answer == -1)
    	{
    		System.out.println("No exact match found");
    	}
    	
    	return answer;
    }
    
    
    
    private int CheckCellRowColumnConstant(RavensProblem problem)
    {
    	int answer = -1;
    	
    	Cell cellA = Cell.FromRavensFigure(problem.getFigures().get("A"));
    	Cell cellB = Cell.FromRavensFigure(problem.getFigures().get("B"));
    	Cell cellC = Cell.FromRavensFigure(problem.getFigures().get("C"));
    	if(cellA.equals(cellB) && cellB.equals(cellC))
    	{
    		System.out.println("A==B && B==C");
    		
    		Cell cellD = Cell.FromRavensFigure(problem.getFigures().get("D"));
        	Cell cellE = Cell.FromRavensFigure(problem.getFigures().get("E"));
        	Cell cellF = Cell.FromRavensFigure(problem.getFigures().get("F"));
        	if(cellD.equals(cellE) && cellE.equals(cellF))
        	{
        		System.out.println("D==E && E==F");
        		
        		Cell cellG = Cell.FromRavensFigure(problem.getFigures().get("G"));
            	Cell cellH = Cell.FromRavensFigure(problem.getFigures().get("H"));
            	if(cellG.equals(cellH))
            	{
            		System.out.println("G==H.  Match found.");
            		
            		// We have a match.  Now, find the answer that matches.
            		for(int cellIndex = 1; cellIndex < 9; cellIndex++)
            		{
            			Cell cell = Cell.FromRavensFigure(problem.getFigures().get(Integer.toString(cellIndex)));
            			if(cellG.equals(cell))
            			{
            				answer = cellIndex;
            				break;
            			}
            		}
            	}        		
        	}    		
    	}   	
    	
    	return answer;
    }
    
    private int CheckCellNumericProgression(RavensProblem problem)
    {
    	int answer = -1;
    	
    	Cell cellA = Cell.FromRavensFigure(problem.getFigures().get("A"));
    	Cell cellB = Cell.FromRavensFigure(problem.getFigures().get("B"));
    	Cell cellC = Cell.FromRavensFigure(problem.getFigures().get("C"));
    	
    	int a = cellA.getObjectCount();
    	int b = cellB.getObjectCount();
    	int c = cellC.getObjectCount();
    	
    	if((b-a) == (c-b) && (a != b))
    	{
    		System.out.println("Numeric Progession for Row 1");
    		
    		Cell cellD = Cell.FromRavensFigure(problem.getFigures().get("D"));
        	Cell cellE = Cell.FromRavensFigure(problem.getFigures().get("E"));
        	Cell cellF = Cell.FromRavensFigure(problem.getFigures().get("F"));
        	
        	int d = cellD.getObjectCount();
        	int e = cellE.getObjectCount();
        	int f = cellF.getObjectCount();
        	
        	if((e-d) == (f-e) && (d != e))
        	{
        		System.out.println("Numeric Progession for Row 2");
        		
        		Cell cellG = Cell.FromRavensFigure(problem.getFigures().get("G"));
            	Cell cellH = Cell.FromRavensFigure(problem.getFigures().get("H"));
            	
            	int g = cellG.getObjectCount();
            	int h = cellH.getObjectCount();
            	
            	if(g != h)
            	{            	
	            	int next = (h - g) + h;
	            	
	            	int shapeValue1 = cellG.getObjects().get("1").getAttributes().get("shape");
	            	
	            	// We have a match.  Now, find the answer that matches.
	        		for(int cellIndex = 1; cellIndex < 9; cellIndex++)
	        		{
	        			Cell cell = Cell.FromRavensFigure(problem.getFigures().get(Integer.toString(cellIndex)));
	        			if(cell.getObjectCount() == next)
	        			{
	        				int shapeValue2 = cell.getObjects().get("1").getAttributes().get("shape");
	        				if(shapeValue1 == shapeValue2)
	        				{
	        					answer = cellIndex;
	        					break;
	        				}
	        			}
	        		}
            	}
        	}
    	}
    	
    	return answer;
    }
    
    private int CheckCellObjectPattern(RavensProblem problem)
    {
    	int answer = -1;
    	
    	HashMap<String,Cell> cells = new HashMap<String,Cell>();
    	
    	int maxObjects = 0;
    	String[] labelList = {"A","B","C","D","E","F","G","H"};
    	for(String label : labelList)
    	{
    		Cell cell = Cell.FromRavensFigure(problem.getFigures().get(label));
    		if(cell.getObjectCount() > maxObjects)
    		{
    			maxObjects = cell.getObjectCount();
    		}
    		cells.put(label, cell);    		
    	}
    	
    	Cell newCell = new Cell();
    	int count = 1;
    	String[] attributeList = {"shape","fill","size","inside","width","height"};
    	for(int index = 1; index < (maxObjects + 1); index++)
    	{	
    		CellObject newCellObject = new CellObject(Integer.toString(count));
    		
    		// Iterate through attributes
    		for(String attributeText : attributeList)
			{						
    			List<Integer> values = new ArrayList<Integer>();
    			
    			// Iterate through Cells
		    	for(String cellLabel : labelList)
		    	{	
		    		int attributeValue = 0;
		    		
		    		// Get the cell
		    		Cell cell = cells.get(cellLabel);		    		
		    		
		    		// Get cell object
		    		CellObject co = cell.getObjects().get(Integer.toString(index));
		    		if(co != null)
		    		{
		    			try {
							attributeValue = co.getAttributes().get(attributeText);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							attributeValue = 0;
						}
		    		}
		    		
		    		values.add(attributeValue);
		    	}
		    	
		    	int pattern = this.FindCellObjectConstantRowColumnNextValue(values);
		    	if(pattern != -1)
		    	{
		    		if(attributeText.equals("inside") && pattern == 0)
		    		{
		    			
		    		}
		    		else
		    		{
		    			newCellObject.getAttributes().put(attributeText, pattern);
		    		}
		    		System.out.format("Cell %d (%s=%d): Pattern Found (Constant Row-Column). %n", index, attributeText, pattern);		    		
		    	}
		    	
		    	if(pattern == -1)
		    	{
		    		if(/*attributeText.equals("size") || */attributeText.equals("width") || attributeText.equals("height"))
		    		{
		    			pattern = this.FindCellObjectSizeProgressionValue(values);
		    			if(pattern > 0)
		    			{
		    				newCellObject.getAttributes().put(attributeText, pattern);
		    				System.out.format("Cell %d (%s=%d): Pattern Found (Size Progression). %n", index, attributeText, pattern);
		    			}
		    		}
		    	}
		    	
		    	if(pattern == -1)
		    	{
		    		if(/*attributeText.equals("size") || */attributeText.equals("width") || attributeText.equals("height"))
		    		{
		    			pattern = this.FindCellObjectSizeVerticalProgressionValue(values);
		    			if(pattern > 0)
		    			{
		    				newCellObject.getAttributes().put(attributeText, pattern);
		    				System.out.format("Cell %d (%s=%d): Pattern Found (Size Progression). %n", index, attributeText, pattern);
		    			}
		    		}
		    	}
		    	
		    	if(pattern == -1)
		    	{
		    		pattern = this.FindCellObjectDistributionOf2Value(values);
		    		if(pattern > 0)
		    		{
		    			newCellObject.getAttributes().put(attributeText, pattern);
	    				System.out.format("Cell %d (%s=%d): Pattern Found (Distribution of 2). %n", index, attributeText, pattern);
		    		}
		    	}
		    	
		    	if(pattern == -1)
		    	{
		    		pattern = this.FindCellObjectDistributionOf3Value(values);
		    		if(pattern > 0)
		    		{
		    			newCellObject.getAttributes().put(attributeText, pattern);
	    				System.out.format("Cell %d (%s=%d): Pattern Found (Distribution of 3). %n", index, attributeText, pattern);
		    		}
		    	}
			}
    		
    		newCell.getObjects().put(Integer.toString(count), newCellObject);
    		count++;
    	}
    	
    	System.out.println();
    	
    	List<Integer> scoring = new ArrayList<Integer>();
    	
    	// See if there is an exact match
    	for (int cellNumber = 1; cellNumber < 9; cellNumber++)
    	{
    		Cell answerCell = Cell.FromRavensFigure(problem.getFigures().get(Integer.toString(cellNumber)));
    		if(answerCell.equals(newCell))
    		{
    			System.out.format("Answer #%d is an exact match.%n", cellNumber);
    			
    			answer = cellNumber;
    			break;
    		}
    		else
    		{
    			int score = this.CalculateCellScore(newCell, answerCell);
    			scoring.add(score);
    			
    			System.out.format("Cell #%d score = %d%n", cellNumber, score);
    		}
    	}
    	
    	List<Integer> answers = new ArrayList<Integer>();    	
    	
    	int highValue = -1;    	
    	for (int cellNumber = 0; cellNumber < 8; cellNumber++)
    	{
    		int score = scoring.get(cellNumber);
    		
    		if(score == highValue)
    		{	
    			answers.add(cellNumber + 1);
    		}
    		else if(score > highValue)
    		{
    			answers.clear();
    			
    			highValue = score;
    			answers.add(cellNumber + 1);
    		}
    	}
    	
    	if(answers.size() == 1)
    	{
    		answer = answers.get(0);
    	}
    	else if(answers.size() < 4)
    	{
    		int randomInt = this.randomGenerator.nextInt(answers.size());
    		answer = answers.get(randomInt);
    	}
    	
    	return answer;
    }
    
    private int FindCellObjectConstantRowColumnNextValue(List<Integer> values)
    {
    	int pattern = -1;
    	
    	if( (values.get(0) == values.get(1) && values.get(1) == values.get(2)) &&
    		(values.get(3) == values.get(4) && values.get(4) == values.get(5)))
    	{
    		if(values.get(6) == values.get(7))
    		{
    			pattern = values.get(6);
    		}
    	}
    	
    	return pattern;
    }
    
    private int FindCellObjectSizeProgressionValue(List<Integer> values)
    {
    	int pattern = -1;
    	
    	int a = values.get(0);
    	int b = values.get(1);
    	int c = values.get(2);
    	
    	if((b-a) == (c-b) && (a != b))
    	{
    		System.out.println("Size Progession for Row 1");
    		
    		int d = values.get(3);
        	int e = values.get(4);
        	int f = values.get(5);
        	
        	if((e - d) == (f - e) && (d != e))
        	{
        		System.out.println("Size Progession for Row 2");
        		
        		int g = values.get(6);
            	int h = values.get(7);
        		int next = (h - g) + h;
        		pattern = next;
        	}
    	}
    	
    	return pattern;
    }
    
    private int FindCellObjectSizeVerticalProgressionValue(List<Integer> values)
    {
    	int pattern = -1;
    	
    	int a = values.get(0);
    	int d = values.get(3);
    	int g = values.get(6);
    	
    	if((d-a) == (g-d) && (a != d))
    	{
    		System.out.println("Size Vert. Progession for Column 1");
    		
    		int b = values.get(1);
        	int e = values.get(4);
        	int h = values.get(7);
        	
        	if((e - b) == (h - e) && (b != e))
        	{
        		System.out.println("Size Vert. Progession for Row 2");
        		
        		int c = values.get(2);
            	int f = values.get(5);
        		int next = (f - c) + f;
        		pattern = next;
        	}
    	}
    	
    	return pattern;
    }
    
    private int FindCellObjectDistributionOf2Value(List<Integer> values)
    {
    	int pattern = -1;
    	
    	int a = values.get(0);
    	int b = values.get(1);
    	int c = values.get(2);
    	int d = values.get(3);
    	int e = values.get(4);
    	int f = values.get(5);
    	int g = values.get(6);
    	int h = values.get(7);
    	
    	if((b == c && b == d && b == f && b == g && b == h && a != b && a == e ) ||
    		(b == c && b == d && b == e && b == g && a != b && a == f && a == h ) )
    	{
    		pattern = a;
    	}
    	
    	if((a == c && a == e && a == f && a == g && a == h && a != b && b == d )|| 
		(a == c && a == d && a == e && a == h && a != b && b == f && b == g ) )
    	{
    		pattern = b;
    	}
    	
    	if(	(a == b && a == e && a == f && a == g && a != c && c == d && c == h ) ||
    		(a == b && a == d && a == f && a == h && a != c && c == e && c == g ))    		
    	{
    		pattern = c;
    	}
    	
    	return pattern;
    }
    
    private int FindCellObjectDistributionOf3Value(List<Integer> values)
    {
    	int pattern = -1;
    	
    	int a = values.get(0);
    	int b = values.get(1);
    	int c = values.get(2);
    	int d = values.get(3);
    	int e = values.get(4);
    	int f = values.get(5);
    	int g = values.get(6);
    	int h = values.get(7);
    	
    	if((a == f && a == h && b == d && c == e && c == g && a != b && b != c && a != c ) || 
    	(a == e && b == f && b == g && c == d && c == h && a != b && b != c && a != c ) )
    	{
    		pattern = -1;
    	}
    	
    	return pattern;
    }
    
    private int CalculateCellScore(Cell newCell, Cell answerCell)
    {
    	int score = 0;
    	
    	int objectCount1 = newCell.getObjectCount();
    	int objectCount2 = answerCell.getObjectCount();
    	
    	if(objectCount1 == objectCount2)
    	{
    		score += 10;
    	}
    	
    	for(String key : newCell.getObjects().keySet())
    	{
    		CellObject object1 = newCell.getObjects().get(key);
    		
    		CellObject object2 = answerCell.getObjects().get(key);
    		if(object2 != null)
    		{
    			for(String attributeKey : object1.getAttributes().keySet())
    			{
    				int attributeValue1 = object1.getAttributes().get(attributeKey);
    				
    				int attributeValue2 = -1;
    				
    				try {
						attributeValue2 = object2.getAttributes().get(attributeKey);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
    				
    				if(attributeValue1 == attributeValue2)
    				{
    					score++;
    				}    				
    			}
    		}   		
    		
    	}
    	
    	return score;
    }
}

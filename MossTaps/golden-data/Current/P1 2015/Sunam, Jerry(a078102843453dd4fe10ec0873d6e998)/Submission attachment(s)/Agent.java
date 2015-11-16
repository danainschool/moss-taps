package ravensproject;

// Uncomment these lines to access image processing.
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
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
	public static int BLACK;
	public static int WHITE;
	public static int FIG_WIDTH_PIXELS;
	public static int FIG_HEIGHT_PIXELS;
	public enum Figures_2x2 {A, B, C};
	public enum Figures_3x3 {A, B, C, D, E, F, G, H};
	//public enum Objects_2x2 {a, b, c, d, e, f,
	HashMap<String, int[][]> figureData; // maps each figure A, B, C... and 1, 2, 3... to it's corresponding 2D pixel array
	HashMap<String, HashMap<String, String>> objectAttributes;
	 
    public Agent() {
        // setup
		BLACK = new Color(0, 0, 0).getRGB();
		WHITE = new Color(255, 255, 255).getRGB();
		FIG_WIDTH_PIXELS = 184;
		FIG_HEIGHT_PIXELS = 184;
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
		System.out.println("Solving problem: " + problem.getName() + "\n");
		figureData = new HashMap<>(); // create new figureData for each problem
		objectAttributes = new HashMap<String, HashMap<String, String>>();
		
		// this loop: get each figure (A, B, C) for 2x2, (A,B,C,D,E,F,G) for 3x3
		// 			  create verbal representation (if not already exists)
		// 			  write verbal representation to ProblemData.txt file (list attributes in file)
		// after this loop: compare figures to eachother based on attributes: {A, B}, {A, C} for 2x2, elaborate for 3x3...
		//			  write transformations to temporary array or file?
		// after that loop: compare transformations from {A, B}, {A, C} and extrapolate to {C, #}, {B, #}
		
		////////////////////////////////////// LOOP #1		
		for(String figureName : problem.getFigures().keySet()) {
			//System.out.println("Iterating over figures...");
			RavensFigure thisFigure = problem.getFigures().get(figureName);
			int[][] imageData;
			
			if(!problem.hasVerbal()) {
				imageData = ParseImage(problem, figureName);
				figureData.put(figureName, imageData);
				
				// ideally, this would be able to recreate the same verbal representation
				AnalyzeImage(imageData);
				
				// if its BLACK or has BLACK OUTLINE assume it is a shape
				/// Write shape to ProblemData.txt in the same format as objects
				
				
			// no verbal representation exists
			// load image by calling ParseImage()
			// call visual algorithm to get verbal representation
			// call verbal algorithm on output given above
			// create RavensObjects and fill with proper attributes
			//		1) do this by writing to the corresponding ProblemData.txt file in the existing format
			}
		
			// all RavensObjects will exist at this point, verbal or nonverbal
			for(String objectName : thisFigure.getObjects().keySet()) {
				RavensObject thisObject = thisFigure.getObjects().get(objectName);
				
				// compare each object to each other objects
				// foreach solutionFigure
				//		foreach object 
				
				// iterate through all the objects in the Figure and figure out what matches appear (same size/shape/fill)
				for(String attributeName : thisObject.getAttributes().keySet()) {
					String attributeValue = thisObject.getAttributes().get(attributeName);
					
					if(objectAttributes.get(objectName) == null)
						objectAttributes.put(objectName, new HashMap<String, String>());
					
					objectAttributes.get(objectName).put(attributeName, attributeValue);
				}
			}
		}
		
		////////////////////////////////////// LOOP #2
		// Presumably we will have all verbal data filled in at this point		
		if(!problem.hasVerbal()) {
			System.out.println("No verbal representation! Skipping problem...");
			return -1;
		}
		
		System.out.println("Determining problem type...");
		if(problem.getProblemType().equals("2x2")) {
			// check for all figures identical, and pick the identical figure	
			if(CompareIdenticalFigures(problem.getFigures().get("A"), problem.getFigures().get("B")) &&
			   CompareIdenticalFigures(problem.getFigures().get("A"), problem.getFigures().get("C"))) {
				System.out.println("Figures identical!");
				
				String solution = SolutionContainsIdentical(problem, problem.getFigures().get("A"));
				if(!solution.equals("FALSE")) { // if there's an identical solution, return it
					if(!tryParseInt(solution)) { System.out.println("Returning a non-integer: " + solution + "!\n"); System.exit(0); }
					int solutionInt = Integer.parseInt(solution);
					int correctAnswer = problem.checkAnswer(solutionInt);
					if(correctAnswer == solutionInt) { System.out.println("Answer correct! My solution: " + solutionInt + "\n"); }
					else { System.out.println("Answer was INCORRECT! My solution: " + solutionInt + 
						   "\nCorrect solution: " + correctAnswer + "\n"); }
				}
				else {
					System.out.println("There is no identical answer in the solution!");
				}
			
			
			}
			else {
				System.out.println("Figures not identical!");
			}
			
			// compare C to Solution 1, 2, 3, 4, 5, 6
			
			// compare B to Solution 1, 2, 3, 4, 5, 6
		}
		else { // is 3x3
			// compare A, B, C
			// compare A, D, G
			// compare D, E, F
			// compare B, E, H
		
			// compare G, H to Solution 1, 2, 3, 4, 5, 6, 7, 8
			// compare C, F to Solution 1, 2, 3, 4, 5, 6, 7, 8
		
		}

		
		// if there is no best guess, skip the problem (return -1)
		
		//problem.checkAnswer();
        return -1; // return negative if skipping this problem
    }
	
	/*	Read pixels as black and white values only; 
		Detect angles such as 90 deg for squares, 60 degrees for triangles (equilateral), 
		Look for continuity within pixels in each direction in order to determine boundaries
 	 */
	public int[][] ParseImage(RavensProblem problem, String figureName) {
		int[][] imageArray = new int[FIG_WIDTH_PIXELS][FIG_HEIGHT_PIXELS];
		RavensFigure figure = problem.getFigures().get(figureName);
		BufferedImage figureImage; // = ImageIO.read(new File(figure.getVisual()));
		try { // Required by Java for ImageIO.read
			figureImage = ImageIO.read(new File(figure.getVisual()));
		} catch(Exception ex) {
			// Handle failure
			System.out.println("Failed to read image with figure name: " + figureName + "\nAborting...\n");
			figureImage = new BufferedImage(0, 0, 0);
			System.exit(0);
		}
		
		for(int i = 0; i < FIG_WIDTH_PIXELS; i++) {
			for(int j = 0; j < FIG_HEIGHT_PIXELS; j++) {
				int thisPixel = figureImage.getRGB(i, j);
				// classify as black or white (verify no inbetween values)
				// OR just use a threshold to guess if black or white, in case there are grey values, e.g. at edges
				// e.g. if(thisPixel > 240) color = BLACK; else { color = WHITE; }
				if(thisPixel != WHITE) {
					imageArray[i][j] = BLACK;
					/* System.out.println("WARNING: non-black/non-white values detected at figure " + figureName + " at pixel coords (" + i + ", " + j + ")\n" +
									   "Detected value: " + thisPixel +
									   "\nSet threshold values.\nExiting...\n"); 
					System.exit(0); */
				}
				else {
					imageArray[i][j] = WHITE;
				}
			}
		}
		return imageArray;
	}
	
	public int[][] AnalyzeImage(int[][] imageData) {	
		// create 2D array of pixels, look for adjacent pixels (can only be touching if beside, below, or at ~45 angle)
		// if there is a space between black pixels, assume separate shapes;
		// 1) get all pixel colors
		// 2) draw lines of continuity between continuous black edges
		
		int[][] objectArray = new int[FIG_WIDTH_PIXELS][FIG_HEIGHT_PIXELS]; // a 2D array keeping track of all black pixels in the current shape
		
		for(int i = 0; i < FIG_WIDTH_PIXELS; i++) {
			for(int j = 0; j < FIG_HEIGHT_PIXELS; j++) {
				// check for all pixels on borders
				// top border can't check for j < current
				// bottom border can't check for j > current
				// left border can't check for i < current
				// right border can't check for i > current
				
				if(imageData[i][j] == BLACK) {
					objectArray[i][j] = BLACK;
				}
			}
		}
		
		return objectArray;
	
		/* define heuristics for each shape
		  90 degree angle x 4 --> three pixels of form:
			[x][y],[x +/- 1][y], [
			
		*/

		// use edge detection to find "shapes"
		// use angles to parse shapes into RavensObjects
		// create Frame to represent this RavensFigure
		// parse each edge-detected shape into a RavensObject
		// fill in RavensObject with attributes
		// each Frame should have list of RavensObjects
		// after all RavensObjects are present and Frame data is filled in, 
		//		figure out relative positions for RavensFigure 
		// move above output into a verbal representation to be passed to verbal algorithm
		
	}
	
	public boolean CompareObjectsPixels(int[][] problemObject, int[][] solutionObject) {
		for(int i = 0; i < FIG_WIDTH_PIXELS; i++) {
			for(int j = 0; j < FIG_HEIGHT_PIXELS; j++) {
				if(problemObject[i][j] != solutionObject[i][j])
					return false;
			}
		}
		
		return true; // images are the exact same...
	}
	
	// public boolean CompareObjectsAttributes() {
		
		
		
		
	// }
	
	public boolean ObjectsAreSame(RavensObject first, RavensObject second) {
		HashMap<String, String> firstObjAttributes = objectAttributes.get(first.getName());
		HashMap<String, String> secondObjAttributes = objectAttributes.get(second.getName()); 
		
		if(firstObjAttributes.containsKey("shape") && secondObjAttributes.containsKey("shape") && firstObjAttributes.get("shape").equals(secondObjAttributes.get("shape"))) {
			if(firstObjAttributes.containsKey("size") && secondObjAttributes.containsKey("size") && firstObjAttributes.get("size").equals(secondObjAttributes.get("size"))) {
				if(firstObjAttributes.containsKey("fill") && secondObjAttributes.containsKey("fill") && firstObjAttributes.get("fill").equals(secondObjAttributes.get("fill"))) {
					System.out.println("Objects " + first.getName() + " and " + second.getName() + " are identical.");
					return true;				
				} else { System.out.println("firstObj fill: " + firstObjAttributes.get("fill") + " secondObj fill: " + secondObjAttributes.get("fill")); }
			} else { System.out.println("firstObj size: " + firstObjAttributes.get("size") + " secondObj size: " + secondObjAttributes.get("size")); }
		} else { System.out.println("firstObj shape: " + firstObjAttributes.get("shape") + " secondObj shape: " + secondObjAttributes.get("shape")); }
		
		return false;
	}
	
	// return ENUM of changes {IDENTICAL, TRANSFORMED, DELETION} ???
	public boolean CompareIdenticalFigures(RavensFigure firstFigure, RavensFigure secondFigure) {
		System.out.println("Called CompareIdenticalFigures successfully...");
		HashMap<String,RavensObject> firstObjects = firstFigure.getObjects();
		HashMap<String,RavensObject> secondObjects = secondFigure.getObjects();
		List<RavensObject> firstObjectsList = new ArrayList<RavensObject>(firstObjects.values());
		List<RavensObject> secondObjectsList = new ArrayList<RavensObject>(secondObjects.values());
	
		System.out.println("firstObjects size: " + firstObjects.size() + "\nsecondObjects size: " + secondObjects.size());
	
		if(firstObjects.size() != secondObjects.size()) { System.out.println("Unequal number of objects in figures!"); return false; } // unequal number of objects
		
		int i = 0;
		for(RavensObject obj : firstObjectsList) {
			//for(String attributeName : obj.getAttributes().keySet()) { System.out.println("attribute: " + obj.getAttributes().get(attributeName)); }
			RavensObject secObj = secondObjectsList.get(i); 
			//for(String attributeName : secObj.getAttributes().keySet()) { System.out.println("attribute: " + obj.getAttributes().get(attributeName)); }
			i++;		
			System.out.println("Testing if identical for objects: " + obj.getName() + " and " + secObj.getName());
			if(!ObjectsAreSame(obj, secObj)) { // if the object with the corresponding name/identifier is not the same, figures aren't the same
				System.out.println("Objects ARE NOT the same shape/size/fill.");
				return false;
			}
			
			System.out.println("Objects are the same shape/size/fill.");
			for(String attributeName : obj.getAttributes().keySet()) {
				if(attributeName.equals("inside")) break; // TODO: uhh..remove this.
				
				String attributeValue = obj.getAttributes().get(attributeName);	
				if(!attributeValue.equals(secObj.getAttributes().get(attributeName)))
					return false;	// object might appear in both, but the secondObj is transformed
			}
			
			//secondObjectsList.remove(0);
			//}
		}
		
		System.out.println("Figures " + firstFigure.getName() + " and " + secondFigure.getName() + " are identical.");
		return true; // figures are the exact same
		// has the same objects, now see if positions are the same by comparing other attributes
	}
	
	public String SolutionContainsIdentical(RavensProblem problem, RavensFigure figure) {
		HashMap<String, RavensFigure> solutionFigures = problem.getFigures();
		
		for(RavensFigure solutionFigure : solutionFigures.values()) {
			if(tryParseInt(solutionFigure.getName())) {	// only compare figure to the solution set of figures
				if(CompareIdenticalFigures(figure, solutionFigure)) {
					return solutionFigure.getName();
				}
			}
		}

		return "FALSE"; // hacky
	}
	
	// helper method for SolutionContainsIdentical
	boolean tryParseInt(String value) {  
		try {  
			Integer.parseInt(value);
			return true;  
		} catch(NumberFormatException nfe) {  
			return false;  
		}  
	}
}

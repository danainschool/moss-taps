package ravensproject;

import java.util.ArrayList;
import java.util.List;

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
    public int Solve(RavensProblem problem) { 	
    	
    	//int finalanswer = -1;
    	int answer1 = rpmCalc1(problem);
    	//int answer2 = rpmCalc2(problem);
    	
    	/*
    	//System.out.println("Answer1: " + answer1);
    	int answer2 = rpmCalc2(problem);
    	//System.out.println("Answer2: " + answer2);


    	if (answer1 == answer2) {
    		finalanswer = answer1;
    	} else if (answer1 == -1){
    		finalanswer = answer2;
    	} else if (answer2 == -1){
    		finalanswer = answer1;
    	} else if (answer1 != -1 && answer2 !=-1 && answer1 != answer2) {
    		
    	}
    	
    	System.out.println("Answer1: " + answer1);
    	System.out.println("Answer2: " + answer2);
    	*/
    	//System.out.println("FinalAnswer: " + finalanswer);
		//return finalanswer;
		
    	System.out.println("Answer1: " + answer1);
    	//System.out.println("Answer2: " + answer2);

    	return answer1;
    	//return answer2;
    }
    
    
    public int rpmLearningMethod (RavensProblem problem) {
    	
    	int answer;
    	answer = rpmCalc1(problem);
		problem.checkAnswer(answer);
		return -1;	
    }
    
    
    public int rpmCalc1(RavensProblem ravensproblem) {
    	
    	//NEED TO CHANGE problem_transforms name!!!!!!!!!!!
    	List<List<RPM_ChangeLog>> set_construction =  new ArrayList<List<RPM_ChangeLog>>();
    	
    	System.out.println("add A:B");
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "A", "B"));

    	System.out.println("add C:1");
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "1"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "2"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "3"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "4"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "5"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "C", "6"));
    	
    	int[] answer1 = rpmSetLikenessCalc(set_construction);

    	/*
    	System.out.println("add A:C");
    	problem_transforms.add(deriveTransform(ravensproblem, "A", "C"));

    	System.out.println("add B:1");
      	problem_transforms.add(deriveTransform(ravensproblem, "B", "1"));
    	problem_transforms.add(deriveTransform(ravensproblem, "B", "2"));
    	problem_transforms.add(deriveTransform(ravensproblem, "B", "3"));
    	problem_transforms.add(deriveTransform(ravensproblem, "B", "4"));
    	problem_transforms.add(deriveTransform(ravensproblem, "B", "5"));
    	problem_transforms.add(deriveTransform(ravensproblem, "B", "6"));
		*/
		
    	//determine final answer based on similarity score index, answer is represented by 0, sim. score is rep. by 1
    	System.out.println("Final answer: " + answer1[0]);
    	return answer1[0];
    }
    
    
   public int rpmCalc2(RavensProblem ravensproblem) {
    	
    	//NOT USED CURRENTLY, CODE DOES NOT PASS INTO THIS METHOD, NEED TO ADJUST TO FIGURE OUT WHAT 2 "DIMENSIONAL" RPM IS NOT YIELDING VALUE
    	List<List<RPM_ChangeLog>> set_construction =  new ArrayList<List<RPM_ChangeLog>>();
    	
    	System.out.println("add A:C");
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "A", "C"));

    	System.out.println("add B:1");
      	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "1"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "2"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "3"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "4"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "5"));
    	set_construction.add(rpmDeriveObjectTransformations(ravensproblem, "B", "6"));
    	
    	int[] answer2 = rpmSetLikenessCalc(set_construction);
    	
    	return answer2[0];
    }
        
    
    public List<RPM_ChangeLog> rpmDeriveObjectTransformations(RavensProblem ravensproblem, String figure1, String figure2) {
    	
    	System.out.println(figure1 + ":" + figure2+ "," + ravensproblem);
    	System.out.println("keyset: " + ravensproblem.getFigures().keySet());
    	System.out.println("fig1: " + ravensproblem.getFigures().get(figure1));
    	
     	//initialize arraylist to store transformations between objects between figure 1 and figure 2, using arraylist so that it can be resizable and more familiar with arrays than hashmaps
    	List<RPM_ChangeLog> rpmObjectTransformations = new ArrayList<RPM_ChangeLog>();
    		System.out.println("rpmObjectTransformations: " + rpmObjectTransformations);
			
        //initialize arraylist for existing transformations between objects between figs 1 and 2, using arraylist so that it can be resizable and more familiar with arrays than hashmaps
    	List<RavensObject> rpmAllObjectTransformations = new ArrayList<RavensObject>();
    		System.out.println("rpmAllObjectTransformations: " + rpmAllObjectTransformations);
    		
		for (int x=0; x < ravensproblem.getFigures().get(figure1).getObjects().size(); x++) {
    		System.out.println("fig1_numobjects: " + ravensproblem.getFigures().get(figure1).getObjects().size());
    		System.out.println("fig1_currentobjects: " + x);

    		Object obj_x_fig1_temp = ravensproblem.getFigures().get(figure1).getObjects().keySet().toArray()[x];
			RavensObject obj_x_fig1 = ravensproblem.getFigures().get(figure1).getObjects().get(obj_x_fig1_temp);
			System.out.println("obj_x_fig1: " + obj_x_fig1);

    		RavensObject object_x_Correlation_objectsfigure2 = rpmObjectMappingCalc_DetermineSet(obj_x_fig1, ravensproblem.getFigures().get(figure2));
    		System.out.println("object_x_Correlation_objectsfigure2: " + object_x_Correlation_objectsfigure2);
    		
    		if(object_x_Correlation_objectsfigure2 != null) rpmAllObjectTransformations.add(object_x_Correlation_objectsfigure2);
    		
    		rpmObjectTransformations.add(rpmMappedSet_DetermineObjectChange(obj_x_fig1, object_x_Correlation_objectsfigure2));
    	}
    	
    	for (int y=0; y < ravensproblem.getFigures().get(figure2).getObjects().size(); y++) {
    		System.out.println("fig2_numobjects: " + ravensproblem.getFigures().get(figure2).getObjects().size());
    		System.out.println("fig2_currentobjects: " + y);
    		
    		Object obj_y_fig2_temp = ravensproblem.getFigures().get(figure2).getObjects().keySet().toArray()[y];
			RavensObject obj_y_fig2 = ravensproblem.getFigures().get(figure2).getObjects().get(obj_y_fig2_temp);
    		System.out.println("obj_y_fig2: " + obj_y_fig2);
    		
    		if(!rpmAllObjectTransformations.contains(obj_y_fig2)) {
    			rpmObjectTransformations.add(rpmMappedSet_DetermineObjectChange(null,obj_y_fig2));        		
    		}
    	}
    	
    	return rpmObjectTransformations;
    }
    

    
    public RavensObject rpmObjectMappingCalc_DetermineSet(RavensObject ravensobject, RavensFigure ravensfigure) { 
    	
    	//Check a particular object for a mapping to all objects in the other figure
    	System.out.println("obj: " + ravensobject.getName());
    	System.out.println("fig: " + ravensfigure.getName());
    	
    	RavensObject object_mapped = null;
    	long strongest_mapping_score = 0;

    	//Check number of objects in figure 2
		for (int x=0; x < ravensfigure.getObjects().size(); x++) {
    		System.out.println("fig objects size: " + ravensfigure.getObjects().size());
    		
    		Object ravensfigure_obj_x = ravensfigure.getObjects().keySet().toArray()[x];
    		System.out.println("obj_x: " +  ravensfigure.getObjects().keySet().toArray()[x]);

    		long current_mapping_score = 0;			
    		boolean shape_mapped = false;
    		
    		//Iterate through attributes for object in figure 1
    		for(int y=0; y < ravensobject.getAttributes().size(); y++) {
    			System.out.println("y: " + y);
    			System.out.println("obj, fig 1, attribute size: " + ravensobject.getAttributes().size());
        		System.out.println("obj, fig 1, attribute keyset: " + ravensobject.getAttributes().keySet());
        		System.out.println("obj_y, fig 1, attribute type: " + ravensobject.getAttributes().keySet().toArray()[y]);
        		System.out.println("obj_y, fig 1, attribute value: " + ravensobject.getAttributes().values().toArray()[y]);

        		//Iterate through EACH attribute for current object in figure 2, everything looping through all attributes for all objects in figure 2
    			for(int z=0; z < ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().size(); z++) {
        			System.out.println("z: " + z);
    				System.out.println("obj, fig 2, attribute size: " + ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().size());
    	    		System.out.println("obj_z, fig 2, attribute keyset: " + ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet());
    	    		System.out.println("obj_z, fig 2, attribute type: " + ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z]);
    	    		System.out.println("obj_z, fig 2, attribute value: " + ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]);

    	    		/*
    	    		System.out.println("Cond 1: " + ravensobject.getAttributes().keySet().toArray()[y].toString());
    	    		System.out.println("Cond 2: " + ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString());
    	    		System.out.println("Cond 3: " + ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]));
    	    		*/
    	    	
					//Production system 1 to determine if object x in figure 1 maps to object y in figure 2
    	    		//Set correlation weight to what I deem is the most important, i.e. Shape = 1, Size = 2, .....
		    		//Check number 1 is for shape 
    	    		if (ravensobject.getAttributes().keySet().toArray()[y].toString().contains("shape") && ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString().contains("shape") 
    	    				&& ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z])) {	 
    	    			current_mapping_score = 5;
		    			System.out.println("Shape mapped");
		    			shape_mapped = true;
		    		}
		    		
    	    		//Shapes must match for additional rules to be checked, don't consider an object mapping possible if shapes do not match, only check at one level for next match
    	    		if(shape_mapped){
    	    		
		    	    		//Size mapped
		    	    		if (ravensobject.getAttributes().keySet().toArray()[y].toString().contains("size") && ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString().contains("size") &&
					    				ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]))
					    		{
				    			current_mapping_score = 1;
				    			System.out.println("Size mapped");
					    		}
					    	
					    	//Fills mapped
					    	if (ravensobject.getAttributes().keySet().toArray()[y].toString().contains("fill") && ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString().contains("fill") &&
					    				ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]))
					    		{
					    		current_mapping_score = 2;
				    			System.out.println("Fill mapped");
					    		}
					    	
					    	
					    	//Angles mapped
					    	if (ravensobject.getAttributes().keySet().toArray()[y].toString().contains("angle") && ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString().contains("angle") &&
				    				ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]))
					    		{
					    		current_mapping_score  = 3;
				    			System.out.println("Angle mapped");
					    		}
					    	
					    	//Alignments mapped
					    	if (ravensobject.getAttributes().keySet().toArray()[y].toString().contains("alignment") && ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().keySet().toArray()[z].toString().contains("alignment") &&
				    				ravensobject.getAttributes().values().toArray()[y].equals(ravensfigure.getObjects().get(ravensfigure_obj_x).getAttributes().values().toArray()[z]))
					    		{
					    		current_mapping_score  = 4;
				    			System.out.println("Alignment mapped");
					    		}
		    	    		}
					    	
    			}
    		}
    		
        	System.out.println("strongest_mapping_score: " + strongest_mapping_score);
        	System.out.println("current_mapping_score: " + current_mapping_score);
    		if(strongest_mapping_score < current_mapping_score) {
    			strongest_mapping_score = current_mapping_score;
    			object_mapped = ravensfigure.getObjects().get(ravensfigure_obj_x);
    		}

    	}
    	
    	System.out.println("Object_mapped: " + object_mapped);
		return object_mapped;
    }
    
    
    public RPM_ChangeLog rpmMappedSet_DetermineObjectChange(RavensObject mappedSet_object1, RavensObject mappedSet_object2) {
    	
    	//The two mapped objects in figure 1 and figure 2 are passed to this method to determine the change
    	RPM_ChangeLog rpmSetsObjectChange = new RPM_ChangeLog();
    	System.out.println("Loop10, obj1: " + mappedSet_object1 + "; obj2: " + mappedSet_object2);
    	
		//Production system 2 to determine the exact change between objects in the determined sets
    	System.out.println("rpmSetsObjectChange: " + rpmSetsObjectChange);
    	//first set the objects to unchanged
        rpmSetsObjectChange.change = RPM_ChangeLog.UNCHANGE;   
    	
    	//Object exists in figure 2, but not in figure 1
    	if(mappedSet_object1 == null) {
        	System.out.println("Loop11");
        	//System.out.println("null check: " + mappedSet_object2.getAttributes().keySet().toArray()[0].toString())
    		rpmSetsObjectChange.shape = mappedSet_object2.getAttributes().keySet().toArray()[0].toString();
    		rpmSetsObjectChange.change = RPM_ChangeLog.ADD;
    		return rpmSetsObjectChange;
    	}
    			
    	//Object exists in figure 1, but not in figure 2
    	if(mappedSet_object2 == null) {
        	System.out.println("Loop12");
        	rpmSetsObjectChange.change = RPM_ChangeLog.DELETE;
    		return rpmSetsObjectChange;
    	}
      	
    	System.out.println("Loop13");		   	    	
    	System.out.println("obj1: " + mappedSet_object1.getName());
    	System.out.println("obj1 keyset: " + mappedSet_object1.getAttributes().keySet());
    	System.out.println("obj2: " + mappedSet_object2.getName());
    	System.out.println("obj2 keyset: " + mappedSet_object2.getAttributes().keySet());
    	
    	for(int x=0; x < mappedSet_object1.getAttributes().size(); x++) {
    		
        	System.out.println("Loop14,obj1 attributes size: " + mappedSet_object1.getAttributes().size());
        	
    		for(int y=0; y < mappedSet_object2.getAttributes().size(); y++) 
            	{
            	
    			System.out.println("Loop15,obj2 attributes size: " + mappedSet_object2.getAttributes().size());
    			System.out.println("Loop16a: " + mappedSet_object1.getAttributes().keySet().toArray()[x]);
    			System.out.println("Loop16a_v: " + mappedSet_object1.getAttributes().values().toArray()[x]);
    			System.out.println("Loop16b: " + mappedSet_object2.getAttributes().keySet().toArray()[y]);
    			System.out.println("Loop16b_v: " + mappedSet_object2.getAttributes().values().toArray()[y]);

    			//Fill change?
    			if(mappedSet_object1.getAttributes().keySet().toArray()[x].toString().contains("fill") && mappedSet_object2.getAttributes().keySet().toArray()[y].toString().contains("fill")) {	
    				if(mappedSet_object1.getAttributes().values().toArray()[x].equals(mappedSet_object2.getAttributes().values().toArray()[y])) {
    					rpmSetsObjectChange.fill.add("UNCHANGED");
    				
    				} else {
    					System.out.println("Loop 17: ");
        				rpmSetsObjectChange.change |= RPM_ChangeLog.FILL;
    				}
    			}
    			    			
    			//Smaller or larger?
    			if(mappedSet_object1.getAttributes().keySet().toArray()[x].toString().contains("size") && mappedSet_object2.getAttributes().keySet().toArray()[y].toString().contains("size")) {
    				
    				if(
    						(mappedSet_object1.getAttributes().values().toArray()[x].equals("huge")
    						&& 
    						((mappedSet_object2.getAttributes().values().toArray()[y].equals("very large")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("large")) || 
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("medium")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("small")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("very small"))))
    					||
    						(mappedSet_object1.getAttributes().values().toArray()[x].equals("very large")
    						&& 
    						((mappedSet_object2.getAttributes().values().toArray()[y].equals("large")) || 
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("medium")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("small")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("very small"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("large")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("medium")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("small")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("very small"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("medium")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("small")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("very small"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("small")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("very small"))))) {					
    					
    						rpmSetsObjectChange.change |= RPM_ChangeLog.SMALLER;
    				}
    				
    				
    				else if(
    						(mappedSet_object1.getAttributes().values().toArray()[x].equals("very small")
    						&& 
    						((mappedSet_object2.getAttributes().values().toArray()[y].equals("huge")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("very large")) || 
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("large")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("medium")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("small"))))
    					||
    						(mappedSet_object1.getAttributes().values().toArray()[x].equals("small")
    						&& 
    						((mappedSet_object2.getAttributes().values().toArray()[y].equals("huge")) || 
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("very large")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("large")) ||
    						(mappedSet_object2.getAttributes().values().toArray()[y].equals("medium"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("medium")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("huge")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("very large")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("large"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("large")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("huge")) ||
							(mappedSet_object2.getAttributes().values().toArray()[y].equals("very large"))))
						||
							(mappedSet_object1.getAttributes().values().toArray()[x].equals("very large")
							&&
							((mappedSet_object2.getAttributes().values().toArray()[y].equals("huge"))))) {				
    					
    						rpmSetsObjectChange.change |= RPM_ChangeLog.LARGER;
    				}    			
    			
    			}
    			System.out.println("Loop18");
    			
    			
				//Rotate?
    			if(mappedSet_object1.getAttributes().keySet().toArray()[x].toString().contains("angle") && mappedSet_object2.getAttributes().keySet().toArray()[y].toString().contains("angle")) {
    				if((Integer.parseInt(mappedSet_object1.getAttributes().values().toArray()[x].toString())-Integer.parseInt(mappedSet_object2.getAttributes().values().toArray()[y].toString())) != 0) {
    					rpmSetsObjectChange.change |= RPM_ChangeLog.ROTATE;
    					System.out.println("Rotation: " + (Integer.parseInt(mappedSet_object1.getAttributes().values().toArray()[x].toString())-Integer.parseInt(mappedSet_object2.getAttributes().values().toArray()[y].toString())));
    					rpmSetsObjectChange.rotation = (Integer.parseInt(mappedSet_object1.getAttributes().values().toArray()[x].toString())-Integer.parseInt(mappedSet_object2.getAttributes().values().toArray()[y].toString()));
    				}

    			} else if(mappedSet_object1.getAttributes().keySet().toArray()[x].equals("angle") && !mappedSet_object2.getAttributes().keySet().toString().contains("angle")) {
    					rpmSetsObjectChange.change = rpmSetsObjectChange.change | RPM_ChangeLog.ROTATE;
    					rpmSetsObjectChange.rotation = Integer.parseInt(mappedSet_object1.getAttributes().values().toArray()[x].toString());
    				
        			System.out.println("Loop19");

    			} else if(mappedSet_object2.getAttributes().keySet().toArray()[y].equals("angle") && !mappedSet_object1.getAttributes().keySet().toString().contains("angle")) {
    					rpmSetsObjectChange.change = rpmSetsObjectChange.change | RPM_ChangeLog.ROTATE;
    					rpmSetsObjectChange.rotation = Integer.parseInt(mappedSet_object2.getAttributes().values().toArray()[y].toString());
    				
    			}
    			System.out.println("Loop20, rotation: " + rpmSetsObjectChange.rotation);

    			
    			//Flip?
    	    	if(mappedSet_object1.getAttributes().keySet().toArray()[x].toString().contains("alignment") && mappedSet_object2.getAttributes().keySet().toArray()[y].toString().contains("alignment")) {
    	    		if(
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("bottom-left") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("bottom-right"))
    	    			||    	    		
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("top-left") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("top-right"))
    	    			||
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("bottom-right") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("bottom-left"))
    	    			||    				
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("top-right") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("top-left"))) {
    	   			
     					rpmSetsObjectChange.change |= RPM_ChangeLog.FLIP_HORIZONTALLY;
     					rpmSetsObjectChange.flip_hort = true;
     	    			System.out.println("flip_hort: " + rpmSetsObjectChange.flip_hort);

    	   			}
    	    		else if(
    	    				
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("bottom-left") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("top-left"))
    	    			||
    	    				(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("top-left") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("bottom-left"))
        	    	    ||		
        	    	    	(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("bottom-right") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("top-right"))
        	    	    ||				
        	    	    	(mappedSet_object1.getAttributes().values().toArray()[x].toString().contains("top-right") && mappedSet_object2.getAttributes().values().toArray()[y].toString().contains("bottom-right"))) {
        	   			
         					rpmSetsObjectChange.change |= RPM_ChangeLog.FLIP_VERTICALLY;
         					rpmSetsObjectChange.flip_vert = true;
         	    			System.out.println("flip_vert: " + rpmSetsObjectChange.flip_vert);

        	    	}
        	    	
        			System.out.println("Loop21, flipped: " + rpmSetsObjectChange.change);

    	    	}
    			
    		}
    		
    	}
    	System.out.println("change: " + rpmSetsObjectChange);
    	return rpmSetsObjectChange;
    }
    
    
    public int[] rpmSetLikenessCalc(List<List<RPM_ChangeLog>> set_construction) {
    	
    	int most_similar=0, highest_likeness_value=0;
    	
    	List<RPM_ChangeLog> set_construction_BaseSet_x = set_construction.get(0);
		System.out.println("set_construction_BaseSet_x: " + set_construction_BaseSet_x);    	
		
		int set_construction_BaseSet_x_count=0;
		System.out.println("set_construction_BaseSet_x_count: " + set_construction_BaseSet_x.size());
	
		for(int r=0; r < set_construction_BaseSet_x.size(); r++) {
			if(set_construction_BaseSet_x.get(r).change != 0) set_construction_BaseSet_x_count++;
		}
		
		
    	for(int s=1; s < set_construction.size(); s++) {
    		int likeness=0;
    		
    		//Generate and test method, apply likeness score and iterate through possible solution set compared to the base set, solution/answer set with highest likeness score is set answer
    		
    		int set_construction_Answers_count=0;
    		
    		for(int t=0; t < set_construction.get(s).size(); t++) {
    			if(set_construction.get(s).get(t).change != 0) set_construction_Answers_count++;
    		}
    		
    		//Same number of changes, likeness +1
    		if(set_construction_BaseSet_x_count == set_construction_Answers_count) likeness ++ ;    		
    		System.out.println("likeness_ongoing count 1: " + likeness);  
    		System.out.println("set_construction_BaseSet_x_count: " + set_construction_BaseSet_x_count);
    		System.out.println("set_construction_Possibilities: " + set_construction_Answers_count);

    		for(int u=0; u < set_construction_BaseSet_x.size(); u++) {
    			
    			for(int v=0; v < set_construction.get(s).size(); v++) {
    				
    				//Same type of change, likeness +1
    				if(set_construction.get(s).get(v).change == set_construction_BaseSet_x.get(u).change) {
    					likeness++;
    					System.out.println("likeness_ongoing count 2: " + likeness);  
    				}
    				
    				//Shape match
    				if(set_construction.get(s).get(v).shape.equals(set_construction_BaseSet_x.get(u).shape)) {
    					likeness++;
    					likeness++;
    					System.out.println("likeness_ongoing count 3: " + likeness);  
    				}
    				
    				//Size matches
    				if(set_construction.get(s).get(v).size.equals(set_construction_BaseSet_x.get(u).size) && !set_construction.get(s).get(v).size.equals("")) {
    					likeness++;
    					likeness++;
    					System.out.println("likeness_ongoing count 4: " + likeness);  
    				}
    				
    				//Rotation matches   				
    				if((set_construction.get(s).get(v).change & RPM_ChangeLog.ROTATE) > 0 &&
    						(set_construction_BaseSet_x.get(u).change & RPM_ChangeLog.ROTATE) > 0) {
        				if(set_construction.get(s).get(v).rotation == set_construction_BaseSet_x.get(u).rotation)
        						likeness ++;
        					System.out.println("likeness_ongoing count 5: " + likeness);  
    				}
    				    		
    				//flipped_vert matches
    				if((set_construction.get(s).get(v).change & RPM_ChangeLog.FLIP_VERTICALLY) > 0 &&
    						(set_construction_BaseSet_x.get(u).change & RPM_ChangeLog.FLIP_VERTICALLY) > 0) {
        				if(set_construction.get(s).get(v).flip_vert == set_construction_BaseSet_x.get(u).flip_vert)
        						likeness++;
        						System.out.println("likeness_ongoing count 5a: " + likeness);  

    				}
    				
    				//flipped_hort matches
    				if((set_construction.get(s).get(v).change & RPM_ChangeLog.FLIP_HORIZONTALLY) > 0 &&
    						(set_construction_BaseSet_x.get(u).change & RPM_ChangeLog.FLIP_HORIZONTALLY) > 0) {
        				if(set_construction.get(s).get(v).flip_hort == set_construction_BaseSet_x.get(u).flip_hort)
        						likeness++;
        						System.out.println("likeness_ongoing count 5b: " + likeness);  

    				}
    				
    				//Fill
    				if((set_construction.get(s).get(v).change & RPM_ChangeLog.FILL) > 0 && (set_construction_BaseSet_x.get(u).change & RPM_ChangeLog.FILL) > 0) {
        				for(int w=0; w<set_construction.get(s).get(v).fill.size(); w++) {
        						if(set_construction_BaseSet_x.get(u).fill.contains(set_construction.get(s).get(v).fill.get(w))) {
        							likeness++;
        							System.out.println("likeness_ongoing count 6: " + likeness);
        						}
        				}
    				}
    			}
    		}
    		System.out.println("similarity: " + likeness);
    		System.out.println("most_similar_similarity: " + highest_likeness_value);

    		if(likeness > highest_likeness_value) 

    		{
    			highest_likeness_value = likeness;
    			most_similar = s;
    		}
    		
    		else if (likeness > highest_likeness_value) {
    		
    			highest_likeness_value = likeness;
    			most_similar = s;
    		}
    	}

    	int[] answer = new int[2];
    	answer[0] = most_similar;
    	answer[1] = highest_likeness_value;
    	return answer;
    }
    
    public class RPM_ChangeLog {
    	
    	public long change;
    	public String shape;
    	public String size;
    	public int rotation;
		public boolean flip_vert;
		public boolean flip_hort;
    	public List<String> fill;
    	
    	RPM_ChangeLog() {change=0; shape=""; size=""; rotation=0; flip_vert=false; flip_hort=false; fill = new ArrayList<String>();}
    	
    	public static final int UNCHANGE = 0;
       	public static final int DELETE = 1;
    	public static final int ADD = (1 << 1);
    	public static final int ROTATE = (1 << 2);
    	public static final int SMALLER = (1 << 3);
    	public static final int LARGER = (1 << 4);
    	public static final int FILL = (1 << 5);
		public static final int FLIP_VERTICALLY = (1 << 6);
		public static final int FLIP_HORIZONTALLY  = (1 << 7);

    
	}
}

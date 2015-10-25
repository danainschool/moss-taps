package ravensproject;

import java.util.Arrays;
import java.util.Vector;

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
    	// Skip problems that don't have verbal
    	if(!problem.hasVerbal()){
    		return -1;
    	}
    	
    	// Knowledge represent the images
    	java.util.HashMap<java.lang.String, SemanticNet> nets = new java.util.HashMap<java.lang.String, SemanticNet>();
		for(String key: problem.getFigures().keySet()){
			RavensFigure fig = problem.getFigures().get(key);
            nets.put(fig.getName(), new SemanticNet(fig));
        }
    	
		// TODO: actually need to run this in a much more complicated pattern
		double delta = CalculateDelta(nets.get("A"), nets.get("B"));
		double delta2 = CalculateDelta(nets.get("A"), nets.get("C"));
		
		//System.out.println(problem.getName() + ":  " + delta);
		//PrintNet(nets.get("B"));
		
    	// Means-End Analysis
		Vector<Transform> transformations = MeansEndAnalysis(nets.get("A"), nets.get("B"), nets.get("C"));

    	//
		SemanticNet c = nets.get("C");
		for(Transform transform : transformations)
		{
			transform.run(c);
		}
		//PrintNet(nets.get("C"));
    	
    	// Create list of nets
		// TODO: figure out a better way to construct this
		java.util.HashMap<java.lang.String, Double> scores = new java.util.HashMap<java.lang.String, Double>();
		scores.put("1", Double.MAX_VALUE);
		scores.put("2", Double.MAX_VALUE);
		scores.put("3", Double.MAX_VALUE);
		scores.put("4", Double.MAX_VALUE);
		scores.put("5", Double.MAX_VALUE);
		scores.put("6", Double.MAX_VALUE);
		
		// Go through list
		Double min = Double.MAX_VALUE;
		String ans = "-1";
		for(String name: scores.keySet()){
			Double temp = CalculateDelta(c, nets.get(name));
			scores.put(name, temp);
			
			if(temp < min)
			{
				min = temp;
				ans = name;
			}
		}
    	// TODO: handle ties
		
        return Integer.parseInt(ans);
    }
    
    private Vector<Transform> MeansEndAnalysis(SemanticNet start, SemanticNet goal, SemanticNet test)
    {
    	Vector<Transform> transformations = new Vector<Transform>();
    	// 1. Handle any adding / deleting of nodes. Should be easy, as the node names are already matched at this point
    	//Handle deletion
    	for(String key: start.nodes.keySet()){
    		if(!goal.nodes.containsKey(key)){
    			//System.out.println("Delete node " + key);
    			
    			Transform r = Transform.RemoveNode(key);
    			
    			transformations.add(r);
    		}
    	}
    	
    	//Handle new node
    	for(String key: goal.nodes.keySet()){
    		if(!start.nodes.containsKey(key)){
    			//System.out.println("Adding node " + key);
    			
    			Transform r = Transform.AddNode(goal.nodes.get(key));
    			
    			transformations.add(r);
    		}
    	}
    	
    	// 2. Make any necessary changes to the the other nodes. Most should be easy. Relations might be trickier.
    	for(String key: start.nodes.keySet()){
    		if(goal.nodes.containsKey(key)){
    			SemanticNet.Node tempStart = start.nodes.get(key);
    			SemanticNet.Node tempGoal = goal.nodes.get(key);
    			SemanticNet.Node tempTest = test.nodes.get(key);
    			//Handle attribute removals and changes
    			for(String attr: tempStart.attributes.keySet()){
    				if(!tempGoal.attributes.containsKey(attr)){
    					//System.out.println("Removing attribute - value pair: " + 
    					//						attr + ":" + tempStart.attributes.get(attr));
    					
    					Transform r = Transform.RemoveAttribute(key, attr);
    					
    					transformations.add(r);
    					
    				}
    				else if(tempStart.attributes.get(attr).compareTo(tempGoal.attributes.get(attr)) != 0) {
    					
    					// TODO: Handle lists better
    					// TODO: Handle rotations better
    					Transform r = null;
    					// Check for mirroring first
    					if(attr.equalsIgnoreCase("angle") &&
    						Transform.IsMirror(tempStart.attributes.get(attr),
    											tempGoal.attributes.get(attr),
    											tempTest.attributes.get(attr))){
    						
    						//System.out.println("Mirroring figure " + tempStart.name);
    						double rotate = -1*(Double.parseDouble(tempGoal.attributes.get(attr)) - Double.parseDouble(tempStart.attributes.get(attr)));
    						r = Transform.Rotation(key, rotate);
    					}
    					else if(attr.equalsIgnoreCase("alignment"))
    					{
    						//System.out.println("Sliding figure " + tempStart.name);
    						r = Transform.Sliding(key, tempStart.attributes.get(attr), tempGoal.attributes.get(attr));
    					}
    					// Else do normal transform
    					else {
    						//System.out.println("Changing attribute " + attr +
    						//				" from " + tempStart.attributes.get(attr) + 
    						//				" to " + tempGoal.attributes.get(attr));
    						r = Transform.PlainFill(key, attr, tempGoal.attributes.get(attr));
    					}
    					transformations.add(r);
    					
    				}
    			}
    			//Handle new attribute
    			for(String attr: tempGoal.attributes.keySet()){
    				if(!tempStart.attributes.containsKey(attr)){
    					//System.out.println("Added attribute - value pair: " + 
    					//					attr + ":" + tempGoal.attributes.get(attr));
    					
    					Transform r = Transform.AddAttribute(key, attr, tempGoal.attributes.get(attr));
    					
    					transformations.add(r);
    				}
    			}
    		}
    	}
    	
    	return transformations;
    }
    
    private double CalculateDelta(SemanticNet net1, SemanticNet net2){
    	double delta = 0;
    	
    	// Additions / Deletions
    	// TODO: Handle these better
    	delta += Math.abs(net1.nodes.size() - net2.nodes.size());
    	
    	Vector<String> net1NodeNames = new Vector<String>();
    	for(String s : net1.nodes.keySet()){
    		net1NodeNames.add(s);
    	}
    	Vector<String> net2NodeNames = new Vector<String>();
    	for(String s : net2.nodes.keySet()){
    		net2NodeNames.add(s);
    	}
    	double[][] deltas = new double[net1.nodes.size()][net2.nodes.size()];
    	for(int i = 0; i < net1.nodes.size(); ++i){
    		for(int j = 0; j < net2.nodes.size(); ++j){
    			deltas[i][j] = Double.MAX_VALUE;
    		}
    	}
    	
    	// Fill out all the deltas
		for(String key: net1.nodes.keySet()){
			SemanticNet.Node node = net1.nodes.get(key);
			for(String key2: net2.nodes.keySet()){
				SemanticNet.Node node2 = net2.nodes.get(key2);
				
				deltas[net1NodeNames.indexOf(key)][net2NodeNames.indexOf(key2)] = CalculateDeltaNodes(node, node2);		
			}
		}
		
		// Find the best pairing
		int[] pairs = new int[net1.nodes.size()];
		for(int i = 0; i < net1.nodes.size(); ++i){
			pairs[i] = -1;
		}
		
		if(net1.nodes.size() <= net2.nodes.size()){
			for(int i = 0; i < net1.nodes.size(); ++i){
				int index = -1;
				double bestDelta = Double.MAX_VALUE;
				for(int j = 0; j < net2.nodes.size(); ++j){
					if(bestDelta > deltas[i][j])
					{
						// To make sure we're not assigning over old values
						if(!(Arrays.binarySearch(pairs, j) >= 0)){
							bestDelta = deltas[i][j];
							index = j;
						}
					}
				}
				pairs[i] = index;
			}
		}
		else {
			for(int i = 0; i < net2.nodes.size(); ++i){
				int index = -1;
				double bestDelta = Double.MAX_VALUE;
				for(int j = 0; j < net1.nodes.size(); ++j){
					if(bestDelta > deltas[j][i])
					{
						// To make sure we're not assigning over old values
						if(pairs[j] == -1){
							bestDelta = deltas[j][i];
							index = j;
						}
					}
				}
				if(index != -1){
					pairs[index] = i;
				}
			}
		}
		
		// Add to delta and rename
		for(int i = 0; i < pairs.length; ++i){
			if(pairs[i] != -1){
				delta += deltas[i][pairs[i]];
								
				SemanticNet.Node closestNode = net2.nodes.get(net2NodeNames.get(pairs[i]));
				
				for(String key2: net2.nodes.keySet()){
					SemanticNet.Node temp = net2.nodes.get(key2);
					for(String attribute: temp.attributes.keySet()){
						if(attribute.compareTo("above") == 0 ||
								attribute.compareTo("overlaps") == 0 || 
								attribute.compareTo("inside") == 0)
						{
							String attrStr = temp.attributes.get(attribute).replace(closestNode.name, net1NodeNames.get(i));
							temp.attributes.put(attribute, attrStr);
						}
					}
				}
				net2.nodes.remove(closestNode.name);
				closestNode.name = net1NodeNames.get(i);
				net2.nodes.put(closestNode.name, closestNode);
			}
		}
		
    	
    	return delta;
    }
        
    // TODO: add different weights for each transformation
    private double CalculateDelta2(SemanticNet net1, 
    							  SemanticNet net2)
    {
    	double delta = 0;
    	
    	// Additions / Deletions
    	// TODO: Handle these better
    	delta += Math.abs(net1.nodes.size() - net2.nodes.size());
    	//System.out.println("Adding " + delta + " for additions/deletions");
    	
    	// Changes
    	// TODO: figure out a better algorithm to compare a net node-wise. 
    	// TODO: This compares each node from the first net to the most similar node in the second net. 
		for(String key: net1.nodes.keySet()){
			SemanticNet.Node node = net1.nodes.get(key);
			SemanticNet.Node closestNode = null;
			double bestDelta = Double.MAX_VALUE;
			for(String key2: net2.nodes.keySet()){
				SemanticNet.Node node2 = net2.nodes.get(key2);
				
				double tempDelta = CalculateDeltaNodes(node, node2);
				
				if (tempDelta < bestDelta){
					bestDelta = tempDelta;
					// TODO: use this string to exclude matching with this in the future
					closestNode = node2;
				}
			}
			delta += bestDelta;
			
			// Rename the closest node throughout the other net's references
			// TODO: Make sure this works for corner cases
			if(closestNode != null){
				for(String key2: net2.nodes.keySet()){
					SemanticNet.Node temp = net2.nodes.get(key2);
					for(String attribute: temp.attributes.keySet()){
						if(attribute.compareTo("above") == 0 ||
								attribute.compareTo("overlaps") == 0 || 
								attribute.compareTo("inside") == 0)
						{
							String attrStr = temp.attributes.get(attribute).replace(closestNode.name, node.name);
							temp.attributes.put(attribute, attrStr);
						}
					}
				}
				net2.nodes.remove(closestNode.name);
				closestNode.name = node.name;
				net2.nodes.put(closestNode.name, closestNode);
			}
			
			
			//System.out.println("Adding " + bestDelta + " for node " + node.name);
        }
    	
    	return delta;
    }
    
    private double CalculateDeltaNodes(SemanticNet.Node node1, SemanticNet.Node node2)
    {
    	double delta = 0;
    	
    	// Additions / Deletions
    	delta += Math.abs(node1.attributes.size() - node2.attributes.size());
    	//System.out.println("Adding " + delta + " for additions/deletions between nodes " + node1.name + " and " + node2.name);
    	
    	for(String attribute: node1.attributes.keySet())
    	{
    		if(!node2.attributes.containsKey(attribute)){
    			delta += 1;
    			//System.out.println("Adding 1 for node " + node2.name + " not containing " + attribute);
    			continue;
    		}
    		// TODO: Combine these two IF statements
    		// TODO: Add better handling for the relation attributes, 
    		//			for ex: inside:c and inside:a can be functionally identical
    		if(node1.attributes.get(attribute).compareTo(node2.attributes.get(attribute)) != 0 ){
    			delta += 1;
    			//System.out.println("Attribute: " + attribute);
    			//System.out.println(node1.attributes.get(attribute) + "!=" + node2.attributes.get(attribute) );
    			//System.out.println("Adding 1 for attribute " + attribute + " not matching");
    			continue;
    		}
    	}
    	
    	//System.out.println("CalculateDeltaNodes returning " + delta + " for nodes " + node1.name + " and " + node2.name);
    	
    	return delta;
    }
    
    void PrintNet(SemanticNet net)
    {
    	for(String key: net.nodes.keySet()){
    		System.out.println("Node " + net.nodes.get(key).name);
    		SemanticNet.Node node = net.nodes.get(key);
    		for(String attribute: node.attributes.keySet()){
    			System.out.println(attribute + ": " + node.attributes.get(attribute));
    		}
    		System.out.println();
    	}
    }
}

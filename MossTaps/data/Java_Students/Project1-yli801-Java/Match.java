package ravensproject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ravensproject.RavensObject;

public class Match {
	private static final String DELETEDOBJECT = "DELETED";
	public Match(){}
	
	public static int matchFigures(RavensFigure f1, RavensFigure f2, boolean b){
		int score = 0;
		Map<String, HashMap<String, Integer>> objectMatchMap = new HashMap<String, HashMap<String, Integer>>();
		int s1 = f1.getObjects().size();
		int s2 = f2.getObjects().size();
		if (b & s1==s2){
			score += 10;//only for matching anwsers, add score if objects number are the same
		}
		// if each figure has only one object
		if (s1 == 1 && s2 == 1){
			String n1 = "";
			String n2 = "";
			for(String name1 : f1.getObjects().keySet()) {
				n1 = name1;
			}
			for(String name2 : f2.getObjects().keySet()) {
				n2 = name2;
			}
			RavensObject obj1 = f1.getObjects().get(n1);
			RavensObject obj2 = f2.getObjects().get(n2);
			for (String attrName1:obj1.getAttributes().keySet()){
				String attrValue1 = obj1.getAttributes().get(attrName1);
				for (String attrName2:obj2.getAttributes().keySet()){
					String attrValue2 = obj2.getAttributes().get(attrName2);
					if(attrName2.equals(attrName1)) {
	    				if(attrValue2.equals(attrValue1)) {
	    					switch (attrName1) {
	    						case "shape":
	    							score += 20;
	    							break;
	    						case "fill" :
	    							score += 5;
	    							break;
	    						case "size":
	    							score += 15;
	    						case "angle":
	    							score += 10;
	    						default:
	    							score += 1;
	    							break;		    								
	    					}
	   					}
	   				}
				}
			}	
			HashMap<String, String> temp = f2.getObjects().get(n2).getAttributes();
			RavensObject obj = new RavensObject(n1);
			for (String attr:temp.keySet()){
				obj.getAttributes().put(attr,temp.get(attr));
			}					
			f2.getObjects().remove(n2);
			f2.getObjects().put(n1, obj);
		}else {				
	    	for(String objname1 : f1.getObjects().keySet()) {	
				RavensObject obj1 = f1.getObjects().get(objname1);    		
	    		HashMap<String, Integer> objectSimularityMap = new HashMap<String, Integer>();	 	    		
	    		for(String objname2 : f2.getObjects().keySet()) {	    			
	    			RavensObject obj2 = f2.getObjects().get(objname2);    			
	    			int simularityScore = 0; 				
		    		for(String attrName1 : obj1.getAttributes().keySet()) {		    				
		    			String attrValue1 = obj1.getAttributes().get(attrName1);	    				
			    		for(String attrName2 : obj2.getAttributes().keySet()) {			    					
			    			String attrValue2 = obj2.getAttributes().get(attrName1);			    					
			    			if(attrName2.equals(attrName1)) {
			    				if(attrValue2.equals(attrValue1)) {
			    					switch (attrName1) {
			    						case "shape":
			    							simularityScore += 20;
			    							break;
			    						case "size":
			    							simularityScore += 15;
			    							break;
			    						case "fill" :
			    							simularityScore += 5;
			    							break;
			    						case "angle":
			    							simularityScore += 10;
			    						default:
			    							simularityScore += 1;
			    							break;		    								
			    					}
			   					}
			   				}
			   			}
		    		}
		    		objectSimularityMap.put(objname2, Integer.valueOf(simularityScore));
		    		objectMatchMap.put(objname1, objectSimularityMap);
		    	}    		
	    	}
	    		    	
	    	HashMap<String, String> bestMatchesMap = findBestMatches(objectMatchMap);

			//Based on the determined best mapping, change the names of the objects to that
			//matching objects have the same name in each figure
	    	for(String matchName : bestMatchesMap.keySet()) {
	    		String value = bestMatchesMap.get(matchName);
	    		HashMap<String, String> deletedMap = new HashMap<String, String>();
	    		deletedMap.put(DELETEDOBJECT, DELETEDOBJECT);
	    		HashMap<String, String> temp = value.equals(DELETEDOBJECT) ? deletedMap : 
	    			f2.getObjects().get(value).getAttributes();
	    		
				RavensObject obj = new RavensObject(matchName);
				for (String attr:temp.keySet()){
					obj.getAttributes().put(attr,temp.get(attr));
				}
				if (!value.equals(DELETEDOBJECT)){
					f2.getObjects().remove(value);
					f2.getObjects().put(matchName, obj);
					score += objectMatchMap.get(matchName).get(value);
				}
				else score += 30;  		
	    	}
		}
		return score;
	}
	
	private static HashMap<String, String> findBestMatches(Map<String, HashMap<String, Integer>> objectMatchMap) {
		HashMap<String, String> finalMatches = new HashMap<String, String>();
		Map<String, TreeMap<String, Integer>> matchMap = new HashMap<String, TreeMap<String, Integer>>();
		// for each object in figure A, sort the similarity scores  of object in figure B
		for(String objname1 : objectMatchMap.keySet()) {
			HashMap<String, Integer> scoremap = new HashMap<String, Integer>();
			ValueComparator bvc =  new ValueComparator(scoremap);
	        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
	        scoremap.putAll(objectMatchMap.get(objname1));	        
	        sorted_map.putAll(scoremap);	    
	        matchMap.put(objname1, sorted_map);
		}
		// sort the highest score of each object in figure A
		HashMap<String, Integer> obj1scoremap = new HashMap<String, Integer>();
		ValueComparator bvc2 =  new ValueComparator(obj1scoremap);
        TreeMap<String, Integer> sorted_objscoremap = new TreeMap<String, Integer>(bvc2);
		for(String objname1: matchMap.keySet()){		
			obj1scoremap.put(objname1, matchMap.get(objname1).firstEntry().getValue());
		}
		sorted_objscoremap.putAll(obj1scoremap);	 
		//for each highest score, match two objects if they have not been matched
		Set<String> figure2obj = new HashSet<String>();
		for (String objname1 : sorted_objscoremap.keySet()){
			TreeMap<String, Integer> obj2 = matchMap.get(objname1);
			for (String objname2: obj2.keySet()){
				if (!figure2obj.contains(objname2)){
					finalMatches.put(objname1, objname2);
					figure2obj.add(objname2);
					break;
				}
			}		
			// objects that have not been matched means they are deleted
			if (!finalMatches.containsKey(objname1)){
				finalMatches.put(objname1, DELETEDOBJECT);
			}
		}
		return finalMatches;
	}

}

class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}

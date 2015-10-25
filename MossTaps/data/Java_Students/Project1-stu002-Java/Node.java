package ravensproject;

import java.util.ArrayList;

public class Node {
	
	private ArrayList<Placement> objs;
	private String name;
	
	public Node(String name) {
		
		this.name=name;
		objs=new ArrayList<Placement>();
		
	}
	
	public static boolean Compare(Node x,Node y) {
		
		boolean result=false;
		
		if(x.objs.size() == y.objs.size()){
			
			for( int cou=0; cou < x.objs.size();cou++){
				
				result=Placement.Compare(x.objs.get(cou),y.objs.get(cou));
				if(!result) break;
				
			}
			
			
		}else result=false;
		
		return result;
		
	}
	
	public void SetPlacement(Placement obj){
		
		this.objs.add(obj);
		
	}
	
	public ArrayList<Placement> GetPlacements(){
		
		return this.objs;
		
	}
	
	public Placement GetPlacement(String name){
		
		Placement result = null;
		
		for(int cou = 0; cou < objs.size();cou++) 
			if(objs.get(cou).GetStringValue("name").equals(name)) return objs.get(cou);
		
		return result;
	}
	
	public String GetName(){
		
		return name;
		
	}
	

}

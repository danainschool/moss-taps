package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	
	private ArrayList<Placement> objs;
	private String name;
	private int weight = 0;
	
	public Node(String name) {
		
		this.name=name;
		objs=new ArrayList<Placement>();
		
	}
	
	public static boolean Compare(Node x,Node y) {
		
		boolean result=false;
		
		System.out.println(x.GetName()+" <<comparing>> "+y.GetName());
		System.out.println(x.objs.size()+"<< size >>"+y.objs.size());
		
		if(x.objs.size() == y.objs.size()){
			
			for( int cou=0; cou < x.objs.size();cou++){
				
				result=Placement.Compare(x.objs.get(cou),y.objs.get(cou));
				if(!result) break;
				System.out.println(x.objs.get(cou).GetName()+" <<equal>> "+y.objs.get(cou).GetName());
			}
			
			
		}
		
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
	
	public void SetWeight(int weight) {
		
		this.weight = weight;
		
	}
	
	public int GetWeight() {
		
		return this.weight;
		
	}
	
	public void CreateSemNetHierarchy() {
		
		for(int i = 0; i < objs.size(); i++) {
			
			Placement tmp = objs.get(i);
			HashMap<String,String> semkey = tmp.GetOrientation();
			if(semkey.isEmpty() && i == 0) continue;
			else {
				
				if(semkey.containsKey("inside")) {
					
					int no = semkey.get("inside").split(",").length;
					no--;
					System.out.println("Placement position: "+i+"<>"+no);
					if(i != no) {
						Placement ref = objs.get(no);
						objs.set(i, ref);
						objs.set(no, tmp);
						i--;
					}
					
				}
			
			}

				
		}
	}


}

package ravensproject;

import java.util.HashMap;

public class Placement {
	
	private String name;
	private String fill;
	private float angle=0;
	private Element obj;
	private HashMap<String,String> orientation;

	
	public Placement(String name) {
		
		this.name = name;
		this.orientation=new HashMap<String, String>();
	}
	
	public Placement(String name,String fill,float angle,Element obj) {
		
		this.name = name;
		this.fill = fill;
		this.angle=angle;
		this.orientation=new HashMap<String, String>();
		this.obj=obj;
		
	}
	

	public static boolean Compare(Placement x,Placement y){
		
		if(x.angle == y.angle && x.fill == y.fill && Element.Compare(x.obj, y.obj)) return true;
		else return false;
		
	}
	
	public void SetFloatAttribute(String attr,float val){
		
		if(attr.equals("angle")) {
			
			this.angle=val;
		}
	}
	
	public void SetStringAttribute(String attr,String val){
		
		if(attr.equals("fill")) {
			
			this.fill=val;
		}
	}	
	
	public void SetObject(Element obj){
		
		this.obj=obj;
		
	}
	
	public void SetOrientation(String attr,String ref){
		
		
		if(ref.contains(",")) {
			
			String [] ename = ref.split(",");
			String nme = ename[ename.length - 1];
			System.out.println(attr+" : "+ename[ename.length - 1]);
			this.orientation.put(attr, nme);
			
		}else this.orientation.put(attr, ref);
		
		
	}
	
	public String GetStringValue(String attr) {
		
		if(attr.equals("name")) return this.name;
		else if (attr.equals("fill")) return this.fill;
		else return null;
		
	}
	
	public float GetAngle(){
		
		return this.angle;
	}
	
	public Element GetElement(){
		
		return this.obj;
	}
	
	public HashMap<String,String> GetOrientation(){
		
		return this.orientation;
	}
}

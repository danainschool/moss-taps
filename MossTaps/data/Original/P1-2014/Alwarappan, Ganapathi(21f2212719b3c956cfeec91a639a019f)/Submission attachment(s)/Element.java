package ravensproject;

public class Element {
	
	public String name;
	private String shape;
	private String size;
	
	public Element(String name) {
		
		this.name = name;
	}
	
	public Element(String name,String shape,String size) {
		
		this.name = name;
		this.shape=shape;
		this.size=size;
	}
	
	
	public void SetAttribute(String attr,String val){
		
		if(attr.equals("shape")) this.shape=val;
		else if(attr.equals("size")) this.size=val;
	}
	
	public String GetAttribute(String attr,String val){
		
		if(attr.equals("shape")) return this.shape;
		else if(attr.equals("size")) return this.size;
		else return null;
	}
	
	public static  boolean Compare(Element x,Element y) {
		

		if(x.shape == y.shape && x.size == y.size) return true;
		else return false;
		
	}
	
	public String GetElementName(String shape,String size) {
		
		if(this.shape.equals(shape) && this.size.equals(size)) return this.name;
		else return null;
	}
	
	public String GetElementName(){
		
		return this.name;
	}

}

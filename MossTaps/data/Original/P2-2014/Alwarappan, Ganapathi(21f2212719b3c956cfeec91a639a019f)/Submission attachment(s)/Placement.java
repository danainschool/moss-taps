package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;

public class Placement {
	
	private String name = null;
	private String shape = null;
	private String size = null;
	private String fill = null;	
	private int width = 0;
	private int height = 0;
	private float angle=0;
	private HashMap<String,String> semkey;
	private ArrayList<String> sizerule;
	private HashMap<String,String> alignrule;

	
	public Placement(String name) {
		
		this.name = name;
		this.sizerule = new ArrayList<String>();
		this.semkey=new HashMap<String, String>();
		this.alignrule=new HashMap<String, String>();
		AddRules();
	}
	
	public Placement(String name,String shape,String size,int width,int height,String fill,float angle) {
		
		this.name = name;
		this.fill = fill;
		this.angle=angle;
		this.shape=shape;
		this.size=size;
		this.width=width;
		this.height=height;
		this.semkey=new HashMap<String, String>();
		this.sizerule = new ArrayList<String>();
		this.alignrule=new HashMap<String, String>();
		AddRules();
		
	}
	
	void AddRules(){
		
		sizerule.add("very small");
		sizerule.add("small");
		sizerule.add("medium");
		sizerule.add("large");
		sizerule.add("very large");
		sizerule.add("huge");
		alignrule.put("top-left", "top-right");
		alignrule.put("top-right", "top-left");
		alignrule.put("bottom-left", "bottom-right");
		alignrule.put("bottom-right", "bottom-left");
		alignrule.put("top", "bottom");
		alignrule.put("bottom","top");
		alignrule.put("left", "right");
		alignrule.put("right","left");
		alignrule.put("above", "bellow");
		alignrule.put("bellow","above");
		alignrule.put("left-of", "right-of");
		alignrule.put("right-of","left-of");		
		
	}	
	
	public static boolean Compare(Placement x,Placement y){
		
		if(x.shape.trim().equals(y.shape.trim()) && x.size.trim().equals(y.size.trim()) && x.fill.trim().equals(y.fill.trim()) && x.angle == y.angle)
				System.out.println(x.shape +" == "+y.shape +"&&"+  x.size  +" == "+ y.size +"&&"+  x.angle  +" == "+ y.angle +"&&"+ x.fill  +" == "+ y.fill);

		if(x.shape.trim().equals(y.shape.trim()) && x.size.trim().equals(y.size.trim()) && x.fill.trim().equals(y.fill.trim()) && x.angle == y.angle)
			return true;
		else 
			return false;
		
	}
	public void SetFloatAttribute(String attr,float val){
		
		if(attr.equals("angle")) {
			
			this.angle=val;
		}
	}
	
	public void SetStringAttribute(String attr,String val){
		
		if(attr.equals("fill")) this.fill=val;
		if(attr.equals("shape")) this.shape=val;
		if(attr.equals("size")) this.size=val;
	}	
	
	public void SetIntAttribute(String attr,int val){
		
		if(attr.equals("width")) this.width=width;
		if(attr.equals("height")) this.height=height;
	
		
	}
	
	public void SetOrientation(String attr,String ref){
		
		this.semkey.put(attr, ref);
		
		
	}
	
	public String GetStringValue(String attr) {
		
		if(attr.equals("name")) return this.name;
		if (attr.equals("fill")) return this.fill;
		if(attr.equals("shape")) return this.shape;
		if (attr.equals("size")) return this.size;		
		
		return null;
		
	}
	
	public float GetAngle(){
		
		return this.angle;
	}
	
	public int GetIntValue(String attr) {
		
		if(attr.equals("width")) return this.width;
		if (attr.equals("height")) return this.height;
				
		return 0;
		
	}
	
	public HashMap<String,String> GetOrientation(){
		
		return this.semkey;
	}
	
	public String GetName() {
	
		return this.name;
	
	}
	
	public static int Compare(Placement a,Placement b,Placement c,Placement d){
		
		boolean result = false;
		int weight = 0;
		
		// shape transformation
		System.out.println("Shape transformation");
		
		if( a.shape.trim().equals(b.shape.trim()) && c.shape.trim().equals(d.shape.trim()) ||
			a.shape.trim().equals(c.shape.trim()) && b.shape.trim().equals(d.shape.trim())	) {
			System.out.println("Shape scored 1");
			weight++;
		}
		else{
			
			String str1 = a.shape.trim()+" to "+b.shape.trim();
			String str2 = c.shape.trim()+" to "+d.shape.trim();
			if(str1.equals(str2)) {
				weight++;
				System.out.println("Shape scored 1");
			}
			else {
					str1 = a.shape.trim()+" to "+c.shape.trim();
					str2 = b.shape.trim()+" to "+d.shape.trim();				
					if(str1.equals(str2)) {
						
						weight++;
						System.out.println("Shape scored 1");
					}else{
						System.out.println("Shape scored 0");
					}
			}
			
		}
		
		System.out.println("Fill transformation");
		//fill transformation
		if( a.fill.trim().equals(b.fill.trim()) && c.fill.trim().equals(d.fill.trim()) ||
				a.fill.trim().equals(c.fill.trim()) && b.fill.trim().equals(d.fill.trim())	) {
			
			weight++;
			System.out.println("Fill scored 1");
		}else{
				
				String str1 = a.fill.trim()+" to "+b.fill.trim();
				String str2 = c.fill.trim()+" to "+d.fill.trim();
				if(str1.equals(str2)) {
					
					weight++;
					System.out.println("Fill scored 1");
				}
				else {
						str1 = a.fill.trim()+" to "+c.fill.trim();
						str2 = b.fill.trim()+" to "+d.fill.trim();				
						if(str1.equals(str2)) {
							
							weight++;
							System.out.println("Fill scored 1");
						}else {
							System.out.println("Shape scored 0");
						}
				}
				
		}
		
		System.out.println("Size transformation");
		//size transformation
		if( a.size.trim().equals(b.size.trim()) && c.size.trim().equals(d.size.trim()) ||
				a.size.trim().equals(c.size.trim()) && b.size.trim().equals(d.size.trim())	) {
			
			weight++;
			System.out.println("Size scored 1");
		}
		else{
				
				int str1 = a.sizerule.indexOf(a.size.trim())-b.sizerule.indexOf(b.size.trim());
				int str2 = c.sizerule.indexOf(c.size.trim())-d.sizerule.indexOf(d.size.trim());
				//System.out.println("Size diff:"+a.size+"<>"+b.size+" = "+str1+" :: "+c.size+"<>"+d.size+" :: "+str2);
				
				if(str1 == str2) {
					
					weight++;
					System.out.println("Size scored 1");
				}
				else {
					
					str1 = a.sizerule.indexOf(a.size.trim())-c.sizerule.indexOf(c.size.trim());
					str2 = b.sizerule.indexOf(b.size.trim())-d.sizerule.indexOf(d.size.trim());
					System.out.println("Size diff:"+a.size+"<>"+c.size+" = "+str1+" :: "+b.size+"<>"+d.size+" :: "+str2);
					if(str1 == str2) {
						
						weight++;
						System.out.println("Size scored 1");
					}else {
						
						System.out.println("Size scored 0");
					}
					
				}	
		}
		
		System.out.println("Angle transformation");
		//angle transformation
		if( a.angle == b.angle && c.angle == d.angle ||
				 a.angle == c.angle && b.angle == d.angle ) {
			
			weight++;
			System.out.println("Angle scored 1");
		}
		else{
				
				float str1;
				float str2;
				
				if( a.angle > b.angle) str1 = a.angle-b.angle;
				else str1 = b.angle-a.angle;
				
				if( c.angle > d.angle) str2 = c.angle-d.angle;
				else str2 = d.angle-c.angle;				

				if(str1 == str2) {
					
					weight++;
					System.out.println("Angle scored 1");
				}
				else {
					
					if( a.angle > c.angle) str1 = a.angle-c.angle;
					else str1 = c.angle-a.angle;
					
					if( b.angle > d.angle) str2 = b.angle-d.angle;
					else str2 = d.angle-b.angle;
					
					if(str1 == str2) {
						
						weight++;
						System.out.println("Angle scored 1");
					}else {
						
						System.out.println("Angle scored 0");
					}
					
				}	
		}
		
		//Alignment transformation.
		System.out.println("Alignment transformation");
		if( !a.semkey.isEmpty() && !b.semkey.isEmpty() && !c.semkey.isEmpty() && !d.semkey.isEmpty()) {

			String [] akeys = (String []) a.semkey.keySet().toArray(new String[a.semkey.keySet().size()]);
			String [] bkeys = (String []) b.semkey.keySet().toArray(new String[b.semkey.keySet().size()]);
			String [] ckeys = (String []) c.semkey.keySet().toArray(new String[c.semkey.keySet().size()]);
			String [] dkeys = (String []) d.semkey.keySet().toArray(new String[d.semkey.keySet().size()]);
			
			if( akeys.length == bkeys.length && ckeys.length == dkeys.length) {

				
				for(int i =0; i < akeys.length; i++) {
					
					if(akeys[i].equals("alignment") && bkeys[i].equals("alignment") && ckeys[i].equals("alignment") && dkeys[i].equals("alignment")) {
					
						String aval = a.semkey.get(akeys[i]);
						String bval = b.semkey.get(bkeys[i]);
						String cval = c.semkey.get(ckeys[i]);
						String dval = d.semkey.get(dkeys[i]);
						
						if(aval.equals(bval) && cval.equals(dval)) {
							weight++;
							System.out.println("Alignment scored 1");					
						}else {
							
							String aopp = a.alignrule.get(aval);
							String copp = c.alignrule.get(cval);
							System.out.println("Matching:"+aopp+"<>"+bval+" :: "+copp+"<>"+dval);
							if(aopp.trim().equals(bval.trim()) && copp.trim().equals(dval.trim())) {
								weight++;
								System.out.println("Alignment scored 1");
							}else{
								
								System.out.println("No Alignment match:"+aopp+"<>"+copp);
							}
							
						}
						
						
					}else {
						int x = 0;
						int y = 0;
						System.out.println("Comparing keys");
						if(akeys.length < bkeys.length) {
							
							for(int num = 0; num < akeys.length;num++) {
								
								if(akeys[num] == bkeys[num]) x++;
							}
							
						}else{
							for(int num = 0; num < bkeys.length;num++) {
								
								if(akeys[num] == bkeys[num]) x++;
							}					
						}
						if(ckeys.length < dkeys.length) {
							
							for(int num = 0; num < ckeys.length;num++) {
								
								if(ckeys[num] == dkeys[num]) y++;
							}
							
						}else{
							for(int num = 0; num < dkeys.length;num++) {
								
								if(ckeys[num] == dkeys[num]) y++;
							}					
						}
						
						if(x == y){
							
							weight++;
							System.out.println("Added weight");
						}
						else {
							
							if(akeys.length < ckeys.length) {
								
								for(int num = 0; num < akeys.length;num++) {
									
									if(akeys[num] == ckeys[num]) x++;
								}
								
							}else{
								for(int num = 0; num < ckeys.length;num++) {
									
									if(akeys[num] == ckeys[num]) x++;
								}					
							}
							if(bkeys.length < dkeys.length) {
								
								for(int num = 0; num < bkeys.length;num++) {
									
									if(bkeys[num] == dkeys[num]) y++;
								}
								
							}else{
								for(int num = 0; num < dkeys.length;num++) {
									
									if(bkeys[num] == dkeys[num]) y++;
								}					
							}
							
							if(x == y) {
								
								weight++;
								System.out.println("Added weight");
							}
							else System.out.println("Key comparision null");
						}						

					
					}
				}
			}else {
				
				int x = 0;
				int y = 0;
				System.out.println("Comparing keys");
				if(akeys.length < bkeys.length) {
					
					for(int num = 0; num < akeys.length;num++) {
						
						if(akeys[num] == bkeys[num]) x++;
					}
					
				}else{
					for(int num = 0; num < bkeys.length;num++) {
						
						if(akeys[num] == bkeys[num]) x++;
					}					
				}
				if(ckeys.length < dkeys.length) {
					
					for(int num = 0; num < ckeys.length;num++) {
						
						if(ckeys[num] == dkeys[num]) y++;
					}
					
				}else{
					for(int num = 0; num < dkeys.length;num++) {
						
						if(ckeys[num] == dkeys[num]) y++;
					}					
				}
				
				if(x == y){
					
					weight++;
					System.out.println("Added weight");
				}
				else {
					
					if(akeys.length < ckeys.length) {
						
						for(int num = 0; num < akeys.length;num++) {
							
							if(akeys[num] == ckeys[num]) x++;
						}
						
					}else{
						for(int num = 0; num < ckeys.length;num++) {
							
							if(akeys[num] == ckeys[num]) x++;
						}					
					}
					if(bkeys.length < dkeys.length) {
						
						for(int num = 0; num < bkeys.length;num++) {
							
							if(bkeys[num] == dkeys[num]) y++;
						}
						
					}else{
						for(int num = 0; num < dkeys.length;num++) {
							
							if(bkeys[num] == dkeys[num]) y++;
						}					
					}
					
					if(x == y) {
						
						weight++;
						System.out.println("Added weight");
					}
					else System.out.println("Key comparision null");
				}
			}
		}else{
			
			System.out.println("Sem key is empty");
		}
		
	return weight;
	
}
	


}

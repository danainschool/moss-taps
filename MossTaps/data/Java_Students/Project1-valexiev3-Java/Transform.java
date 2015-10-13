package ravensproject;

public class Transform {
   public String node;
   public String attr;
   public String value;
   public SemanticNet.Node semnode;
   public double angle;

   public Transform(String n) {
	   node = n;
   }
   
   public Transform(String n, String a, String v){
	   node = n;
	   attr = a;
	   value = v;
   }
   
   public Transform(SemanticNet.Node nd)
   {
	   semnode = nd;
   }
   
   public Transform(String n, Double a)
   {
	   node = n;
	   angle = a;
   }

   public void run(SemanticNet net) {
   }
   
   public static Transform RemoveNode(String node) {
	   return new Transform(node){
			@Override
			public void run(SemanticNet net) {
				net.nodes.remove(node);
			}
		};
   }
   
   public static Transform AddNode(SemanticNet.Node node) {
	   return new Transform(node){
			@Override
			public void run(SemanticNet net) {
				net.nodes.put(semnode.name, semnode);
			};
		};
   }
   
   public static Transform PlainFill(String n, String a, String v) {
	   return new Transform(n, a, v){
			@Override
			public void run(SemanticNet net){
				SemanticNet.Node temp = net.nodes.get(node);
				// TODO: figure out how to handle missing nodes in the apply phase
				if(temp != null)
				{
					temp.attributes.put(attr, value);
					net.nodes.put(node, temp);
				}
			}
		};
   }
   
   public static boolean IsMirror(String start, String goal, String test) {
	   
	   double rotate1 = Double.parseDouble(goal) - Double.parseDouble(start);
	   double rotate2 = Double.parseDouble(test) - Double.parseDouble(start);
	   
	   return (rotate1 == -1*rotate2) || (Math.abs(rotate1 - rotate2) == 180);
   }
   
   public static Transform Rotation(String name, Double angle) {
	   return new Transform(name, angle){
		   @Override
		   public void run(SemanticNet net){
			   	SemanticNet.Node temp = net.nodes.get(node);
			   	// TODO: figure out how to handle missing nodes in the apply phase
				if(temp != null)
				{
					double newAngle = Double.parseDouble(temp.attributes.get("angle")) + angle;
					temp.attributes.put("angle", Double.toString(newAngle));
					net.nodes.put(node, temp);
				}
		   }
	   };
   }
   
   public static Transform Sliding(String name, String oldAlign, String newAlign)
   {
	   if((oldAlign.contains("top") && newAlign.contains("top")) ||
			   (oldAlign.contains("bottom") && newAlign.contains("bottom"))){
		   // Horizontal slide
		   return new Transform(name){
			   @Override
			   public void run(SemanticNet net){
				   SemanticNet.Node temp = net.nodes.get(node);
				   // TODO: Figure out how to handle missing nodes
				   if(temp != null){
					   String attr = temp.attributes.get("alignment");
					   String newAttr = "";
					   if(attr.contains("right")){
						   newAttr = attr.replace("right", "left");
					   }
					   else{
						   newAttr = attr.replace("left", "right");
					   }
					   temp.attributes.put("alignment", newAttr);
					   net.nodes.put(node, temp); 
				   }
			   }
		   };
	   }
	   else if((oldAlign.contains("right") && newAlign.contains("right")) ||
			   (oldAlign.contains("left") && newAlign.contains("left"))){
		   // Vertical slide
		   return new Transform(name){
			   @Override
			   public void run(SemanticNet net){
				   SemanticNet.Node temp = net.nodes.get(node);
				   // TODO: Figure out how to handle missing nodes
				   if(temp != null){
					   String attr = temp.attributes.get("alignment");
					   String newAttr = "";
					   if(attr.contains("top")){
						   newAttr = attr.replace("top", "bottom");
					   }
					   else{
						   newAttr = attr.replace("bottom", "top");
					   }
					   temp.attributes.put("alignment", newAttr);
					   net.nodes.put(node, temp); 
				   }
			   }
		   };
	   }
	   else {
		   // Diagonal slide
		   return new Transform(name){
			   @Override
			   public void run(SemanticNet net){
				   SemanticNet.Node temp = net.nodes.get(node);
				   // TODO: Figure out how to handle missing nodes
				   if(temp != null){
					   String attr = temp.attributes.get("alignment");
					   String newAttr = "";
					   if(attr.contains("top")){
						   newAttr = attr.replace("top", "bottom");
					   }
					   else{
						   newAttr = attr.replace("bottom", "top");
					   }
					   if(attr.contains("right")){
						   newAttr = newAttr.replace("right", "left");
					   }
					   else{
						   newAttr = newAttr.replace("left", "right");
					   }
					   temp.attributes.put("alignment", newAttr);
					   net.nodes.put(node, temp); 
				   }
			   }
		   };
	   }
   }
   
   public static Transform RemoveAttribute(String name, String attr) {
	   return new Transform(name, attr, ""){
			@Override
			public void run(SemanticNet net){
				SemanticNet.Node temp = net.nodes.get(node);
				temp.attributes.remove(attr);
				net.nodes.put(node, temp);
			}
		};
   }
   
   public static Transform AddAttribute(String node, String attr, String value) {
	   return new Transform(node, attr, value){
			@Override
			public void run(SemanticNet net){
				SemanticNet.Node temp = net.nodes.get(node);
				temp.attributes.put(attr, value);
				net.nodes.put(node, temp);
			}
		};
   }
}

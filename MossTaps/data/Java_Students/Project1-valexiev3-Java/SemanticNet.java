package ravensproject;

// TODO: do we even need this?
public class SemanticNet {
	
	public class Node {
		public String name;
		public java.util.HashMap<java.lang.String,String> attributes;
		
		public Node(RavensObject obj)
		{
			name = obj.getName();
			attributes = obj.getAttributes();
		}
	}
	
	public java.util.HashMap<java.lang.String,Node> nodes = new java.util.HashMap<java.lang.String,Node>(); 
	
	public SemanticNet(RavensFigure fig)
	{
		for(String key: fig.getObjects().keySet()){
			RavensObject obj = fig.getObjects().get(key);
			nodes.put(obj.getName(), new Node(obj));
        }
	}
	
}

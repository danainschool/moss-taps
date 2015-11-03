package ravensproject;


import java.util.Collection;
import java.util.ArrayList;


public class KnowledgeDB {
	
	private RuleInferencer rule;
	private	String problemType;
    public ArrayList<Node> options;
    public ArrayList<Node> questions;
	
	public KnowledgeDB(String type) {
		
		this.options = new ArrayList<Node>();
		this.questions = new ArrayList<Node>();
		this.rule = new RuleInferencer();
		this.problemType=type;
		
	}
		
	public String GetProblemType() {
		
		return this.problemType;
	}
	
	
	public void EliminateOptions(int d) {
		
		for( int i=0; i < options.size();i++) {
			if(options.get(i).GetPlacements().size() != d) {
				options.remove(i);
				i--;
			}
				
		}
		
		System.out.println("Effective options:"+options.size());
		
	}
	
	
	public RuleInferencer GetRuleInferencer() {
		
		return rule;
		
	}
	
    public void DecomposeElements(Collection<RavensFigure> rfsets){
    	
    	for(RavensFigure fig : rfsets) {
    		
    		Node fnode= new Node(fig.getName());
    		
    		System.out.println("Node: "+fig.getName());
    		
    		if(fig.getName().trim().matches("[A-Z]"))
    			this.questions.add(fnode);
    		else
    			this.options.add(fnode);

    		for(String objName : fig.getObjects().keySet()) {

    			RavensObject obj = fig.getObjects().get(objName);
    			Placement pos = new Placement(obj.getName());
    			fnode.SetPlacement(pos);
				
    			System.out.println("Placement: "+obj.getName());
    			

    			
			    for(String attr : obj.getAttributes().keySet()) {
			    	
			    		
						
			    		if( attr.equals("shape")) pos.SetStringAttribute("shape",obj.getAttributes().get(attr));
			    		else
						if( attr.equals("size")) pos.SetStringAttribute("size",obj.getAttributes().get(attr));
						else
						if( attr.equals("fill")) pos.SetStringAttribute("fill",obj.getAttributes().get(attr));
						else
						if( attr.equals("angle")) pos.SetFloatAttribute("angle",Float.valueOf(obj.getAttributes().get(attr)));
						else
			    		if( attr.equals("height")) {
			    			
			    			String tmp = obj.getAttributes().get("width")+"-"+obj.getAttributes().get("height");
			    			if(tmp.equals("huge-large") || tmp.equals("large-huge")) tmp="huge";
			    			if(tmp.equals("small-large") || tmp.equals("large-small")) tmp="small";
			    			if(tmp.equals("small-huge") || tmp.equals("huge-small")) tmp="medium";
			    			
			    			pos.SetStringAttribute("size",tmp);
			    			
			    		}
			    		else {
			    			
			    			if( !attr.equals("width") &&  !attr.equals("height") )
			    				pos.SetOrientation(attr,obj.getAttributes().get(attr));
			    		}
			    			

			    }

			    
    	     }
    		
    		fnode.CreateSemNetHierarchy();
    		
    	}
    	
    	System.out.println("Questions: "+questions.size()+" Options:"+options.size());
    	rule.GenerateRule(this.questions);
    	
    }
    
   public String Resolver(){
	   
	   String result = rule.DoEqualityReasoning(this.questions,this.options);
	   
	   if(result == null){
		   
		  result = rule.DoTranformReasoning(this.questions,this.options);
		  
	   }
	   
	   return result;
	   
   }
    
	
}

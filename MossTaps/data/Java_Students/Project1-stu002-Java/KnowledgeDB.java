package ravensproject;


import java.util.Collection;
import java.util.ArrayList;


public class KnowledgeDB {
	
	private RuleInferencer rule;
	private	String problemType;
    public ArrayList<Element> elements;
    public ArrayList<Node> options;
    public ArrayList<Node> questions;
	
	public KnowledgeDB(String type) {
		
		this.elements = new ArrayList<Element>();
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
    		
    		System.out.println(fig.getName());
    		
    		if(fig.getName().trim().matches("[A-Z]"))
    			this.questions.add(fnode);
    		else
    			this.options.add(fnode);

    		for(String objName : fig.getObjects().keySet()) {

    			RavensObject obj = fig.getObjects().get(objName);
    			Placement pos = new Placement(obj.getName());
    			fnode.SetPlacement(pos);
    			System.out.println("Placement size: "+fnode.GetPlacements().size());
    			
    			String shape = obj.getAttributes().get("shape");
    			String size = obj.getAttributes().get("size");
    			
			    for(String attr : obj.getAttributes().keySet()) {
			    	
			    	if( !attr.equals("shape") &&  !attr.equals("size") ) {
			    		System.out.println("Placement: "+attr);
			    		if( attr.equals("angle")) {
			    			
			    			String val = obj.getAttributes().get(attr);
			    			pos.SetFloatAttribute("angle",Float.valueOf(val));
			    		}else{
			    			if( attr.equals("fill")) {
			    				
			    				String val = obj.getAttributes().get("fill");
			    				pos.SetStringAttribute("fill", val);
			    			}else
			    				pos.SetOrientation(attr,obj.getAttributes().get(attr));
			    			
			    		}
			    	}

    			
			    }

    			String name = null;
    			
    		   if(!this.elements.isEmpty()) {
    			   
    			   for(int cou=0;cou < this.elements.size();cou++) {
    				   
    				  name = elements.get(cou).GetElementName(shape, size);
    				  if(name != null) {
    					  
    					  pos.SetObject(elements.get(cou));
    					  break;
    				  }
    			   }
    		   }
    		   
    		   if(name == null){
    			   
    			   Element ety = new Element(objName,shape,size);
    			   elements.add(ety);
    			   pos.SetObject(ety);
    			   
    		   }
    			
    		}
    		
    	}
    	
    	System.out.println("Questions: "+questions.size()+" Opions:"+options.size()+" Elements:"+elements.size());
    	
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

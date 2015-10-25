package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;

public class RuleInferencer {
	

	private int d = 0;
	private int i = 0;

	
	public RuleInferencer() {
		

	}
	
	public void GenerateRule(ArrayList<Node> questions) {
		
		int rw = 0;
		int cl = 0;
		int ea = 0;
		int eb = 0;
		int ec = 0;
		boolean two = false;
		
		if(questions.size() == 3)
			two=true;
		
		if(two) {
			
			Node x = questions.get(0);
			Node y = questions.get(1);
			Node z = questions.get(2);
			
			ea = x.GetPlacements().size();
			eb = y.GetPlacements().size();
			ec = z.GetPlacements().size();
			
			System.out.println(ea+" : "+eb+" : "+ec);
			
			if(ea == eb) rw = ec;
			else 
				if(ea > eb) rw = ec-(ea-eb);
				else
					if(eb > ea) rw = ec+(eb-ea);
			
			if(ea == ec) cl = eb;
			else
				if(ea > ec) cl = eb-(ea-ec);
				else
					if(ec > ea) cl = eb+(ec-ea);
			
			if(rw == cl) d = rw;
			
			System.out.println("2x2: "+x.GetName()+" : "+y.GetName()+" : "+z.GetName()+" Size: "+d);
			
			
			
		}else{
			
			Node a = questions.get(0);
			Node b = questions.get(1);
			Node c = questions.get(2);
			Node d = questions.get(3);
			Node e = questions.get(4);
			Node f = questions.get(5);
			Node g = questions.get(6);
			Node h = questions.get(7);
			
			if( a.GetPlacements().size() == b.GetPlacements().size() && b.GetPlacements().size() == c.GetPlacements().size() &&
					d.GetPlacements().size() == e.GetPlacements().size() &&  e.GetPlacements().size() == f.GetPlacements().size() &&
					g.GetPlacements().size() == h.GetPlacements().size()) i=g.GetPlacements().size();
			else {
				
				if( a.GetPlacements().size() == d.GetPlacements().size() && d.GetPlacements().size() == g.GetPlacements().size() &&
						b.GetPlacements().size() == e.GetPlacements().size() &&  e.GetPlacements().size() == h.GetPlacements().size() &&
						c.GetPlacements().size() == f.GetPlacements().size()) i=g.GetPlacements().size();
				else if( a.GetPlacements().size() - b.GetPlacements().size() == b.GetPlacements().size() - c.GetPlacements().size() &&
						 d.GetPlacements().size() - e.GetPlacements().size() == e.GetPlacements().size() - f.GetPlacements().size() ) i = g.GetPlacements().size() - h.GetPlacements().size();
				else if( a.GetPlacements().size() - d.GetPlacements().size() == d.GetPlacements().size() - g.GetPlacements().size() &&
						b.GetPlacements().size() -  e.GetPlacements().size() ==  e.GetPlacements().size() - h.GetPlacements().size()) i = c.GetPlacements().size() - f.GetPlacements().size();
				
			}
			

			
		}
		
	}
	
	public int GetTotalElements(String val) {
		
		int result = 0;
		
		if(val.equals("D")) result=this.d;
		else result=this.i;
		
		return result;
		
	}
	
	public String DoEqualityReasoning(ArrayList<Node> questions,ArrayList<Node> answers){
		
		Node a,b,c,d,e,f,g,h,i;

		if( answers.size() == 1 ) return answers.get(0).GetName();
		
		if(questions.size() == 3){
		//2x2
			
			a=questions.get(0);
			b=questions.get(1);
			c=questions.get(2);
			
			// 100 % equality
			if( Node.Compare(a, b) && Node.Compare(a, c) ) {

				for(int cou=0;cou < answers.size();cou++)
					if(Node.Compare(a, answers.get(cou))) return answers.get(cou).GetName();
				
			}
			
			for(int cou=0;cou < answers.size();cou++) {
				
				d = answers.get(cou);
				
				if(Node.Compare(a, b)){
					
					if(Node.Compare(c, d)) return d.GetName();

				}else{
					
					if(Node.Compare(a, c)) {
						
						if(Node.Compare(b, d)) return d.GetName();
					}
					
				}
			
			}
			
		}else{
			
			//3x3
			a = questions.get(0);
			b = questions.get(1);
			c = questions.get(2);
			d = questions.get(3);
			e = questions.get(4);
			f = questions.get(5);
			g = questions.get(6);
			h = questions.get(7);
			
			for(int cou=0;cou < answers.size();cou++) {
				
				i = answers.get(cou);
				
				/*if(Node.Compare(a, b) && Node.Compare(, c) && (Node.Compare(a, b) && Node.Compare(b, c)){
					
					if(Node.Compare(c, d)) return d.GetName();

				}else{
					
					if(Node.Compare(a, c)) {
						
						if(Node.Compare(b, d)) return d.GetName();
					}
					
				}*/
			
			}			
			
			
			
			
			
		}
		
		System.out.println("System not able to resolve : null");
		
		return null;
		
	}
	
public String DoTranformReasoning(ArrayList<Node> questions,ArrayList<Node> answers){
		
		Node a,b,c,d,e,f,g,h,i;
		ArrayList<Integer> weight = new ArrayList<Integer>();
        String result = null;
		
		if( answers.size() == 1 ) return answers.get(0).GetName();
		
		if(questions.size() == 3){
		//2x2
			
			a=questions.get(0);
			b=questions.get(1);
			c=questions.get(2);
			
			for(int cou=0;cou < answers.size();cou++) {
				
				d = answers.get(cou);
				ArrayList<String> qrrule = GetTransRule(a,b);
				ArrayList<String> arrule = GetTransRule(c,d);
				ArrayList<String> qcrule = GetTransRule(a,c);
				ArrayList<String> acrule = GetTransRule(b,d);
				int j =0;
				if(CompareRules(qrrule,arrule))
					j=1;
				if(CompareRules(qcrule,acrule))
					j++;
				weight.add(j);
				
			}
			
			
			for(int cou=0;cou < answers.size();cou++) {
				
				if(weight.get(cou).intValue() == 2) return answers.get(cou).GetName();
				
			}
			
			for(int cou=0,found=0;cou < answers.size();cou++) {
				
				if(weight.get(cou).intValue() == 1) {
					
					found++;
					if(result == null && found == 1)
						result = answers.get(cou).GetName();
					if(found > 1) result = null;
				}
				
			}
			
			

			
		}else{
			
			//3x3
		}
		
		return result;

}


private ArrayList<String> GetTransRule(Node x,Node y){
	
	ArrayList<String> rule = new ArrayList<String>();
	ArrayList<Placement> xp = x.GetPlacements();
	ArrayList<Placement> yp = y.GetPlacements();
	
	for(int cou=0; cou < xp.size(); cou++) {
		
		boolean found = false;
		
		for(int no=0; no < yp.size(); no++) {
	
			if(xp.get(cou).GetElement().GetElementName().equals(yp.get(no).GetElement().GetElementName())) {
			   
				found = true;
				rule.add("Element detected");
				// Angular diff
				if(xp.get(cou).GetAngle() > yp.get(no).GetAngle())
					rule.add("Angulear diff:"+String.valueOf(xp.get(cou).GetAngle()-yp.get(no).GetAngle()));
				else {
				 
					if(yp.get(no).GetAngle() > xp.get(cou).GetAngle())
						rule.add("Angulear diff:"+String.valueOf(yp.get(no).GetAngle()-xp.get(cou).GetAngle()));
					else
						rule.add("Equal Angle");
				}
			
				// fill diff
				//rule.add("Fill: "+xp.get(cou).GetStringValue("fill")+"->"+yp.get(no).GetStringValue("fill"));
				if(xp.get(cou).GetStringValue("fill").equals(yp.get(no).GetStringValue("fill")))
					rule.add("Fill Same");
				else
					rule.add("Fill: "+xp.get(cou).GetStringValue("fill")+"->"+yp.get(no).GetStringValue("fill"));
				
				//positional diff

			}
		}
		
		if(!found){
			
			rule.add("Element deleted");
			rule.add("no angle");
			rule.add("no fill");
			rule.add("no position");
		}
	}
	
	return rule;
	
}





private boolean CompareRules(ArrayList<String> rule1,ArrayList<String> rule2){
	
	System.out.println("Rule1: "+rule1.toString());
	System.out.println("Rule2: "+rule2.toString());	
	return rule1.toString().equals(rule2.toString());
	
}


}

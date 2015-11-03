package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;

public class RuleInferencer {
	

	private int elm = -1;
	
	
	
	public RuleInferencer() {
		

	}
	
	public void GenerateRule(ArrayList<Node> questions) {
		
		int rw = 0;
		int cl = 0;
		int ea = 0;
		int eb = 0;
		int ec = 0,ed=0,ee=0,ef=0,eg=0,eh=0;
		Node a,b,c,d,e,f,g,h;
		boolean two = false;
		
		if(questions.size() == 3)
			two=true;
		
		if(two) {
			
			a = questions.get(0);
			b = questions.get(1);
			c = questions.get(2);
			
			ea = a.GetPlacements().size();
			eb = b.GetPlacements().size();
			ec = c.GetPlacements().size();
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
			
			if(rw == cl) elm = rw;
			
			
		}else {
		
			a = questions.get(0);
			b = questions.get(1);
			c = questions.get(2);
			d = questions.get(3);
			e = questions.get(4);
			f = questions.get(5);
			g = questions.get(6);
			h = questions.get(7);

			
			ea = a.GetPlacements().size();
			eb = b.GetPlacements().size();
			ec = c.GetPlacements().size();
			ed = d.GetPlacements().size();
			ee = e.GetPlacements().size();
			ef = f.GetPlacements().size();
			eg = g.GetPlacements().size();
			eh = h.GetPlacements().size();
			
			if( ea == eb && eb == ec && ec == ed && ed == ee && ee == ef && ef == eg && eg == eh) elm = ea;
			else {
				
				if( ea == eb && eb == ec && ed == ee && ee == ef && eg == eh) elm = eg;
				else {
					
					if( ea == ed && ed == eg && eb == ee && ee == eh && ec == ef) elm = ec;
					else {
						
						if( ea-eb == eb-ec && ed-ee == ee-ef){
							
							if( eg > eh ) elm = eh - (eg-eh);
							else elm = eh + (eh-eg);
						}else{
							
							if(ea == eb && eb == ec && ed == ef) elm = ef;
							
						}
							
					}
				}
			}
		}
		

			System.out.println("Elemination decider: "+elm);
			
	}
	
	public int GetTotalElements() {
		

		return this.elm;
		
	}
	
	public String DoEqualityReasoning(ArrayList<Node> questions,ArrayList<Node> answers){
		
		Node a,b,c,d;
		int size =  answers.size();
		int weight = 0;
		
		if( size == 1 ) return answers.get(0).GetName();
		
		if(questions.size() == 3){
		//2x2
			
			a=questions.get(0);
			b=questions.get(1);
			c=questions.get(2);
			
		}else{
		
			a=questions.get(4);
			b=questions.get(5);
			c=questions.get(7);		
		
		}
			
			// 100 % equality
			System.out.println("Full compare");
			if( Node.Compare(a, b) && Node.Compare(a, c) ) {

				for(int cou=0;cou < size;cou++)
					if(Node.Compare(a, answers.get(cou))) return answers.get(cou).GetName();
				
			}else {
			
				System.out.println("Partial compare");
				
				for(int cou=0;cou < size;cou++) {
				
					d = answers.get(cou);
					
					if(Node.Compare(a, b)){
					
						
						if(Node.Compare(c, d)) return d.GetName();

					}else{
					
						if(Node.Compare(a, c)) {
							
							if(Node.Compare(b, d)) return d.GetName();
						}
					
					}
				}
				
			}
			
		System.out.println("System not able to resolve : null");
		
		return null;
		
	}
	
public String DoTranformReasoning(ArrayList<Node> questions,ArrayList<Node> answers) {

	Node a,b,c,d;
	int size =  answers.size();
	int weight = 0;
	String result = null;
	
	if( size == 1 ) return answers.get(0).GetName();
	
	if(questions.size() == 3){
	//2x2
		
		a=questions.get(0);
		b=questions.get(1);
		c=questions.get(2);
		
	}else{
	
		a=questions.get(4);
		b=questions.get(5);
		c=questions.get(7);		
	
	}
	
	System.out.println("Compare transformation");
	for(int cou=0;cou < size;cou++) {
		
		d = answers.get(cou);
		int asize = a.GetPlacements().size();
		int bsize = b.GetPlacements().size();
		int csize = c.GetPlacements().size();
		int dsize = d.GetPlacements().size();
		
		System.out.println("Processing Node:"+d.GetName());
		if(asize == bsize && csize == dsize){
		
			for( int no = 0; no < asize; no++ ) {
				
				Placement pa = a.GetPlacements().get(no);
				Placement pb = b.GetPlacements().get(no);
				Placement pc = c.GetPlacements().get(no);
				Placement pd = d.GetPlacements().get(no);
				
				weight =+ Placement.Compare(pa, pb, pc, pd);
				System.out.println(d.GetName()+"::"+pd.GetName()+" weight: "+weight);
			}
			
			d.SetWeight(weight);
			
		}else {
			
			if(asize == csize &&  bsize == dsize) {
				
				for( int no = 0; no < a.GetPlacements().size(); no++ ) {
					
					Placement pa = a.GetPlacements().get(no);
					Placement pb = b.GetPlacements().get(no);
					Placement pc = c.GetPlacements().get(no);
					Placement pd = d.GetPlacements().get(no);
					
					weight =+ Placement.Compare(pa, pc, pb, pd);
					System.out.println(d.GetName()+"::"+pd.GetName()+" weight: "+weight);
				}
				
				d.SetWeight(weight);
				
			}else {
				
				int asz = 0;
				int csz = 0;
				int sz = 0;
				
				// asz = 
				
				if(a.GetPlacements().size() > b.GetPlacements().size()) asz = b.GetPlacements().size();
				else asz = a.GetPlacements().size();
				if( c.GetPlacements().size() > d.GetPlacements().size()) csz = d.GetPlacements().size();
				else csz = c.GetPlacements().size();
				
				if( asz > csz) sz=csz;
				else sz = asz;
					
				for( int no = 0; no < sz; no++ ) {
						
						Placement pa = a.GetPlacements().get(no);
						Placement pb = b.GetPlacements().get(no);
						Placement pc = c.GetPlacements().get(no);
						Placement pd = d.GetPlacements().get(no);

						weight =+ Placement.Compare(pa, pc, pb, pd);
						System.out.println(d.GetName()+"::"+pd.GetName()+" weight: "+weight);
				}
					
				d.SetWeight(weight);
		}
	}
	
	}
	
	result = GetGreatestWeight(answers);
	
	return result;
}

public String GetGreatestWeight(ArrayList<Node> answers) {
	
	int weight = 0;
	boolean conflict = false;
	String result = null;
	
	for(int cou = 0; cou < answers.size(); cou++ ) {
		
		if( answers.get(cou).GetWeight() > weight) {
			
			weight = answers.get(cou).GetWeight();
			result = answers.get(cou).GetName();
			if(conflict) conflict=false;
			
		}else {
			
			if(answers.get(cou).GetWeight() == weight)
				conflict=true;
		}
	}
	
	if(conflict) result = null;
	
	System.out.println("Transform Result: "+result);
	
	return result;

}

}

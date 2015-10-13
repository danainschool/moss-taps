package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransitionList {
	List<Transition> list;
	
	public TransitionList() {
		list = new ArrayList<Transition>();
	}
	
	public void add(Transition transition) {
		list.add(transition);
	}
	public void addAll(TransitionList list)  {
		this.list.addAll(list.getAll());
	}
	public List<Transition> getAll() {
		return list;
	}
	
	static public TransitionList calcTransitions(RavensFigure figureA,RavensFigure figureB){
		TransitionList list = new TransitionList();
		
		for (String objname : figureA.getObjects().keySet()) {		
			Transition transition = new Transition(objname);
			RavensObject objA = figureA.getObjects().get(objname);
			RavensObject objB = figureB.getObjects().get(objname);
			
			if (objA != null && objB != null) {
				for (String attrname : objA.getAttributes().keySet()) {
					if (objB.getAttributes().containsKey(attrname)) {
						String newValue = objB.getAttributes().get(attrname);
						String oldValue = objA.getAttributes().get(attrname);
						if (newValue.equals(oldValue)) {
							transition.setChange(attrname, Change.NOCHANGE,null, null);
						}else{
							transition.setChange(attrname, Change.CHANGE,oldValue, newValue);																				
						}
					}else{
						transition.setChange(attrname, Change.DELETED, null,null);
					}
				}
				for (String oldAttrName : objB.getAttributes().keySet()) {
					if (!transition.checkAttrExists(oldAttrName)) {
						transition.setChange(oldAttrName, Change.DELETED, null,null);
					}
				}
			}else {
				transition.setChange(objname, Change.OBJDELETED, null, null);
			}
			list.add(transition);
		}
		for (String objectnameB : figureB.getObjects().keySet()) {
			RavensObject obj = figureA.getObjects().get(objectnameB);
			if (obj == null) {
				RavensObject objB = figureB.getObjects().get(objectnameB);
				for (String attrnameB: objB.getAttributes().keySet()){
					String attrvalue = objB.getAttributes().get(attrnameB);
					Transition transition = new Transition(objectnameB);
					transition.setChange(objectnameB, Change.OBJADDED, attrnameB,attrvalue);
					list.add(transition);
				}			
			}
		}
		return list;
	}
	
	public static void transform(RavensFigure f1, TransitionList transitions, RavensFigure f2){
		HashMap<String, RavensObject> objects1 = f1.getObjects();
		HashMap<String, RavensObject> objects2 = f2.getObjects();
		objects2.putAll(objects1);
		for(Transition t : transitions.getAll()) {
			String objectName = t.getObjectId();
			
			for(String attrname : t.getChanges().keySet()) {				
				Change change = t.getChanges().get(attrname);
				if (change.getChange().equals(Change.OBJADDED)){
					if(!objects2.containsKey(change.getName())){
						RavensObject newobj = new RavensObject(change.getName());
						newobj.getAttributes().put(change.getOldValue(), change.getNewValue());
						objects2.put(change.getName(), newobj);
					}else{
						objects2.get(change.getName()).getAttributes().put(change.getOldValue(), change.getNewValue());
					}
				}else if (change.getChange().equals(Change.OBJDELETED)){
					objects2.remove(objectName);
				}else{ 
					RavensObject obj1 = objects1.get(objectName);
					RavensObject obj2 = objects2.get(objectName);								
					if(obj1 != null && obj2 != null){	
						HashMap<String,String> attrs1 = obj1.getAttributes();
						HashMap<String,String> attrs2 = obj2.getAttributes();
						if (attrs1.containsKey(attrname)){
							switch (change.getChange()) {
							case Change.NOCHANGE:	
								break;
							case Change.DELETED:
								attrs2.remove(attrname);
								break;					
							case Change.CHANGE:
								if(attrname.equals("angle")) {
									int amt = Integer.valueOf(change.getOldValue()).intValue() - Integer.valueOf(change.getNewValue()).intValue();
									String attrvalue = attrs1.get(attrname);
									int oldangle = Integer.valueOf(attrvalue);
									int newangle = oldangle + amt;
									attrs2.remove(attrname);
									attrs2.put(attrname, String.valueOf(newangle));
								}else if (attrname.equals("alignment")){
									String[] a1 = change.getOldValue().split("-");
									String[] a2 = change.getNewValue().split("-");
									String[] a3 = attrs2.get(attrname).split("-");
									if (!a1[0].equals(a2[0])){
										a3[0] = a3[0].equals(a1[0]) ? a2[0] :a1[0];
									}
									if (!a1[1].equals(a2[1])){
										a3[1] = a3[1].equals(a1[1]) ? a2[1] :a1[1];
									}
									attrs2.remove(attrname);
									attrs2.put(attrname, a3[0]+"-"+a3[1]);
								}else{
									attrs2.remove(attrname);
									attrs2.put(attrname, change.getNewValue());										
								}
								break;
							}
						}
					}
				}
			}
		}
	}
}

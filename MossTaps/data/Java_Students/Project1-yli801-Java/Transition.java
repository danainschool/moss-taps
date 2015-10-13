package ravensproject;

import java.util.HashMap;
import java.util.Map;

public class Transition {

	Map<String, Change> changeMap;
	String objectId;
	
	public Transition(String objectId) {		
		changeMap = new HashMap<String, Change>();
		this.objectId = objectId;
	}
	
	public void setChange(String name, String type, String oldValue, String newValue) {
		changeMap.put(name, new Change(name, type, oldValue, newValue));
	}
	
	public boolean checkAttrExists(String attrName) {
		boolean exist = false;
		for(String name : changeMap.keySet()) {
			if(attrName.equals(name)) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	public boolean isObjectDeleted(String objectId) {
		boolean deleted = false;
		Change change = null;
		if((change = changeMap.get(objectId)) != null) {
			if(change.getChange().equals(Change.OBJDELETED)) {
				deleted = true;
			}
		}
		return deleted;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public Change getValue(String name) {
		return changeMap.get(name);
	}
	
	public Map<String, Change> getChanges() {
		return this.changeMap;
	}
	
	
}

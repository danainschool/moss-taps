package ravensproject;

public class Change {
	private String name;
	private String change;
	private String oldValue;
	private String newValue;
	
	final static String NOCHANGE = "NoChange";
	final static String DELETED = "Deleted";
	final static String CHANGE = "Change";
	final static String OBJDELETED = "ObjectDeleted";
	final static String OBJADDED = "ObjectAdded";
	
	public Change(String name, String change, String oldValue, String newValue) {
		this.name = name;
		this.change = change;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}

package ravensproject;

import java.util.HashMap;

public class ObjectWrapper {
	public static final int INVALID_POSITION = -9999;
	
	private String name;
	private HashMap<String, String> attributes;
	private int row = INVALID_POSITION;
	private int column = INVALID_POSITION;
	
	@SuppressWarnings("unchecked")
	public ObjectWrapper(String name, HashMap<String, String> attributes) {
		super();
		this.name = name;
		this.attributes = (HashMap<String, String>) attributes.clone();
	}
	
	public ObjectWrapper(RavensObject original) {
		this.name = original.getName();
		this.attributes = original.getAttributes();
	}

	public ObjectWrapper(ObjectWrapper original) {
		this.name = original.getName();
		this.attributes = original.getAttributes();
		this.row = original.getRow();
		this.column = original.getColumn();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public boolean equals(ObjectWrapper otherObj) {
		return name.equals(otherObj.getName()) && attributes.equals(otherObj.getAttributes());
	}
	
	public String toString() {
		return "ObjectWrapper\tname: " + name + ", attributes: " + attributes + ", row: " + row + ", column: " + column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
}

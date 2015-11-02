package ravensproject;

/**
 * @author linh
 * Parent class for 'inside' and 'above' relationships 
 */
public class RelationshipNetworkEdge extends NetworkEdge {
	public static final String INSIDE = "inside"; 
	public static final String ABOVE = "above"; 
	public static final String OVERLAPS = "overlaps";
	public static final String LEFT_OF = "left-of";
	
//	enum Relationship {
//		INSIDE("inside"), 
//		ABOVE("above"), 
//		OVERLAPS("overlaps"), 
//		LEFT_OF("left-of");
//		
//		private String name;
//
//		Relationship(String relName) {
//			this.setName(relName);
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		private void setName(String name) {
//			this.name = name;
//		}
//	}
	
	String relationship;

//	public RelationshipNetworkEdge(ObjectWrapper source,
//			ObjectWrapper destination, String relationship) {
//		this.source = source;
//		this.destination = destination;
//		this.relationship = relationship;
//	}

	public RelationshipNetworkEdge(ObjectWrapper source, ObjectWrapper destination, String attr) {
		this.source = source;
		this.destination = destination;

		switch (attr) {
		case "inside":
			this.relationship = INSIDE;
			break;
		case "above":
			this.relationship = ABOVE;
			break;
		case "overlaps":
			this.relationship = OVERLAPS;
			break;
		case "left-of":
			this.relationship = LEFT_OF;
			break;
		default:
			break;
		}
	}
	
	public RelationshipNetworkEdge(RelationshipNetworkEdge original) {
		super(original);
		this.relationship = original.relationship;
	}

	public String toString() {
		return "source: " + getSource().getName() 
				+ ", destination: " + getDestination().getName()
				+ ", relationship: " + relationship;
	}

	@Override
	public NetworkEdge deepCopy() {
		return new RelationshipNetworkEdge(this);
	}
}

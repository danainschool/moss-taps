package ravensproject;

/**
 * @author linh
 * Parent class for 'inside' and 'above' relationships 
 */
public class RelationshipNetworkEdge extends NetworkEdge {
	enum Relationship {
		INSIDE, ABOVE, OVERLAPS
	}
	
	Relationship relationship;

	public RelationshipNetworkEdge(RavensObject source,
			RavensObject destination, Relationship relationship) {
		this.source = source;
		this.destination = destination;
		this.relationship = relationship;
	}

	public String toString() {
		return "source: " + getSource().getName() 
				+ ", destination: " + getDestination().getName()
				+ ", relationship: " + relationship;
	}
}

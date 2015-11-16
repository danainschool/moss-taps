package ravensproject;


public abstract class NetworkEdge {
	RavensObject source;
	RavensObject destination;
	
	public RavensObject getSource() {
		return source;
	}

	public void setSource(RavensObject source) {
		this.source = source;
	}

	public RavensObject getDestination() {
		return destination;
	}

	public void setDestination(RavensObject destination) {
		this.destination = destination;
	}
}

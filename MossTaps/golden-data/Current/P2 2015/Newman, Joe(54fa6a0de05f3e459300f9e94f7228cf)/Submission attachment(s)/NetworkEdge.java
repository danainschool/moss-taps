package ravensproject;


public abstract class NetworkEdge {
	ObjectWrapper source;
	ObjectWrapper destination;
	
	public ObjectWrapper getSource() {
		return source;
	}

	public void setSource(ObjectWrapper source) {
		this.source = source;
	}

	public ObjectWrapper getDestination() {
		return destination;
	}

	public void setDestination(ObjectWrapper destination) {
		this.destination = destination;
	}
	
	public NetworkEdge() {
		
	}
	
	public NetworkEdge(NetworkEdge original) {
		this.source = original.source;
		this.destination = original.destination;
	}
	
	public abstract NetworkEdge deepCopy();
}

package ravensproject;

public class TransformationNetworkEdge extends NetworkEdge {

	Transformation transformation;

	public Transformation getTransformation() {
		return transformation;
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	public TransformationNetworkEdge(RavensObject source,
			RavensObject destination, Transformation transformation) {
		this.source = source;
		this.destination = destination;
		this.transformation = transformation;
	}
	
	public String toString() {
		return "source: " + getSource().getName() 
				+ ", destination: " + getDestination().getName()
				+ ", transformation: " + transformation.getClass();
	}
}

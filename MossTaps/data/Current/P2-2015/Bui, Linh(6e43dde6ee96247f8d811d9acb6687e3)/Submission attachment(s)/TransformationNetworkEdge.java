package ravensproject;

public class TransformationNetworkEdge extends NetworkEdge {

	Transformation transformation;

	public Transformation getTransformation() {
		return transformation;
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	public TransformationNetworkEdge(ObjectWrapper source,
			ObjectWrapper destination, Transformation transformation) {
		this.source = source;
		this.destination = destination;
		this.transformation = transformation;
	}
	
	public TransformationNetworkEdge(TransformationNetworkEdge original) {
		super(original);
		this.transformation = original.transformation;
	}

	public String toString() {
		return "source: " + getSource().getName() 
				+ ", destination: " + getDestination().getName()
				+ ", transformation: " + transformation.getClass();
	}

	@Override
	public NetworkEdge deepCopy() {
		return new TransformationNetworkEdge(this);
	}
}

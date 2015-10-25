package ravensproject;

import ravensproject.AgentShapeMapping.mappingTransformations;

public class AgentTransformation {

	mappingTransformations theTransformation;
	Object theValue;
	
	public AgentTransformation(	mappingTransformations trans, Object val) {
		this.theTransformation = trans;
		this.theValue = val;
	}
	
	public static AgentTransformation copy(AgentTransformation A) {
		return new AgentTransformation(A.theTransformation, A.theValue);
	}
	
}

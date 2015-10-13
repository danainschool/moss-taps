package ravensproject;

import ravensproject.AgentShapeMapping.mappingTransformations;

public class AgentSpecialHandling {

	enum specialType { REFLECTION_ROTATION };
	specialType theType;
	int theValue;
	mappingTransformations convertFromTransform;
	mappingTransformations convertToTransform;
	
	
	public AgentSpecialHandling(specialType theType, int theValue, mappingTransformations fromTransform, mappingTransformations toTransform) {
		this.theType = theType;
		this.theValue = theValue;
		convertFromTransform = fromTransform;
		convertToTransform = toTransform;
	}
	
}

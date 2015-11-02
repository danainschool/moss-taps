package ravensproject;

import java.util.HashMap;

/**
 * Created by kalagarr on 6/7/15.
 */
public class EdgeProperties {

    //This implementation will only work for 2 X 2 RPM problems,
    // this has to be extended for 3 X 3 RPM problems later in the course

    private Integer objectDifference;
    //object's are mapped by shape, size, fill, etc with all the available attributes
    //this hashmap will have object ID from A-B as key and all properties related to A-B
    //for single objects in A and B, example values
    private EdgeObjectProperties edgeABChangeProperties;

    //this hashmap will have object ID from C-D as key and all properties related to C-D
    //once we build the objectABChangeProperties, we can build the edgeCDChangeProperties
    //based on the ABObjectChange properties hashmap.
    private EdgeObjectProperties edgeCDChangeProperties;


    public Integer getObjectDifference() {
        return objectDifference;
    }

    public void setObjectDifference(Integer objectDifference) {
        this.objectDifference = objectDifference;
    }

    public EdgeObjectProperties getEdgeABChangeProperties() {
        return edgeABChangeProperties;
    }

    public void setEdgeABChangeProperties(EdgeObjectProperties edgeABChangeProperties) {
        this.edgeABChangeProperties = edgeABChangeProperties;
    }

    public EdgeObjectProperties getEdgeCDChangeProperties() {
        return edgeCDChangeProperties;
    }

    public void setEdgeCDChangeProperties(EdgeObjectProperties edgeCDChangeProperties) {
        this.edgeCDChangeProperties = edgeCDChangeProperties;
    }
}

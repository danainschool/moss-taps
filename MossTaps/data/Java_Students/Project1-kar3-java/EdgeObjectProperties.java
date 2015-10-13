package ravensproject;

import java.util.HashMap;

/**
 * Created by kalagarr on 6/7/15.
 */
public class EdgeObjectProperties {

    /*
    this hashmap will have object ID A[i] and related matching properties from B[j]
    for example from B12 A1 will have entries like
    Key - A1 and ObjectProperties like

    example values in this hashmap will be for B-12- A1
    A1-shape-circle as key and value will be disappeared - Since there is no matching object in B. If the value is
    disappeared then continue with next property

    example values in this hashmap will be for B-12- A3
    A3-shape-circle as key and value will be B1-shape-circle,
    A3-size-large as key and valye will B1-size-large.
    In this way each objects transformations is captured in the edge properties.

    */
    private HashMap<String, ObjectProperties> edgeObjectChangeProperties;

    public EdgeObjectProperties(){
    }


    public HashMap<String, ObjectProperties> getEdgeObjectChangeProperties() {
        return edgeObjectChangeProperties;
    }

    public void setEdgeObjectChangeProperties(HashMap<String, ObjectProperties> edgeObjectChangeProperties) {
        this.edgeObjectChangeProperties = edgeObjectChangeProperties;
    }
}

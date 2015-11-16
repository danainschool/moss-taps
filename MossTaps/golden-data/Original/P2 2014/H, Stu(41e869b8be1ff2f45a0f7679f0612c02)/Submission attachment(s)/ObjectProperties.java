package ravensproject;

import java.util.HashMap;

/**
 * Created by kalagarr on 6/7/15.
 */
public class ObjectProperties {

    /*this hashmap will have object ID from A[i]-B[i] as key and all properties related to A[i]-B[i]
    example values in this hashmap will be for B-12- A1
    A1-shape-circle as key and value will be disappeared - Since there is no matching object in B. If the value is
    disappeared then continue with next property
    example values in this hashmap will be for B-12- A3
    A3-shape-circle as key and value will be B1-shape-circle,
    A3-size-large as key and valye will B1-size-large.
    In this way each objects transformations is captured in the edge properties.
    */
    private HashMap<String, String> objectChangeProperties;

    public ObjectProperties(){
        objectChangeProperties = new HashMap<>();
    }

    public HashMap<String, String> getObjectChangeProperties() {
        return objectChangeProperties;
    }

    public void setObjectChangeProperties(HashMap<String, String> objectChangeProperties) {
        this.objectChangeProperties = objectChangeProperties;
    }

    public void setProperty(String key, String value){
        objectChangeProperties.put(key, value);
    }


    public String getProperty(String key) {
        return objectChangeProperties.get(key);
    }
}

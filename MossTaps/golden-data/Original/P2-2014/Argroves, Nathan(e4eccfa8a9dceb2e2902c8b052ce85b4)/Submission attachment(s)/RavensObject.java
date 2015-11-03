/*
 * DO NOT MODIFY THIS FILE.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */
package ravensproject;

import java.util.HashMap;

/**
 * A single object in a RavensFigure -- typically, a single shape in a frame,
 * such as a triangle or a circle -- comprised of a list of RavensAttributes.
 * 
 */
public class RavensObject {
    private String name;
    private HashMap<String,String> attributes;
    
    /**
     * Constructs a new RavensObject given a name.
     * 
     * Your agent does not need to use this method.
     * 
     * @param name the name of the object
     */
    public RavensObject(String name) {
        this.name=name;
        attributes=new HashMap<>();
    }

    /**
     * The name of this RavensObject. Within a RavensFigure, each RavensObject has a unique name.
     * 
     * @return the name of the RavensObject
     */
    public String getName() {
        return name;
    }
    /**
     * Returns a HashMap of attributes characterizing this RavensObject. The key
     * for these attributes is the name of the attribute, and the value is the
     * value of the attribute. For example, a filled large square would have
     * attribute pairs "shape:square", "size:large", and "filled:yes".
     * 
     * @return a HashMap of name-value attribute pairs.
     * 
     */
    public HashMap<String,String> getAttributes() {
        return attributes;
    }
}

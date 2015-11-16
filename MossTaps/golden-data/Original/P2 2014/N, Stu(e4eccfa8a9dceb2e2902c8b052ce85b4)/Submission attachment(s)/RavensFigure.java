/*
 * DO NOT MODIFY THIS FILE.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */
package ravensproject;

import java.io.File;
import java.util.HashMap;

/**
 * A single figure in a Raven's Progressive Matrix problem, comprised of a name 
 * and a HashMap of RavensObjects.
 * 
 */
public class RavensFigure {
    private String name;
    private HashMap<String,RavensObject> objects;
    
    private String visualFilename;
        
    /**
     * Creates a new figure for a Raven's Progressive Matrix given a name.
     * 
     * Your agent does not need to use this method.
     * 
     * @param name the name of the figure
     */
    public RavensFigure(String name, String problemName, String setName) {
        this.name=name;
        visualFilename="Problems" + File.separator + setName + File.separator + problemName + File.separator + name + ".png";
        objects=new HashMap<>();
    }
    
    /**
     * Returns the name of the figure. The name of the figure will always match
     * the HashMap key for this figure.
     * 
     * The figures in the problem will be named A, B, and C for 2x2 problems. 
     * The figures in the problem will be named A, B, C, D, E, F, G, and H in
     * 3x3 problems. The first row is A, B, and C; the second row is D, E, and
     * F; and the third row is G and H.
     * 
     * Answer options will always be named "1" through "6" for 2x2 problems and
     * "1" through "8" for 3x3 problems.
     *  
     * @return the name of this figure
     */
    public String getName() {
        return name;
    }
    /**
     * Returns a HashMap of RavensObjects from the figure. This is the verbal
     * representation of the problem. If no verbal representation is available
     * (if hasVerbal() returns false), then this HashMap will be empty.
     * 
     * @return a HashMap of RavensObject, indexed by object names.
     */
    public HashMap<String,RavensObject> getObjects() {
        return objects;
    }
    /**
     * Returns the filename of the visual representation of this figure in the
     * problem. The files will always be .PNG files.
     * 
     * @return the filename of the visual representation of this figure.
     */
    public String getVisual() {
        return visualFilename;
    }
}

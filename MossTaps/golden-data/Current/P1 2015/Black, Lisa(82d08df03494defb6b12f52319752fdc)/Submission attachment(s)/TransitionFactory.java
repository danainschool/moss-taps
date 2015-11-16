package ravensproject;

import java.util.HashMap;

/**
 * Created by jblac_000 on 6/2/2015.
 */
public class TransitionFactory {

    public static Transition getTransition(TransitionType type, RavensObject initial, RavensObject current) {
        Transition t = new Transition(type);
        HashMap<String,String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();


        switch(type) {
            case ROTATED:
                if (initialAttr.containsKey("angle") && currentAttr.containsKey("angle")) {
                    try {
                        int angle1 = Integer.parseInt(initialAttr.get("angle"));
                        int angle2 = Integer.parseInt(currentAttr.get("angle"));
                        int diff = angle1 - angle2;
                        t.setValue(Integer.toString(diff));
                    }
                    catch(NumberFormatException ex) {
                        t.setValue("DETECTION_FAILURE");
                    }
                }
                else if(initialAttr.containsKey("angle") && !currentAttr.containsKey("angle")) {
                    t.setValue(initialAttr.get("angle"));
                }
                else
                    t.setValue(currentAttr.get("angle"));

                return t;

            case SCALED:
                String initialSize = initialAttr.get("size");
                String currentSize = currentAttr.get("size");

                if(initialSize.equalsIgnoreCase("very small")){
                    t.setValue("grow");
                }
                else if(initialSize.equalsIgnoreCase("small")){
                    switch(currentSize.toLowerCase()){
                        case "very small":
                            t.setValue("shrink");
                            break;
                        default:
                            t.setValue("grow");
                            break;
                    }
                }
                else if(initialSize.equalsIgnoreCase("medium")){
                    switch(currentSize.toLowerCase()) {
                        case "very small":
                        case "small":
                            t.setValue("shrink");
                            break;
                        case "large":
                        case "very large":
                        case "huge":
                            t.setValue("grow");
                            break;
                    }
                }
                else if(initialSize.equalsIgnoreCase("large")){
                    switch(currentSize.toLowerCase()) {
                        case "very small":
                        case "small":
                        case "medium":
                            t.setValue("shrink");
                            break;
                        case "very large":
                        case "huge":
                            t.setValue("grow");
                            break;
                    }
                }
                else if(initialSize.equalsIgnoreCase("very large")){
                    switch(currentSize.toLowerCase()){
                        case "huge":
                            t.setValue("grow");
                            break;
                        default:
                            t.setValue("shrink");
                            break;
                    }
                }
                else
                    t.setValue("shrink");

                return t;
            case SHAPE_CHANGED:
                t.setValue(currentAttr.get("shape"));
                return t;

            case DELETED:
                t.setValue(initial.getName());
                return t;

            case UNCHANGED:
            case REFLECTED:
                return t;
        }

        return t;
    }

}

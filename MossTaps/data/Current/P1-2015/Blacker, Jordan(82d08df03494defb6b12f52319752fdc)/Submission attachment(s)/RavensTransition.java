package ravensproject;

import java.util.*;

/**
 * Created by jblac_000 on 6/1/2015.
 */
public class RavensTransition {
    private final FigureNetwork figureNet;
    private final String direction;
    private HashMap<String, List<Transition>> changes;

    public RavensTransition(FigureNetwork fnet, String direction) {
        this.figureNet = fnet;
        this.direction = direction;
        this.changes = new HashMap<>();
    }

    public FigureNetwork getFigureNetwork() {
        return figureNet;
    }

    public String getDirection() {
        return direction;
    }

    public HashMap<String, List<Transition>> getChanges() {
        return changes;
    }

    public void calculateTransition(FigureNetwork fromFigure) {
        List<RavensObject> initialObjects = fromFigure.getObjects();
        List<RavensObject> currentObjects = this.figureNet.getObjects();
        Boolean currentLarger = null;

        if(fromFigure.getObjectCount() > this.figureNet.getObjectCount()) {
            currentLarger = false;
        }
        else if(fromFigure.getObjectCount() < this.figureNet.getObjectCount()) {
            currentLarger = true;
        }

        if(fromFigure.getObjectCount() == 1 && this.figureNet.getObjectCount() == 1){
            RavensObject currentObj = this.figureNet.getObjects().get(0);
            RavensObject initialObj = fromFigure.getObjects().get(0);

            List<Transition> transitions = getTransitions(initialObj, currentObj);
            this.changes.put(initialObj.getName(), transitions);
        }
        else{
            HashMap<String, RavensObject> correspondingObjects = getCorrespondingObjects(fromFigure);

            if(currentLarger == null) {
                //same sizes don't worry about changes in object counts
                correspondingObjects.forEach((k,v) -> {
                    List<Transition> transitions = getTransitions(v, this.figureNet.getFigure().getObjects().get(k));
                    this.changes.put(v.getName(), transitions);
                });
            }
            else if(currentLarger) {
                correspondingObjects.forEach((k,v) -> {
                    if(v == null){
                        List<Transition> t = new ArrayList<>();
                        t.add(new Transition(TransitionType.ADDED, k));
                        this.changes.put("ADDED", t);
                    }
                    else{
                        List<Transition> transitions = getTransitions(v, this.figureNet.getFigure().getObjects().get(k));
                        this.changes.put(v.getName(), transitions);
                    }
                });
            }
            else { //assumes potentialDeletion
                correspondingObjects.forEach((k,v) -> {
                    if(v == null){
                        List<Transition> t = new ArrayList<>();
                        t.add(new Transition(TransitionType.DELETED, k));
                        this.changes.put("DELETED", t);
                    }
                    else{
                        List<Transition> transitions = getTransitions(v, fromFigure.getFigure().getObjects().get(k));
                        this.changes.put(v.getName(), transitions);
                    }
                });
            }
        }
    }

    private HashMap<String, RavensObject> getCorrespondingObjects(FigureNetwork fromFigure){
        HashMap<String, RavensObject> corresponding = new HashMap<>();

        Boolean currentLarger = null;

        if(fromFigure.getObjectCount() > this.figureNet.getObjectCount()) {
            currentLarger = false;
        }
        else if(fromFigure.getObjectCount() < this.figureNet.getObjectCount()) {
            currentLarger = true;
        }

        List<RavensObject> primaryObjects;
        List<RavensObject> secObjects;

        if(currentLarger == null){
            primaryObjects = this.figureNet.getObjectHierarchy();
            secObjects = fromFigure.getObjectHierarchy();
            for(int i = 0; i < this.figureNet.getObjectCount(); i++){
                corresponding.put(primaryObjects.get(i).getName(), secObjects.get(i));
            }

            return corresponding;
        }
        else if(currentLarger){
            primaryObjects = this.figureNet.getObjects();
            secObjects = fromFigure.getObjects();
        }
        else {
            primaryObjects = fromFigure.getObjects();
            secObjects = this.figureNet.getObjects();
        }

        HashMap<RavensObject, HashMap<RavensObject, Integer>> scoreDictionary = new HashMap<>();

        for(RavensObject o1 : primaryObjects){
            HashMap<RavensObject, Integer> scores = new HashMap<>();

            for(RavensObject o2 : secObjects) {
                int score = 0;

                if (!hasShapeChanged(o1, o2)) {
                    score += 5;
                }

                if (!hasSizeChanged(o1, o2)) {
                    score += 4;
                }

                if (!hasFillChanged(o1, o2)) {
                    score += 3;
                }

                if (!hasAngleChanged(o1, o2)) {
                    score += 2;
                }

                if (!hasAlignmentChanged(o1, o2)) {
                    score += 1;
                }

                scores.put(o2, score);
            }

            scoreDictionary.put(o1, scores);
        }

        int count = scoreDictionary.size();
        List<RavensObject> used = new ArrayList<>();
        while(count > 0)
        {
            int highestScore = 0;
            RavensObject pair1 = null;
            RavensObject pair2 = null;

            if(count == 1){
                //handle extra remaining extra value
                List<RavensObject> available = new ArrayList<>(primaryObjects);
                available.removeAll(used);
                corresponding.put(available.get(1).getName(), null);
                break;
            }

            for(RavensObject primaryKey : scoreDictionary.keySet()){
                for(RavensObject secondaryKey : scoreDictionary.get(primaryKey).keySet()){
                    int score = scoreDictionary.get(primaryKey).get(secondaryKey);
                    if(score > highestScore){
                        highestScore = score;
                        pair1 = primaryKey;
                        pair2 = secondaryKey;
                    }
                }
            }

            corresponding.put(pair1.getName(), pair2);
            scoreDictionary.get(pair1).remove(pair2);
            used.add(pair2);
            count--;
        }

        return corresponding;
    }

    private static List<Transition> getTransitions(RavensObject initialObj, RavensObject currentObj) {
        List<Transition> transitions = new ArrayList<>();


        if(hasShapeChanged(initialObj, currentObj))
            transitions.add(TransitionFactory.getTransition(TransitionType.SHAPE_CHANGED, initialObj, currentObj));
        if(hasSizeChanged(initialObj, currentObj))
            transitions.add(TransitionFactory.getTransition(TransitionType.SCALED, initialObj, currentObj));

        if(hasFillChanged(initialObj, currentObj)){
            String initialFill = initialObj.getAttributes().get("fill");
            String currentFill = currentObj.getAttributes().get("fill");

            if(initialFill.equalsIgnoreCase("yes") && currentFill.equalsIgnoreCase("no")){
                transitions.add(new Transition(TransitionType.FILL_CHANGED, "removed"));
            }
            else if(initialFill.equalsIgnoreCase("no") && currentFill.equalsIgnoreCase("yes")){
                transitions.add(new Transition(TransitionType.FILL_CHANGED, "added"));
            }
        }

        if(hasAngleChanged(initialObj, currentObj)){
            transitions.add(TransitionFactory.getTransition(TransitionType.ROTATED, initialObj, currentObj));
        }

        if(hasAlignmentChanged(initialObj, currentObj)){
            String initialAlign = initialObj.getAttributes().get("alignment");
            String currentAlign = currentObj.getAttributes().get("alignment");

            switch(initialAlign) {
                case "top-right":
                    switch (currentAlign){
                        case "top-left":
                            transitions.add(new Transition(TransitionType.REFLECTED, "horizontal"));
                            break;
                        case "bottom-right":
                            transitions.add(new Transition(TransitionType.REFLECTED, "vertical"));
                            break;
                        case "bottom-left":
                            transitions.add(new Transition(TransitionType.ROTATED, "180"));
                            break;
                    }
                    break;
                case "top-left":
                    switch(currentAlign){
                        case "top-right":
                            transitions.add(new Transition(TransitionType.REFLECTED, "horizontal"));
                            break;
                        case "bottom-left":
                            transitions.add(new Transition(TransitionType.REFLECTED, "vertical"));
                            break;
                        case "bottom-right":
                            transitions.add(new Transition(TransitionType.ROTATED, "180"));
                            break;
                    }
                    break;
                case "bottom-left":
                    switch(currentAlign){
                        case "bottom-right":
                            transitions.add(new Transition(TransitionType.REFLECTED, "horizontal"));
                            break;
                        case "top-left":
                            transitions.add(new Transition(TransitionType.REFLECTED, "vertical"));
                            break;
                        case "top-right":
                            transitions.add(new Transition(TransitionType.ROTATED, "180"));
                            break;
                    }
                    break;
                case "bottom-right":
                    switch(currentAlign){
                        case "bottom-leftt":
                            transitions.add(new Transition(TransitionType.REFLECTED, "horizontal"));
                            break;
                        case "top-right":
                            transitions.add(new Transition(TransitionType.REFLECTED, "vertical"));
                            break;
                        case "top-left":
                            transitions.add(new Transition(TransitionType.ROTATED, "180"));
                            break;
                    }
                    break;
            }
        }

        if(transitions.size() == 0)
            transitions.add(new Transition(TransitionType.UNCHANGED));

        return transitions;
    }

    private static RavensObject locatePrimaryOrBottomObject(RavensFigure figure){
        RavensObject primary = null;
        for(RavensObject obj : figure.getObjects().values()) {
            HashMap<String,String> attr = obj.getAttributes();
            if(!attr.containsKey("inside") && !attr.containsKey("above") && !attr.containsKey("overlaps"))
                primary = obj;
        }

        return primary;
    }

    private static boolean hasShapeChanged(RavensObject initial, RavensObject current){
        HashMap<String, String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();

        if(initialAttr.containsKey("shape") && currentAttr.containsKey("shape")){
            return !initialAttr.get("shape").equalsIgnoreCase(currentAttr.get("shape"));
        }
        else
            return false;
    }

    private static boolean hasSizeChanged(RavensObject initial, RavensObject current){
        HashMap<String, String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();

        if(initialAttr.containsKey("size") && currentAttr.containsKey("size")){
            return !initialAttr.get("size").equalsIgnoreCase(currentAttr.get("size"));
        }
        else
            return false;
    }

    private static boolean hasFillChanged(RavensObject initial, RavensObject current){
        HashMap<String, String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();

        if(initialAttr.containsKey("fill") && currentAttr.containsKey("fill")){
            return !initialAttr.get("fill").equalsIgnoreCase(currentAttr.get("fill"));
        }
        else
            return false;
    }

    private static boolean hasAngleChanged(RavensObject initial, RavensObject current) {
        HashMap<String, String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();

        if(initialAttr.containsKey("angle") && currentAttr.containsKey("angle")){
            return !initialAttr.get("angle").equalsIgnoreCase(currentAttr.get("angle"));
        }
        else if((initialAttr.containsKey("angle") && !currentAttr.containsKey("angle")) ||
                !initialAttr.containsKey("angle") && currentAttr.containsKey("angle"))
            return true;
        else
            return false;
    }

    private static boolean hasAlignmentChanged(RavensObject initial, RavensObject current){
        HashMap<String, String> initialAttr = initial.getAttributes(), currentAttr = current.getAttributes();

        if(initialAttr.containsKey("alignment") && currentAttr.containsKey("alignment")){
            return !initialAttr.get("alignment").equalsIgnoreCase(currentAttr.get("alignment"));
        }
        else if((initialAttr.containsKey("alignment") && !currentAttr.containsKey("alignment")) ||
                !initialAttr.containsKey("alignment") && currentAttr.containsKey("alignment"))
            return true;
        else
            return false;
    }

}

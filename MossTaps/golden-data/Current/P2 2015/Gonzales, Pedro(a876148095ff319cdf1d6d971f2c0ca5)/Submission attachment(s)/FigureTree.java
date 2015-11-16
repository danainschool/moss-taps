package ravensproject;

import java.util.*;

/**
 * A traversable data structure to map the location of objects in a figure
 */
public class FigureTree {

    private Node root;
    private int count;
    private Set<String> objectNamesInTree;

    public FigureTree() {
        root = null;
        count = 0;
        objectNamesInTree = new HashSet<>();
    }

    public void addObject(RavensObject obj){
        Node toAdd = new Node(obj);
        objectNamesInTree.add(obj.getName());
        count++;

        if(root == null){
            root = toAdd;
        }
        else{
            Map<String,String> objAttrs = obj.getAttributes();
            if(!objAttrs.containsKey("inside") && !objAttrs.containsKey("left-of") && !objAttrs.containsKey("above") &&
                    !objAttrs.containsKey("overlaps")){

                Node temp = root;
                root = toAdd;

                String dir = determineDirection(toAdd.getObject().getName(), temp);
                root.addChild(temp, dir);
            }
            else {
                String traversalDirection = determineDirection(toAdd.getObject().getName(), root);
                Node next = null, last = root;
                int currentLevel = 0;
                int levelsDeep = levelsDeepInDirection(traversalDirection, toAdd.getObject());

                do{
                    if(currentLevel != 0)
                        last = next;

                    next = last.traverse(traversalDirection);
                    currentLevel++;

                } while(next != null && levelsDeep > currentLevel);

                if(levelsDeep == currentLevel && next != null){
                    last.replaceChild(toAdd, traversalDirection);
                    toAdd.addChild(next, traversalDirection);
                }
                else if(next != null) {
                    Node child = last.children.get(traversalDirection);
                    last.replaceChild(toAdd, traversalDirection);
                    toAdd.addChild(child, traversalDirection);
                }
                else {
                    last.addChild(toAdd, traversalDirection);
                }
            }
        }
    }

    public int getCount() {
        return count;
    }

    public Node getRoot() {
        return root;
    }

    private boolean doesRootContainObjectViaAttribute(String objName){
        String directionVal = determineDirection(objName, this.root);
        return !directionVal.equalsIgnoreCase("");
    }

    private static String determineDirection(String newNodeName, Node oldNode){
        Map<String,String> oldRootAttr = oldNode.getObject().getAttributes();
        boolean above = false, inside = false, left = false, overlap = false;

        if(oldRootAttr.containsKey("above")){
            String aboveString = oldRootAttr.get("above");
            if(aboveString.length() == 1 && aboveString.equalsIgnoreCase(newNodeName)){
                above = true;
            }
            else if(aboveString.length() > 1){
                String[] aboveObjNames = aboveString.split(",");
                for(String name : aboveObjNames){
                    if(name.equalsIgnoreCase(newNodeName)){
                        above = true;
                        break;
                    }
                }
            }
        }

        if(oldRootAttr.containsKey("left-of")){
            String leftString = oldRootAttr.get("left-of");
            if(leftString.length() == 1 && leftString.equalsIgnoreCase(newNodeName)){
                left = true;
            }
            else if(leftString.length() > 1){
                String[] leftObjNames = leftString.split(",");
                for(String name : leftObjNames){
                    if(name.equalsIgnoreCase(newNodeName)){
                        left = true;
                        break;
                    }
                }
            }
        }

        if(oldRootAttr.containsKey("inside")){
            String insideString = oldRootAttr.get("inside");
            if(insideString.length() == 1 && insideString.equalsIgnoreCase(newNodeName)){
                inside = true;
            }
            else if(insideString.length() > 1){
                String[] insideObjNames = insideString.split(",");
                for(String name : insideObjNames){
                    if(name.equalsIgnoreCase(newNodeName)){
                        inside = true;
                        break;
                    }
                }
            }
        }

        if(oldRootAttr.containsKey("overlaps")){
            String overlapString = oldRootAttr.get("overlaps");
            if(overlapString.length() == 1 && overlapString.equalsIgnoreCase(newNodeName)){
                overlap = true;
            }
            else if(overlapString.length() > 1){
                String[] overlapObjNames = overlapString.split(",");
                for(String name : overlapObjNames){
                    if(name.equalsIgnoreCase(newNodeName)){
                        overlap = true;
                        break;
                    }
                }
            }
        }

        if(inside){
            return "IN";
        }

        String direction = "";
        if(above)
            direction += "N";
        if(left)
            direction += "W";
        if(overlap)
            direction += "O";

        return direction;
    }

    private static int levelsDeepInDirection(String direction, RavensObject obj){
        if(direction.equalsIgnoreCase("IN") && obj.getAttributes().containsKey("inside")){
            String innerStr = obj.getAttributes().get("inside");
            if(innerStr.length() == 1)
                return 1;
            else{
                String[] objs = innerStr.split(",");
                return objs.length;
            }
        }
        else if(direction.equalsIgnoreCase("N") && obj.getAttributes().containsKey("above")){
            String aboveStr = obj.getAttributes().get("above");
            if(aboveStr.length() == 1)
                return 1;
            else{
                String[] objs = aboveStr.split(",");
                return objs.length;
            }
        }
        else if(direction.equalsIgnoreCase("W") && obj.getAttributes().containsKey("left-of")){
            String aboveStr = obj.getAttributes().get("left-of");
            if(aboveStr.length() == 1)
                return 1;
            else{
                String[] objs = aboveStr.split(",");
                return objs.length;
            }
        }
        else if(direction.equalsIgnoreCase("O") && obj.getAttributes().containsKey("overlaps")){
            String aboveStr = obj.getAttributes().get("overlaps");
            if(aboveStr.length() == 1)
                return 1;
            else{
                String[] objs = aboveStr.split(",");
                return objs.length;
            }
        }
        else if(direction.equalsIgnoreCase("NWO") && obj.getAttributes().containsKey("above") &&
                obj.getAttributes().containsKey("left-of") && obj.getAttributes().containsKey("overlaps")){

            String[] aboveObj = obj.getAttributes().get("above").split(",");
            String[] leftObj = obj.getAttributes().get("left-of").split(",");
            String[] overlapObj = obj.getAttributes().get("overlaps").split(",");

            Set<String> aboveSet = new HashSet<>(Arrays.asList(aboveObj));
            Set<String> leftSet = new HashSet<>(Arrays.asList(leftObj));
            Set<String> overlapSet = new HashSet<>(Arrays.asList(overlapObj));

            Set<String> intersection = new HashSet<>(aboveSet);
            intersection.retainAll(leftSet);
            intersection.retainAll(overlapSet);

            return intersection.size();
        }
        else if(direction.equalsIgnoreCase("NW") && obj.getAttributes().containsKey("above") &&
                obj.getAttributes().containsKey("left-of")){

            String[] aboveObj = obj.getAttributes().get("above").split(",");
            String[] leftObj = obj.getAttributes().get("left-of").split(",");

            Set<String> aboveSet = new HashSet<>(Arrays.asList(aboveObj));
            Set<String> leftSet = new HashSet<>(Arrays.asList(leftObj));

            Set<String> intersection = new HashSet<>(aboveSet);
            intersection.retainAll(leftSet);

            return intersection.size();

        }
        else if(direction.equalsIgnoreCase("NO") && obj.getAttributes().containsKey("above") &&
                obj.getAttributes().containsKey("overlaps")){

            String[] aboveObj = obj.getAttributes().get("above").split(",");
            String[] overlapObj = obj.getAttributes().get("overlaps").split(",");

            Set<String> aboveSet = new HashSet<>(Arrays.asList(aboveObj));
            Set<String> overlapSet = new HashSet<>(Arrays.asList(overlapObj));

            Set<String> intersection = new HashSet<>(aboveSet);
            intersection.retainAll(overlapSet);

            return intersection.size();

        }
        else if(direction.equalsIgnoreCase("WO") && obj.getAttributes().containsKey("left-of") &&
                obj.getAttributes().containsKey("overlaps")){

            String[] leftObj = obj.getAttributes().get("left-of").split(",");
            String[] overlapObj = obj.getAttributes().get("overlaps").split(",");

            Set<String> leftSet = new HashSet<>(Arrays.asList(leftObj));
            Set<String> overlapSet = new HashSet<>(Arrays.asList(overlapObj));

            Set<String> intersection = new HashSet<>(leftSet);
            intersection.retainAll(overlapSet);

            return intersection.size();

        }
        else
            return 0;
    }



    class Node {
        private Node parent;
        private RavensObject object;
        private Map<String, Node> children;

        public Node(RavensObject obj){
            this.object = obj;
            children = new HashMap<>();
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public RavensObject getObject() {
            return object;
        }

        public void setObject(RavensObject object) {
            this.object = object;
        }

        public boolean isRoot(){
            return parent == null;
        }

        public boolean isLeaf(){
            return children.size() == 0;
        }

        public Set<String> directionalChildren(){
            return this.children.keySet();
        }

        public void addChild(Node n, String direction) throws IllegalArgumentException {
            n.setParent(this);
            if(this.children.containsKey(direction))
                throw new IllegalArgumentException("Object already exists at specified location");

            this.children.put(direction, n);
        }

        public void replaceChild(Node n, String direction) {
            n.setParent(this);
            this.children.put(direction, n);
        }

        public Node traverse(String direction){
            return this.children.get(direction);
        }
    }

}

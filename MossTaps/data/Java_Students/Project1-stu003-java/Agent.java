package ravensproject;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

import java.util.HashMap;
import java.util.List;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {


    private EdgeProperties edgeProperties;

    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
        //System.out.println("inside Solve problem = " + problem);

        edgeProperties = new EdgeProperties();


        if ( problem != null && problem.getName().indexOf("Challenge") >=0  ) {
            //System.out.println("Skipping Challenge problem (" + problem.getName() + ") ");
            return -1;
        }

        try {
            buildABEdgeProperties(problem);
            final Integer answer = buildCDEdgeProperties(problem);
            return answer;
        }catch(Exception e){
            System.out.println("Exception in solve method for problem (" +  problem.getName() + ") = " + e );
            return -1;
        }
    }


    private void buildABEdgeProperties(final RavensProblem problem){
        final HashMap<String, RavensFigure> ravenFigures = problem.getFigures();
        final RavensFigure ravensFigureA = ravenFigures.get("A");
        final RavensFigure ravensFigureB = ravenFigures.get("B");


        final HashMap<String, RavensObject> ravensObjectsA = ravensFigureA.getObjects();
        final HashMap<String, RavensObject> ravensObjectsB = ravensFigureB.getObjects();

        //set object difference
        edgeProperties.setObjectDifference( Math.abs(ravensObjectsA.size() - ravensObjectsB.size()));

        //for each object in A iterate through the objects in B and build EdgeObjectproperties
        final EdgeObjectProperties edgeObjectPropertiesAB = buildEdgeObjectProperties("A", "B", ravensObjectsA, ravensObjectsB);
        edgeProperties.setEdgeABChangeProperties(edgeObjectPropertiesAB);
    }

    private Integer buildCDEdgeProperties(final RavensProblem problem){
        final HashMap<String, RavensFigure> ravenFigures = problem.getFigures();
        final RavensFigure ravensFigureC = ravenFigures.get("C");

        final HashMap<String, RavensObject> ravensObjectsC = ravensFigureC.getObjects();
        final Integer objectDifference = edgeProperties.getObjectDifference();
        final EdgeObjectProperties edgeObjectPropertiesAB = edgeProperties.getEdgeABChangeProperties();
        final HashMap<String, ObjectProperties> edgeObjectPropertiesHashMap = edgeObjectPropertiesAB.getEdgeObjectChangeProperties();

        final HashMap<String, EdgeObjectProperties> answerEdgeObjectPropertiesHashMap = new HashMap<>();

        //for each object in A iterate through the objects in B and build EdgeObjectproperties
        for(final String figureID: ravenFigures.keySet()){
            try{
                final Integer answerFigureID = Integer.parseInt(figureID);
                //pass A and B instead of C and figure ID. This is just to compare the keys and values without parsing.
                final EdgeObjectProperties edgeObjectPropertiesC = buildEdgeObjectProperties("A", "B", ravensObjectsC, ravenFigures.get(figureID).getObjects());
                answerEdgeObjectPropertiesHashMap.put(figureID, edgeObjectPropertiesC);
            }catch (Exception e){
                System.out.println("Exception while parsing Integer value from figureID for answers = " + e );
            }
        }

        final HashMap <Integer, Integer> matchCountHashMap = new HashMap<>();


        //now iterate through the answers and figure out the correct answer
        for (final String figureID: answerEdgeObjectPropertiesHashMap.keySet()){
            final EdgeObjectProperties answerEdgeObjectProperties = answerEdgeObjectPropertiesHashMap.get(figureID);
            final HashMap<String, ObjectProperties> objectPropertiesHashMap = answerEdgeObjectProperties.getEdgeObjectChangeProperties();

            Integer totalMatchCount = 0;

            for ( final String objectID: edgeObjectPropertiesHashMap.keySet()){
                for ( final String answerObjectID: objectPropertiesHashMap.keySet()){
                    final Integer objectAttributeMatchCount =  matchingAttributes(edgeObjectPropertiesHashMap.get(objectID).getObjectChangeProperties(),
                            objectPropertiesHashMap.get(answerObjectID).getObjectChangeProperties());
                    totalMatchCount += objectAttributeMatchCount;
                }
            }
            matchCountHashMap.put(Integer.parseInt(figureID), totalMatchCount);
        }

        return getMaxMatchFigureID(matchCountHashMap);
    }

    private Integer getMaxMatchFigureID(final HashMap <Integer, Integer> matchCountHashMap){
        Integer answerFigure = -1;
        Integer maxMatchCount = 0;

        for ( final Integer figureID: matchCountHashMap.keySet() ){
            if ( matchCountHashMap.get(figureID) >= maxMatchCount ){
                maxMatchCount = matchCountHashMap.get(figureID);
                answerFigure = figureID;
            }
        }
        return answerFigure;
    }

    private Integer matchingAttributes(final HashMap<String, String> attributesMap1, final HashMap<String, String> attributesMap2){
        Integer matchingAttributes = 0;

        for ( final String key1: attributesMap1.keySet()){
            for ( final String key2: attributesMap2.keySet()){
                if ( key1.equals(key2) && attributesMap1.get(key1).equals(attributesMap2.get(key2))){
                    matchingAttributes++;
                }
            }
        }
        return matchingAttributes;
    }



    private EdgeObjectProperties buildEdgeObjectProperties(final String imageOneKey, final String imageTwoKey, final HashMap<String, RavensObject> ravensObjectsA, final HashMap<String, RavensObject> ravensObjectsB ){
        //for each object in A iterate through the objects in B and build EdeObjectproperties
        //if there is only one object in A, then things are simple as the EdgeObjectProperties hashmap attribute will have just one map item
        Integer count = 1; //object ID will get replaced by count.

        final HashMap<String, ObjectProperties> edgeObjectPropertiesHashMap = new HashMap<>();

        for ( final String objectID: ravensObjectsA.keySet() ){
            final ObjectProperties objectProperties = new ObjectProperties();

            edgeObjectPropertiesHashMap.put(imageOneKey + count, objectProperties); //initialize the object properties

            buildObjectProperties(imageOneKey, imageTwoKey, count, objectProperties, ravensObjectsA.get(objectID), ravensObjectsB);
            count++;
        }
        final EdgeObjectProperties edgeObjectProperties = new EdgeObjectProperties();
        edgeObjectProperties.setEdgeObjectChangeProperties(edgeObjectPropertiesHashMap);
        return edgeObjectProperties;
    }

    private void buildObjectProperties(final String imageOneKey, final String imageTwoKey, final Integer count, final ObjectProperties objectProperties,
                                       final RavensObject ravensObjectAofi, final HashMap<String, RavensObject> ravensObjectsB){

        Integer countB = 1;

        //get attributes of A
        final HashMap<String, String> ravensObjectAAttributes = ravensObjectAofi.getAttributes();

        final HashMap<String, String> objectAliasNameMap = new HashMap<>();
        objectAliasNameMap.put(ravensObjectAofi.getName(), count.toString());

        //iterate through all the objects in B and build the object properties.
        for ( final String objectID: ravensObjectsB.keySet() ){
            final RavensObject ravensObject = ravensObjectsB.get(objectID);
            objectAliasNameMap.put(ravensObject.getName(), countB.toString());

            for ( final String attributeA: ravensObjectAAttributes.keySet()){
                final String shapeA = ravensObjectAAttributes.get("shape");

                //iterate through all the objects in B and build the object properties.
                final StringBuilder attributeStringForA = new StringBuilder().append(imageOneKey).append(count).append("-").
                        append(attributeA).append("-").append(ravensObjectAAttributes.get(attributeA));

                final HashMap<String, String> ravensObjectBAttributes = ravensObject.getAttributes();
                final String shapeB = ravensObjectBAttributes.get("shape");

                Boolean disappeared = Boolean.TRUE;
                for ( final String attributeB: ravensObjectBAttributes.keySet()){
                    if (   attributeA.equals(attributeB) && shapeA.equals(shapeB)){

                        if ( attributeA.equals("inside") ){
                            final StringBuilder attributeStringForB = new StringBuilder().append(imageTwoKey).append(countB).append("-").
                                    append(attributeB).append("-").append( objectAliasNameMap.get(ravensObjectBAttributes.get(attributeB)));
                            objectProperties.setProperty(attributeStringForA.toString(), attributeStringForB.toString());
                            disappeared = Boolean.FALSE;
                        }else{
                            final StringBuilder attributeStringForB = new StringBuilder().append(imageTwoKey).append(countB).append("-").
                                    append(attributeB).append("-").append(ravensObjectBAttributes.get(attributeB));
                            objectProperties.setProperty(attributeStringForA.toString(), attributeStringForB.toString());
                            disappeared = Boolean.FALSE;
                        }

                    }
                }
                if ( disappeared ){
                    objectProperties.setProperty(attributeStringForA.toString(), attributeStringForA.append("-").append("propertydisappeared").toString());
                }
            }
            countB++;
        }


    }


}

package ravensproject;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 This class currently CANNOT support 3x3 matrix
 */
public class AnswerTester {

    private ProblemNetwork network;
    private List<Transition> downwardTransition;
    private List<Transition> acrossTransition;
    private Map<String, Map<TransitionType, Integer>> scoreSystem;


    public AnswerTester(ProblemNetwork network) {
        this.network = network;
        scoreSystem = new HashMap<>();
        analyzeTransitions();
        setScores();
    }

    public void setScores() {
        Map<TransitionType, Integer> acrossScores = initializeScoreMap();
        Map<TransitionType, Integer> downScores = initializeScoreMap();

        for(Transition t : acrossTransition){
            TransitionType type = t.getTransitionType();
            switch(type){
                case UNCHANGED:
                    acrossScores.replace(type, 5);
                    break;
                case SCALED:
                    acrossScores.replace(type, 3);
                    break;
                case ROTATED:
                case REFLECTED:
                    acrossScores.replace(type, 4);
                    break;
                case DELETED:
                case ADDED:
                    acrossScores.replace(type, 2);
                    break;
                case FILL_CHANGED:
                    acrossScores.replace(type, 1);
                    break;
                case SHAPE_CHANGED:
                    acrossScores.replace(type, 0);
            }
        }

        for(Transition t : downwardTransition){
            TransitionType type = t.getTransitionType();
            switch(type){
                case UNCHANGED:
                    downScores.replace(type, 5);
                    break;
                case SCALED:
                    downScores.replace(type, 3);
                    break;
                case ROTATED:
                case REFLECTED:
                    downScores.replace(type, 4);
                    break;
                case DELETED:
                case ADDED:
                    downScores.replace(type, 2);
                    break;
                case FILL_CHANGED:
                    downScores.replace(type, 1);
                    break;
                case SHAPE_CHANGED:
                    downScores.replace(type, 0);
            }
        }

        scoreSystem.put("across", acrossScores);
        scoreSystem.put("down", downScores);
    }

    private static Map<TransitionType, Integer> initializeScoreMap() {
        Map<TransitionType, Integer> scoreMap = new HashMap<>();
        EnumSet<TransitionType> allTransitions = EnumSet.allOf(TransitionType.class);
        for(TransitionType t : allTransitions) {
            scoreMap.put(t, 0);
        }

        return scoreMap;
    }

    public void analyzeTransitions() {
        FigureNetwork upperLeft = network.getMatrix()[0][0];
        List<RavensTransition> adjacency = network.getAdjacencyList().get(upperLeft);
        adjacency.forEach(fn -> {
            List<Transition> transitionList = new ArrayList<>();
            fn.getChanges().values().forEach(transitionList::addAll);

            switch(fn.getDirection()){
                case "across":
                    acrossTransition = transitionList;
                    break;
                case "down":
                    downwardTransition = transitionList;
                    break;
            }
        });
    }

    public double calculateScore(FigureNetwork fnet, String direction) {
        FigureNetwork[][] matrix = network.getMatrix();
        List<Integer> score = new ArrayList<>();
        boolean bonusPoints = true;

        if(direction.equalsIgnoreCase("down")){
            RavensTransition downTransition = new RavensTransition(fnet, "down");
            downTransition.calculateTransition(matrix[1][0]);
            downTransition.getChanges().forEach((k, v) ->
                            score.add(v.stream().mapToInt(a -> {
                                int potentialScore = scoreSystem.get("down").get(a.getTransitionType());
                                if(potentialScore == 0) return 0;

                                int awardedScore = 0;
                                for (Transition t : downwardTransition) {
                                    if (t.getTransitionType() == a.getTransitionType()) {
                                        if(t.getValue() != null && t.getValue().equalsIgnoreCase(a.getValue()) ||
                                                t.getValue() == null && a.getValue() == null)
                                            awardedScore = potentialScore;
                                        else
                                            awardedScore = (int)Math.round(potentialScore / 2d);
                                    }
                                }

                                return awardedScore;
                            }).sum())
            );

            List<TransitionType> existingTransitions = new ArrayList<>();
            downTransition.getChanges().forEach((k,v) -> v.forEach(t -> existingTransitions.add(t.getTransitionType())));

            for(Transition t: downwardTransition){
                int index = existingTransitions.indexOf(t.getTransitionType());
                if(index == -1){
                    bonusPoints = false;
                    break;
                }

                existingTransitions.remove(index);
            }

            if(bonusPoints && existingTransitions.size() > 0)
                bonusPoints = false;
        }
        else {
            RavensTransition aTransition = new RavensTransition(fnet, "across");
            aTransition.calculateTransition(matrix[1][0]);
            aTransition.getChanges().forEach((k, v) ->
                            score.add(v.stream().mapToInt(a -> {
                                int potentialScore = scoreSystem.get("across").get(a.getTransitionType());
                                if(potentialScore == 0) return 0;

                                int awardedScore = 0;
                                for (Transition t : acrossTransition) {
                                    if (t.getTransitionType() == a.getTransitionType()) {
                                        if (t.getValue() != null && t.getValue().equalsIgnoreCase(a.getValue()) ||
                                                t.getValue() == null && a.getValue() == null)
                                            awardedScore = potentialScore;
                                        else
                                            awardedScore = (int)Math.round(potentialScore / 2d);
                                    }
                                }

                                return awardedScore;
                            }).sum())
            );

            List<TransitionType> existingTransitions = new ArrayList<>();
            aTransition.getChanges().forEach((k,v) -> v.forEach(t -> existingTransitions.add(t.getTransitionType())));
            for(Transition t: acrossTransition){
                int index = existingTransitions.indexOf(t.getTransitionType());
                if(index == -1){
                    bonusPoints = false;
                    break;
                }

                existingTransitions.remove(index);
            }

            if(bonusPoints && existingTransitions.size() > 0)
                bonusPoints = false;
        }

        if(bonusPoints)
            return score.stream().mapToInt(a -> a).sum() + 10;
        else
            return score.stream().mapToInt(a -> a).sum();
    }

    public int getAnswer(Map<Integer, FigureNetwork> answers) {
        final Map<Integer, Double> scoresAcross = new HashMap<>();
        final Map<Integer, Double> scoresDown = new HashMap<>();

        answers.forEach((k,v) -> scoresAcross.put(k, calculateScore(v, "across")));
        answers.forEach((k,v) -> scoresDown.put(k, calculateScore(v, "down")));

        Set<Integer> potentialAnswersAcross = new HashSet<>();
        Set<Integer> potentialAnswersDown = new HashSet<>();

        Integer currentHighScoreHolderAcross = null;
        double  highestScoreAcross = -1;

        for(Integer i : answers.keySet()) {
            if(scoresAcross.get(i) > highestScoreAcross && potentialAnswersAcross.size() > 0){
                potentialAnswersAcross.clear();
                highestScoreAcross = scoresAcross.get(i);
                currentHighScoreHolderAcross = i;
            }
            else if(scoresAcross.get(i) > highestScoreAcross){
                highestScoreAcross = scoresAcross.get(i);
                currentHighScoreHolderAcross = i;
            }
            else if(scoresAcross.get(i) == highestScoreAcross && currentHighScoreHolderAcross != null) {
                potentialAnswersAcross.add(i);
                currentHighScoreHolderAcross = i;
            }
        }

        if(!potentialAnswersAcross.contains(currentHighScoreHolderAcross)){
            potentialAnswersAcross.add(currentHighScoreHolderAcross);
        }

        Integer currentHighScoreHolderDown = null;
        double  highestScoreDown = -1;

        for(Integer i : answers.keySet()) {
            if(scoresDown.get(i) > highestScoreDown && potentialAnswersDown.size() > 0){
                potentialAnswersDown.clear();
                highestScoreDown = scoresDown.get(i);
                currentHighScoreHolderDown = i;
            }
            else if(scoresDown.get(i) > highestScoreDown){
                highestScoreDown = scoresDown.get(i);
                currentHighScoreHolderDown = i;
            }
            else if(scoresDown.get(i) == highestScoreDown && currentHighScoreHolderDown != null) {
                potentialAnswersDown.add(i);
                currentHighScoreHolderDown = i;
            }
        }

        if(!potentialAnswersDown.contains(currentHighScoreHolderDown)){
            potentialAnswersDown.add(currentHighScoreHolderDown);
        }

        if(potentialAnswersAcross.size() == 1 && potentialAnswersDown.contains(currentHighScoreHolderAcross)){
            return currentHighScoreHolderAcross;
        }
        else if(potentialAnswersDown.size() == 1 && potentialAnswersAcross.contains(currentHighScoreHolderDown)){
            return currentHighScoreHolderDown;
        }
        else{
            potentialAnswersAcross.retainAll(potentialAnswersDown);
            Map<String, Map<Integer, Double>> allScores = new HashMap<>();
            allScores.put("down", scoresDown);
            allScores.put("across", scoresAcross);

            Map<Integer, FigureNetwork> potentialAnswers = new HashMap<>();
            for(Integer i : potentialAnswersAcross){
                potentialAnswers.put(i, answers.get(i));
            }

            if(potentialAnswers.size() > 0){
                return tieBreaker(allScores, potentialAnswers);
            }
            else {
                Map<Integer, FigureNetwork> topAnswers = new HashMap<>();
                potentialAnswersAcross.forEach(s -> topAnswers.put(s, answers.get(s)));
                potentialAnswersDown.forEach(s -> topAnswers.put(s, answers.get(s)));
                return tieBreaker(allScores, topAnswers);
            }
        }
    }



    public int tieBreaker(Map<String, Map<Integer, Double>> scores, Map<Integer, FigureNetwork> potentialAnswers){

        //get average scores
        Map<Integer, Double> averages = new HashMap<>();

        for(Integer i : potentialAnswers.keySet()){
            double down = scores.get("down").get(i);
            double across = scores.get("across").get(i);

            averages.put(i, (down + across) / 2.0d);
        }

        List<Integer> highestScores = new ArrayList<>();
        double highestScore = -1;

        for(Map.Entry<Integer, Double> entry : averages.entrySet()) {
            if(entry.getValue() > highestScore){
                highestScore = entry.getValue();
                highestScores.clear();
                highestScores.add(entry.getKey());
            }
            else if(entry.getValue() == highestScore)
                highestScores.add(entry.getKey());
        }

        if (highestScores.size() == 1) {
            return highestScores.get(0);
        }
        else{
            return runProductionRules(highestScores, potentialAnswers);
        }
    }

    public int runProductionRules(List<Integer> tiedAnswers, Map<Integer, FigureNetwork> allPotentialAnswers){
        boolean sameCount = false;

        Map<Integer, RavensTransition> answerTransitionsAcross = new HashMap<>();
        for(Integer i : tiedAnswers){
            FigureNetwork answer = allPotentialAnswers.get(i);
            FigureNetwork acrossFigure = network.getMatrix()[0][1];
            RavensTransition trans = new RavensTransition(answer, "across");
            trans.calculateTransition(acrossFigure);
            answerTransitionsAcross.put(i, trans);
        }

        Map<Integer, RavensTransition> answerTransitionsDown = new HashMap<>();
        for(Integer i : tiedAnswers){
            FigureNetwork answer = allPotentialAnswers.get(i);
            FigureNetwork downFigure = network.getMatrix()[1][0];
            RavensTransition trans = new RavensTransition(answer, "down");
            trans.calculateTransition(downFigure);
            answerTransitionsDown.put(i, trans);
        }

        //check for unnecessary shape changes & eliminate those answers
        boolean downShapeChange = false;
        boolean acrossShapeChange = false;
        for(Transition t: downwardTransition){
            if(t.getTransitionType() == TransitionType.SHAPE_CHANGED)
                downShapeChange = true;
        }

        for(Transition t : acrossTransition){
            if(t.getTransitionType() == TransitionType.SHAPE_CHANGED)
                acrossShapeChange = true;
        }

        Set<Integer> removalSet = new HashSet<>();
        if(!downShapeChange){
            for(Map.Entry<Integer, RavensTransition> entry : answerTransitionsDown.entrySet()){

                innerLoop:
                for(List<Transition> ts : entry.getValue().getChanges().values()){
                    for(Transition t : ts){
                        if(t.getTransitionType() == TransitionType.SHAPE_CHANGED){
                            removalSet.add(entry.getKey());
                            break innerLoop;
                        }
                    }
                }
            }
        }

        if(!acrossShapeChange){
            for(Map.Entry<Integer, RavensTransition> entry : answerTransitionsAcross.entrySet()){

                innerLoop:
                for(List<Transition> ts : entry.getValue().getChanges().values()){
                    for(Transition t : ts){
                        if(t.getTransitionType() == TransitionType.SHAPE_CHANGED){
                            removalSet.add(entry.getKey());
                            break innerLoop;
                        }
                    }
                }
            }
        }

        if(removalSet.size() > 0){
            for(Integer i : removalSet){
                int idx = tiedAnswers.indexOf(i);
                tiedAnswers.remove(idx);
            }
        }

        if(tiedAnswers.size() == 1){
            return tiedAnswers.get(0);
        }


        if(doAllFiguresHaveSameObjectCount()){
            sameCount = true;
            int count = network.getMatrix()[0][0].getObjectCount();
            allPotentialAnswers = allPotentialAnswers.entrySet()
                    .stream()
                    .filter(e -> tiedAnswers.contains(e.getKey()))
                    .filter(e -> e.getValue().getObjectCount() == count)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if(allPotentialAnswers.size() == 1)
                return allPotentialAnswers.entrySet().stream().findFirst().get().getKey();
        }

        if(doAllFiguresHaveSameFill()){
            if(sameCount && network.getMatrix()[0][0].getObjectCount() == 1){
                String fillValue = network.getMatrix()[0][0].getObjects().get(0).getAttributes().get("fill");
                allPotentialAnswers = allPotentialAnswers.entrySet()
                        .stream()
                        .filter(e -> tiedAnswers.contains(e.getKey()))
                        .filter(e -> e.getValue().getObjects().get(0).getAttributes().get("fill").equalsIgnoreCase(fillValue))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            else if(sameCount) {
                //locate same types
                int count = network.getMatrix()[0][0].getObjectCount();
                List<RavensObject> primary = network.getMatrix()[0][0].getObjectHierarchy();
                List<Integer> removalList = new ArrayList<>();

                for(Map.Entry<Integer, FigureNetwork> entry :
                        allPotentialAnswers.entrySet().stream().filter(e -> tiedAnswers.contains(e.getKey())).collect(Collectors.toSet())){
                    List<RavensObject> current = entry.getValue().getObjectHierarchy();

                    for(int i = 0; i < count; i++){
                        if(primary.get(i).getAttributes().get("fill").equalsIgnoreCase(current.get(i).getAttributes().get("fill"))){
                            removalList.add(entry.getKey());
                            break;
                        }
                    }
                }

                for(Integer i : removalList){
                    allPotentialAnswers.remove(i);
                }
            }

            if(allPotentialAnswers.size() == 1)
                return allPotentialAnswers.entrySet().stream().findFirst().get().getKey();
        }

        if(doAllFiguresHaveSameShapes()) {
            if (sameCount && network.getMatrix()[0][0].getObjectCount() == 1) {
                String fillValue = network.getMatrix()[0][0].getObjects().get(0).getAttributes().get("shape");
                allPotentialAnswers = allPotentialAnswers.entrySet()
                        .stream()
                        .filter(e -> tiedAnswers.contains(e.getKey()))
                        .filter(e -> e.getValue().getObjects().get(0).getAttributes().get("shape").equalsIgnoreCase(fillValue))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            else if (sameCount) {
                //locate same types
                int count = network.getMatrix()[0][0].getObjectCount();
                List<RavensObject> primary = network.getMatrix()[0][0].getObjectHierarchy();
                List<Integer> removalList = new ArrayList<>();

                for (Map.Entry<Integer, FigureNetwork> entry :
                        allPotentialAnswers.entrySet().stream().filter(e -> tiedAnswers.contains(e.getKey())).collect(Collectors.toSet())) {
                    List<RavensObject> current = entry.getValue().getObjectHierarchy();

                    for (int i = 0; i < count; i++) {
                        if (primary.get(i).getAttributes().get("shape").equalsIgnoreCase(current.get(i).getAttributes().get("sha[e"))) {
                            removalList.add(entry.getKey());
                            break;
                        }
                    }
                }

                for (Integer i : removalList) {
                    allPotentialAnswers.remove(i);
                }
            }

            if (allPotentialAnswers.size() == 1)
                return -1;
        }


        //TODO:  ADD MORE RULES
        return allPotentialAnswers.entrySet().stream().findFirst().get().getKey();
    }

    public boolean doAllFiguresHaveSameObjectCount(){
        FigureNetwork[][] matrix = network.getMatrix();
        int firstFigureCount = matrix[0][0].getObjectCount();
        boolean sameCount = true;
        for(int x = 0; x < matrix.length; x++){
            boolean breakOut = false;

            for(int y = 0; y < matrix[x].length; y++){
               if(network.getSize() == MatrixSize.TWOSQUARE && x == 1 && y == 1){
                   breakOut = true;
                   break;
               }
                else if(network.getSize() == MatrixSize.THREESQUARE && x == 2 && y == 2){
                   breakOut = true;
                   break;
               }

                if(matrix[x][y].getObjectCount() != firstFigureCount){
                    sameCount = false;
                    breakOut = true;
                    break;
                }
            }

            if(breakOut)
                break;
        }

        return sameCount;
    }

    public boolean doAllFiguresHaveSameShapes(){
        FigureNetwork[][] matrix = network.getMatrix();
        List<String> firstFigurehapeList = new ArrayList<>();
        boolean sameShapes = true;

        matrix[0][0].getObjects().forEach(s -> firstFigurehapeList.add(s.getAttributes().get("shape")));

        for(int x = 0; x < matrix.length; x++){
            boolean breakOut = false;

            for(int y = 0; y < matrix[x].length; y++){
                if(network.getSize() == MatrixSize.TWOSQUARE && x == 1 && y ==1 ){
                    breakOut = true;
                    break;
                }
                else if(network.getSize() == MatrixSize.THREESQUARE && x == 2 && y == 2){
                    breakOut = true;
                    break;
                }


                for(RavensObject obj : matrix[x][y].getObjects()){
                    String shape = obj.getAttributes().get("shape");
                    if(!firstFigurehapeList.contains("shape")){
                        sameShapes = false;
                        breakOut = true;
                        break;
                    }
                }

                if(breakOut)
                    break;
            }

            if(breakOut)
                break;
        }

        return sameShapes;
    }

    public boolean doAllFiguresHaveSameFill(){
        FigureNetwork[][] matrix = network.getMatrix();
        boolean sameFill = true;

        if(matrix[0][0].getObjectCount() == 1){
            String firstFill = matrix[0][0].getObjects().get(0).getAttributes().get("fill");

            for(int x = 0; x < matrix.length; x++){
                boolean breakOut = false;

                for(int y = 0; y < matrix[x].length; y++){
                    if(network.getSize() == MatrixSize.TWOSQUARE && x == 1 && y ==1 ){
                        breakOut = true;
                        break;
                    }
                    else if(network.getSize() == MatrixSize.THREESQUARE && x == 2 && y == 2){
                        breakOut = true;
                        break;
                    }

                    if(!firstFill.equalsIgnoreCase(matrix[x][y].getObjects().get(0).getAttributes().get("fill"))){
                        sameFill = false;
                        breakOut = true;
                        break;
                    }
                }

                if(breakOut)
                    break;
            }

            return sameFill;
        }
        else{
            FigureNetwork firstFigure = matrix[0][0];
            Map<String, List<TripleStore>> firstFigureRelationships = matrix[0][0].getRelationships().stream()
                    .collect(Collectors.groupingBy(TripleStore::getRelationship));

            for(int x = 0; x < matrix.length; x++){
                boolean breakOut = false;
                for(int y = 0; y < matrix[x].length; y++){
                    if(network.getSize() == MatrixSize.TWOSQUARE && x == 1 && y == 1 ){
                        breakOut = true;
                        break;
                    }
                    else if(network.getSize() == MatrixSize.THREESQUARE && x == 2 && y == 2){
                        breakOut = true;
                        break;
                    }

                    if(matrix[x][y].getRelationships().size() == 0 || matrix[x][y].getObjects().size() == 1){
                        String key = firstFigureRelationships.entrySet().stream().findFirst().get().getKey();
                        String primaryFill = firstFigureRelationships.get(key).get(0).getRelated().getAttributes().get("fill");
                        if(!matrix[x][y].getObjects().get(0).getAttributes().get("fill").equalsIgnoreCase(primaryFill)){
                            sameFill = false;
                            breakOut = true;
                            break;
                        }
                    }
                    else if(matrix[x][y].getObjects().size() == firstFigure.getObjects().size()){
                        Map<String, List<TripleStore>> currentRelationships = matrix[x][y].getRelationships().stream()
                                .collect(Collectors.groupingBy(TripleStore::getRelationship));

                        for(String rel : currentRelationships.keySet()){
                            if(firstFigureRelationships.containsKey(rel)){
                                for(int i = 0; i < currentRelationships.size(); i++){
                                    TripleStore t = currentRelationships.get(rel).get(i);
                                    String primaryFill = t.getPrimary().getAttributes().get("fill");
                                    String relatedFill = t.getRelated().getAttributes().get("fill");

                                    String firstPrimaryFill = firstFigureRelationships.get(rel).get(i)
                                            .getPrimary().getAttributes().get("fill");
                                    String firstRelatedFill = firstFigureRelationships.get(rel).get(i)
                                            .getRelated().getAttributes().get("fill");

                                    if(!primaryFill.equalsIgnoreCase(firstPrimaryFill) ||
                                            !relatedFill.equalsIgnoreCase(firstRelatedFill)){
                                        return false;
                                    }
                                }
                            }

                        }
                    }
                }

                if(breakOut)
                    break;
            }

            return sameFill;
        }
    }
}

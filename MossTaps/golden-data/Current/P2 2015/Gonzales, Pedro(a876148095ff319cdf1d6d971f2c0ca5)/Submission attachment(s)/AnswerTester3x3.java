package ravensproject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests 3x3 answers
 */
public class AnswerTester3x3 {

    private ProblemNetwork network;
    private List<Transition> partialDownwardTransition;
    private List<Transition> partialAcrossTransition;

    private List<Transition> compositeDownardTransition;
    private List<Transition> compositeAcrossTransition;
    private Map<String, Map<TransitionType, Integer>> scoreSystem;

    public AnswerTester3x3(ProblemNetwork network){
        this.network = network;
        scoreSystem = new HashMap<>();
        analyzeTransitions();
        setScores();
    }

    public void analyzeTransitions() {
        analyzePartialTransitions();
        analyzeCompositeTransitions();
    }

    private void analyzePartialTransitions(){
        FigureNetwork upperLeft = network.getMatrix()[1][1];
        List<RavensTransition> adjacency = network.getAdjacencyList().get(upperLeft);
        adjacency.forEach(fn -> {
            List<Transition> transitionList = new ArrayList<>();
            fn.getChanges().values().forEach(transitionList::addAll);

            switch(fn.getDirection()){
                case "across":
                    partialAcrossTransition = transitionList;
                    break;
                case "down":
                    partialDownwardTransition = transitionList;
                    break;
            }
        });
    }

    private void analyzeCompositeTransitions(){
        final List<List<Transition>> across = new ArrayList<>();
        final List<List<Transition>> down = new ArrayList<>();

        outerLoop:
        for(int x = 0; x < network.getMatrix().length; x++) {
            for(int y = 0; y < network.getMatrix()[x].length; y++){
                if(x == 2 && y == 2)
                    break outerLoop;

                List<RavensTransition> firstAdjacency = network.getAdjacencyList().get(network.getMatrix()[x][y]);
                if(firstAdjacency != null){
                    firstAdjacency.forEach(fn -> {
                        List<Transition> transitionList = new ArrayList<>();
                        fn.getChanges().values().forEach(transitionList::addAll);

                        switch (fn.getDirection()) {
                            case "across":
                                across.add(transitionList);
                                break;
                            case "down":
                                down.add(transitionList);
                                break;
                        }
                    });
                }
            }
        }

        List<Transition> flattenedAcross = new ArrayList<>();
        across.forEach(flattenedAcross::addAll);


        Map<TransitionType, List<Transition>> groupedAcross = flattenedAcross
                .stream()
                .collect(Collectors.groupingBy(Transition::getTransitionType));

        compositeAcrossTransition = buildCompositeTransition(groupedAcross);

        List<Transition> flattenedDown = new ArrayList<>();
        across.forEach(flattenedDown::addAll);

        Map<TransitionType, List<Transition>> groupedDown = flattenedDown
                .stream()
                .collect(Collectors.groupingBy(Transition::getTransitionType));

        compositeDownardTransition = buildCompositeTransition(groupedDown);
    }

    private static List<Transition> buildCompositeTransition(Map<TransitionType,List<Transition>> transitions){
        List<Transition> composite = new ArrayList<>();
        List<Transition> addRemove = new ArrayList<>();

        for(TransitionType t : transitions.keySet()){
            switch(t){
                case ROTATED:
                    int rotation = transitions.get(t).stream().mapToInt(v -> Integer.parseInt(v.getValue())).sum();
                    Transition compRotation = new Transition(TransitionType.ROTATED);
                    compRotation.setValue(Integer.toString(rotation));
                    composite.add(compRotation);
                    break;

                case SCALED:
                case EXPANDED_H:
                case EXPANDED_W:
                    int amtGrown = 0;
                    for(Transition scT : transitions.get(t)){
                        if(scT.getValue().equalsIgnoreCase("grow"))
                            amtGrown++;
                        else
                            amtGrown--;
                    }

                    if(amtGrown > 0){
                        Transition compScale = new Transition(t);
                        compScale.setValue("grow");
                        composite.add(compScale);
                    }
                    else if(amtGrown > 0){
                        Transition compScale = new Transition(t);
                        compScale.setValue("shrink");
                        composite.add(compScale);
                    }
                    break;

                case REFLECTED:
                    if(transitions.get(t).size() % 2 != 0){
                        composite.add(new Transition(TransitionType.REFLECTED));
                    }
                    break;

                case SHAPE_CHANGED:
                    int lastIndex = transitions.get(t).size() - 1;
                    if(lastIndex >= 0){
                        String shape = transitions.get(t).get(lastIndex).getValue();
                        Transition compShape = new Transition(TransitionType.SHAPE_CHANGED);
                        compShape.setValue(shape);
                        composite.add(compShape);
                    }
                    break;

                case FILL_CHANGED:
                    int moreFill = 0;
                    for(Transition fillT : transitions.get(t)){
                        if(fillT.getValue().equalsIgnoreCase("added"))
                            moreFill++;
                        else if(fillT.getValue().equalsIgnoreCase("removed"))
                            moreFill--;
                    }

                    if(moreFill > 0){
                        Transition compScale = new Transition(TransitionType.SCALED);
                        compScale.setValue("added");
                        composite.add(compScale);
                    }
                    else if(moreFill > 0){
                        Transition compScale = new Transition(TransitionType.SCALED);
                        compScale.setValue("removed");
                        composite.add(compScale);
                    }
                    break;

                case DELETED:
                case ADDED:
                    addRemove.addAll(transitions.get(t));
                    break;
            }
        }

        if(addRemove.size() > 0){
            Map<String, Integer> amtChanged = new HashMap<>();

            for(Transition t : addRemove){
                if(!amtChanged.containsKey(t.getValue()))
                    amtChanged.put(t.getValue(), 0);

                int currentValue = amtChanged.get(t.getValue());

                if(t.getTransitionType() == TransitionType.ADDED)
                    amtChanged.replace(t.getValue(), ++currentValue);
                else
                    amtChanged.replace(t.getValue(), --currentValue);
            }

            for(String s : amtChanged.keySet()){
                if(amtChanged.get(s) > 0){
                    for(int i = 0; i < amtChanged.get(s); i++){
                        Transition added = new Transition(TransitionType.ADDED);
                        added.setValue(s);
                        composite.add(added);
                    }
                }
                else if(amtChanged.get(s) < 0){
                    for(int i = 0; i < amtChanged.get(s) * -1; i++){
                        Transition removed = new Transition(TransitionType.DELETED);
                        removed.setValue(s);
                        composite.add(removed);
                    }
                }
            }
        }

        return composite;
    }

    public void setScores() {
        Map<TransitionType, Integer> acrossScores = initializeScoreMap();
        Map<TransitionType, Integer> downScores = initializeScoreMap();

        for(Transition t : compositeAcrossTransition){
            TransitionType type = t.getTransitionType();
            switch(type){
                case UNCHANGED:
                    acrossScores.replace(type, 5);
                    break;
                case SCALED:
                case EXPANDED_H:
                case EXPANDED_W:
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

        for(Transition t : compositeDownardTransition){
            TransitionType type = t.getTransitionType();
            switch(type){
                case UNCHANGED:
                    downScores.replace(type, 5);
                    break;
                case SCALED:
                case EXPANDED_H:
                case EXPANDED_W:
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

    public double calculateScore(FigureNetwork fnet, String direction, int upperLeftX, int upperLeftY, boolean partial) {

        FigureNetwork[][] matrix = network.getMatrix();
        List<Integer> score = new ArrayList<>();
        boolean bonusPoints = true;
        List<Transition> knownTransitions;
        int x, y;
        if(direction.equalsIgnoreCase("down") && partial){
            knownTransitions = partialDownwardTransition;
            x = upperLeftX;
            y = upperLeftY + 1;
        }
        else if(direction.equalsIgnoreCase("across") && partial){
            knownTransitions = partialAcrossTransition;
            x = upperLeftX + 1;
            y = upperLeftY;
        }
        else if(direction.equalsIgnoreCase("down")){
            knownTransitions = compositeDownardTransition;
            x = upperLeftX + 2;
            y = upperLeftY;
        }
        else{
            knownTransitions = compositeAcrossTransition;
            x = upperLeftX;
            y = upperLeftY + 2;
        }



        RavensTransition transition = new RavensTransition(fnet, direction);
        transition.calculateTransition(matrix[x][y]);
        transition.getChanges().forEach((k, v) ->
                        score.add(v.stream().mapToInt(a -> {
                            int potentialScore = scoreSystem.get(direction).get(a.getTransitionType());
                            if(potentialScore == 0) return 0;

                            int awardedScore = 0;
                            for (Transition t : knownTransitions) {
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
        transition.getChanges().forEach((k,v) -> v.forEach(t -> existingTransitions.add(t.getTransitionType())));

        for(Transition t: knownTransitions){
            int index = existingTransitions.indexOf(t.getTransitionType());
            if(index == -1){
                bonusPoints = false;
                break;
            }

            existingTransitions.remove(index);
        }

        if(bonusPoints && existingTransitions.size() > 0)
            bonusPoints = false;

        if(bonusPoints)
            return score.stream().mapToInt(a -> a).sum() + 10;
        else
            return score.stream().mapToInt(a -> a).sum();
    }

    public int getAnswer(Map<Integer, FigureNetwork> answers) {
        List<Integer> meaAnswer = attemptMEASolve(answers);
        if(meaAnswer.size() == 1){
            System.out.printf("%s solved using MEA\n", this.network.getProblem().getName());
            return meaAnswer.get(0);
        }

        else if(meaAnswer.size() == 0){
            return fullSolve(answers);
        }

        else {
            Map<Integer, FigureNetwork> potential = new HashMap<>();
            meaAnswer.forEach(a -> potential.put(a, answers.get(a)));
            return fullSolve(potential);
       }
    }

    public List<Integer> attemptMEASolve(Map<Integer, FigureNetwork> answers) {

        List<Integer> potentialAnswers = new ArrayList<>();

        final Map<Integer, Double> scoresAcross = new HashMap<>();
        final Map<Integer, Double> scoresDown = new HashMap<>();

        answers.forEach((k,v) -> scoresAcross.put(k, calculateScore(v, "across", 1, 1, true)));
        answers.forEach((k,v) -> scoresDown.put(k, calculateScore(v, "down", 1, 1, true)));

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
            potentialAnswers.add(currentHighScoreHolderAcross);
        }
        else if(potentialAnswersDown.size() == 1 && potentialAnswersAcross.contains(currentHighScoreHolderDown)){
            potentialAnswers.add(currentHighScoreHolderDown);
        }
        else{
            potentialAnswersAcross.retainAll(potentialAnswersDown);
            Map<String, Map<Integer, Double>> allScores = new HashMap<>();
            allScores.put("down", scoresDown);
            allScores.put("across", scoresAcross);

            Map<Integer, FigureNetwork> checkMap = new HashMap<>();
            for(Integer i : potentialAnswersAcross){
                checkMap.put(i, answers.get(i));
            }

            if(checkMap.size() > 0){
                return checkHighestScore(allScores, checkMap);
            }
            else {
                Map<Integer, FigureNetwork> topAnswers = new HashMap<>();
                potentialAnswersAcross.forEach(s -> topAnswers.put(s, answers.get(s)));
                potentialAnswersDown.forEach(s -> topAnswers.put(s, answers.get(s)));
                return checkHighestScore(allScores, topAnswers);
            }
        }

        return potentialAnswers;
    }

    public int fullSolve(Map<Integer, FigureNetwork> answers) {

        final Map<Integer, Double> scoresAcross = new HashMap<>();
        final Map<Integer, Double> scoresDown = new HashMap<>();

        answers.forEach((k,v) -> scoresAcross.put(k, calculateScore(v, "across", 0, 0, false)));
        answers.forEach((k,v) -> scoresDown.put(k, calculateScore(v, "down", 0, 0, false)));

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
                List<Integer> highest = checkHighestScore(allScores, potentialAnswers);
                if(highest.size() == 1)
                    return highest.get(0);
                else{
                    System.out.printf("%s:Cannot decide between: ", network.getProblem().getName());
                    highest.forEach(System.out::print);
                    System.out.println("");

                    return -1;
                }

            }
            else {
                Map<Integer, FigureNetwork> topAnswers = new HashMap<>();
                potentialAnswersAcross.forEach(s -> topAnswers.put(s, answers.get(s)));
                potentialAnswersDown.forEach(s -> topAnswers.put(s, answers.get(s)));
                List<Integer> highest = checkHighestScore(allScores, topAnswers);
                if(highest.size() == 1){
                    return highest.get(0);
                }
                else{
                    System.out.printf("%s:Cannot decide between: ", network.getProblem().getName());
                    highest.forEach(System.out::print);
                    System.out.println("");
                    return -1;
                }
            }
        }
    }

    public List<Integer> checkHighestScore(Map<String, Map<Integer, Double>> scores, Map<Integer, FigureNetwork> potentialAnswers){
        Map<Integer, Double> averages = calculateAveragedScores(scores, potentialAnswers);


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

        return highestScores;
    }

    private Map<Integer, Double> calculateAveragedScores(Map<String, Map<Integer, Double>> scores, Map<Integer, FigureNetwork> potentialAnswers) {
        //get average scores
        Map<Integer, Double> averages = new HashMap<>();

        for(Integer i : potentialAnswers.keySet()){
            double down = scores.get("down").get(i);
            double across = scores.get("across").get(i);

            averages.put(i, (down + across) / 2.0d);
        }
        return averages;
    }
}

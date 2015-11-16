package ravensproject;

import java.util.*;

/**
 * Generates potential answers for the Answer Tester
 */
public class AnswerGenerator {

    private ProblemNetwork network;
    private boolean generated;
    private HashMap<Integer, FigureNetwork> generatedAnswers;

    public AnswerGenerator(ProblemNetwork network){
        this.network = network;
        this.generated = false;
        this.generatedAnswers = new HashMap<>(8);
    }

    public Map<Integer, FigureNetwork> generateSmart() {
        if(network.getSize() == MatrixSize.TWOSQUARE) {
            return generateAll();
        }
        else {
            SortedSet<FigureNetwork> answers = network.getAnswerNetworks();
            Map<Integer, FigureNetwork> updatedAnswers = EliminateAnswers(answers);

            if(updatedAnswers.size() == 0)
                return generateAll(); //fail-safe
            else
                return updatedAnswers;
        }
    }

    public Map<Integer, FigureNetwork> generateAll() {
        SortedSet<FigureNetwork> answers = network.getAnswerNetworks();

        if(!generated) {
            int i = 1;
            for(FigureNetwork a : answers){
                generatedAnswers.put(i, a);
                i++;
            }

            generated = true;
        }

        return generatedAnswers;
    }

    private Map<Integer, FigureNetwork> EliminateAnswers(SortedSet<FigureNetwork> answers) {
        Map<Integer, FigureNetwork> answerMap = new HashMap<>();
        FigureNetwork[] answerArray = new FigureNetwork[8];
        answers.toArray(answerArray);

        for(int i = 0; i < 8; i++){
            answerMap.put(i + 1, answerArray[i]);
        }

        if(SimilarityChecks.doAllFiguresHaveSameShape(this.network)){
            String firstShape = this.network.getMatrix()[0][0].getObjects().get(0).getAttributes().get("shape");
            answerMap = EliminateByAttribute(answerMap, "shape", firstShape);
        }

        if(SimilarityChecks.doAllFiguresHaveSameFill(this.network)){
            String firstFill = this.network.getMatrix()[0][0].getObjects().get(0).getAttributes().get("fill");
            answerMap = EliminateByAttribute(answerMap, "fill", firstFill);
        }

        return answerMap;
    }

    private Map<Integer, FigureNetwork> EliminateByAttribute(Map<Integer, FigureNetwork> answers, String attribute, String value){

        List<Integer> deletionList = new ArrayList<>(7);

        for(Integer i : answers.keySet()){
            for(RavensObject o : answers.get(i).getObjects()){
                if(o.getAttributes().containsKey(attribute) && !o.getAttributes().get(attribute).equalsIgnoreCase(value)){
                    deletionList.add(i);
                    break;
                }
                else if(!o.getAttributes().containsKey((attribute))){
                    deletionList.add(i);
                    break;
                }

            }
        }

        deletionList.forEach(answers::remove);
        return answers;
    }

}

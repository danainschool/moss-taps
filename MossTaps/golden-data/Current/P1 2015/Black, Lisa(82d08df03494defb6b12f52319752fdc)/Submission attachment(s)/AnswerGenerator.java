package ravensproject;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * Created by jblac_000 on 6/4/2015.
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

    public HashMap<Integer, FigureNetwork> generateSmart() {
        if(network.getSize() == MatrixSize.TWOSQUARE) {
            return generateAll();
        }
        else {
            //TODO:  Write code for smart generation of THREESQUARE answers
            SortedSet<FigureNetwork> answers = network.getAnswersNetworks();
            //this is failsafe
            return generateAll();
        }
    }

    public HashMap<Integer, FigureNetwork> generateAll() {
        SortedSet<FigureNetwork> answers = network.getAnswersNetworks();

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

}

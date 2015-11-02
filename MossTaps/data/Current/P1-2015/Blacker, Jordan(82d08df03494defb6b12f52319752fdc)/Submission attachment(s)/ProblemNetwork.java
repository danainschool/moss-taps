package ravensproject;

import java.util.*;

/**
 * Created by jblac_000 on 6/1/2015.
 */
public class ProblemNetwork {

    private static final String[] RPM2_FIGURES = { "A", "B", "C" };
    private static final String[] RPM3_FIGURES = { "A", "B", "C", "D", "E", "F", "G", "H" };
    private static final String[] ANSWERS = { "1", "2", "3", "4", "5", "6", "7", "8" };

    private MatrixSize size;
    private FigureNetwork[][] matrix;
    private HashMap<FigureNetwork, List<RavensTransition>> adjacencyList;
    private RavensProblem problem;

    public ProblemNetwork(RavensProblem problem, MatrixSize size) {
        this.size = size;
        this.problem = problem;
        int width = size.getSize();
        adjacencyList = new HashMap<>(width * width);
        this.matrix = new FigureNetwork[width][width];
        buildNetwork();
    }

    public MatrixSize getSize() {
        return size;
    }

    public FigureNetwork[][] getMatrix() {
        return matrix;
    }

    public HashMap<FigureNetwork, List<RavensTransition>> getAdjacencyList() {
        return adjacencyList;
    }

    public RavensProblem getProblem() {
        return problem;
    }

    public SortedSet<FigureNetwork> getAnswersNetworks(){

        SortedSet<FigureNetwork> answerSet = new TreeSet<>((FigureNetwork u1, FigureNetwork u2) -> {
            int answer1 = Integer.parseInt(u1.getFigure().getName());
            int answer2 = Integer.parseInt(u2.getFigure().getName());
            if (answer1 > answer2)
                return 1;
            else if (answer1 < answer2)
                return -1;
            else
                return 0;
        });

        for(int i = 0; i < ((this.size == MatrixSize.TWOSQUARE) ? 6 : 8); i++){
            RavensFigure figure = this.problem.getFigures().get(Integer.toString(i + 1));
            FigureNetwork fnet = new FigureNetwork(figure);
            fnet.buildNetwork();
            answerSet.add(fnet);
        }

        return answerSet;
    }

    public void buildNetwork() {
        HashMap<String, RavensFigure> figures = problem.getFigures();
        String[] figureNameList;

        if(size == MatrixSize.TWOSQUARE)
            figureNameList = RPM2_FIGURES;
        else
            figureNameList = RPM3_FIGURES;

        //build FigureNetworks
        //build matrix
        for(String s : figureNameList){
            RavensFigure figure = figures.get(s);
            FigureNetwork fn = new FigureNetwork(figure);

            switch(s) {
                case "A":
                    matrix[0][0] = fn;
                    break;
                case "B":
                    matrix[1][0] = fn;
                    break;
                case "C":
                    if(size == MatrixSize.THREESQUARE)
                        matrix[2][0] = fn;
                    else
                        matrix[0][1] = fn;
                    break;
                case "D":
                    matrix[0][1] = fn;
                    break;
                case "E":
                    matrix[1][1] = fn;
                    break;
                case "F":
                    matrix[2][1] = fn;
                    break;
                case "G":
                    matrix[0][2] = fn;
                    break;
                case "H":
                    matrix[1][2] = fn;
                    break;
                default:
                    continue;

            }

            fn.buildNetwork();
        }

        //build adjacency list for transitions
        for(int x = 0; x < size.getSize(); x++){
            for(int y = 0; y < size.getSize(); y++) {
                List<RavensTransition> transitions = new ArrayList<>();
                int left = x + 1;
                int below = y + 1;
                if(left < size.getSize() && matrix[left][y] != null) {
                    RavensTransition t = new RavensTransition(matrix[left][y], "across");
                    t.calculateTransition(matrix[x][y]);
                    transitions.add(t);
                }
                if (below < size.getSize() && matrix[x][below] != null) {
                    RavensTransition t = new RavensTransition(matrix[x][below], "down");
                    t.calculateTransition(matrix[x][y]);
                    transitions.add(t);
                }

                if(transitions.size() > 0)
                    adjacencyList.put(matrix[x][y], transitions);
                else
                    adjacencyList.put(matrix[x][y], null);
            }
        }
    }


}

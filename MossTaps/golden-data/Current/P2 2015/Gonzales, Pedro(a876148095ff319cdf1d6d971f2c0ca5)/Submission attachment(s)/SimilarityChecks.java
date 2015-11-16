package ravensproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Static Methods for Testing Similarities Throughout the Entire Problem Network
 */
public class SimilarityChecks {

    public static boolean doAllFiguresHaveSameObjectCount(ProblemNetwork network){
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

    public static boolean doAllFiguresHaveSameShape(ProblemNetwork network){
        return hasSameAttribute(network, "shape");
    }

    public static boolean doAllFiguresHaveSameFill(ProblemNetwork network) {
        return hasSameAttribute(network, "fill");
    }

    private static boolean hasSameAttribute(ProblemNetwork network, String attr){
        FigureNetwork[][] matrix = network.getMatrix();
        List<String> firstFigureAttrList = new ArrayList<>();
        String firstAttr;
        boolean same = true;

        if(matrix[0][0].getObjectCount() == 0){
            matrix[0][1].getObjects().forEach(s -> firstFigureAttrList.add(s.getAttributes().get(attr)));
        }
        else {
            matrix[0][0].getObjects().forEach(s -> firstFigureAttrList.add(s.getAttributes().get(attr)));
        }

        firstAttr = firstFigureAttrList.get(0);

        for(String s : firstFigureAttrList){
            if(!s.equalsIgnoreCase(firstAttr))
                return false;
        }

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
                    String shape = obj.getAttributes().get(attr);
                    if(shape.equalsIgnoreCase(firstAttr)){
                        same = false;
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

        return same;
    }
}

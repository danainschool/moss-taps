package ravensproject.betzel;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;
import ravensproject.betzel.interfaces.IFrame;
import ravensproject.betzel.interfaces.IFrameGroup;
import ravensproject.betzel.interfaces.ISolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scott betzel on 6/4/15.
 *
 * Use this to create a FrameMatrix from a RavensProblem
 */
public final class SolverFactory {

    public static ISolver createFrameMatrix(RavensProblem problem) {
        IFrameGroup currentFrameGroup;
        List<IFrame> currentFrameList;
        IFrame currentFrame;
        IFrameGroup[][] matrixFrameGroups;
        List<IFrameGroup> solutionFrameGroups = new ArrayList<>();

        String problemType = problem.getProblemType().toLowerCase();

        if (problemType.equals("2x2")) {
            matrixFrameGroups = new IFrameGroup[2][2];
        } else if (problemType.equals("3x3")) {
            matrixFrameGroups = new IFrameGroup[3][3];
        } else {
            System.err.println("Invalid Problem Type Detected");
            return null;
        }

        for(String figureName : problem.getFigures().keySet()) {
            RavensFigure thisFigure = problem.getFigures().get(figureName);
            currentFrameList = new ArrayList<>();

            for(String objectName : thisFigure.getObjects().keySet()) {
                RavensObject thisObject = thisFigure.getObjects().get(objectName);
                currentFrame = new Frame(objectName, new MasterComparator());

                for(String attributeName : thisObject.getAttributes().keySet()) {
                    String attributeValue = thisObject.getAttributes().get(attributeName);

                    currentFrame.put(attributeName, attributeValue);
                }

                currentFrameList.add(currentFrame);
            }

            currentFrameGroup = new FrameGroup(figureName, currentFrameList);
            currentFrameGroup.initialize();

            try {
                Integer.parseInt(figureName);
                solutionFrameGroups.add(currentFrameGroup);
            } catch (Exception ex) {
                assignToCorrectSlot(matrixFrameGroups, currentFrameGroup, problemType);
            }

        }

        FrameMatrix toRet;

        try {
            toRet = new FrameMatrix(matrixFrameGroups, solutionFrameGroups, problem.getName());
            toRet.initialize();
        } catch (Exception ex) {
            System.err.println("Unable to initialize FrameMatrix");
            toRet = null;
        }

        return toRet;
    }

    /**
     * This is lame
     *
     * @param frameGroupMatrix
     * @param toAssign
     * @param problemType
     */
    private static void assignToCorrectSlot(IFrameGroup[][] frameGroupMatrix, IFrameGroup toAssign, String problemType) {
        String name =toAssign.get_name().toUpperCase();
        problemType = problemType.toLowerCase();
        int x = 0;
        int y = 0;


        if (problemType.equals("2x2")) {
            if (name.equals("A")) {
                x = 0;
                y = 0;
            } else if (name.equals("B")) {
                x = 1;
                y = 0;
            } else if (name.equals("C")) {
                x = 0;
                y = 1;
            } else {
                System.err.println("Invalid letter found in 2x2");
            }
        } else {
            if (name.equals("A")) {
                x = 0;
                y = 0;
            } else if (name.equals("B")) {
                x = 1;
                y = 0;
            } else if (name.equals("C")) {
                x = 2;
                y = 0;
            } else if (name.equals("D")) {
                x = 1;
                y = 0;
            } else if (name.equals("E")) {
                x = 1;
                y = 1;
            } else if (name.equals("F")) {
                x = 1;
                y = 2;
            } else if (name.equals("G")) {
                x = 2;
                y = 0;
            } else if (name.equals("H")) {
                x = 2;
                y = 1;
            } else {
                System.err.println("Invalid letter found in 2x2");
            }
        }

        frameGroupMatrix[x][y] = toAssign;
    }

}

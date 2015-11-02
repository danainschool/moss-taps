package ravensproject.betzel.agents;

import ravensproject.RavensProblem;

import java.util.Set;

/**
 * Created by scott betzel on 6/24/15.
 *
 *
 */
public abstract class AgentBase {

    private String name;

    protected AgentBase(String theName) {
        this.name = theName;
    }

    public String getName() {
        return name;
    }

    public abstract Set<Integer> solve(RavensProblem problem) throws Exception;

    protected int getRow(String name) {
        name = name.toUpperCase();
        int toRet = -1;

        switch(name) {
            case "A":
                toRet = 0;
                break;
            case "B":
                toRet = 0;
                break;
            case "C":
                toRet = 0;
                break;
            case "D":
                toRet = 1;
                break;
            case "E":
                toRet = 1;
                break;
            case "F":
                toRet = 1;
                break;
            case "G":
                toRet = 2;
                break;
            case "H":
                toRet = 2;
                break;
        }

        return toRet;
    }

    protected int getCol(String name) {
        name = name.toUpperCase();
        int toRet = -1;

        switch(name) {
            case "A":
                toRet = 0;
                break;
            case "B":
                toRet = 1;
                break;
            case "C":
                toRet = 2;
                break;
            case "D":
                toRet = 0;
                break;
            case "E":
                toRet = 1;
                break;
            case "F":
                toRet = 2;
                break;
            case "G":
                toRet = 0;
                break;
            case "H":
                toRet = 1;
                break;
        }

        return toRet;
    }
}

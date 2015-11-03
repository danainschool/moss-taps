package ravensproject.MyPackage2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;

public class ProductionSystem3x3 {

	public static int solve(RavensProblem problem) {
		HashMap<String, RavensFigure> figures = problem.getFigures();
		RavensFigure fA = figures.get("A");
		RavensFigure fB = figures.get("B");
		RavensFigure fC = figures.get("C");
		RavensFigure fG = figures.get("G");
		RavensFigure fH = figures.get("H");

		List<RavensFigure> answersList = new ArrayList<RavensFigure>();
		answersList.add(figures.get("1"));
		answersList.add(figures.get("2"));
		answersList.add(figures.get("3"));
		answersList.add(figures.get("4"));
		answersList.add(figures.get("5"));
		answersList.add(figures.get("6"));
		answersList.add(figures.get("7"));
		answersList.add(figures.get("8"));

		
//		RavensFigureUtilities.printAttributes(fA);
//		RavensFigureUtilities.printAttributes(fB);
//		RavensFigureUtilities.printAttributes(fC);
//		RavensFigureUtilities.printAttributes(fG);
//		RavensFigureUtilities.printAttributes(fH);
//		RavensFigureUtilities.printAttributes(figures.get("4"));
		//RavensFigureUtilities.printAttributes(figures.get("7"));
		//RavensFigureUtilities.printAttributes(figures.get("1"));
		
		ProductionSystemRules.isTripple(fA,fC);
		
		int rule1 = applyRules(fA, fC);
		if (rule1 != -1) {
			for (RavensFigure fAns : answersList) {
				if (applyRules(fG, fAns) == rule1)
					return Integer.parseInt(fAns.getName());
			}
		}
		return -1;
	}

	public static int applyRules(RavensFigure figA, RavensFigure figB) {
		int ruleApplied = -1; // Default = -1

		if (ProductionSystemRules.isEqual(figA, figB))
			ruleApplied = 1;
		else if (ProductionSystemRules.isVerticalMirrorEffect(figA, figB))
			ruleApplied = 2;
		else if (ProductionSystemRules.isMiddleMirrorEffect(figA, figB))
			ruleApplied = 3;
		else if (ProductionSystemRules.isDouble(figA, figB))
			ruleApplied = 4;
		else if (ProductionSystemRules.isTripple(figA, figB))
			ruleApplied = 5;

		return ruleApplied;
	}
}

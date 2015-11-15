package ravensproject.MyPackage2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.MyPackage1.ProductionSystem;

public class GenerateAndTest3x3 {

	public static int solve(RavensProblem problem) {
		/*
		 * In 3x3 problems, only corner figures are used: A, C, G
		 */
		HashMap<String, RavensFigure> figures = problem.getFigures();
		RavensFigure fA = figures.get("A");
		RavensFigure fC = figures.get("C");
		RavensFigure fG = figures.get("G");
		RavensFigure fE = figures.get("E");

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
//		RavensFigureUtilities.printAttributes(fC);
		// RavensFigureUtilities.printAttributes(fE);
		// RavensFigureUtilities.printAttributes(fE);
		// RavensFigureUtilities.printAttributes(figures.get("7"));

		// Case: Diagonal Mirror
		// if fC=fG and fA=empty, then remove colors from fE and match
		// refer to figure C-08
		if (ProductionSystemRules.isEqual(fC, fG) && fA.getObjects().size() == 0) {
			RavensFigure fTmp = RavensFigureUtilities.getClone(fC);
			RavensFigureUtilities.makeTransparent(fTmp);
			for (RavensFigure fAns : answersList) {
				if (ProductionSystemRules.isEqual(fTmp, fAns)) {
					return Integer.parseInt(fAns.getName());
				}
			}
		}

		// Case: Generate and Test Attribute Transformations (Corners): A->C,
		// G->Ans
		ArrayList attributeTransformations = Transformations.getAttributeTransformations(fA, fC);
		//System.out.println(attributeTransformations);
		RavensFigure fTmp = RavensFigureUtilities.getClone(fG);
		//RavensFigureUtilities.printAttributes(fTmp);
		Transformations.applyAttributeTransformations(fTmp, attributeTransformations);
		//RavensFigureUtilities.printAttributes(fTmp);
		for (RavensFigure fAns : answersList) {
			if (ProductionSystemRules.isEqual(fTmp, fAns)) {
				return Integer.parseInt(fAns.getName());
			}
		}

		// Case: Generate and Test Attribute Transformations (Diagonal): A->E,
		// E->Ans
		ArrayList attributeTransformationsD = Transformations.getAttributeTransformations(fA, fE);
		RavensFigure fTmpD = RavensFigureUtilities.getClone(fE);
		Transformations.applyAttributeTransformations(fTmpD, attributeTransformationsD);
		// After transformation: original and modified should not be same
		// this is to avoid case getting true for C-12
		if (!ProductionSystemRules.isEqual(fTmpD, fE)) {
			for (RavensFigure fAns : answersList) {
				if (ProductionSystemRules.isEqual(fTmpD, fAns)) {
					return Integer.parseInt(fAns.getName());
				}
			}
		}

		// Generate and Test Shape Count Transformations
		// Apply it only on figures with more than one shape
		Map shapeCountTransformations = Transformations.getShapeCountTransformations(fA, fC);
		if (shapeCountTransformations.size() > 1) {
			for (RavensFigure fAns : answersList) {
				Map tmp = Transformations.getShapeCountTransformations(fG, fAns);
				if (shapeCountTransformations.equals(tmp)) {
					return Integer.parseInt(fAns.getName());
				}
			}
		}

		// Case: For figures with single shape objects only
		// compare orientation of objects: above, below, left, right
		if (RavensFigureUtilities.getShapeCount(fA).size() == 1 && RavensFigureUtilities.getShapeCount(fC).size() == 1
				&& RavensFigureUtilities.getShapeCount(fG).size() == 1) {
			Map<String, Integer> A = Transformations.getObjectsPositionTransformations(fA, fC);
			Map<String, Integer> B = Transformations.getObjectsPositionTransformations(fA, fG);
			Map<String, Integer> C = new HashMap<String, Integer>();
			for (String key : A.keySet()) {
				C.put(key, A.get(key) + B.get(key));
			}

			for (RavensFigure fAns : answersList) {
				if (RavensFigureUtilities.getShapeCount(fAns).size() == 1) {
					Map tmp = RavensFigureUtilities.getObjectsPosition(fAns);
					if (C.equals(tmp)) {
						return Integer.parseInt(fAns.getName());
					}
				}

			}

		}

		return -1;
	}
}

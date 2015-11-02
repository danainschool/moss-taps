package ravensproject;

import ravensproject.MyPackage1.MeansEndAnalysis;
import ravensproject.MyPackage1.ProductionSystem;
import ravensproject.MyPackage2.ProductionSystem3x3;
import ravensproject.MyPackage2.GenerateAndTest3x3;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {

	}

	/**
	 * The primary method for solving incoming Raven's Progressive Matrices. For
	 * each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return a String representing its
	 * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
	 * are also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName().
	 * 
	 * In addition to returning your answer at the end of the method, your Agent
	 * may also call problem.checkAnswer(String givenAnswer). The parameter
	 * passed to checkAnswer should be your Agent's current guess for the
	 * problem; checkAnswer will return the correct answer to the problem. This
	 * allows your Agent to check its answer. Note, however, that after your
	 * agent has called checkAnswer, it will *not* be able to change its answer.
	 * checkAnswer is used to allow your Agent to learn from its incorrect
	 * answers; however, your Agent cannot change the answer to a question it
	 * has already answered.
	 * 
	 * If your Agent calls checkAnswer during execution of Solve, the answer it
	 * returns will be ignored; otherwise, the answer returned at the end of
	 * Solve will be taken as your Agent's answer to this problem.
	 * 
	 * @param problem
	 *            the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public int Solve(RavensProblem problem) {
		/**
		 * 1. first pass the problem to ProductionSystem to Solve() 2.
		 * ProductionSystem will take figure A and B and apply rules to it 3. if
		 * any rule applies to A and B it will be returned otherwise
		 * ProductionSystem returns -1 meaning not applicable 4. use the
		 * returned rule between A and B, and apply it between C and answers 5.
		 * return the answer if C matches rule with answer choices, otherwise -1
		 */
		int result = -1;
		String problemType = problem.getProblemType();

		/*
		 * If problem is 2x2 then call: ProductionSystem, MeansEndAnalysis If
		 * problem is 3x3 then call:
		 */

		if (problemType.equals("2x2")) {
			result = ProductionSystem.solve(problem);
			if (result != -1)
				return result;
			/*
			 * B-10, 11, 12 Means-End Analysis. Calculate the differences
			 * between A,B and then apply them between C,Ans
			 */
			result = MeansEndAnalysis.solve(problem);
			return result;
		} else {

			/**
			 * Notes for Project 2 -Problems that can be solved by matching
			 * corner cases C01, C02,
			 */
			// System.out.println(problem.getName());
			// Skip non-verbal problems
			if (!problem.hasVerbal())
				return -1;

			result = ProductionSystem3x3.solve(problem);
			if (result != -1)
				return result;

			result = GenerateAndTest3x3.solve(problem);
		}

		return result;
	}
}
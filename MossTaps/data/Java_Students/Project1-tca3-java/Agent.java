package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

	public static int MAX_ANGLE = 360;
	public static int MAX_REFLECTION_SPLITS = 4; // maximum number of
													// reflections which the
													// agent will attempt to
													// match numbers

	public static HashMap<String, String> BINARY_PAIRS;
	public static String[] BinaryPairs1 = { "top", "left" };
	public static String[] BinaryPairs2 = { "bottom", "right" };

	public CaseBasedReasoningSystem mCaseBasedReasoningSystem;
	public ArrayList<ObjectPairing> mMostRecentObjectPairings;

	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {
		BINARY_PAIRS = new HashMap<String, String>();
		for (int i = 0; i < BinaryPairs1.length; i++) {
			BINARY_PAIRS.put(BinaryPairs1[i], BinaryPairs2[i]);
			BINARY_PAIRS.put(BinaryPairs2[i], BinaryPairs1[i]);
		}

		mCaseBasedReasoningSystem = new CaseBasedReasoningSystem();
		mMostRecentObjectPairings = new ArrayList<ObjectPairing>();

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

		// declaration of guess
		int myGuess = -1;

		// get problem type
		String problemType = problem.getProblemType();

		// 2x2 verbal problems
		if (problem.hasVerbal() && problemType.equals("2x2")) {
			// attempt to solve verbal representation
			myGuess = SolveVerbal(problem);
		}
		// TODO: Implement other problem types

		int rightAnswer = problem.checkAnswer(myGuess);

		// store information to case-based reasoning system
		mCaseBasedReasoningSystem.AddResult(rightAnswer == myGuess);

		// TODO: Use right answer to affect behavior going forward

		return myGuess;
	}

	// Solves 2x2 verbal problems
	public int SolveVerbal(RavensProblem problem) {
		// create multiple dimension array out of RavensFigures
		HashMap<String, RavensFigure> figuresHash = problem.getFigures();
		RavensFigure[][] figures = new RavensFigure[2][2];

		// iterate through hash map and store figures to array
		int arrayIndex = 0;
		for (char i = 'A'; i < 'D'; i++) {
			// check that hash map contains the desired key
			// if not, return -1
			if (!(figuresHash.containsKey(Character.toString(i)))) {
				System.out.println("Improperly formatted problem");
				return -1;
			}

			figures[arrayIndex % 2][arrayIndex / 2] = figuresHash.get(Character
					.toString(i));
			arrayIndex++;
		}

		// create strings to represent transformations
		TransformList horizontalTransforms = CompareFigures(figures[0][0],
				figures[1][0]);
		TransformList verticalTransforms = CompareFigures(figures[0][0],
				figures[0][1]);

		System.out.println("\n" + problem.getName());
		System.out.println("Horizontal Transform:\n"
				+ horizontalTransforms.toString());
		System.out.println("Vertical Transform:\n"
				+ verticalTransforms.toString());

		// create guess for figure D by applying transforms to existing figure
		RavensFigure guess = ApplyTransforms(figures[0][0],
				horizontalTransforms);
		guess = ApplyTransforms(guess, verticalTransforms);

		int myGuess = 0;
		double myGuessDifference = Double.POSITIVE_INFINITY;
		TransformList myGuessTransformList = null;

		// compare guess to possible answer choices
		for (int i = 1; i <= 6; i++) {
			TransformList transformDifference = CompareFigures(guess,
					figuresHash.get(Integer.toString(i)));
			System.out.printf("Answer Choice: %d %d\n", i,
					transformDifference.GetDifferenceScore());
			// DEBUG System.out.println(transformDifference.toString());
			if (transformDifference.GetDifferenceScore() < myGuessDifference) {
				myGuessDifference = transformDifference.GetDifferenceScore();
				myGuess = i;
				myGuessTransformList = transformDifference;
			}
			// in case of a tie, consult case-based reasoning system to break
			// tie
			else if (mCaseBasedReasoningSystem
					.GetNumSuccesses(transformDifference) > mCaseBasedReasoningSystem
					.GetNumSuccesses(myGuessTransformList)) {
				myGuessDifference = transformDifference.GetDifferenceScore();
				myGuess = i;
				myGuessTransformList = transformDifference;
			}
		}

		return myGuess;
	}

	// returns list of transforms describing difference between inputted figures
	public TransformList CompareFigures(RavensFigure figure1,
			RavensFigure figure2) {
		TransformList result = new TransformList();

		Iterator<RavensObject> it1 = figure1.getObjects().values().iterator();
		Iterator<RavensObject> it2 = figure2.getObjects().values().iterator();

		// create lists of RavensObjects
		ArrayList<RavensObject> list1 = new ArrayList<RavensObject>();
		ArrayList<RavensObject> list2 = new ArrayList<RavensObject>();
		while (it1.hasNext()) {
			list1.add(it1.next());
		}

		while (it2.hasNext()) {
			list2.add(it2.next());
		}

		// map objects across figures
		ObjectPairingList objectPairs = new ObjectPairingList();
		for (RavensObject object1 : list1) {
			for (RavensObject object2 : list2) {
				objectPairs.AddObjectPairing(object1, object2,
						CalculateObjectDifferenceScore(object1, object2));
			}
		}

		mMostRecentObjectPairings = objectPairs.GetBestPairings();

		// get difference strings between each object mapping
		for (ObjectPairing objectPairing : mMostRecentObjectPairings) {
			result.AddTransforms(objectPairing.toTransformList());
		}

		return result;
	}

	// returns difference score between two inputted objects
	public int CalculateObjectDifferenceScore(RavensObject object1,
			RavensObject object2) {
		int result = 0;

		HashMap<String, String> hashmap1 = object1.getAttributes();
		HashMap<String, String> hashmap2 = object2.getAttributes();

		for (Map.Entry<String, String> entry : hashmap1.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			// check if hashmap contains key, if not, add 1 to diff score
			if (hashmap2.containsKey(key)) {
				// check if values are different, if so, add 1 to diff score
				if (!(value.equals(hashmap2.get(key)))) {
					result++;
				}
			} else {
				result++;
			}
		}

		// also check whether object2 has any attributes which object1 does not
		// have
		for (Map.Entry<String, String> entry : hashmap2.entrySet()) {
			String key = entry.getKey();

			if (!(hashmap1.containsKey(key))) {
				result++;
			}
		}

		return result;
	}

	public RavensFigure ApplyTransforms(RavensFigure figure,
			TransformList transforms) {

		// get relevant data out of RavensFigure
		HashMap<String, RavensObject> hashmap = figure.getObjects();

		for (Transform transform : transforms.mList) {

			// get relevant object out of hashmap
			RavensObject object = hashmap.get(transform.mOriginalObjectName);

			// if creation
			if (transform.isCreation()) {
				hashmap.put(transform.mNewObjectName,
						transform.mCreationDeletionObject);
			}

			// if deletion
			else if (transform.isDeletion()) {
				hashmap.remove(transform.mOriginalObjectName);
			}

			// if numeric, apply numeric transform
			else if (transform.isNumeric()) {

				// attempt to apply reflection logic instead of
				// addition/subtraction
				if (transform.GetReflectionLine() != null) {
					String attribute = object.getAttributes().get(
							transform.mAttribute);
					int attributeNum = Integer.parseInt(attribute);
					double transformDifference = 2 * ((transform
							.GetReflectionLine() - attributeNum));
					if (Math.abs(transformDifference) > MAX_ANGLE / 2) {
						transformDifference += MAX_ANGLE;
					}

					attributeNum += transformDifference;
					attributeNum = NormalizeAttributeValue(
							transform.mAttribute, attributeNum); // get number
																	// into
																	// range

					object.getAttributes().put(transform.mAttribute,
							Integer.toString(attributeNum));

				} else {
					String attribute = object.getAttributes().get(
							transform.mAttribute);
					int transformDifference = transform.GetNumericDifference();
					int attributeNum = Integer.parseInt(attribute);

					attributeNum += transformDifference;
					attributeNum = NormalizeAttributeValue(
							transform.mAttribute, attributeNum); // get number
																	// into
																	// range

					object.getAttributes().put(transform.mAttribute,
							Integer.toString(attributeNum));
				}

			}

			else if (object == null) {
				// do nothing
			}
			// otherwise, apply text transform
			else {

				// attempt to apply reflection logic before resorting to direct
				// text
				// replacement
				String attribute;
				if ((transform.getBinaryTextTransform()) != null) {
					String[] textTransform = transform.getBinaryTextTransform();

					attribute = object.getAttributes()
							.get(transform.mAttribute);
					attribute = attribute.replace(textTransform[0],
							textTransform[1]);
					object.getAttributes().put(transform.mAttribute, attribute);
				} else {
					attribute = transform.mNewValue;

					object.getAttributes().put(transform.mAttribute, attribute);
				}
			}

			// TODO: semantic network representation, apply to transform using
			// object mappings
		}
		return figure;
	}

	public int NormalizeAttributeValue(String attributeName, int attributeNum) {
		// if attribute is an angle
		if (attributeName.equals("angle")) {
			return attributeNum % MAX_ANGLE;
		}

		// otherwise, just return the number unchanged
		return attributeNum;
	}

	class CaseBasedReasoningSystem {

		public HashMap<String, Integer> mSuccesses;
		public TransformList mMostRecentCase;

		public CaseBasedReasoningSystem() {
			mSuccesses = new HashMap<String, Integer>();
			mMostRecentCase = new TransformList();
		}

		public void AddCase(TransformList transforms) {
			mMostRecentCase = transforms;
		}

		public void AddResult(boolean result) {
			for (Transform t : mMostRecentCase.mList) {
				if (!(mSuccesses.containsKey(t.mAttribute))) {
					// add case
					mSuccesses.put(t.mAttribute, 0);
				}

				// if successful, increment hash table entry by 1
				if (result) {
					mSuccesses.put(t.mAttribute,
							mSuccesses.get(t.mAttribute) + 1);
				}

			}
		}

		// returns average number of successes with a particular set of
		// transforms
		public double GetNumSuccesses(TransformList transforms) {
			int result = 0;

			for (Transform t : transforms.mList) {
				if (mSuccesses.containsKey(t.mAttribute)) {
					result += mSuccesses.get(t.mAttribute);
				}
			}

			return 1.0 * result / transforms.mList.size();
		}
	}
}

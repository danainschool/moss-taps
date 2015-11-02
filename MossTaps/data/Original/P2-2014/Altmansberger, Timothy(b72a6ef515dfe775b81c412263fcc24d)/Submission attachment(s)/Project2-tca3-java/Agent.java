package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

// Uncomment these lines to access image processing.
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

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
	public static int COMPRESS_SIZE = 46;

	public static double SIMILARITY_PERCENT = 0.15;
	public static double STDEV_PERCENT = 0.10;

	public static final String DIFFSTRING_CENTERX = "centerx";
	public static final String DIFFSTRING_CENTERY = "centery";
	public static final String DIFFSTRING_NUMPOINTS = "numpoints";
	public static final String DIFFSTRING_HEIGHT = "height";
	public static final String DIFFSTRING_WIDTH = "width";
	public static final String DIFFSTRING_AREA = "area";
	public static final String DIFFSTRING_DENSITY = "density";

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
		GuessList myGuesses = new GuessList();

		// PARSE FIGURES

		// N = size of problem (i.e. 2x2, 3x3, etc.) - assuming matrix is square
		String problemSize = problem.getProblemType();
		int N = Integer.parseInt((problemSize.split("x"))[0]);

		// create multiple dimension array out of RavensFigures
		HashMap<String, RavensFigure> figuresHash = problem.getFigures();
		RavensFigure[][] figures = new RavensFigure[N][N];

		// determine number of problem figures that need to be loaded
		char maxChar = 'A';
		for (int i = 0; i < Math.pow(N, 2) - 1; i++) {
			maxChar++;
		}

		// iterate through hash map and store figures to array
		int arrayIndex = 0;
		for (char i = 'A'; i < maxChar; i++) {
			// check that hash map contains the desired key
			// if not, return -1
			if (!(figuresHash.containsKey(Character.toString(i)))) {
				System.out.println("Improperly formatted problem");
				return -1;
			}

			figures[arrayIndex % N][arrayIndex / N] = figuresHash.get(Character
					.toString(i));
			arrayIndex++;
		}

		// VERBAL SOLUTION
		// if (problem.hasVerbal()) {
		// // attempt to solve verbal representation
		// myGuesses.AddGuess((SolveVerbal(problem, figures)));
		// }

		// VISUAL SOLUTION
		if (problem.hasVisual()) {
			myGuesses.AddGuess(SolveVisual(problem, figures));
		}

		// CONSTRUCT FINAL GUESS
		int myGuess = myGuesses.GetMostPopularGuess();

		int rightAnswer = problem.checkAnswer(myGuess);

		// store information to case-based reasoning system
		mCaseBasedReasoningSystem.AddResult(rightAnswer == myGuess);

		// TODO: need to improve case-based reasoning

		return myGuess;
	}

	// Solves verbal problems
	public Guess SolveVerbal(RavensProblem problem, RavensFigure[][] figures) {

		int N = figures.length;

		// get all information describing each transform
		// 0 1 2
		// ______________
		// 0 | | | |
		// --------------
		// 1 | | | |
		// --------------
		// 2 | | | |
		// --------------

		ArrayList<TransformList> horizontalTransforms = new ArrayList<TransformList>();
		ArrayList<TransformList> verticalTransforms = new ArrayList<TransformList>();

		// TODO: am going to need to split the transforms by row and column
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (isHorizontalTransformFromPosition(i, j, N)) {
					horizontalTransforms.add(CompareFigures(figures[i][j],
							figures[i + 1][j]));
				}

				if (isVerticalTransformFromPosition(i, j, N)) {
					verticalTransforms.add(CompareFigures(figures[i][j],
							figures[i][j + 1]));
				}
			}
		}

		// aggregate all transforms together
		ArrayList<TransformList> aggregateHorizontalTransforms = AggregateTransformLists(
				horizontalTransforms, N);
		ArrayList<TransformList> aggregateVerticalTransforms = AggregateTransformLists(
				verticalTransforms, N);
		RavensFigure guess = figures[0][0];

		System.out.println("\n" + problem.getName());

		// create guess for figure D by applying transforms to existing figure
		for (int i = 0; i < aggregateHorizontalTransforms.size(); i++) {
			System.out.printf("Horizontal Transform %d: %s\n", i,
					aggregateHorizontalTransforms.get(i).toString());
			guess = ApplyTransforms(guess, aggregateHorizontalTransforms.get(i));

		}

		for (int i = 0; i < aggregateVerticalTransforms.size(); i++) {
			System.out.printf("Vertical Transform %d: %s\n", i,
					aggregateVerticalTransforms.get(i).toString());
			guess = ApplyTransforms(guess, aggregateVerticalTransforms.get(i));
		}

		Guess myGuess = new Guess();
		myGuess.mAnswerChoice = 0;
		myGuess.mDifferenceScore = Integer.MAX_VALUE;

		TransformList myGuessTransformList = null;

		// compare guess to possible answer choices
		int answerIdx = 1;
		HashMap<String, RavensFigure> figuresHash = problem.getFigures();

		while (figuresHash.containsKey(Integer.toString(answerIdx))) {
			TransformList transformDifference = CompareFigures(guess,
					figuresHash.get(Integer.toString(answerIdx)));
			System.out.printf("Answer Choice: %d %d\n", answerIdx,
					transformDifference.GetDifferenceScore());

			if (transformDifference.GetDifferenceScore() < myGuess.mDifferenceScore) {
				myGuess.mAnswerChoice = answerIdx;
				myGuess.mDifferenceScore = transformDifference
						.GetDifferenceScore();
				myGuessTransformList = transformDifference;
			}

			// in case of a tie, consult case-based reasoning system to break
			// tie
			else if (mCaseBasedReasoningSystem
					.GetNumSuccesses(transformDifference) > mCaseBasedReasoningSystem
					.GetNumSuccesses(myGuessTransformList)) {
				myGuess.mAnswerChoice = answerIdx;
				myGuess.mDifferenceScore = transformDifference
						.GetDifferenceScore();

				myGuessTransformList = transformDifference;
			}

			answerIdx++;
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
			if (objectPairing.mObject1 != null
					&& objectPairing.mObject2 != null) {
				// System.out.println(objectPairing.toString());
			}

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

			// skip if it involves relative positioning
			if (key.equals("inside") || key.equals("above")
					|| key.equals("left-of") || key.equals("overlaps")) {
				continue;
			}

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

			if (key.equals("inside") || key.equals("above")
					|| key.equals("left-of") || key.equals("overlaps")) {
				continue;
			}

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

	// N = matrice size
	public boolean isHorizontalTransformFromPosition(int X, int Y, int N) {
		return (!((X == N - 1) || ((X == N - 2) && (Y == N - 1))));
	}

	// N = matrice size
	public boolean isVerticalTransformFromPosition(int X, int Y, int N) {
		return (!((Y == N - 1) || ((Y == N - 2) && (X == N - 1))));
	}

	public ArrayList<TransformList> AggregateTransformLists(
			ArrayList<TransformList> transforms, int N) {
		ArrayList<TransformList> result = new ArrayList<TransformList>();
		int ResultLength = N - 1;

		// TODO: Still could use improvement
		for (int i = 0; i < ResultLength; i++) {
			result.add(transforms.get(i));
		}

		for (TransformList transform : transforms) {
			System.out.println(transform.toString());
		}

		// THOUGHTS: For each transform, if stuff changes, abstract it away,
		// creation of objects should be chained
		// for example, if size changes

		// THOUGHTS: Much potential for case-based reasoning here (e.g. building
		// sequences - small->medium->large->huge)

		return result;
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

	class GuessList {

		ArrayList<Guess> mGuessList;

		public GuessList() {
			mGuessList = new ArrayList<Guess>();
		}

		public void AddGuess(Guess g) {
			mGuessList.add(g);
		}

		public int GetMostPopularGuess() {
			HashMap<Integer, Integer> votes = new HashMap<Integer, Integer>();

			if (mGuessList.isEmpty()) {
				return 0;
			}

			for (Guess g : mGuessList) {
				if (!votes.containsKey(g.mAnswerChoice)) {
					votes.put(g.mAnswerChoice, 0);
				}

				votes.put(g.mAnswerChoice, votes.get(g.mAnswerChoice) + 1);
			}

			Entry<Integer, Integer> maxEntry = null;

			for (Entry<Integer, Integer> entry : votes.entrySet()) {
				if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
					maxEntry = entry;
				}
			}

			return maxEntry.getKey();
		}

		public int GetGuessWithMostCertainty() {
			Guess bestGuess = new Guess();
			bestGuess.mAnswerChoice = -1;
			bestGuess.mDifferenceScore = Integer.MAX_VALUE;

			for (Guess g : mGuessList) {
				if (bestGuess.mDifferenceScore > g.mDifferenceScore) {
					bestGuess.mAnswerChoice = g.mAnswerChoice;
					bestGuess.mDifferenceScore = g.mDifferenceScore;
				}
			}

			return bestGuess.mAnswerChoice;
		}

	}

	class Guess {
		public int mAnswerChoice;
		public int mDifferenceScore;
	}

	// /////////////////////
	// IMAGING FUNCTIONS //
	// /////////////////////

	// Solves visual problems
	public Guess SolveVisual(RavensProblem problem, RavensFigure[][] figures) {

		Guess myGuess = new Guess();

		// first, attempt to find pattern in pixels (saves on execution time)
		int[][] pixelCounts = new int[figures.length][figures.length];
		for (int i = 0; i < figures.length; i++) {
			for (int j = 0; j < figures.length; j++) {
				if (!(i == figures.length - 1 && j == figures.length - 1)) {
					pixelCounts[i][j] = GetPixelCount(figures[j][i]);
					System.out.printf("%d ", pixelCounts[i][j]);
				}
			}
			System.out.println();
		}

		int bestAnswer = 0;
		int bestDifferenceScore = Integer.MAX_VALUE;
		int secondBestDifferenceScore = Integer.MAX_VALUE;

		IntHolder guessedPixels = new IntHolder(pixelCounts[0][0]);

		HashMap<String, RavensFigure> figuresHash = problem.getFigures();
		int answerIdx = 1;

		if (isPixelPattern(pixelCounts, guessedPixels)) {

			while (figuresHash.containsKey(Integer.toString(answerIdx))) {

				// get shapes from array
				RavensFigure answerCandidate = figuresHash.get(Integer
						.toString(answerIdx));

				int pixelCount = GetPixelCount(answerCandidate);

				System.out.printf("%d  %d\n", pixelCount, guessedPixels.x);
				int diff = Math.abs(pixelCount - guessedPixels.x);

				if (diff < bestDifferenceScore && // better than current
													// difference score
						(diff < (0.15 * pixelCount)))// a good guess overall
				{
					secondBestDifferenceScore = bestDifferenceScore;
					bestDifferenceScore = diff;
					bestAnswer = answerIdx;
				} else if (diff < secondBestDifferenceScore && // better than
																// current
																// difference
																// score
						(diff < (0.15 * pixelCount))) // a good guess overall
				{
					secondBestDifferenceScore = diff;
				}

				answerIdx++;
			}
		}

		System.out.println(problem.getName());

		System.out.printf("%d    %d\n", bestDifferenceScore,
				secondBestDifferenceScore);
		if (bestAnswer != 0
				&& ((secondBestDifferenceScore - bestDifferenceScore) > 100)) {
			myGuess.mAnswerChoice = bestAnswer;
			return myGuess;
		}

		// try corners method
		if (figures.length == 3) {
			int horzDiff = GetPixelCount(figures[2][0])
					- GetPixelCount(figures[0][0]);
			System.out.printf("Horizontal Pixel Diff : %d\n", horzDiff);
			int vertDiff = GetPixelCount(figures[2][1])
					- GetPixelCount(figures[2][0]);
			System.out.printf("Vertical Pixel Diff : %d\n", vertDiff);
			guessedPixels.x = GetPixelCount(figures[0][0]) + horzDiff + 2
					* vertDiff;
			System.out.printf("Pixel Guess : %d\n", guessedPixels.x);

			answerIdx = 1;

			while (figuresHash.containsKey(Integer.toString(answerIdx))) {

				// get shapes from array
				RavensFigure answerCandidate = figuresHash.get(Integer
						.toString(answerIdx));

				int pixelCount = GetPixelCount(answerCandidate);

				System.out.printf("%d  %d\n", pixelCount, guessedPixels.x);
				int diff = Math.abs(pixelCount - guessedPixels.x);

				if (diff < bestDifferenceScore && // better than current
													// difference score
						(diff < (0.15 * pixelCount)))// a good guess overall
				{
					secondBestDifferenceScore = bestDifferenceScore;
					bestDifferenceScore = diff;
					bestAnswer = answerIdx;
				} else if (diff < secondBestDifferenceScore && // better than
																// current
																// difference
																// score
						(diff < (0.15 * pixelCount))) // a good guess overall
				{
					secondBestDifferenceScore = diff;
				}

				answerIdx++;
			}

		}

		System.out.printf("%d    %d\n", bestDifferenceScore,
				secondBestDifferenceScore);
		if (bestAnswer != 0
				&& ((secondBestDifferenceScore - bestDifferenceScore) > 100)) {
			myGuess.mAnswerChoice = bestAnswer;
			return myGuess;
		}

		// try corners method using center
		if (figures.length == 3) {
			Point topRightPixelCenter = getPixelCenter(figures[2][0]);
			Point midRightPixelCenter = getPixelCenter(figures[2][1]);
			
			System.out.println(topRightPixelCenter.toString());
			System.out.println(midRightPixelCenter.toString());

			int diffX = midRightPixelCenter.x - topRightPixelCenter.x;
			int diffY = midRightPixelCenter.y - topRightPixelCenter.y;

			Point guessedCenter = new Point(topRightPixelCenter.x + 2*diffX,
					topRightPixelCenter.y + 2*diffY);

			answerIdx = 1;
			int numAnswersFound = 0;

			while (figuresHash.containsKey(Integer.toString(answerIdx))) {

				RavensFigure answerCandidate = figuresHash.get(Integer
						.toString(answerIdx));

				Point answerCandidateCenter = getPixelCenter(answerCandidate);
				System.out.printf("%s   %s\n", guessedCenter.toString(), answerCandidateCenter.toString());

				if (Math.abs(guessedCenter.x - answerCandidateCenter.x) < 3 && Math.abs(guessedCenter.y - answerCandidateCenter.y) < 3) {
					myGuess.mAnswerChoice = answerIdx;
					numAnswersFound++;
				}

				answerIdx++;
			}
			
			if (numAnswersFound == 1)
			{
				return myGuess;
			}
			else
			{
				myGuess.mAnswerChoice = -1;
				numAnswersFound = 0;
			}
			
			Point bottomLeftPixelCenter = getPixelCenter(figures[0][2]);
			Point bottomMiddlePixelCenter = getPixelCenter(figures[1][2]);
			
			System.out.println(bottomLeftPixelCenter.toString());
			System.out.println(bottomMiddlePixelCenter.toString());

			diffX = bottomMiddlePixelCenter.x - bottomLeftPixelCenter.x;
			diffY = bottomMiddlePixelCenter.y - bottomLeftPixelCenter.y;

			guessedCenter = new Point(bottomLeftPixelCenter.x + 2*diffX,
					bottomLeftPixelCenter.y + 2*diffY);

			answerIdx = 1;

			while (figuresHash.containsKey(Integer.toString(answerIdx))) {

				RavensFigure answerCandidate = figuresHash.get(Integer
						.toString(answerIdx));

				Point answerCandidateCenter = getPixelCenter(answerCandidate);
				System.out.printf("%s   %s\n", guessedCenter.toString(), answerCandidateCenter.toString());

				if (Math.abs(guessedCenter.x - answerCandidateCenter.x) < 3 && Math.abs(guessedCenter.y - answerCandidateCenter.y) < 3) {
					myGuess.mAnswerChoice = answerIdx;
					numAnswersFound++;
				}

				answerIdx++;
			}
			
			if (numAnswersFound == 1)
			{
				return myGuess;
			}
			else
			{
				myGuess.mAnswerChoice = -1;
				numAnswersFound = 0;
			}
		}

		// transforms
		ArrayList<Shape[]> horizontalShapeMappings = MapShapes(figures[0][0],
				figures[figures.length - 1][0]);
		ArrayList<Shape[]> verticalShapeMappings = MapShapes(figures[0][0],
				figures[0][figures.length - 1]);

		// horizontal transform
		for (Shape[] s : horizontalShapeMappings) {

			if (s[0] != null && s[1] != null) {
				ArrayList<String> transformation = s[0]
						.GetDifferenceString(s[1]);

				s[0].ApplyTransform(transformation, s[1]);

			}
		}

		// vertical transform
		for (Shape[] s : verticalShapeMappings) {
			if (s[0] != null && s[1] != null) {
				ArrayList<String> transformation = s[0]
						.GetDifferenceString(s[1]);

				// go find the corresponding shape in the horizontal shape
				// mappings, use first point as unique identifier
				for (Shape[] t : horizontalShapeMappings) {
					if (t[0] != null) {
						if (t[0].mFirstPoint.equals(s[0].mFirstPoint)) {
							t[0].ApplyTransform(transformation, s[1]);
						}
					}
				}

			}

		}

		// construct guess
		ArrayList<Shape> myGuessShapes = new ArrayList<Shape>();
		for (Shape[] s : horizontalShapeMappings) {
			if (s[0] != null && s[1] != null) {
				myGuessShapes.add(s[0]);
			} else if (s[0] == null) { // creation
				myGuessShapes.add(s[1]);
			} else if (s[1] == null) { // deletion
				myGuessShapes.remove(s[0]);
			}
		}

		for (Shape[] s : verticalShapeMappings) {
			if (s[0] == null) { // creation
				myGuessShapes.add(s[1]);
			} else if (s[1] == null) { // deletion
				myGuessShapes.remove(s[0]);
			}
		}

		// now compare to possible answer choices
		answerIdx = 1;

		bestDifferenceScore = Integer.MAX_VALUE;
		bestAnswer = -1;

		while (figuresHash.containsKey(Integer.toString(answerIdx))) {

			// get shapes from array
			RavensFigure answerCandidate = figuresHash.get(Integer
					.toString(answerIdx));

			ArrayList<Shape> shapes = getShapes(answerCandidate);

			ArrayList<Shape[]> mappedShapes = MapShapes(myGuessShapes, shapes);

			int currentDifferenceScore = 0;

			for (Shape[] s : mappedShapes) {
				if (s[0] != null && s[1] != null) {
					currentDifferenceScore += s[0].GetDifferenceScore(s[1]);
					System.out.println();
					System.out.println(s[0].toString());
					System.out.println();
					System.out.println(s[1].toString());
					System.out.println(s[0].GetDifferenceString(s[1]));
					System.out.println();
				} else // unmatched shape
				{
					// TODO: Improve..
					currentDifferenceScore++;
				}

			}

			System.out.printf("%d    %d\n", answerIdx, currentDifferenceScore);

			if (currentDifferenceScore < bestDifferenceScore) {
				bestDifferenceScore = currentDifferenceScore;
				bestAnswer = answerIdx;
			}

			answerIdx++;
		}

		myGuess.mAnswerChoice = bestAnswer;

		return myGuess;
	}

	public ArrayList<Shape[]> MapShapes(ArrayList<Shape> shapes1Original,
			ArrayList<Shape> shapes2Original) {
		// create copies of parameters
		ArrayList<Shape> shapes1 = (ArrayList<Shape>) shapes1Original.clone();
		ArrayList<Shape> shapes2 = (ArrayList<Shape>) shapes2Original.clone();

		// construct array of possible pairings
		ArrayList<Shape[]> possiblePairings = new ArrayList<Shape[]>();
		for (Shape s1 : shapes1) {
			for (Shape s2 : shapes2) {
				Shape[] array = new Shape[2];
				array[0] = s1;
				array[1] = s2;
				possiblePairings.add(array);
			}
		}

		// get best pairings out of the array
		ArrayList<Shape[]> bestPairings = new ArrayList<Shape[]>();
		Shape[] bestPairing = new Shape[2];
		int bestPairingDifferenceScore;
		while (!possiblePairings.isEmpty()) {

			bestPairingDifferenceScore = Integer.MAX_VALUE;
			bestPairing = null;

			for (Shape[] pairing : possiblePairings) {
				int currentDifferenceScore = pairing[0]
						.GetDifferenceScore(pairing[1]);

				if (currentDifferenceScore < bestPairingDifferenceScore) {
					bestPairingDifferenceScore = currentDifferenceScore;
					bestPairing = pairing;
					// TODO:RemoveSystem.out.printf("Pairing found: %s %s\n",
					// pairing[0].toString(), pairing[1].toString());
				}
			}

			// add to best pairings
			bestPairings.add(bestPairing);

			for (Iterator<Shape[]> it = possiblePairings.iterator(); it
					.hasNext();) {
				Shape[] removalCandidate = it.next();

				if (removalCandidate[0].attributesEquals(bestPairing[0])
						|| removalCandidate[1].attributesEquals(bestPairing[1])) {
					it.remove();
				}
			}

			// mark those shapes as counted
			shapes1.remove(bestPairing[0]);
			shapes2.remove(bestPairing[1]);

			// System.out.println(possiblePairings.size());
			// System.out.printf("1: %s\n",possiblePairings.get(0)[0].toString());
			// System.out.printf("2: %s\n",possiblePairings.get(0)[1].toString());

		}

		// deletions
		while (!shapes1.isEmpty()) {
			Shape[] deletionPairing = new Shape[2];
			deletionPairing[0] = shapes1.get(0);
			bestPairings.add(deletionPairing);

			shapes1.remove(0);
		}

		// creations
		while (!shapes2.isEmpty()) {
			Shape[] creationPairing = new Shape[2];
			creationPairing[1] = shapes2.get(0);
			bestPairings.add(creationPairing);

			shapes2.remove(0);
		}

		return bestPairings;
	}

	public ArrayList<Shape[]> MapShapes(RavensFigure figure1,
			RavensFigure figure2) {
		// get shapes out of figures
		ArrayList<Shape> figure1Shapes = getShapes(figure1);
		ArrayList<Shape> figure2Shapes = getShapes(figure2);

		return MapShapes(figure1Shapes, figure2Shapes);

	}

	class Shape {
		ArrayList<Point> mPoints;

		// metadata
		public Point mCenter;
		public boolean mCenterCalculated;

		public int mNumPoints;
		public boolean mNumPointsCalculated;

		public int mHeight;
		public boolean mHeightCalculated;

		public int mWidth;
		public boolean mWidthCalculated;

		public int mArea;
		public boolean mAreaCalculated;

		public double mDensity;
		public boolean mDensityCalculated;

		public Point mFirstPoint;

		public Shape() {
			mPoints = new ArrayList<Point>();

			mCenterCalculated = false;
			mNumPointsCalculated = false;
			mHeightCalculated = false;
			mWidthCalculated = false;
			mAreaCalculated = false;
			mDensityCalculated = false;

		}

		public void AddPoint(Point p) {
			if (mPoints.isEmpty()) {
				mFirstPoint = p; // store as unique identifier for later
			}
			mPoints.add(p);
		}

		public void SetCenter(Point p) {
			mCenterCalculated = true;
			mCenter = p;
		}

		public Point GetCenter() {
			if (!mCenterCalculated) {

				int sumX = 0;
				int sumY = 0;
				for (Point p : mPoints) {
					sumX += p.x;
					sumY += p.y;
				}

				int avgX = sumX / mPoints.size();
				int avgY = sumY / mPoints.size();

				mCenter = new Point(avgX, avgY);
				mCenterCalculated = true;
			}

			return mCenter;
		}

		public void SetNumPoints(int n) {
			mNumPointsCalculated = true;
			mNumPoints = n;
		}

		public int GetNumPoints() {
			if (!mNumPointsCalculated) {
				mNumPoints = mPoints.size();
				mNumPointsCalculated = true;
			}
			return mNumPoints;
		}

		public void SetHeight(int n) {
			mHeightCalculated = true;
			mHeight = n;
		}

		public int GetHeight() {
			if (!mHeightCalculated) {

				int maxY = 0;
				int minY = Integer.MAX_VALUE;

				for (Point p : mPoints) {

					if (p.y > maxY) {
						maxY = p.y;
					}

					if (p.y < minY) {
						minY = p.y;
					}
				}

				mHeight = (maxY - minY + 1);
				mHeightCalculated = true;
			}

			return mHeight;
		}

		public void SetWidth(int n) {
			mWidthCalculated = true;
			mWidth = n;
		}

		public int GetWidth() {
			if (!mWidthCalculated) {
				int maxX = 0;
				int minX = Integer.MAX_VALUE;

				for (Point p : mPoints) {
					if (p.x > maxX) {
						maxX = p.x;
					}

					if (p.x < minX) {
						minX = p.x;
					}
				}

				mWidth = (maxX - minX + 1);
				mWidthCalculated = true;
			}

			return mWidth;
		}

		public void SetBoundingBoxArea(int n) {
			mAreaCalculated = true;
			mArea = n;
		}

		public int GetBoundingBoxArea() {
			if (!mAreaCalculated) {
				mAreaCalculated = true;
				mArea = GetHeight() * GetWidth();
			}
			return mArea;
		}

		public void SetDensity(double n) {
			mDensityCalculated = true;
			mDensity = n;
		}

		public double GetDensity() {
			if (!mDensityCalculated) {
				mDensity = 1.0 * GetNumPoints() / GetBoundingBoxArea();
				mDensityCalculated = true;
			}
			return mDensity;
		}

		public int GetDifferenceScore(Shape otherShape) {

			// TODO: Change to using isSimilar

			int result = 0;

			if (!isSimilar(GetCenter().x, otherShape.GetCenter().x)) {
				result++;
			}

			if (!isSimilar(GetCenter().y, otherShape.GetCenter().y)) {
				result++;
			}

			if (!isSimilar(GetNumPoints(), otherShape.GetNumPoints())) {
				result++;
			}

			if (!isSimilar(GetHeight(), otherShape.GetHeight())) {
				result++;
			}

			if (!isSimilar(GetWidth(), otherShape.GetWidth())) {
				result++;
			}

			if (!isSimilar(GetBoundingBoxArea(),
					otherShape.GetBoundingBoxArea())) {
				result++;
			}

			if (!isSimilar(GetDensity(), otherShape.GetDensity())) {
				result++;
			}

			return result;
		}

		public String toString() {
			String result = "";

			result += "Center of Shape: " + GetCenter().toString() + "\n";
			result += "Number of Points: " + GetNumPoints() + "\n";
			result += "Height: " + GetHeight() + "\n";
			result += "Width: " + GetWidth() + "\n";
			result += "Bounding Box Area: " + GetBoundingBoxArea() + "\n";
			result += "Pixel Density: " + GetDensity();

			return result;
		}

		public ArrayList<String> GetDifferenceString(Shape otherShape) {
			ArrayList<String> result = new ArrayList<String>();

			// x-coordinate of center
			if (!isSimilar(GetCenter().x, otherShape.GetCenter().x)) {
				result.add(DIFFSTRING_CENTERX);
			}

			// y-coordinate of center
			if (!isSimilar(GetCenter().y, otherShape.GetCenter().y)) {
				result.add(DIFFSTRING_CENTERY);
			}

			// number of points
			if (!isSimilar(GetNumPoints(), otherShape.GetNumPoints())) {
				result.add(DIFFSTRING_NUMPOINTS);
			}

			// height
			if (!isSimilar(GetHeight(), otherShape.GetHeight())) {
				result.add(DIFFSTRING_HEIGHT);
			}

			// width
			if (!isSimilar(GetWidth(), otherShape.GetWidth())) {
				result.add(DIFFSTRING_WIDTH);
			}

			// area
			if (!isSimilar(GetBoundingBoxArea(),
					otherShape.GetBoundingBoxArea())) {
				result.add(DIFFSTRING_AREA);
			}

			// density
			if (!isSimilar(GetDensity(), otherShape.GetDensity())) {
				result.add(DIFFSTRING_DENSITY);
			}

			return result;
		}

		public void ApplyTransform(ArrayList<String> transformation,
				Shape otherShape) {
			for (String s : transformation) {
				int diff;
				switch (s) {
				case DIFFSTRING_CENTERX:
				case DIFFSTRING_CENTERY:
					int diffX = otherShape.GetCenter().x - GetCenter().x;
					int diffY = otherShape.GetCenter().y - GetCenter().y;
					SetCenter(new Point(GetCenter().x + diffX, GetCenter().y + diffY));
					break;
				case DIFFSTRING_NUMPOINTS:
					diff = otherShape.GetNumPoints() - GetNumPoints();
					SetNumPoints(GetNumPoints() + diff);
					break;
				case DIFFSTRING_HEIGHT:
					diff = otherShape.GetHeight() - GetHeight();
					SetHeight(GetHeight() + diff);
					break;
				case DIFFSTRING_WIDTH:
					diff = otherShape.GetWidth() - GetWidth();
					SetWidth(GetWidth() + diff);
					break;
				case DIFFSTRING_AREA:
					diff = otherShape.GetBoundingBoxArea() - GetBoundingBoxArea();
					SetBoundingBoxArea(GetBoundingBoxArea() + diff);
					break;
				case DIFFSTRING_DENSITY:
					double diffdbl = otherShape.GetDensity() - GetDensity();
					SetDensity(GetDensity() + diffdbl);
					break;
				}
			}
		}

		public boolean isReflectionRelationWith(Shape s) {
			return true;
		}

		// quicker way to compare shapes
		public boolean attributesEquals(Shape otherShape) {
			return GetCenter().equals(otherShape.GetCenter())
					&& GetNumPoints() == otherShape.GetNumPoints()
					&& GetHeight() == otherShape.GetHeight()
					&& GetWidth() == otherShape.GetWidth()
					&& GetBoundingBoxArea() == otherShape.GetBoundingBoxArea()
					&& GetDensity() == otherShape.GetDensity();
		}

		// less efficient but exhaustive comparison of points between shapes
		public boolean pointsEquals(Shape otherShape) {
			for (Point p : mPoints) {
				boolean foundInList = false;
				for (Point q : otherShape.mPoints) {
					if (p.equals(q)) {
						foundInList = true;
						break;
					}
				}

				if (!foundInList) {
					return false;
				}
			}

			// if we get here, all points matched up
			return true;
		}
	}

	class Point {
		public int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String toString() {
			String s = "X: " + x + " Y: " + y;
			return s;
		}

		public boolean equals(Point otherPoint) {
			return (x == otherPoint.x && y == otherPoint.y);
		}
	}

	public ArrayList<Shape> getShapes(RavensFigure figure) {
		BufferedImage bi = null;

		boolean[][] uncompressedPixels = null;

		try {
			String filename = figure.getVisual();
			File file = new File(filename);
			bi = ImageIO.read(file);

			int w = bi.getWidth();
			int h = bi.getHeight();

			uncompressedPixels = new boolean[w][h];

			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					uncompressedPixels[i][j] = TranslateRGBtoBinary(bi.getRGB(
							i, j));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean[][] pixels = compressPixels(uncompressedPixels, COMPRESS_SIZE);

		// printPixels(pixels);

		ArrayList<Shape> shapeArray = new ArrayList<Shape>();
		boolean[][] visited = new boolean[pixels.length][pixels[0].length];

		// iterate through each pixel
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				// if we have not visited this pixel before, visit it (makes use
				// of recursive function to hit all contiguous pixels)
				if (!visited[i][j]) {
					Shape s = new Shape();
					VisitPixel(pixels, visited, i, j, s);

					if (!s.mPoints.isEmpty()) {
						shapeArray.add(s);
					}
				}
			}
		}

		return shapeArray;
	}

	public boolean[][] compressPixels(boolean[][] uncompressedPixels,
			int compressSize) {
		boolean[][] result = new boolean[compressSize + 1][compressSize + 1];

		int h = uncompressedPixels.length;
		int w = uncompressedPixels[0].length;

		double scalingFactor = 1.0 * h / compressSize;

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (uncompressedPixels[i][j]) {
					int newX = (int) (i / scalingFactor);
					int newY = (int) (j / scalingFactor);

					result[newX][newY] = true;
				}
			}
		}

		return result;

	}

	public void printPixels(boolean[][] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				if (pixels[i][j]) {
					System.out.printf("%d ", 1);
				} else {
					System.out.printf("%d ", 0);
				}
			}
			System.out.println();
		}
	}

	public boolean TranslateRGBtoBinary(int val) {
		if (val == -16777216) {
			return true;
		} else {
			return false;
		}
	}

	public void VisitPixel(boolean[][] pixels, boolean[][] visited, int x,
			int y, Shape shapeInProgress) {
		// check that dimensions are valid and that we have not visited this
		// space yet, if not, return
		int h = pixels.length;
		int w = pixels[0].length;

		if (x < 0 || y < 0 || x >= h || y >= w || visited[x][y]) {
			return;
		}

		// mark as visited
		visited[x][y] = true;

		// if true (black), check for contiguous pixels
		if (pixels[x][y]) {
			// check whether we have a shape in progress
			if (shapeInProgress == null) {
				// if not, create one
				shapeInProgress = new Shape();
			}

			// add this pixel to it
			// System.out.printf("(%d, %d)\n", x, y);
			Point p = new Point(x, y);
			if (!shapeInProgress.mPoints.contains(p)) {
				shapeInProgress.AddPoint(p);
				// shapeInProgress.mPoints.add(p);
			}

			// check all adjacent pixels
			VisitPixel(pixels, visited, x + 1, y, shapeInProgress); // right
			VisitPixel(pixels, visited, x - 1, y, shapeInProgress); // left
			VisitPixel(pixels, visited, x, y + 1, shapeInProgress); // down
			VisitPixel(pixels, visited, x, y - 1, shapeInProgress); // up
		}

	}

	public boolean isSimilar(double val1, double val2) {
		return (Math.abs(val1 - val2) < val1 * SIMILARITY_PERCENT);
	}

	public int GetPixelCount(RavensFigure figure) {
		BufferedImage bi = null;
		int result = 0;

		try {
			String filename = figure.getVisual();
			File file = new File(filename);
			bi = ImageIO.read(file);

			int w = bi.getWidth();
			int h = bi.getHeight();

			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					if (TranslateRGBtoBinary(bi.getRGB(i, j))) {
						result++;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public Point getPixelCenter(RavensFigure figure) {
		BufferedImage bi = null;
		int result = 0;

		int sumX = 0;
		int sumY = 0;
		int numPoints = 0;

		try {
			String filename = figure.getVisual();
			File file = new File(filename);
			bi = ImageIO.read(file);

			int w = bi.getWidth();
			int h = bi.getHeight();

			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					if (TranslateRGBtoBinary(bi.getRGB(i, j))) {
						sumX += i;
						sumY += j;
						numPoints++;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (numPoints != 0) {
			return new Point(sumX / numPoints, sumY / numPoints);
		} else {
			return null;
		}

	}

	public boolean isPixelPattern(int[][] pixelCounts, IntHolder guessedPixels) {
		return isAdditivePattern(pixelCounts, guessedPixels)
				|| isMultiplicativePattern(pixelCounts, guessedPixels);
		// TODO: Implement multiplicative pattern
	}

	public boolean isAdditivePattern(int[][] pixelCounts,
			IntHolder guessedPixels) {
		int N = pixelCounts.length;
		guessedPixels.x = pixelCounts[0][0]; // restore original value

		int[][] horizontalPixelDiffs = new int[N][N - 1];
		int[][] verticalPixelDiffs = new int[N - 1][N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (isHorizontalTransformFromPosition(i, j, N)) {
					verticalPixelDiffs[i][j] = pixelCounts[i + 1][j]
							- pixelCounts[i][j];
				}

				if (isVerticalTransformFromPosition(i, j, N)) {
					horizontalPixelDiffs[i][j] = pixelCounts[i][j + 1]
							- pixelCounts[i][j];
				}

				// if (!(i == pixelCounts.length - 1 && j == pixelCounts.length
				// - 1)) {
				//
				// }
			}
		}

		System.out.println("Horizontal Pixel Differentials:");
		for (int[] i : horizontalPixelDiffs) {
			for (int j : i) {
				System.out.printf("%d ", j);
			}
			System.out.println();
		}

		System.out.println();

		System.out.println("Vertical Pixel Differentials:");
		for (int[] i : verticalPixelDiffs) {
			for (int j : i) {
				System.out.printf("%d ", j);
			}
			System.out.println();
		}

		// first consider horizontal and vertical diff arrays as a whole
		ArrayList<Double> horizDiffs = new ArrayList<Double>();
		for (int i = 0; i < N * (N - 1) - 1; i++) {
			horizDiffs.add((double) horizontalPixelDiffs[i % N][i / N]);
		}

		ArrayList<Double> vertDiffs = new ArrayList<Double>();
		for (int i = 0; i < N * (N - 1) - 1; i++) {
			vertDiffs.add((double) verticalPixelDiffs[i / N][i % N]);
		}

		if (Math.abs(calculateStandardDeviation(horizDiffs)
				/ calculateMean(horizDiffs)) < STDEV_PERCENT
				&& Math.abs(calculateStandardDeviation(vertDiffs)
						/ calculateMean(vertDiffs)) < STDEV_PERCENT) {
			guessedPixels.x += (int) calculateMean(horizDiffs);
			guessedPixels.x += (int) calculateMean(horizDiffs);
			guessedPixels.x += (int) calculateMean(vertDiffs);
			guessedPixels.x += (int) calculateMean(vertDiffs);
			System.out.println(guessedPixels.x);
			System.out.println("I returned true");
			return true;
		}

		// then consider rows and columns
		if (N == 2) {
			return false; // TODO: Implement
		} else if (N == 3) {
			ArrayList<Double> col1HorzDiffs = new ArrayList<Double>();
			col1HorzDiffs.add(horizDiffs.get(0));
			col1HorzDiffs.add(horizDiffs.get(1));
			col1HorzDiffs.add(horizDiffs.get(2));

			ArrayList<Double> col2HorzDiffs = new ArrayList<Double>();
			col2HorzDiffs.add(horizDiffs.get(3));
			col2HorzDiffs.add(horizDiffs.get(4));

			ArrayList<Double> row1VertDiffs = new ArrayList<Double>();
			row1VertDiffs.add(vertDiffs.get(0));
			row1VertDiffs.add(vertDiffs.get(1));
			row1VertDiffs.add(vertDiffs.get(2));

			ArrayList<Double> row2VertDiffs = new ArrayList<Double>();
			row2VertDiffs.add(vertDiffs.get(3));
			row2VertDiffs.add(vertDiffs.get(4));

			if (Math.abs(calculateStandardDeviation(col1HorzDiffs)
					/ calculateMean(col1HorzDiffs)) < STDEV_PERCENT
					&& Math.abs(calculateStandardDeviation(col2HorzDiffs)
							/ calculateMean(col2HorzDiffs)) < STDEV_PERCENT
					&& Math.abs(calculateStandardDeviation(row1VertDiffs)
							/ calculateMean(row1VertDiffs)) < STDEV_PERCENT
					&& Math.abs(calculateStandardDeviation(row2VertDiffs)
							/ calculateMean(row2VertDiffs)) < STDEV_PERCENT) {
				guessedPixels.x += (int) calculateMean(col1HorzDiffs);
				guessedPixels.x += (int) calculateMean(col2HorzDiffs);
				guessedPixels.x += (int) calculateMean(row1VertDiffs);
				guessedPixels.x += (int) calculateMean(row2VertDiffs);
				System.out.println(guessedPixels.x);
				System.out.println("I returned true");
				return true;
			}

		}

		return false;
	}

	public boolean isMultiplicativePattern(int[][] pixelCounts,
			IntHolder guessedPixels) {
		int N = pixelCounts.length;
		if (N != 3) {
			return false;
		}
		guessedPixels.x = pixelCounts[0][0]; // restore original value

		double horizontalFactor = 1.0 * pixelCounts[0][2] / pixelCounts[0][0];
		double verticalFactor = 1.0 * pixelCounts[2][0] / pixelCounts[0][0];

		guessedPixels.x *= horizontalFactor;
		guessedPixels.x *= verticalFactor;

		return true;
	}

	public double calculateMean(ArrayList<Double> list) {
		double sum = 0;

		for (double i : list) {
			sum += i;
		}

		return sum / list.size();
	}

	public double calculateStandardDeviation(ArrayList<Double> list) {
		ArrayList<Double> squaredDifferences = new ArrayList<Double>();
		double mean = calculateMean(list);

		for (double i : list) {
			double diff = i - mean;
			double squaredDiff = diff * diff;
			squaredDifferences.add(squaredDiff);
		}

		double variance = calculateMean(squaredDifferences);
		return Math.sqrt(variance);
	}

	public class IntHolder {
		int x;

		public IntHolder(int x) {
			this.x = x;
		}
	}

}

# Your Agent for solving Raven's Progressive Matrices. You MUST modify this file.
#
# You may also create and submit new files in addition to modifying this file.
#
# Make sure your file retains methods with the signatures:
# def __init__(self)
# def Solve(self,problem)
#
# These methods will be necessary for the project's main method
# to run.

# Install Pillow and uncomment this line to access image
# processing.
from PIL import Image

# Check object attributes
# If the key does not exist, return None
def getAttr(Object, key):
    try:
        value = Object.attributes[key]
    except:
        value = None
    return value

# Check scores
def upside(matches, before, after, score):
    found = False
    if len(matches) == 0:
        match = Match(before, after, score)
        matches.append(match)
    else:
        for match in matches:
            if match.before == before:
                found = True
                if score > match.score:
                    match.before = before
                    match.after = after
                    match.score = score
            if match.after == after:
                found = True
                if score > match.score:
                    match.before = before
                    match.after = after
                    match.score = score
        if found == False:
            match = Match(before, after, score)
            matches.append(match)

# Check matches
def findInMatches(matches, value):
    for match in matches:
        if match.before == value:
            return True
        if match.after == value:
            return True
    return False


# Compare transitions between objects
# Increase score if transition matches
def compareTransitions(trans1, trans2):

    score = 0
    for i in range(0, min(len(trans1), len(trans2))):
        obj_a = trans1[i]
        obj_b = trans2[i]
	
	# Primary object
	# Check if shapes match
        if obj_a.shape == obj_b.shape:
            score += 1
        
	# Check if sizes match
	if obj_a.size == obj_b.size:
            score += 1
        
	# Check if shading matches
	if obj_a.fill == obj_b.fill:
            score += 1
        
	# Check if alignment matches
	if obj_a.alignment['up'] == obj_b.alignment['up']:
            score += 1
        if obj_a.alignment['right'] == obj_b.alignment['right']:
            score += 1
        
	# Check if angle matches
	if obj_a.angle == obj_b.angle:
            score += 1
        
	# Inner object, assuming one exists
	# Check for fill matching
	if obj_a.inside['fill'] == obj_b.inside['fill']:
            score += 1
	
	# Check for Shape matching        
	if obj_a.inside['shape'] == obj_b.inside['shape']:
            score += 1
	
	# Check for Size matching
        if obj_a.inside['size'] == obj_b.inside['size']:
            score += 1

	# Above object, assuming one exists
	# Check for fill matching
        if obj_a.above['fill'] == obj_b.above['fill']:
            score += 1

	# Check for shape matching
        if obj_a.above['shape'] == obj_b.above['shape']:
            score += 1

	# Check for size matching
        if obj_a.above['size'] == obj_b.above['size']:
            score += 1

    # Return total score
    return score


class Agent:
    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().

    def __init__(self):
        pass

    # The primary method for solving incoming Raven's Progressive Matrices.
    # For each problem, your Agent's Solve() method will be called. At the
    # conclusion of Solve(), your Agent should return an integer representing its
    # answer to the question: "1", "2", "3", "4", "5", or "6". These integers
    # are also the Names of the individual RavensFigures, obtained through
    # RavensFigure.getName() (as Strings).
    #
    # In addition to returning your answer at the end of the method, your Agent
    # may also call problem.checkAnswer(int givenAnswer). The parameter
    # passed to checkAnswer should be your Agent's current guess for the
    # problem; checkAnswer will return the correct answer to the problem. This
    # allows your Agent to check its answer. Note, however, that after your
    # agent has called checkAnswer, it will *not* be able to change its answer.
    # checkAnswer is used to allow your Agent to learn from its incorrect
    # answers; however, your Agent cannot change the answer to a question it
    # has already answered.
    #
    # If your Agent calls checkAnswer during execution of Solve, the answer it
    # returns will be ignored; otherwise, the answer returned at the end of
    # Solve will be taken as your Agent's answer to this problem.
    #
    # Make sure to return your answer *as an integer* at the end of Solve().
    # Returning your answer as a string may cause your program to
    # crash.
    def Solve(self,problem):

        sim = Similarities(problem)

        maxScore = 0
        bestAnswer = 0
        answers = []

        for answer in sim.fits:

	    # Check transitions
            score = compareTransitions(
                sim.fits[answer][0], sim.matches)
	
	    # Check removed objects
            if sim.fits[answer][1] == sim.deleted:
                score += (12 * sim.deleted)
	
	    # Check added objects
            if sim.fits[answer][2] == sim.added:
                score += (12 * sim.added)
	    
	    # Update answers
            answers.append({"answer": answer, "score": score})
            if score > maxScore:
                maxScore = score
                bestAnswer = int(answer)

        return -1

# Define transition matches for Frames
class Match:

    def setTransitions(self, A, B):
        if self.before.shape == self.after.shape:
            self.shape = "unchanged"
        else:
            self.shape = self.before.shape + \
                "->" + self.after.shape

        if self.before.size == self.after.size:
            self.size = "unchanged"
        else:
            self.size = self.before.size + "->" + self.after.size

        if self.before.fill == self.after.fill:
            self.fill = "unchanged"
        else:
            self.fill = str("" if self.before.fill is None else self.before.fill) + \
                "->" + \
                str("" if self.after.fill is None else self.after.fill)
	
	# Check Alignment
        self.alignment = {}
        if self.before.alignment == self.after.alignment:
            self.alignment['up'] = "nochange"
            self.alignment['right'] = "nochange"
        elif self.before.alignment is not None and self.after.alignment is not None:
            if "top" in self.before.alignment and "top" in self.after.alignment:
                self.alignment['up'] = "nochange"
            elif "top" in self.before.alignment and "bottom" in self.after.alignment:
                self.alignment['up'] = "down"
            elif "bottom" in self.before.alignment and "top" in self.after.alignment:
                self.alignment['up'] = "up"
            elif "bottom" in self.before.alignment and "bottom" in self.after.alignment:
                self.alignment['up'] = "nochange"
            else:
                self.alignment['up'] = "changed"

            if "left" in self.before.alignment and "left" in self.after.alignment:
                self.alignment['right'] = "nochange"
            elif "left" in self.before.alignment and "right" in self.after.alignment:
                self.alignment['right'] = "right"
            elif "center" in self.before.alignment and "left" in self.after.alignment:
                self.alignment['right'] = "left"
            elif "right" in self.before.alignment and "right" in self.after.alignment:
                self.alignment['right'] = "nochange"
            elif "right" in self.before.alignment and "left" in self.after.alignment:
                self.alignment['right'] = "left"
            else:
                self.alignment['right'] = "changed"
        elif self.before.alignment is not None and self.after.alignment is None:
            self.alignment['up'] = "deleted"
            self.alignment['right'] = "deleted"
        elif self.before.alignment is None and self.after.alignment is not None:
            self.alignment['up'] = "added"
            self.alignment['right'] = "added"
        else:
            self.alignment['up'] = "abnormal"
            self.alignment['right'] = "abnormal"

	# Check angles
        if self.before.angle is not None and self.after.angle is not None:
            if abs(int(self.before.angle) - int(self.after.angle)) == 0:
                self.angle = "nochange"
            elif abs(int(self.before.angle) - int(self.after.angle)) == 90:
                self.angle = "rotate"
            elif abs(int(self.before.angle) - int(self.after.angle)) == 270:
                self.angle = "rotate-reverse"
            elif abs(int(self.before.angle) - int(self.after.angle)) == 180:
                self.angle = "flip"
            else:
                self.angle = "changed"
        elif self.before.angle is not None and self.after.angle is None:
            self.angle = "deleted"
        elif self.before.angle is None and self.after.angle is not None:
            self.angle = "added"
        else:
            self.angle = "abnormal"
	
	# Check Inside
        self.inside = {}
        if self.before.inside is not None and self.after.inside is not None:
            if len(self.before.inside) == len(self.after.inside):
                arr1 = self.before.inside.split(",")
                arr2 = self.after.inside.split(",")
                for i, val in enumerate(arr1):

                    if A[arr1[i]].fill == B[arr2[i]].fill:
                        self.inside['fill'] = "unchanged"
                    else:
                        self.inside['fill'] = "changed"
                    if A[arr1[i]].size == B[arr2[i]].size:
                        self.inside['size'] = "unchanged"
                    else:
                        self.inside['size'] = "changed"
                    if A[arr1[i]].shape == B[arr2[i]].shape:
                        self.inside['shape'] = "unchanged"
                    else:
                        self.inside['shape'] = "changed"
            else:
                self.inside['fill'] = "changed"
                self.inside['shape'] = "changed"
                self.inside['size'] = "changed"
        elif self.before.inside == self.after.inside:
            self.inside['fill'] = "unchanged"
            self.inside['shape'] = "unchanged"
            self.inside['size'] = "unchanged"
        else:
            self.inside['fill'] = "changed"
            self.inside['shape'] = "changed"
            self.inside['size'] = "changed"

       	# Check above
        self.above = {}
        if self.before.above is not None and self.after.above is not None:
            if len(self.before.above) == len(self.after.above):
                arr1 = self.before.above.split(",")
                arr2 = self.after.above.split(",")
                for i, val in enumerate(arr1):

                    if A[arr1[i]].fill == B[arr2[i]].fill:
                        self.above['fill'] = "unchanged"
                    else:
                        self.above['fill'] = "changed"
                    if A[arr1[i]].size == B[arr2[i]].size:
                        self.above['size'] = "unchanged"
                    else:
                        self.above['size'] = "changed"
                    if A[arr1[i]].shape == B[arr2[i]].shape:
                        self.above['shape'] = "unchanged"
                    else:
                        self.above['shape'] = "changed"
            else:
                self.above['fill'] = "changed"
                self.above['shape'] = "changed"
                self.above['size'] = "changed"
        elif self.before.above == self.after.above:
            self.above['fill'] = "unchanged"
            self.above['shape'] = "unchanged"
            self.above['size'] = "unchanged"
        else:
            self.above['fill'] = "changed"
            self.above['shape'] = "changed"
            self.above['size'] = "changed"

    def __init__(self, before, after, score):
        self.before = before
        self.after = after
        self.score = score

# Check similarities
class Similarities:

    # Compare the objects as defined in the frames
    def compare_frame(self, obj_a, obj_b, A, B):
        score = 0
        if obj_a.shape == obj_b.shape:
            score += 1
        if obj_a.size == obj_b.size:
            score += 1
        if obj_a.fill == obj_b.fill:
            score += 1
        if obj_a.alignment == obj_b.alignment:
            score += 1
        if obj_a.inside is not None and obj_b.inside is not None:
            if len(obj_a.inside) == len(obj_b.inside):
                arr1 = obj_a.inside.split(",")
                arr2 = obj_b.inside.split(",")
                for i, val in enumerate(arr1):
                    if A[arr1[i]].fill == B[arr2[i]].fill:
                        score += 1
                    if A[arr1[i]].size == B[arr2[i]].size:
                        score += 1
                    if A[arr1[i]].shape == B[arr2[i]].shape:
                        score += 1
        elif obj_a.inside == obj_b.inside:
            score += 1
        if obj_a.above is not None and obj_b.above is not None:
            if len(obj_a.above) == len(obj_b.above):
                arr1 = obj_a.above.split(",")
                arr2 = obj_b.above.split(",")
                for i, val in enumerate(arr1):
                    if A[arr1[i]].fill == B[arr2[i]].fill:
                        score += 1
                    if A[arr1[i]].size == B[arr2[i]].size:
                        score += 1
                    if A[arr1[i]].shape == B[arr2[i]].shape:
                        score += 1
        elif obj_a.above == obj_b.above:
            score += 1
        if obj_a.angle == obj_b.angle:
            score += 1
        return score

    def find_transitions(self, A, B):
        matches = []
        deleted = 0
        added = 0
        for frame_1 in A.frames:
            for frame_2 in B.frames:
                score = self.compare_frame(
                    A.frames[frame_1], B.frames[frame_2], A.frames, B.frames)
                upside(
                    matches, A.frames[frame_1], B.frames[frame_2], score)

        for match in matches:
            match.setTransitions(A.frames, B.frames)

        for f in A.frames:
            if findInMatches(matches, A.frames[f].name) == False:
                deleted += 1

        for f in B.frames:
            if findInMatches(matches, B.frames[f].name) == False:
                added += 1

        return matches, deleted, added

    def __init__(self, problem):
        self.name = problem.name
        self.A = Figure('A', problem.figures['A'])
        self.B = Figure('B', problem.figures['B'])
        self.C = Figure('C', problem.figures['C'])
        self.answers = {}
        self.answers['1'] = Figure('1', problem.figures['1'])
        self.answers['2'] = Figure('2', problem.figures['2'])
        self.answers['3'] = Figure('3', problem.figures['3'])
        self.answers['4'] = Figure('4', problem.figures['4'])
        self.answers['5'] = Figure('5', problem.figures['5'])
        self.answers['6'] = Figure('6', problem.figures['6'])

        self.matches, self.deleted, self.added = self.find_transitions(
            self.A, self.B)

        self.fits = {}

        for answer in self.answers:
            self.fits[answer] = self.find_transitions(
                self.C, self.answers[answer])

# Define figure, in frames
class Figure:

    def __init__(self, figureName, thisFigure):
        self.name = figureName
        self.frames = {}
        for objectName in thisFigure.objects:
            thisObject = thisFigure.objects[objectName]
            frame = Frame(objectName, thisObject)
            self.frames[frame.name] = frame

# Define frame, properties
class Frame:

    def __init__(self, objectName, Object):

        self.name = objectName
        self.shape = getAttr(Object, 'shape')
        self.size = getAttr(Object, 'size')
        self.fill = getAttr(Object, 'fill')
        self.angle = getAttr(Object, 'angle')
        self.alignment = getAttr(Object, 'alignment')
        self.inside = getAttr(Object, 'inside')
        self.above = getAttr(Object, 'above')


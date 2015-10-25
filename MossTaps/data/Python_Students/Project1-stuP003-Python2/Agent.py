# Your Agent for solving Raven's Progressive Matrices. You MUST modify this file.
#
# You may also create and submit new files in addition to modifying this file.
#
# Make sure your file retains methods with the signatures:
# def __init__(self)
# def Solve(self,problem)
#
# These methods will be necessary for the project's main method to run.

# Install Pillow and uncomment this line to access image processing.
#from PIL import Image

import copy
import itertools

class Agent:

    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().
    def __init__(self):
        pass


    def mapping(self, Aobjects, Bobjects):

        CanadateMapping = {}
        mapping = {}

        # create an initial mapping
        for Akey, Bkey in zip(Aobjects, Bobjects):
            CanadateMapping[Akey] = Bkey

        AobjectsString = ''
        for s in Aobjects:
            AobjectsString += s

        BobjectsString = ''
        for s in Bobjects:
            BobjectsString += s

        best = 9999

        for x in itertools.permutations(BobjectsString, len(BobjectsString)):

            for i in range(min(len(AobjectsString), len(x))):
                CanadateMapping[AobjectsString[i]] = x[i]

            score = 0

            for obj in CanadateMapping:
                score += self.comapareObjects(CanadateMapping, Aobjects[obj], Bobjects[CanadateMapping[obj]])

            if score < best:
                mapping = copy.deepcopy(CanadateMapping)
                best = score

        return mapping

    def findTransform(self, mapping, Aobjects, Bobjects):

        transform = []

        # add objects
        if len(Aobjects) < len(Bobjects):
            for key in Bobjects:
                if key not in mapping.values():
                    transform.append([key, 'add', Bobjects[key]])

        # remove objects
        if len(Aobjects) > len(Bobjects):
            for key in Aobjects:
                if key not in mapping:
                    transform.append([key, 'remove'])

        for key in mapping:
            for AAttr in Aobjects[key].attributes:
                for BAttr in Bobjects[mapping[key]].attributes:
                    if AAttr == BAttr:
                        if Aobjects[key].attributes[AAttr] != Bobjects[mapping[key]].attributes[BAttr]:

                            if AAttr == 'angle':
                                # mirror
                                mirrorAngle = abs(90 - (int(Aobjects[key].attributes[AAttr]) % 180))
                                mirrorAngle2 = 90 - mirrorAngle

                                Aangle = int(Bobjects[mapping[key]].attributes[BAttr])
                                Bangle = int(Aobjects[key].attributes[AAttr])

                                if Bangle == (Aangle + 2*mirrorAngle) % 360:
                                    if Bangle - mirrorAngle == 0 or Bangle - mirrorAngle == 180:
                                        transform.append([key, 'mirror', 'H', 2*mirrorAngle])
                                    else:
                                        transform.append([key, 'mirror', 'V', 2*mirrorAngle])

                                elif Bangle == (Aangle - 2*mirrorAngle) % 360:
                                    if Bangle + mirrorAngle == 0 or Bangle + mirrorAngle == 180:
                                        transform.append([key, 'mirror', 'H', 2*mirrorAngle])
                                    else:
                                        transform.append([key, 'mirror', 'V', 2*mirrorAngle])

                                elif Bangle == (Aangle + 2*mirrorAngle2) % 360:
                                    if Bangle - mirrorAngle2 == 0 or Bangle - mirrorAngle2 == 180:
                                        transform.append([key, 'mirror', 'H', 2*mirrorAngle2])
                                    else:
                                        transform.append([key, 'mirror', 'V', 2*mirrorAngle2])

                                elif Bangle == (Aangle + 2*mirrorAngle2) % 360:
                                    if Bangle + mirrorAngle2 == 0 or Bangle + mirrorAngle2 == 180:
                                        transform.append([key, 'mirror', 'H', 2*mirrorAngle2])
                                    else:
                                        transform.append([key, 'mirror', 'V', 2*mirrorAngle2])

                                else:
                                    transform.append([key, 'rotation',  int(Bobjects[mapping[key]].attributes[BAttr]) - int(Aobjects[key].attributes[AAttr])])

                            elif AAttr in ['above', 'inside']:
                                Aelements = Aobjects[key].attributes[AAttr].split(',')
                                Belements = Bobjects[mapping[key]].attributes[AAttr].split(',')
                                for a,b in zip(Aelements, Belements):
                                    if mapping[a] != b:
                                        transform.append([key, 'move', Bobjects[mapping[key]].attributes[BAttr]])

                            elif AAttr == 'alignment':
                                a = Aobjects[key].attributes[AAttr].split('-')
                                b = Bobjects[mapping[key]].attributes[BAttr].split('-')

                                if a[0] != b[0]:
                                    transform.append([key, 'translated', b[0]])
                                if a[1] != b[1]:
                                    transform.append([key, 'translated', b[1]])

                            else:
                                transform.append([key, AAttr, Bobjects[mapping[key]].attributes[BAttr]])

        return transform

    def applyTransform(self, start, transform):

        solution = start

        for trans in transform:
            if trans[1] in 'add':
                solution[trans[0]] = trans[2]
            elif trans[0] in solution:
                if trans[1] in 'rotation':
                    solution[trans[0]].attributes['angle'] = str((int(solution[trans[0]].attributes['angle']) + trans[2]) % 360)

                elif trans[1] in 'mirror':
                    currentAngle = int(solution[trans[0]].attributes['angle'])
                    if trans[2] == 'H':

                        if 90 < currentAngle < 180 or currentAngle > 270:
                            solution[trans[0]].attributes['angle'] = str((int(solution[trans[0]].attributes['angle']) + trans[3]) % 360)

                        else:
                            solution[trans[0]].attributes['angle'] = str((int(solution[trans[0]].attributes['angle']) - trans[3]) % 360)

                    else:
                        if currentAngle < 90 or 180 < currentAngle < 270:
                            solution[trans[0]].attributes['angle'] = str((int(solution[trans[0]].attributes['angle']) + trans[3]) % 360)

                        else:
                            solution[trans[0]].attributes['angle'] = str((int(solution[trans[0]].attributes['angle']) - trans[3]) % 360)

                elif trans[1] in 'remove':
                    del solution[trans[0]]

                elif trans[1] in 'translated':
                    current = solution[trans[0]].attributes['alignment'].split('-')

                    if trans[2] in ['top', 'bottom']:
                        solution[trans[0]].attributes['alignment'] = trans[2] + '-' + current[1]
                    else:
                        solution[trans[0]].attributes['alignment'] = current[0] + '-' + trans[2]

                else:
                    solution[trans[0]].attributes[trans[1]] = trans[2]

        return solution

    def comapareObjects(self, mapping, ob1, ob2):

        Attr_costs = {'shape': 7,
             'size': 6,
             'inside': 4,
             'angle': 3,
             'alignment': 5,
             'above': 2,
             'fill': 1}

        score = 0

        for key in ob1.attributes:
            if key not in ob2.attributes:
                score += 5

        for key in ob2.attributes:
            if key not in ob1.attributes:
                score += 5

        for key in ob1.attributes:
            if key in ob2.attributes and ob1.attributes[key] != ob2.attributes[key]:
                if key in ['above', 'inside']:
                    ob1Attr = ob1.attributes[key].split(',')
                    ab2Attr = ob2.attributes[key].split(',')
                    for attr in ob1Attr:
                        if attr in mapping:
                            if mapping[attr] not in ab2Attr:
                                score += Attr_costs[key]
                                break
                        else:
                            score += Attr_costs[key]
                else:
                    score += Attr_costs[key]

        return score

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
    # Returning your answer as a string may cause your program to crash.
    def Solve(self,problem):

        if not problem.hasVerbal:
            return -1

        print problem.problemType, problem.name

        ABshapeMapping = []
        ACshapeMapping = []
        numberOfObjects = len(problem.figures['A'].objects)

        # Find mapping between squares
        ABMapping = self.mapping(problem.figures['A'].objects, problem.figures['B'].objects)
        ACMapping = self.mapping(problem.figures['A'].objects, problem.figures['C'].objects)

        print "Mapping"
        print ABMapping
        print ACMapping

        # find the transform to match mapping
        ABTransform = self.findTransform(ABMapping, problem.figures['A'].objects, problem.figures['B'].objects)
        ACTransform = self.findTransform(ACMapping, problem.figures['A'].objects, problem.figures['C'].objects)

        print "transform"
        print ABTransform
        print ACTransform

        # Apply Transform
        solution = self.applyTransform(problem.figures['A'].objects, ABTransform)
        solution = self.applyTransform(solution, ACTransform)

        print 'solution'
        for key in solution:
            print key, ':', solution[key].attributes

        # compare to the answers

        answerScores = []

        for answ in range(1, 7):
            mappedSolution = self.mapping(solution, problem.figures[str(answ)].objects)
            print 'Answer', answ, 'Mapped solution', mappedSolution
            score = 0
            for keys in mappedSolution:
                score += self.comapareObjects(mappedSolution, solution[keys], problem.figures[str(answ)].objects[mappedSolution[keys]])

            # missing objects
            if len(solution) != len(problem.figures[str(answ)].objects):
                score += abs(len(solution) - len(problem.figures[str(answ)].objects)) * 10
            answerScores.append([score, answ])

        print sorted(answerScores)
        print 'answer ', sorted(answerScores)[0][1]
        #print 'correnct answer ', problem.correctAnswer
        print
        return sorted(answerScores)[0][1]




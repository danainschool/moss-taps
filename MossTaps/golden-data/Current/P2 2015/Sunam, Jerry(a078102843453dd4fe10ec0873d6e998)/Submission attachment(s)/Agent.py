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
import services.semanticNetwork as semanticNetwork
import services.generateAndTest2 as generateAndTest2
import services.generateAndTest3 as generateAndTest3
import json


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
    # Returning your answer as a string may cause your program to crash.
    def Solve(self, problem):
        if problem.hasVerbal:
            print '\n' + problem.name
            ans = -3
            if problem.problemType == '2x2':
                semanticNetworks = generateAndTest2.generate(problem)
                ans = self.getBest(generateAndTest2.test(semanticNetworks, semanticNetwork.identity))
                if ans < 1:
                    ans = self.getBest(generateAndTest2.test(semanticNetworks, semanticNetwork.simplify))
            elif problem.problemType == '3x3':
                semanticNetworks = generateAndTest3.generate(problem)
                ans = self.getBest(generateAndTest3.test(semanticNetworks, semanticNetwork.identity))
                if ans < 1:
                    ans = self.getBest(generateAndTest3.test(semanticNetworks, semanticNetwork.simplify))
            print ans
            return ans
        return -4

    def getBest(self, votes):
        print json.dumps(votes)

        best = -1
        val = -1
        dup = False
        for solution, count in enumerate(votes):
            if count > val:
                best = solution
                val = count
                dup = False
            elif count == val:
                dup = True
        if val == -1 or val == 0:
            return -1
        elif dup:
            return -2
        else:
            return best + 1
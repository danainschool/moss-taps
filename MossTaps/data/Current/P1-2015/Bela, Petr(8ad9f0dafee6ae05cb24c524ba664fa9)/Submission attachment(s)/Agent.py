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
from Transformation import Transformation
from RavensFigure import RavensFigure
from RavensObject import RavensObject

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
    def Solve(self,problem):
        # the agent cannot solve visual problems yet ;-(
        if not problem.hasVerbal:
            print "Skipping visual problem %s" % problem.name
            return -1

        print "\nProblem %s" % problem.name

        A = problem.figures['A']
        B = problem.figures['B']
        C = problem.figures['C']

        A_to_B = self.calculate_transformations(A, B)
        A_to_C = self.calculate_transformations(A, C)

        D_from_B = self.apply_transformations(B, A_to_C, A_to_B)
        D_from_C = self.apply_transformations(C, A_to_B, A_to_C)

        # two ways to come to the result
        # sometimes horizontal transformation is easier to calculate, sometimes vertical
        D = [D_from_B, D_from_C]

        for d in D:
            answer = self.find_answer(problem, d)
            if answer:
                problem.checkAnswer(answer)
                result = problem.getCorrect()
                if result == 'Correct': result += u' \u2713'
                if result == 'Incorrect': result += ' :-('
                print "Answer: %s. %s" % (answer, result)
                return answer

        for d in D:
            for obj_name, obj in d.objects.iteritems():
                print obj.attributes

        # not sure what the right answer is
        print "Skipping"
        return -1

    def find_answer(self, problem, solution):
        for i in range(1, 6+1):
            option = problem.figures[str(i)]

            diff = self.calculate_transformations(solution, option)

            same = True
            for obj_name in diff:
                if len(diff[obj_name].changes) > 0:
                    same = False

            if same:
                return i  # found the answer!

    # Calculate transformations from source figure to target figure
    def calculate_transformations(self, source_figure, target_figure):
        transformations = {}

        for source_obj_name, source_obj in source_figure.objects.iteritems():
            for target_obj_name, target_obj in target_figure.objects.iteritems():
                transformation = Transformation(source_obj, target_obj)

                for attr in source_obj.attributes:
                    if attr not in target_obj.attributes:
                        transformation.changes[attr] = {'from': source_obj.attributes[attr]}
                    elif source_obj.attributes[attr] != target_obj.attributes[attr]:
                        transformation.changes[attr] = {'from': source_obj.attributes[attr], 'to': target_obj.attributes[attr]}

                if source_obj_name not in transformations or transformation.cost() < transformations[source_obj_name].cost():
                    already_used = False
                    for obj_name in transformations:
                        if transformations[obj_name].target == transformation.target:
                            already_used = True
                    if not already_used:
                        transformations[source_obj_name] = transformation

        return transformations

    # Apply a set of transformations on a source figure.
    def apply_transformations(self, source_figure, transformations, mapping):
        target_figure = RavensFigure('target', '', '')

        for source_obj_name, source_obj in source_figure.objects.iteritems():
            target_obj = RavensObject(source_obj_name)
            target_obj_name = None
            for obj_name in mapping:
                if mapping[obj_name].target.name == source_obj_name:
                    target_obj_name = obj_name

            for attr in source_obj.attributes:
                if target_obj_name in transformations and attr in transformations[target_obj_name].changes and 'to' in transformations[target_obj_name].changes[attr]:
                    change = transformations[target_obj_name].changes[attr]
                    if attr == 'angle':
                        target_obj.attributes[attr] = str(int(source_obj.attributes[attr]) + int(change['from']) - int(change['to']))
                    elif attr != 'overlaps' and attr != 'above' and attr != 'inside':
                        target_obj.attributes[attr] = change['to']
                else:
                    target_obj.attributes[attr] = source_obj.attributes[attr]

            target_figure.objects[source_obj_name] = target_obj

        return target_figure

    # Helper
    # def print_attributes(self, problem):
    #     attributes = {}
    #     for figure_name, figure in problem.figures.iteritems():
    #         for obj_name, obj in figure.objects.iteritems():
    #             for attr, value in obj.attributes.iteritems():
    #                 if attr not in attributes:
    #                     attributes[attr] = [value]
    #                 elif value not in attributes[attr]:
    #                     attributes[attr].append(value)
    #     print attributes

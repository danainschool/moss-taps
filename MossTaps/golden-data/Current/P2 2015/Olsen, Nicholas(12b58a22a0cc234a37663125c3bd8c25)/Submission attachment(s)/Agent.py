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
from random import randint, choice
from math import pi, sin, cos

from PIL import Image
import scoring
import networks
import patterns


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
        print(problem.name)

        self.fix_object_names(problem)

        if problem.problemType == "2x2":
            return self.solve_2x2(problem)
        else:
            return self.solve_3x3(problem)

    def fix_object_names(self, problem):
        # rename the objects so they will be distinct, in case object names are repeated
        # (I'm lookin' at you, Basic B-02)
        for figure_name in problem.figures:
            new_objects = {}
            for object_name in problem.figures[figure_name].objects:
                problem.figures[figure_name].objects[object_name].name = figure_name + "-" + object_name
                new_objects[figure_name + "-" + object_name] = problem.figures[figure_name].objects[object_name]
            problem.figures[figure_name].objects = new_objects

    def solve_3x3(self, problem):
        self.answer_count = 8
        if problem.hasVerbal:
            return self.solve_3x3_verbal(problem)
        else:
            return self.solve_3x3_visual(problem)

    def solve_3x3_verbal(self, problem):
        pattern_matches = []
        pattern = patterns.detect_progressive_addition(problem)
        if pattern:
            for i in range(self.answer_count):
                answer_name = str(i + 1)
                if patterns.count_shape(problem.figures[answer_name], pattern[1]) == pattern[0]:
                    pattern_matches.append(answer_name)

        horiz_network = networks.build_networks(problem.figures['E'], problem.figures['F'])
        horiz_network += networks.build_networks(problem.figures['D'], problem.figures['F'])
        vert_network = networks.build_networks(problem.figures['E'], problem.figures['H'])
        vert_network += networks.build_networks(problem.figures['B'], problem.figures['H'])
        # diag_network = self.build_networks(problem.figures['A'], problem.figures['E'])

        score = [0, 0, 0, 0, 0, 0, 0, 0]
        max_score = 0
        max_at = ''
        tie_count = 0
        for i in range(self.answer_count):
            answer_name = str(i + 1)
            candidate_h = networks.build_networks(problem.figures['H'], problem.figures[answer_name])
            candidate_h += networks.build_networks(problem.figures['G'], problem.figures[answer_name])
            candidate_v = networks.build_networks(problem.figures['F'], problem.figures[answer_name])
            candidate_v += networks.build_networks(problem.figures['C'], problem.figures[answer_name])
            # candidate_d = self.build_networks(problem.figures['E'], problem.figures[answer_name])
            score[i] = scoring.score_networks(horiz_network, candidate_h) + scoring.score_networks(vert_network,
                                                                                                   candidate_v)
            # + scoring.score_networks(diag_network, candidate_d)
            if score[i] > max_score:
                max_score = score[i]
                max_at = answer_name
                tie_count = 1
            elif score[i] == max_score:
                tie_count += 1

        if tie_count < len(pattern_matches) or len(pattern_matches) == 0:
            num_choices = tie_count
            print("Using networks")
            if num_choices == 1:  # The winnah and undisputed champeen!
                guess = max_at
            elif num_choices == self.answer_count:  # Don't guess if you can't tell the difference between any of them.
                guess = '-1'
            else:  # Guessing is a net gain, do it.
                int_guess = randint(0, self.answer_count - 1)
                while score[int_guess] < max_score:
                    int_guess = randint(0, self.answer_count - 1)
                guess = str(int_guess + 1)
        else:
            num_choices = len(pattern_matches)
            print("Using pattern: ", num_choices, " choices")
            guess = choice(pattern_matches)

        int_correct_answer = problem.checkAnswer(guess)
        if int(guess) == int_correct_answer:
            if num_choices == 1:
                print("Correct Answer")
            else:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Lucky guess among ", num_choices)
                self.review_problem(problem, score, num_choices)
        else:
            if score[int(int_correct_answer) - 1] == max_score:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Unlucky guess among", num_choices)
                self.review_problem(problem, score, num_choices)
            else:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Total miss")
                self.review_problem(problem, score, num_choices)

        return guess

    def solve_3x3_visual(self, problem):
        return -1

    def solve_2x2(self, problem):
        self.answer_count = 6
        if problem.hasVerbal:
            return self.solve_2x2_verbal(problem)
        else:
            return self.solve_2x2_visual(problem)

    def solve_2x2_verbal(self, problem):
        horiz_network = networks.build_networks(problem.figures['A'], problem.figures['B'])
        vert_network = networks.build_networks(problem.figures['A'], problem.figures['C'])

        score = [0, 0, 0, 0, 0, 0]
        max_score = 0
        max_at = ''
        tie_count = 0
        for i in range(self.answer_count):
            answer_name = str(i + 1)
            candidate_h = networks.build_networks(problem.figures['C'], problem.figures[answer_name])
            candidate_v = networks.build_networks(problem.figures['B'], problem.figures[answer_name])
            score[i] = scoring.score_networks(horiz_network, candidate_h) + scoring.score_networks(vert_network,
                                                                                                   candidate_v)
            if score[i] > max_score:
                max_score = score[i]
                max_at = answer_name
                tie_count = 1
            elif score[i] == max_score:
                tie_count += 1

        if tie_count == 1:  # The winnah and undisputed champeen!
            guess = max_at
        elif tie_count == self.answer_count:  # Don't guess if you can't tell the difference between any of them.
            guess = '-1'
        else:  # Guessing is a net gain, do it.
            int_guess = randint(0, self.answer_count - 1)
            while score[int_guess] < max_score:
                int_guess = randint(0, self.answer_count - 1)
            guess = str(int_guess + 1)

        int_correct_answer = problem.checkAnswer(guess)
        if int(guess) == int_correct_answer:
            if tie_count == 1:
                print("Correct Answer")
            else:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Lucky guess among ", tie_count)
                self.review_problem(problem, score, tie_count)
        else:
            if score[int(int_correct_answer) - 1] == max_score:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Unlucky guess among", tie_count)
                self.review_problem(problem, score, tie_count)
            else:
                print("My guess:", guess, " Correct answer: ", int_correct_answer)
                print("Total miss")
                self.review_problem(problem, score, tie_count)

        return guess

    def solve_2x2_visual(self, problem):
        images = {}
        for figure_name in problem.figures:
            figure = problem.figures[figure_name]
            images[figure_name] = Image.open(figure.visualFilename)
        return -1

    def review_problem(self, problem, score, tie_count):
        return 0
        print("Reviewing problem: ", problem.name)
        print("Scores: ", score, ' tie count: ', tie_count)

        if problem.problemType == '2x2':
            print("Horizontal network", networks.build_networks(problem.figures['A'], problem.figures['B']))
            print("Vertical network", networks.build_networks(problem.figures['A'], problem.figures['C']))
            for i in range(6):
                answer_name = str(i + 1)
                candidate_h = networks.build_networks(problem.figures['C'], problem.figures[answer_name])
                print("Horizontal Candidate ", answer_name, ": ", candidate_h)
                candidate_v = networks.build_networks(problem.figures['B'], problem.figures[answer_name])
                print("Vertical Candidate ", answer_name, ": ", candidate_v)
        else:
            print("Horizontal network", networks.build_networks(problem.figures['E'], problem.figures['F']))
            print("Vertical network", networks.build_networks(problem.figures['E'], problem.figures['H']))
            print("Diagonal network", networks.build_networks(problem.figures['A'], problem.figures['E']))
            for i in range(8):
                answer_name = str(i + 1)
                candidate_h = networks.build_networks(problem.figures['H'], problem.figures[answer_name])
                print("Horizontal Candidate ", answer_name, ": ", candidate_h)
                candidate_v = networks.build_networks(problem.figures['F'], problem.figures[answer_name])
                print("Vertical Candidate ", answer_name, ": ", candidate_v)
                candidate_d = networks.build_networks(problem.figures['E'], problem.figures[answer_name])
                print("Diagonal Candidate ", answer_name, ": ", candidate_d)

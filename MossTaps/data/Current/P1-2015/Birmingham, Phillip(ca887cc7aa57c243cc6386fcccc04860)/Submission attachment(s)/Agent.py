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
# from PIL import Image
from collections import defaultdict
from random import randint


class Agent:
    DELETION_DIFF = 4
    # 0 unchanged
    # 1 reflected
    # 2 rotated
    # 3 scaled
    # 4 deleted
    # 5 shape changed
    difference_weights = {'above': .5, 'alignment': 1, 'angle': 1.5, 'fill': .5, 'inside': .5, 'overlaps': .5,
                          'shape': 5, 'size': 3}
    relationship_names = difference_weights.keys()

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

        if problem.problemType == "2x2":
            return self.solve_2x2(problem)
        else:
            return self.solve_3x3(problem)

    def solve_3x3(self, problem):
        if problem.hasVerbal:
            return self.solve_3x3_verbal(problem)
        else:
            return self.solve_3x3_visual(problem)

    def solve_3x3_verbal(self, problem):
        return -1

    def solve_3x3_visual(self, problem):
        return -1

    def solve_2x2(self, problem):
        if problem.hasVerbal:
            return self.solve_2x2_verbal(problem)
        else:
            return self.solve_2x2_visual(problem)

    def solve_2x2_verbal(self, problem):
        horiz_network = self.build_network(problem.figures['A'], problem.figures['B'])
        vert_network = self.build_network(problem.figures['A'], problem.figures['C'])

        score = [0, 0, 0, 0, 0, 0]
        max_score = 0
        max_at = ''
        tie_count = 1
        for i in range(6):
            answer_name = str(i + 1)
            candidate_h = self.build_network(problem.figures['C'], problem.figures[answer_name])
            candidate_v = self.build_network(problem.figures['B'], problem.figures[answer_name])
            score[i] = self.compare_networks(horiz_network, candidate_h) + self.compare_networks(vert_network,
                                                                                                 candidate_v)
            if score[i] > max_score:
                max_score = score[i]
                max_at = answer_name
                tie_count = 1
            elif score[i] == max_score:
                tie_count += 1

        if tie_count == 1:      # The winnah and undisputed champeen!
            guess = max_at
        elif tie_count == 6:    # Don't guess if you can't tell the difference between any of them.
            guess = '-1'
        else:                   # Guessing is a net gain, so do it.  Pick an answer at random,
                                # if it scores high enough use it.  Otherwise pick another till you pick a high scorer.
            int_guess = randint(0, 5)
            while score[int_guess] < max_score:
                int_guess = randint(0, 5)
            guess = str(int_guess + 1)

        correct = problem.checkAnswer(guess)
        print("My guess:", guess, " Correct answer: ", correct)

        if tie_count > 1 or int(guess) != int(correct):
            print("Scores: ", score, ' tie count: ', tie_count)
            self.review_problem(problem)
        return guess

    def solve_2x2_visual(self, problem):
        return -1

    def object_difference(self, obj1, obj2):
        diff = 0
        for attrName in obj1.attributes:
            if attrName in obj2.attributes:
                if obj1.attributes[attrName] != obj2.attributes[attrName]:
                    diff += self.difference_weights[attrName]
            else:
                diff += self.difference_weights[attrName]
        for attrName in obj2.attributes:
            if attrName not in obj1.attributes:
                diff += self.difference_weights[attrName]

        return diff

    # Compute a hit score between the two networks.  Currently this is crude --
    # we just count the number of relationship/transformations that are the same, and give one
    # extra point for having the same number of relationships.

    def compare_networks(self, network_a, network_b):
        score = 0
        counts = defaultdict(int)

        if len(network_a) == len(network_b):
            score += 1

        for relationship in network_a:
            counts[relationship[0]] += 1
        for relationship in network_b:
            counts[relationship[0]] -= 1
        for key in counts:
            if counts[key] == 0:
                score += 1
        return score

    def build_network(self, initial_figure, final_figure):
        tuples = self.find_best_mapping(initial_figure, final_figure)
        network = self.build_transformation_subnetwork(initial_figure, final_figure, tuples)
        # network += self.build_relationship_subnetwork(initial_figure)
        # network += self.build_relationship_subnetwork(final_figure)
        return network

    def build_relationship_subnetwork(self, figure):
        network = []
        # build the intra-figure relationships
        for object_name in figure.objects:
            for attribute_name in figure.objects[object_name].attributes:
                if attribute_name in ['inside', 'above', 'overlaps']:
                    for object_ref in figure.objects[object_name].attributes[attribute_name].split(','):
                        network.append((object_name, attribute_name, object_ref))
        return network

    def build_transformation_subnetwork(self, initial_figure, final_figure, tuples):
        network = []
        for this_tuple in tuples:
            initial_object_name = this_tuple[0]
            final_object_name = this_tuple[1]
            if initial_object_name == 'none':
                network.append(('added', final_object_name))
            elif final_object_name == 'none':
                network.append(('deleted', initial_object_name))
            else:
                initial_object = initial_figure.objects[initial_object_name]
                final_object = final_figure.objects[final_object_name]
                for attribute_name in initial_object.attributes:
                    if attribute_name in final_object.attributes:
                        if initial_object.attributes[attribute_name] != final_object.attributes[attribute_name]:
                            if attribute_name == 'angle':
                                if not self.object_is_reflected(initial_object, final_object): #special case for reflection
                                    rotation_angle = int(final_object.attributes['angle']) - int(initial_object.attributes['angle'])
                                    if rotation_angle < 0:
                                        rotation_angle += 360
                                    rotation = str(rotation_angle)
                                    network.append(('rotate' + rotation, initial_object_name))
                            if attribute_name == 'size':
                                network.append(('scale', initial_object_name))
                            if attribute_name == 'alignment':
                                reflection_status = self.object_is_reflected(initial_object, final_object)
                                if reflection_status:
                                        network.append((reflection_status, initial_object_name))
                            if attribute_name == 'fill':
                                network.append(('change_color', initial_object_name))
                            if attribute_name == 'shape':
                                network.append(('change_shape', initial_object_name))
                            # if attribute_name == 'inside':
                            #     print("Not currently handling relational attributes in transformations")
                            # if attribute_name == 'overlaps':
                            #     print("Not currently handling relational attributes in transformations")
                            # if attribute_name == 'above':
                            #     print("Not currently handling relational attributes in transformations")
        return network

    def list_all_tuples(self, initial_figure, final_figure):
        return_list = []
        initial_keys = list(initial_figure.objects.keys())
        final_keys = list(final_figure.objects.keys())
        for i in range(len(initial_keys)):
            for j in range(len(final_keys)):
                return_list.append((initial_keys[i],
                                    final_keys[j],
                                    self.object_difference(initial_figure.objects[initial_keys[i]],
                                                           final_figure.objects[final_keys[j]])
                                    ))
        return return_list


# Simple matching method: sort the candidate matches in order of increasing difference between the objects
# Move the first one (it will be the one with the greatest similarity) to the final list and remove from the candidate
# list all pairings that have either of the objects in the pairing we selected.  Also remove them from a list of all
# the object names.  When we are out of pairings, go through the remaining objects and assign them to "none"

    def find_best_mapping(self, initial_figure, final_figure):
        tuple_list = sorted(self.list_all_tuples(initial_figure, final_figure), key=lambda pair: -pair[2])
        initial_keys = list(initial_figure.objects.keys())
        final_keys = list(final_figure.objects.keys())
        key_list = initial_keys + final_keys
        return_list = []

        while len(tuple_list) > 0:
            top_tuple = tuple_list.pop()
            return_list.append(top_tuple)
            key_list.remove(top_tuple[0])
            key_list.remove(top_tuple[1])

            tuple_list = list(filter(lambda x: not self.tuples_have_common_members(x, top_tuple), tuple_list))

        for key in key_list:
            if key in initial_figure.objects.keys():
                return_list.append((key, 'none', self.DELETION_DIFF))
            else:
                return_list.append(('none', key, self.DELETION_DIFF))

        return return_list

    def tuples_have_common_members(self, tuple1, tuple2):
        for i in range(2):
            for j in range(2):
                if tuple1[i] == tuple2[j]:
                    return True
        else:
            return False

    def object_is_reflected(self, initial_object, final_object):
        if 'alignment' not in initial_object.attributes or 'alignment' not in final_object.attributes:
            return False
        #alignment attributes follow the pattern 'vertical_descriptor-horizontal_descriptor' e.g. 'lower-right'
        initial_alignment = initial_object.attributes['alignment'].split('-')
        final_alignment = final_object.attributes['alignment'].split('-')
        if initial_alignment[0] == final_alignment[0] and initial_alignment[1] != final_alignment[1]:
            return 'reflect_about_vertical'
        elif initial_alignment[0] != final_alignment[0] and initial_alignment[1] == final_alignment[1]:
            return 'reflect_about_horizontal'
        else:
            return False

    def review_problem(self, problem):
        print("Reviewing problem: ", problem.name)
        print("Horizontal network", self.build_network(problem.figures['A'], problem.figures['B']))
        print("Vertical network", self.build_network(problem.figures['A'], problem.figures['C']))
        for i in range(6):
            answer_name = str(i + 1)
            candidate_h = self.build_network(problem.figures['C'], problem.figures[answer_name])
            print("Horizontal Candidate ", answer_name, ": ", candidate_h)
            candidate_v = self.build_network(problem.figures['B'], problem.figures[answer_name])
            print("Vertical Candidate ", answer_name, ": ", candidate_v)


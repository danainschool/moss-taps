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
from PIL import Image
from RavensFigure import RavensFigure
from RavensObject import RavensObject


# Define Attribute Scores for Weight Testing
ATTRIBUTE_SCORE = {'shape': 5,
                   'fill': 4,
                   'angle': 3,
                   'alignment': 3,
                   'size':2,
                   'inside': 1,
                   'above': 1}

# Define Alignment Scores For Weight Testing, First Object
ALIGN1 = {'bottom-right': (1, -1),
              'bottom-left': (-1, -1),
              'top-right': (1,1),
              'top-left': (-1, 1)
              }

# Define Alignment Scores For Weight Testing, Second Object
ALIGN2 = {(1, -1): 'bottom-right',
              (-1, -1): 'bottom-left',
              (1,1): 'top-right',
              (-1, 1): 'top-left'}

class Agent:
    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().
    def __init__(self):
        self.selections = []

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

    def is_match(self, case_start, case_end, sorted_origin, sorted_order, ans_match=False):

        record = []
        match = {}
        score_record = {}
        best_match, best_score = -1, -1
        for i in sorted_order:
            obj_dest = case_end.objects[i]
            for j in sorted_origin:
                obj_origin = case_start.objects[j]
                if (not ans_match and obj_origin not in record) or ans_match:
                    score = self.set_score(obj_origin, obj_dest)
                    if len(obj_origin.attributes) == len(obj_dest.attributes):
                        score += 1
                    if score > best_score:
                        best_score = score
                        best_match = obj_origin
                        match[obj_origin] = obj_dest
                        score_record[j] = best_score
                        record.append(obj_origin)

        remaining = set(case_start.objects.values()) - set(record)
        for obj in remaining:
            match[obj_origin] = None

        return match, score_record


    def set_score(self, object1, object2):
        score = 0
        for row, col in object2.attributes.iteritems():
            if row in object1.attributes and object1.attributes[row] == col:
                score += ATTRIBUTE_SCORE[row]
            elif row in ['inside', 'above', 'below'] and row in object1.attributes:
                score += 1
        return score


    def screen(self, rule, problem, case_start, case_end, case_x):
        if rule == 'number':
            counter_origin, counter_dest, counter_x = {}, {}, {}
            for figure, counter in zip([case_start, case_end, case_x], [counter_origin, counter_dest, counter_x]):
                for object in figure.objects:
                    shape = figure.objects[object].attributes['shape']
                    if shape not in counter:
                        counter[shape] = 1
                    else:
                        counter[shape] += 1
            predicted = {}
            sorted_order = sorted(counter_origin.keys(), key=lambda x: counter_origin[x], reverse=True)
            sorted_x = sorted(counter_x.keys(), key=lambda x: counter_x[x], reverse=True)
            helper = 0
            all_shapes = set(counter_origin.keys())|set(counter_dest.keys())
            delta_map = {}
            for key in all_shapes:
                if key not in counter_origin:
                    counter_origin[key] = 0
                if key not in counter_dest:
                    counter_dest[key] = 0
                delta_map[key] = counter_dest[key] - counter_origin[key]

            for shape1, shape2 in zip(sorted_order, sorted_x):
                dest_count = counter_dest.get(shape1) or 0
                delta = dest_count - counter_origin[shape1]
                predicted[shape2] = counter_x[shape2] + delta
                helper += 1

            if helper < len(sorted_x):
                for remaining in sorted_x[helper:]:
                    d = delta_map[remaining]
                    predicted[remaining] = counter_x[remaining] + d
            selections = []
            option_iterator = self.shape_count(problem)

            for i in xrange(1, 7):
                next_option = option_iterator.next()
                for shape in predicted:
                    no_match = False
                    if next_option.get(shape) != predicted[shape]:
                        no_match = True
                        break
                if not no_match:
                    selections.append(i)
            self.selections = selections
            return

        elif rule == 'detail' and len(case_start.objects) == len(case_x.objects):
            options = self.selections
            # get transform scores from objects have more attribute to less
            match = []
            sorted_origin = sorted(case_start.objects.keys(), key=lambda x:len(case_start.objects[x].attributes))
            for mp in [case_end, case_x]:
                sorted_order = sorted(mp.objects.keys(), key=lambda x:len(mp.objects[x].attributes))
                m, _ = self.is_match(case_start, mp, sorted_origin, sorted_order)
                match.append(m)

            # apply the transform rule to case_x
            other_match, case_x_match = match
            predicted = RavensFigure('predicted', '', '')
            helper = 0
            for start, case_x in case_x_match.iteritems():
                start_attrs = start.attributes
                if other_match[start]:
                    end_attrs = other_match[start].attributes
                else:
                    continue
                new_object = RavensObject(str(helper))

                for attr in end_attrs:
                    if attr == 'shape':
                        if start_attrs.get('shape') != end_attrs['shape']:
                            new_object.attributes['shape'] = end_attrs['shape']
                        else:
                            new_object.attributes['shape'] = case_x.attributes['shape']
                    elif attr == 'angle' and 'angle' in start_attrs and 'angle' in case_x.attributes:
                        # parallel relationship has higher order
                        if end_attrs['angle'] == start_attrs['angle']:
                            new_object.attributes['angle'] = case_x.attributes['angle']
                            continue
                        reflection = (int(end_attrs['angle']) + int(start_attrs['angle'])) % 360
                        if reflection == 0:
                            new_object.attributes['angle'] = str(360 - int(case_x.attributes['angle']))
                        elif reflection == 180:
                            angle = 180 - int(case_x.attributes['angle'])
                            if angle < 0:
                                angle += 360
                            new_object.attributes['angle'] = str(angle)
                        if start_attrs['angle'] == case_x.attributes['angle']:
                            new_object.attributes['angle'] = end_attrs['angle']
                    elif attr == 'alignment' and attr in start_attrs and attr in case_x.attributes:
                        alignment = [x['alignment'] for x in [start_attrs, end_attrs, case_x.attributes]]
                        p1, p2, p3 = [ALIGN1[x] for x in alignment]
                        if p1 == p3:
                            new_object.attributes['alignment'] = end_attrs['alignment']
                        elif p1[0] == p2[0] and p1[1] + p2[1] == 0:
                            p4 = (p3[0], -p3[1])
                            new_object.attributes['alignment'] = ALIGN2[p4]
                        elif p1[1] == p2[1] and p1[0] + p2[0] == 0:
                            p4 = (-p3[0], p3[1])
                            new_object.attributes['alignment'] = ALIGN2[p4]
                    else:
                        if start_attrs.get(attr) != end_attrs[attr]:
                            new_object.attributes[attr] = end_attrs[attr] if case_x.attributes.get(attr) == start_attrs.get(attr) else \
                            start_attrs.get(attr)
                        else:
                            new_object.attributes[attr] = case_x.attributes.get(attr)

                predicted.objects[str(helper)] = new_object
                helper += 1

            if self.selections:
                self.find_answer(predicted, problem, selections=True)
            else:
                self.find_answer(predicted, problem, selections=False)

        else:
            return

    def find_answer(self, predicted, problem, selections=False):
        if self.selections:
            options = [str(x) for x in self.selections]
        elif problem.problemType == '2x2':
            options = ['1', '2', '3', '4', '5', '6']
        max_score = 0
        candidate = None
        sorted_predict = sorted(predicted.objects.keys(), key=lambda x:len(predicted.objects[x].attributes))
        for i in options:
            figure = problem.figures[i]
            if len(figure.objects) != len(predicted.objects):
                continue
            sorted_order = sorted(figure.objects.keys(), key=lambda x:len(figure.objects[x].attributes))
            _, score = self.is_match(predicted, figure, sorted_predict, sorted_order, True)
            final_score = sum(score.values())
            if final_score > max_score:
                max_score = final_score
                candidate = int(i)
        self.selections = [candidate]
        return



    def shape_count(self, problem):
        if problem.problemType == '2x2':
            options = ['1', '2', '3', '4', '5', '6']
        for opt in options:
            counter = {}
            figure = problem.figures[opt]
            for i in figure.objects:
                object = figure.objects[i]
                if object.attributes['shape'] not in counter:
                    counter[object.attributes['shape']] = 1
                else:
                    counter[object.attributes['shape']] += 1
            yield counter


    def Solve(self,problem):
        self.selections = []
        select_order = ['number', 'detail']
        if problem.problemType == '2x2':
            case_start = problem.figures['A']
            case_end = problem.figures['B']
            case_x = problem.figures['C']
        if not problem.hasVerbal:
            return -1
        for rule in select_order:
            try:
                self.screen(rule=rule, problem=problem, case_start=case_start, case_end=case_end, case_x=case_x)
            except:
                continue
            if len(self.selections) == 1:
                return self.selections[0]
        if self.selections and len(self.selections) <= 2:
            return self.selections[-1]
        else:
            return -1
            
        
        
        
        
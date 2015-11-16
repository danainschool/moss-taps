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
        if not problem.hasVerbal:
            return -1
        def list_of_names_in_figure(fig_objs):
            nlist = []
            for obj in fig_objs:
                nlist.append(obj)
            return nlist
 
        def combo_maker(list1, list2):
            list1 = list_of_names_in_figure(list1)
            list2 = list_of_names_in_figure(list2)
            combinations = []
            if not list1:
                for object2 in list2:
                    combinations.append([None, object2])
                return combinations
            elif not list2:
                for object1 in list1:
                    combinations.append([object1, None])
                return combinations
            else:
                # initialize with all a transformations
                if list1[0]:
                    object_a = list1[0]
                for object2 in list2:
                    combinations.append([(object_a,object2)])
                combinations.append([(object_a, None)])
                change = True
                while change:
                    change = False
                    for obj1 in list1[1:]:
                        for obj2 in list2:
                            for path in combinations:
                                if (obj1, obj2) not in path:
                                    if obj1 not in (i[0] for i in path) and obj2 not in (i[1] for i in path):
                                        path.append((obj1, obj2))
                                        change = True
                                    elif obj2 not in (i[1] for i in path):
                                        path2 = list(path)
                                        for pair in path2:
                                            if pair[0] == obj1:
                                                path2.remove(pair)
                                                path2.append((obj1, obj2))
                                        if path2 not in combinations:
                                            combinations.append(path2)
                                            change = True
                                if (obj1, None) not in path:
                                    if obj1 not in (i[0] for i in path):
                                        path3 = list(path)
                                        path3.append((obj1, None))
                                        if path3 not in combinations:
                                            combinations.append(path3)
                                            change = True
            for fin_path in combinations:
                for end_obj in list2:
                    if end_obj not in (i[1] for i in fin_path):
                        fin_path.append((None, end_obj))
            return combinations

        transform_frame = {"shape": "missing", "size": "missing", "fill": "missing", "angle": "missing", "inside": "missing", "above" : "missing"}

        def transform(obj1, obj2, tframe = transform_frame):
            filled_tframe = dict(tframe)
            def fill_frames(obj):
                objframe = dict(tframe)
                for tkey in obj.attributes.keys():
                    if tkey == "inside" or tkey == "above":
                        attr_val = str(len(obj.attributes[tkey]))
                        objframe[tkey] = attr_val
                    else:
                        objframe[tkey] = obj.attributes[tkey]
                return objframe
            obj1frame = fill_frames(obj1)
            obj2frame = fill_frames(obj2)
            for tkey in tframe.keys():
               if obj1frame[tkey] == obj2frame[tkey]: 
                    filled_tframe[tkey] = "No_change"
               elif tkey == "angle":
                    if obj1frame[tkey] != "missing" and obj2frame[tkey] != "missing":
                        tval = str(abs(int(obj1frame[tkey]) - int(obj2frame[tkey])))
                        filled_tframe[tkey] = tval
                    else:
                        filled_tframe[tkey] = obj1frame[tkey] + "_" + obj2frame[tkey]
               elif obj1frame[tkey] != obj2frame[tkey]:
                    filled_tframe[tkey] = obj1frame[tkey] + "_" + obj2frame[tkey]
            return filled_tframe
            
        def transform_removed(obj1, tframe = transform_frame):
            filled_tframe = dict(tframe)
            for tkey in obj1.attributes.keys():
                filled_tframe[tkey] = obj1.attributes[tkey] + "_missing"
            return filled_tframe

        def transform_new(obj1, tframe = transform_frame):
            filled_tframe = dict(tframe)
            for tkey in obj1.attributes.keys():
                filled_tframe[tkey] = "missing_" + obj1.attributes[tkey] 
            return filled_tframe

        def transform_path(combos, fig1, fig2):
            filled_tpath = []
            for path in combos:
                newtpath = []
                for pair in path:
                    if not pair[1]:
			tr = transform_removed(fig1.objects[pair[0]])
                    elif not pair[0]:
			tr = transform_new(fig2.objects[pair[1]])
                    else:
			tr = transform(fig1.objects[pair[0]], fig2.objects[pair[1]])
                    newtpath.append(tr)
                filled_tpath.append(newtpath)
            return filled_tpath

        def tie_breaker(ans1, ans2, score):
            # [TODO] define tie breaker
            return ans1


        top_score = 0
        top_ans_choice = None
        remaining_ans_choices = []    

        if problem.problemType == '2x2':
            A = problem.figures["A"]
            B = problem.figures["B"]
            C = problem.figures["C"]
            comboAB = combo_maker(A.objects, B.objects)
            comboAC = combo_maker(A.objects, C.objects)
            tpathAB = transform_path(comboAB, A, B)
            tpathAC = transform_path(comboAC, A, C)

            matching_pathBD = []
            matching_pathCD = []

            def check_answer(ans):
                matching_pathBD.append([])
                matching_pathCD.append([])
                score = -1
                D = problem.figures[ans]
                comboBD = combo_maker(B.objects, D.objects)
                comboCD = combo_maker(C.objects, D.objects)
                tpathBD = transform_path(comboBD, B, D)
                tpathCD = transform_path(comboCD, C, D)
                for path in tpathBD:
                    if path in tpathAC:
                        score += 2
                        matching_pathBD[int(ans)-1].append(path)
                for path in tpathCD:
                    if path in tpathAB:
                        score += 2
                        matching_pathCD[int(ans)-1].append(path)
                return score
            for ans_int in range(1,7):
                ans_choice = str(ans_int)
                score = check_answer(ans_choice) 
                if score > top_score:
                    top_score = score
                    top_ans_choice = ans_choice
                elif score == top_score:
                    #top_ans_choice = tie_breaker(ans_choice, top_ans_choice, score)
                    top_ans_choice = ans_choice
        elif problem.problemType == '3x3':
            A = problem.figures["A"]
            B = problem.figures["B"]
            C = problem.figures["C"]
            D = problem.figures["D"]
            E = problem.figures["E"]
            F = problem.figures["F"]
            G = problem.figures["G"]
            H = problem.figures["H"]
            # initiate string of remaining answer choices
            for ans_int in range(1,9):
                remaining_ans_choices.append(str(ans_int))

            constant_num_objs = True
            figs_list = [A, B, C, D, E, F, G, H]
            num_objs_all = {} 
            objs = len(A.objects)
            for fig in figs_list:
                objs1 = len(fig.objects)
                num_objs_all[str(fig)] = objs1
                if objs1 != objs:
                    constant_num_objs = False
                objs = objs1
            def diff_num_objs(fig1, fig2):
                if len(fig1.objects) == len(fig2.objects):
                    return 0
                if len(fig1.objects) > len(fig2.objects):
                    return -1
                if len(fig1.objects) < len(fig2.objects):
                    return 1

            def check_answer1(ans):
                score = -1
                I = problem.figures[ans]
                if constant_num_objs and len(I.objects) == objs:
                    score += 2
                if not constant_num_objs:
                    if [diff_num_objs(A,B), diff_num_objs(B,C)] == [diff_num_objs(G,H), diff_num_objs(H,I)]:
                        score += 2 
                    if [diff_num_objs(D,E), diff_num_objs(E,F)] == [diff_num_objs(G,H), diff_num_objs(H,I)]:
                        score += 2 
                    if [diff_num_objs(A,D), diff_num_objs(D,G)] == [diff_num_objs(C,F), diff_num_objs(F,I)]:
                        score += 2 
                    if [diff_num_objs(B,E), diff_num_objs(E,H)] == [diff_num_objs(C,F), diff_num_objs(F,I)]:
                        score += 2 
                    if [diff_num_objs(A,E)] == [diff_num_objs(E,I)]:
                        score += 2 
                return score

            def num_obj_names(figlist1):
                list_of_names = []
                for fig1 in figlist1:
                    for obj in fig1.objects:
                        if fig1.objects[obj].attributes['shape'] not in list_of_names:
                            list_of_names.append(fig1.objects[obj].attributes['shape']) 
                return len(list_of_names)

            def check_answer2(ans):
                # check types of objects
                score = -1
                I = problem.figures[ans]
                if num_obj_names([A,B,C]) == num_obj_names([G,H,I]):
                    score += 2 
                if num_obj_names([D,E,F]) == num_obj_names([G,H,I]):
                    score += 2 
                if num_obj_names([A,D,G]) == num_obj_names([C,F,I]):
                    score += 2 
                if num_obj_names([B,E,H]) == num_obj_names([C,F,I]):
                    score += 2 
                if num_obj_names([A,E]) == num_obj_names([E,I]):
                    score += 2 
                return score
            
            def is_same_obj(obj1, obj2):
                same = True
                if obj1.attributes.keys() != obj2.attributes.keys():
                    return False
                for attr in obj1.attributes.keys():
                    if attr in ['left-of', 'right-of', 'above', 'below']:
                        continue
                    elif obj1.attributes[attr] != obj2.attributes[attr]:
                        return False
                return same

            def num_same_objs(fig1, fig2):
                same_objs = 0
                for figobj1 in fig1.objects:
                    for figobj2 in fig2.objects:
                        if is_same_obj(fig1.objects[figobj1], fig2.objects[figobj2]):
                            same_objs += 1
                return same_objs

            def check_answer3(ans):
                # checks number of objects staying the same
                score = -1
                I = problem.figures[ans]
                if [num_same_objs(A,B), num_same_objs(B,C)] == [num_same_objs(G,H), num_same_objs(H,I)]:
                    score += 2 
                if [num_same_objs(D,E), num_same_objs(E,F)] == [num_same_objs(G,H), num_same_objs(H,I)]:
                    score += 2 
                if [num_same_objs(A,D), num_same_objs(D,G)] == [num_same_objs(C,F), num_same_objs(F,I)]:
                    score += 2 
                if [num_same_objs(B,E), num_same_objs(E,H)] == [num_same_objs(C,F), num_same_objs(F,I)]:
                    score += 2 
                if [num_same_objs(A,E)] == [num_same_objs(E,I)]:
                    score += 2 
                if (num_same_objs(A,B) - num_same_objs(B,C)) == (num_same_objs(G,H) - num_same_objs(H,I)):
                    score += 2 
                if (num_same_objs(D,E) - num_same_objs(E,F)) == (num_same_objs(G,H) - num_same_objs(H,I)):
                    score += 2 
                if (num_same_objs(A,D) - num_same_objs(D,G)) == (num_same_objs(C,F) - num_same_objs(F,I)):
                    score += 2 
                if (num_same_objs(B,E) - num_same_objs(E,H)) == (num_same_objs(C,F) - num_same_objs(F,I)):
                    score += 2 
                if (num_same_objs(A,E)) == (num_same_objs(E,I)):
                    score += 2 
                return score

            def size_value(fig1):
                sizes = {'very small' : 1, 'small' : 2, 'medium' : 3, 'large': 4, 'very large': 5, 'huge': 6}
                sval = 0
                n = 0
                for obj in fig1.objects:
                    for attr in fig1.objects[obj].attributes.keys():
                        if attr == 'size':
                            sval += 2*(sizes[fig1.objects[obj].attributes[attr]])     
                            n += 2
                        if attr == 'width':
                            sval += sizes[fig1.objects[obj].attributes[attr]] 
                            n += 1
                        if attr == 'size':
                            sval += sizes[fig1.objects[obj].attributes[attr]] 
                            n += 1
                if n > 0: 
                    return (sval/n)
                else:
                    return sval
            def diff_size_value(fig1, fig2):
                if size_value(fig1) == size_value(fig2):
                    return 0
                if size_value(fig1) > size_value(fig2):
                    return -1
                if size_value(fig1) < size_value(fig2):
                    return 1

            def check_answer4(ans):
                 # check for growth trend along paths
                 score = -1
                 I = problem.figures[ans]
                 if [diff_size_value(A,B), diff_size_value(B,C)] == [diff_size_value(G,H), diff_size_value(H,I)]:
                     score += 2
                 if [diff_size_value(D,E), diff_size_value(E,F)] == [diff_size_value(G,H), diff_size_value(H,I)]:
                     score += 2
                 if [diff_size_value(A,D), diff_size_value(D,G)] == [diff_size_value(C,F), diff_size_value(F,I)]:
                     score += 2
                 if [diff_size_value(B,E), diff_size_value(E,H)] == [diff_size_value(C,F), diff_size_value(F,I)]:
                     score += 2
                 if [diff_size_value(A,E)] == [diff_size_value(E,I)]:
                     score += 2
                 return score

            def check_answer_evaluate(check_function, remaining_ans_choices1):
                scores = []
                for ans in remaining_ans_choices1:
                    scores.append(check_function(ans)) 
                if max(scores) > 0:
                    new_remaining_ans_choices = []
                    for counter, score in enumerate(scores):
                        if score == max(scores):
                            new_remaining_ans_choices.append(remaining_ans_choices1[counter])
                remaining_ans_choices = new_remaining_ans_choices
                return remaining_ans_choices

            remaining_ans_choices = check_answer_evaluate(check_answer1, remaining_ans_choices)
            if len(remaining_ans_choices) != 1:
                remaining_ans_choices = check_answer_evaluate(check_answer2, remaining_ans_choices)
            if len(remaining_ans_choices) != 1:
                remaining_ans_choices = check_answer_evaluate(check_answer3, remaining_ans_choices)
            if len(remaining_ans_choices) != 1:
                remaining_ans_choices = check_answer_evaluate(check_answer4, remaining_ans_choices)

            if len(remaining_ans_choices) == 1:
                top_ans_choice = remaining_ans_choices[0]
        
        if top_ans_choice:
            final_answer = int(top_ans_choice)
        else:
            final_answer = -1
        return final_answer

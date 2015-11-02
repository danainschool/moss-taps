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

        A = problem.figures["A"]
        B = problem.figures["B"]
        C = problem.figures["C"]
        comboAB = combo_maker(A.objects, B.objects)
        comboAC = combo_maker(A.objects, C.objects)
        tpathAB = transform_path(comboAB, A, B)
        tpathAC = transform_path(comboAC, A, C)

        def tie_breaker(ans1, ans2, score):
            # [TODO] define tie breaker
            return ans1

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

        top_score = 0
        top_ans_choice = None
        for ans_int in range(1,7):
            ans_choice = str(ans_int)
            score = check_answer(ans_choice) 
            if score > top_score:
                top_score = score
                top_ans_choice = ans_choice
            elif score == top_score:
                #top_ans_choice = tie_breaker(ans_choice, top_ans_choice, score)
                top_ans_choice = ans_choice

        if top_ans_choice:
            final_answer = int(top_ans_choice)
        else:
            final_answer = -1
        return final_answer

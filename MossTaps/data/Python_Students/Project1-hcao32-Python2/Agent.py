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
import re
import itertools
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
        print "Working on problem", problem.getName()
        
        #Potential answers will go in a list
        answer = []
        #Get Figure objects
        A = problem.getFigures().get("A")
        B = problem.getFigures().get("B")
        C = problem.getFigures().get("C")
        one = problem.getFigures().get("1")
        two = problem.getFigures().get("2")
        three = problem.getFigures().get("3")   
        four = problem.getFigures().get("4")
        five = problem.getFigures().get("5")
        six = problem.getFigures().get("6")

        #generate transformation relationships between frames. Returns dictionary of {objectName: transformations}
        AtoB = self.getRelationships(A,B,{},problem)
        Cto1 = self.getRelationships(C,one,AtoB,problem)
        Cto2 = self.getRelationships(C,two,AtoB,problem)
        Cto3 = self.getRelationships(C,three,AtoB,problem)
        Cto4 = self.getRelationships(C,four,AtoB,problem)
        Cto5 = self.getRelationships(C,five,AtoB,problem)
        Cto6 = self.getRelationships(C,six,AtoB,problem)

        possible = {"1":Cto1, "2":Cto2, "3":Cto3, "4":Cto4, "5":Cto5, "6":Cto6}

        


        #Choose answers C-># with similar transformations as A->B
        scores = {} #naive 'delta' score
        for name,rels in possible.iteritems():
            scores[name] = -1000               
            rel_permutations = list(itertools.permutations(rels.values()))
            best_score=-1000  
            for rel_value in rel_permutations:            
                scores[name] = 0                
                for AtoBval,relsval in zip(AtoB.values(),rel_value):
                    scores[name] += len(set(AtoBval).intersection(relsval))
                    AtoB_num_Deleted=len(set(AtoBval).intersection({"deleted":"deleted"}))
                    AtoB_num_Added=len(set(AtoBval).intersection({"added":"added"}))
                    relsval_num_Deleted=len(set(relsval).intersection({"deleted":"deleted"}))
                    relsval_num_Added=len(set(relsval).intersection({"added":"added"}))                   
    
                    scores[name]-=(abs((AtoB_num_Deleted-relsval_num_Deleted))+abs(AtoB_num_Added-relsval_num_Added))*5
                if scores[name]>=best_score:
                    best_score=scores[name]
            scores[name]=best_score
            #print(rels)
            #print(scores[name])

        for name,score in scores.iteritems():
                if score == max(scores.itervalues()):
                    answer.append(name)
        if len(answer) > 1:
            answer=min(answer)
        print ("Answers:"), answer
        #if there is more than one possible answer, compare relative positions of objects in B with solutions
        #if there is still more than one possible answer, compare frames B with potential answers for similarity
                    #e.g. are all shapes same size? same fill or different? etc.
        return min(answer) if len(answer) > 0 else "1" #pick one randomly if multiple answers left. If there are no answers left choose 1 (shouldn't happen)

        
    def getRelationships(self,A,B,matchWith,problem):
        #generates a dictionary of lists of relations between each object A->B
        #examines each possible mapping of objects from A->B and picks best mapping based on weight
        A_Objs = A.getObjects()
        B_Objs = B.getObjects()
        
        A_names = A_Objs.keys()
        B_names = B_Objs.keys()
        while len(A_names) != len(B_names):
            if len(A_names) > len(B_names):
                B_names.append(None)
            if len(B_names) > len(A_names):
                A_names.append(None)
        B_permutations = list(itertools.permutations(B_names))
       
        bestweight = 0
        bestrels = {}
        for B_names in B_permutations:
            weight = 0
            rels = {}
            
            for A_name,B_name in zip(A_names,B_names):
                
                for obj in A_Objs.values():
                    if obj.getName() == A_name:
                        A_Obj = obj
                for obj in B_Objs.values():
                    if obj.getName() == B_name:
                        B_Obj = obj
                if not A_name:
                    #obj was added to b
                    rels[B_name] = []
                    rels[B_name].append("added")
                elif not B_name:
                    #obj was deleted from a
                    rels[A_name] = []
                    rels[A_name].append("deleted")
                else:
                    rels[B_name] = []
                    A_atts = {}
                    B_atts = {}
                    for A_att,B_att in zip(A_Obj.getAttributes(),B_Obj.getAttributes()):
                        A_atts[A_att]=A_Obj.getAttributes()[A_att]
                        B_atts[B_att]=B_Obj.getAttributes()[B_att]

                    #now for some attribute rules:
                    #shape
                    try:
                        if A_atts["shape"] == B_atts["shape"]:
                            rels[B_name].append("shapeSame")
                            weight += 5
                        else:
                            rels[B_name].append("shapeDiff")
                    except KeyError:
                        pass

                    #size
                    try:
                        if A_atts["size"] == B_atts["size"]:
                            rels[B_name].append("sizeSame")
                            weight += 5
                        else:
                            rels[B_name].append("sizeDiff")
                            weight += 2
                    except KeyError:
                        pass

                    #fill
                    try:
                        A_atts["fill"]
                    except KeyError:
                        A_atts["fill"] = "no"

                    try:
                        B_atts["fill"]
                    except KeyError:
                        B_atts["fill"] = "no"
                    
                    if A_atts["fill"] == B_atts["fill"]:
                        rels[B_name].append("fillSame")
                        weight += 5
                    else:
                        rels[B_name].append("fill:" + A_atts["fill"] + B_atts["fill"])
                        weight += 2
                    

                    
                    #alignment
                    try:
                        A_atts["alignment"]
                    except KeyError:
                        A_atts["alignment"]=0
                    try:
                        B_atts["alignment"]
                    except KeyError:
                        B_atts["alignment"]=0 
                    
                    if  A_atts["alignment"]==0 and B_atts["alignment"]==0:
                        pass 
                    else:
                        A_align=re.split("-",A_atts["alignment"])    
                        B_align=re.split("-",B_atts["alignment"])    
                        if A_align[0]==B_align[0] and A_align[1]==B_align[1]:
                            rels[B_name].append("alignVertical:"+"verticalSame")
                            rels[B_name].append("alignHorizon:"+"horizonSame")
                            weight +=4
                        elif A_align[0]==B_align[0] and A_align[1]!=B_align[1]:            
                            rels[B_name].append("alignVertical:"+"verticalSame")
                            rels[B_name].append("alignHorizon:"+"horizonDiff")
                            weight +=2
                        elif A_align[0]!=B_align[0] and A_align[1]==B_align[1]: 
                            rels[B_name].append("alignVertical:"+"verticalDiff")
                            rels[B_name].append("alignHorizon:"+"horizonSame")
                            weight +=2
                        else: 
                            rels[B_name].append("alignVertical:"+"verticalDiff")
                            rels[B_name].append("alignHorizon:"+"horizonDiff")
                            weight +=1
                   
                   #above
                    try:
                        A_atts["above"]
                    except KeyError:
                        A_atts["above"]=0
                    try:
                        B_atts["above"]
                    except KeyError:
                        B_atts["above"]=0 
                    if A_atts["above"] == 0 and B_atts["above"]==0:
                        pass
                    else:                    
                        if A_atts["above"] == B_atts["above"]:
                            rels[B_name].append("aboveSame")
                            weight += 5                  
                        else:
                            rels[B_name].append("aboveDiff")
                    
                       
                   
                     #angle
                    try:
                        A_atts["angle"]
                    except KeyError:
                        A_atts["angle"] = 0
                    try:
                        B_atts["angle"]
                    except KeyError:
                        B_atts["angle"] = 0
                                        
                    if A_atts["shape"] == "circle" and B_atts["shape"] == "circle": #ignore angle changes for circle
                        if matchWith:
                            for obj in matchWith.iterkeys():
                                if 'angleDiff' in matchWith[obj]:
                                    rels[B_name].append('angleDiff')
                                    rels[B_name].append([n for n in matchWith[obj] if type(n) == type(1)][0]) #copy angle value
                                    break
                                if 'angleSame' in matchWith[obj]:
                                    rels[B_name].append('angleSame')
                                    break
                        elif A_atts["angle"] == B_atts["angle"]:
                            rels[B_name].append("angleSame")
                            weight += 4
                        
                    elif A_atts["angle"] == B_atts["angle"]:
                        rels[B_name].append("angleSame")
                        weight += 4
                    else:
                        rels[B_name].append("angleDiff")
                        rels[B_name].append(abs(int(A_atts["angle"]) - int(B_atts["angle"])))
                        weight +=3

                    #vertical-flip
                    try:
                        A_atts["vertical-flip"]
                    except KeyError:
                        A_atts["vertical-flip"] = "no"
                    try:
                        B_atts["vertical-flip"]
                    except KeyError:
                        B_atts["vertical-flip"] = "no"

                    if A_atts["vertical-flip"] == B_atts["vertical-flip"]:
                        rels[B_name].append("vertflipSame")
                        
                    else:
                        rels[B_name].append("vertflipDiff")
      #      print rels
      #      print weight
            if rels == matchWith:
                weight += 100
            if weight > bestweight:
                bestrels = rels
                bestweight = weight
         
     #   print
     #   print bestrels
        return bestrels








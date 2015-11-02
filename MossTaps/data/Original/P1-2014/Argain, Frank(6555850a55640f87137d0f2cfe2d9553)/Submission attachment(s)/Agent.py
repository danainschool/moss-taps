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
from copy import deepcopy

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
    # conclusion of Solve(), your Agent should return a String representing its
    # answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
    # are also the Names of the individual RavensFigures, obtained through
    # RavensFigure.getName().
    #
    # In addition to returning your answer at the end of the method, your Agent
    # may also call problem.checkAnswer(String givenAnswer). The parameter
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
        print "problem name:  ", problem.name
        
        A_attributes = populateAttributes(problem, "A")
        B_attributes = populateAttributes(problem, "B")
        C_attributes = populateAttributes(problem, "C")
        
        AB_Probability,AB_Transformation,AB_Rotation,AB_TransformationVaule,\
          AB_RotationValue, B_populationChange = \
          mapTransitions(problem, A_attributes, B_attributes, "A", "B")
        AC_Probability,AC_Transformation,AC_Rotation,AC_TransformationVaule,\
          AC_RotationValue, C_populationChange = \
          mapTransitions(problem, A_attributes, C_attributes, "A", "C")
        
        nodes = populateSemanticNet(problem, A_attributes, B_attributes, C_attributes,\
                                    AB_Probability, AC_Probability,\
                                    AB_Transformation, AB_TransformationVaule,\
                                    AB_Rotation, AB_RotationValue,\
                                    AC_Transformation, AC_TransformationVaule,\
                                    AC_Rotation, AC_RotationValue)
        
        nodes = findAdditions(problem, nodes)

        bestProbability = 0
        selection = 1

        for optionNumber in range(1,7):
            probability = \
              compareOptions(problem, B_attributes, C_attributes, B_populationChange, \
                             C_populationChange, optionNumber, nodes)
        
            if bestProbability < probability:
                bestProbability = probability
                selection = optionNumber
        
        print selection, problem.checkAnswer(selection)
        print
        print
        return selection
    
def populateAttributes(problem, figureName):
    attributes = AttributeStruct()

    for objectName in problem.figures[figureName].objects:
        figureObject = problem.figures[figureName].objects[objectName]

        attributes.name.append(objectName)
        try:
            attributes.shape.append(figureObject.attributes["shape"])
        except KeyError:
            attributes.shape.append("unknown")
        try:
            attributes.fill.append(figureObject.attributes["fill"])
        except KeyError:
            attributes.fill.append("unknown")
        try:
            attributes.size.append(figureObject.attributes["size"])
        except KeyError:
            attributes.size.append("unknown")
        try:
            attributes.angle.append(int(figureObject.attributes["angle"]))
        except KeyError:
            attributes.angle.append(-1)
        try:
            attributes.inside.append(figureObject.attributes["inside"])
        except KeyError:
            attributes.inside.append("unknown")
        try:
            attributes.above.append(figureObject.attributes["above"])
        except KeyError:
            attributes.above.append("unknown")
        try:
            attributes.alignment.append(figureObject.attributes["alignment"])
        except KeyError:
            attributes.alignment.append("unknown")
        try:
            attributes.overlaps.append(figureObject.attributes["overlaps"])
        except KeyError:
            attributes.overlaps.append("unknown")
    return attributes


def mapTransitions(problem, firstAttributes, secondAttributes, firstFigureName, secondFigureName):
    probability = [[0 for x in range(10)] for x in range(10)] 
    transformation = [[0 for x in range(10)] for x in range(10)] 
    rotation = [[0 for x in range(10)] for x in range(10)] 
    transformationValue = [[0 for x in range(10)] for x in range(10)] 
    rotationValue = [[0 for x in range(10)] for x in range(10)] 

    populationChange = \
      len(problem.figures[secondFigureName].objects) - len(problem.figures[firstFigureName].objects)

    i = 0
    for firstObjectName in problem.figures[firstFigureName].objects:
        j = 0
        for secondObjectName in problem.figures[secondFigureName].objects:
            if (firstAttributes.shape[i] == secondAttributes.shape[j]):
                if (firstAttributes.fill[i] == secondAttributes.fill[j]) and\
                (firstAttributes.size[i] == secondAttributes.size[j]):
                    transformation[i][j] = "unchanged"
                    transformationValue[i][j] = 0
                    probability[i][j] = .9
                elif (firstAttributes.fill[i] == secondAttributes.fill[j]) and\
                (firstAttributes.size[i] != secondAttributes.size[j]):
                    transformation[i][j] = "size change"
                    probability[i][j] = .8
                elif (firstAttributes.fill[i] != secondAttributes.fill[j]) and\
                (firstAttributes.size[i] == secondAttributes.size[j]):
                    transformation[i][j] = "fill change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .7
                else:
                    transformation[i][j] = "size and fill change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .6
            else: 
                if(firstAttributes.fill[i] == secondAttributes.fill[j]) and\
                (firstAttributes.size[i] == secondAttributes.size[j]):
                    transformation[i][j] = "shape change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .5
                elif (firstAttributes.fill[i] != secondAttributes.fill[j]) and\
                (firstAttributes.size[i] == secondAttributes.size[j]):
                    transformation[i][j] = "shape and fill change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .4
                elif (firstAttributes.fill[i] == secondAttributes.fill[j]) and\
                (firstAttributes.size[i] != secondAttributes.size[j]):
                    transformation[i][j] = "shape and size change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .3
                else:
                    transformation[i][j] = "shape, fill and size change"
                    transformationValue[i][j] = 0
                    probability[i][j] = .1
                
            if (firstAttributes.angle[i] == secondAttributes.angle[j]) or\
            (firstAttributes.angle[i] == -1) or (secondAttributes.angle[j] == -1):
                rotation[i][j] = "unrotated"
                rotationValue[i][j] = 0
                probability[i][j] -= 0
            elif (abs(180 - firstAttributes.angle[i]) == abs(180 - secondAttributes.angle[j])):
                rotation[i][j] = "reflected"
                rotationValue[i][j] = 1 #horizontal
                probability[i][j] -= .03
            elif (abs(90 - firstAttributes.angle[i]) == abs(90 - secondAttributes.angle[j])) or\
            (abs(270 - firstAttributes.angle[i]) == abs(270 - secondAttributes.angle[j])):
                rotation[i][j] = "reflected"
                rotationValue[i][j] = 0 #vertical
                probability[i][j] -= .03
            else:
                rotation[i][j] = "rotated"
                rotationValue[i][j] = firstAttributes.angle[i] - secondAttributes.angle[j]
                probability[i][j] -= .06
               
            j += 1
        i += 1
        
    return probability, transformation, rotation, transformationValue, rotationValue, populationChange

def mapTranslations(problem, firstAttributes, secondAttributes, i, j, nodes):
    if (firstAttributes.inside[i] == secondAttributes.inside[j]) and\
    (firstAttributes.overlaps == secondAttributes.overlaps) and\
    (firstAttributes.above == secondAttributes.above):
        if (firstAttributes.alignment == secondAttributes.alignment):
            translation = "unmoved"
        else:
            translation = "alignment change"
    else:
        if (firstAttributes.alignment == secondAttributes.alignment):
            translation = "relationship changed"
        else:
            translation = "relationship and alignment changed"
    
    start = [0,0]
    end = [0,0]
    if (firstAttributes.alignment == "top") or\
      (firstAttributes.alignment == "top-center"):
        start = [1,0]
    if (firstAttributes.alignment == "top-right"):
        start = [2,0]
    if (firstAttributes.alignment == "top-left"):
        start = [0,0]
    if (firstAttributes.alignment == "bottom") or\
      (firstAttributes.alignment == "bottom-center"):
        start = [1,2]
    if (firstAttributes.alignment == "bottom-right"):
        start = [2,2]
    if (firstAttributes.alignment == "bottom-left"):
        start = [0,2]
    if (firstAttributes.alignment == "right") or\
      (firstAttributes.alignment == "center-right"):
        start = [2,1]
    if (firstAttributes.alignment == "left") or\
      (firstAttributes.alignment == "center-left"):
        start = [0,1]
    if (firstAttributes.alignment == "top") or\
      (firstAttributes.alignment == "top-center"):
        end = [1,0]
    if (firstAttributes.alignment == "top-right"):
        end = [2,0]
    if (firstAttributes.alignment == "top-left"):
        end = [0,0]
    if (firstAttributes.alignment == "bottom") or\
      (firstAttributes.alignment == "bottom-center"):
        end = [1,2]
    if (firstAttributes.alignment == "bottom-right"):
        end = [2,2]
    if (firstAttributes.alignment == "bottom-left"):
        end = [0,2]
    if (firstAttributes.alignment == "right") or\
      (firstAttributes.alignment == "center-right"):
        end = [2,1]
    if (firstAttributes.alignment == "left") or\
      (firstAttributes.alignment == "center-left"):
        end = [0,1]
    translationValue = [end[0] - start[0], end[1] - start[1]]

    return translation, translationValue

def populateSemanticNet(problem, A_attributes, B_attributes, C_attributes,\
                        AB_Probability, AC_Probability,\
                        AB_Transformation, AB_TransformationValue,\
                        AB_Rotation, AB_RotationValue,\
                        AC_Transformation, AC_TransformationValue,\
                        AC_Rotation, AC_RotationValue):
    nodes = []
    B_alreadyPicked = []
    C_alreadyPicked = []
    B_doOverList = []
    C_doOverList = []
    
    i=0
    for A_objectName in problem.figures["A"].objects:
        nodes.append(NodeStruct(i,0,0,\
                               0,0,\
                               A_objectName,"","",\
                               "","",0,0,\
                               "","",0,0,\
                               "","",0,0))

        genericNode = GenericNodeStruct(0, 0, "", "", 0, "", 0, "", 0)

        B_alreadyPicked, B_doOverList, nodes, genericNode = \
          FindLikeObjects(problem, i, AB_Probability, "B", AB_Transformation, \
                          AB_TransformationValue, AB_Rotation, AB_RotationValue, A_attributes,\
                          B_attributes, B_alreadyPicked, B_doOverList, nodes, genericNode)
        nodes[i].B_index = genericNode.index
        nodes[i].AB_probability = genericNode.probability
        nodes[i].B_objectName = genericNode.objectName
        nodes[i].AB_transition = genericNode.transition
        nodes[i].AB_transitionValue = genericNode.transitionValue
        nodes[i].AB_rotation = genericNode.rotation
        nodes[i].AB_rotationValue = genericNode.rotationValue
        nodes[i].AB_translation = genericNode.translation
        nodes[i].AB_translationValue = genericNode.translationValue

        genericNode = GenericNodeStruct(0, 0, "", "", 0, "", 0, "", 0)

        C_alreadyPicked, C_doOverList, nodes, genericNode = \
          FindLikeObjects(problem, i, AC_Probability, "C", AC_Transformation, \
                          AC_TransformationValue, AC_Rotation, AC_RotationValue, A_attributes,\
                          C_attributes, C_alreadyPicked, C_doOverList, nodes, genericNode)
        nodes[i].C_index = genericNode.index
        nodes[i].AC_probability = genericNode.probability
        nodes[i].C_objectName = genericNode.objectName
        nodes[i].AC_transition = genericNode.transition
        nodes[i].AC_transitionValue = genericNode.transitionValue
        nodes[i].AC_rotation = genericNode.rotation
        nodes[i].AC_rotationValue = genericNode.rotationValue
        nodes[i].AC_translation = genericNode.translation
        nodes[i].AC_translationValue = genericNode.translationValue

        i += 1
    
    for A_objectIndex in B_doOverList:
        genericNode = GenericNodeStruct(0, 0, "", "", 0, "", 0, "", 0)
        B_alreadyPicked, B_doOverList, nodes, genericNode = \
          FindLikeObjects(problem, A_objectIndex, AB_Probability, "B", AB_Transformation, \
                          AB_TransformationValue, AB_Rotation, AB_RotationValue, \
                          A_attributes, B_attributes, B_alreadyPicked, B_doOverList, nodes, genericNode)
        nodes[A_objectIndex].B_index = genericNode.index
        nodes[A_objectIndex].AB_probability = genericNode.probability
        nodes[A_objectIndex].B_objectName = genericNode.objectName
        nodes[A_objectIndex].AB_transition = genericNode.transition
        nodes[A_objectIndex].AB_transitionValue = genericNode.transitionValue
        nodes[A_objectIndex].AB_rotation = genericNode.rotation
        nodes[A_objectIndex].AB_rotationValue = genericNode.rotationValue
        nodes[A_objectIndex].AB_translation = genericNode.translation
        nodes[A_objectIndex].AB_translationValue = genericNode.translationValue
                       
    for A_objectIndex in C_doOverList:
        genericNode = GenericNodeStruct(0, 0, "", "", 0, "", 0, "", 0)
        C_alreadyPicked, B_doOverList, nodes, genericNode = \
          FindLikeObjects(problem, A_objectIndex, AC_Probability, "C", AC_Transformation, \
                          AC_TransformationValue, AC_Rotation, AC_RotationValue, \
                          C_attributes, C_attributes, C_alreadyPicked, C_doOverList, nodes, genericNode)
        nodes[A_objectIndex].C_index = genericNode.index
        nodes[A_objectIndex].AC_probability = genericNode.probability
        nodes[A_objectIndex].C_objectName = genericNode.objectName
        nodes[A_objectIndex].AC_transition = genericNode.transition
        nodes[A_objectIndex].AC_transitionValue = genericNode.transitionValue
        nodes[A_objectIndex].AC_rotation = genericNode.rotation
        nodes[A_objectIndex].AC_rotationValue = genericNode.rotationValue
        nodes[A_objectIndex].AC_translation = genericNode.translation
        nodes[A_objectIndex].AC_translationValue = genericNode.translationValue
          

    return nodes

def FindLikeObjects(problem, i, probability, figureName, transformation, \
                    transformationValue, rotation, rotationValue, firstAttributes,\
                    secondAttributes, alreadyPicked, doOverList, nodes, genericNode):
    j=0
    tempProbability = -1
    tempIndex = -1
    tempObjectName = ""
    tempTransition = ""
    tempTransitionValue = -1
    tempRotation = ""
    tempRotationValue = -1
    tempTranslation = ""
    tempTranslationValue = -1
    
    for objectName in problem.figures[figureName].objects:
        if probability[i][j] > tempProbability:
            tempProbability = probability[i][j]
            tempIndex = j
            tempObjectName = objectName
            tempTransition = transformation[i][j]
            tempTransitionValue = transformationValue[i][j]
            tempRotation = rotation[i][j]
            tempRotationValue = rotationValue[i][j]
            tempTranslation, tempTranslationValue = \
              mapTranslations(problem, firstAttributes, secondAttributes, i, j, nodes)
        
            if objectName in alreadyPicked:
                k=0
                m=0
                for node in nodes:
                    if node.B_objectName == objectName:
                        m=k
                    k += 1
                if nodes[m].AB_probability < tempProbability:
                    doOverList.append(nodes[m].A_index)
                    genericNode.probability = tempProbability
                    genericNode.index = tempIndex
                    genericNode.objectName = tempObjectName
                    genericNode.transition = tempTransition
                    genericNode.transitionValue = tempTransitionValue
                    genericNode.rotation = tempRotation
                    genericNode.rotationValue = tempRotationValue
                    genericNode.translation = tempTranslation
                    genericNode.translationValue = tempTranslationValue
                else:
                    tempProbability = genericNode.probability
            else:
                genericNode.probability = tempProbability
                genericNode.index = tempIndex
                genericNode.objectName = tempObjectName
                genericNode.transition = tempTransition
                genericNode.transitionValue = tempTransitionValue
                genericNode.rotation = tempRotation
                genericNode.rotationValue = tempRotationValue
                genericNode.translation = tempTranslation
                genericNode.translationValue = tempTranslationValue
            
        j += 1
    
    alreadyPicked.append(genericNode.objectName)
        
    return alreadyPicked, doOverList, nodes, genericNode

def findAdditions(problem, nodes):
        #find the additions in B
    i = 0
    for objectName in problem.figures["B"].objects:
        k=0
        m=0
        objectHasMatch = 0
        for node in nodes:
            if node.B_objectName == objectName:
                m=k
                objectHasMatch = 1
            k += 1
            
        if objectHasMatch == 0:
            nodes.append(NodeStruct(-1,i,-1,\
                         -1,-1,\
                         "",objectName,"",\
                         "addition","",0,0,\
                         "unrotated","",0,0,\
                         "unmoved","",0,0))
        i += 1

    #find the additions in C
    i = 0
    for objectName in problem.figures["C"].objects:
        k=0
        m=0
        objectHasMatch = 0
        for node in nodes:
            if node.C_objectName == objectName:
                m=k
                objectHasMatch = 1
            k += 1
            
        if objectHasMatch == 0:
            nodes.append(NodeStruct(-1,-1,i,\
                         -1,-1,\
                         "","",objectName,\
                         "","addition",0,0,\
                         "","unrotated",0,0,\
                         "","unmoved",0,0))
        i += 1

    return nodes


def compareOptions(problem, B_attributes, C_attributes, B_populationChange, \
                   C_populationChange, optionNumber, nodes):
    certainty = 0
    
    O_attributes = populateAttributes(problem, str(optionNumber))
    
    tempProbability, tempTransformation, tempRotation, tempTrasformationValue, \
      tempRotationValue, O_populationChange =\
      mapTransitions(problem, B_attributes, O_attributes, "B", str(optionNumber))
    BO_Probability = tempProbability
    BO_Transition = tempTransformation
    BO_TransitionValue = tempTrasformationValue
    BO_Rotation = tempRotation
    BO_RotationValue = tempRotationValue
    
    if (O_populationChange == C_populationChange):
        certainty += .2
        
    alreadyPicked = []
    i=0
    for B_objectName in problem.figures["B"].objects:
        j=0
        probability = 0
        for O_objectName in problem.figures[str(optionNumber)].objects:
            if BO_Probability[i][j] > probability:
                probability = BO_Probability[i][j]
                O_index = j
                bestObjectName = B_objectName
                bestOptionObjectName = O_objectName
                bestTransition = BO_Transition[i][j]
                bestTransitionValue = BO_TransitionValue[i][j]
                bestRotation = BO_Rotation[i][j]
                bestRotationValue = BO_RotationValue[i][j]
                bestTranslation, bestTranslationValue = \
                  mapTranslations(problem, B_attributes, O_attributes, i, j, nodes)
            j += 1
        
        k=0
        m=0
        for node in nodes:
            if node.B_objectName == B_objectName:
                m=k
            k += 1
        
        if bestTransition == nodes[m].AC_transition:
            certainty += .2
            if bestTransitionValue == nodes[m].AC_transitionValue:
                certainty += .1
        if bestRotation == nodes[m].AC_rotation:
            certainty += .1
            if bestRotationValue == nodes[m].AC_rotationValue:
                certainty += .1
        if bestTranslation == nodes[m].AC_translation:
            certainty += .1
            if bestTranslationValue == nodes[m].AC_translationValue:
                certainty += .1
        
        alreadyPicked.append(bestOptionObjectName)
        i += 1

    newShape = ""
    for node in nodes:
        if node.A_index == -1:
            if node.C_index != -1:
                for C_objectName in problem.figures["C"].objects:
                    thisObject = problem.figures["C"].objects[C_objectName]
                    if C_objectName == node.C_objectName:
                        newShape = thisObject.attributes["shape"]
                        newShapeSize = thisObject.attributes["size"]
                        newShapeFill = thisObject.attributes["fill"]
                        
    for objectName in problem.figures[str(optionNumber)].objects:
        thisObject = problem.figures[str(optionNumber)].objects[objectName]
        if objectName not in alreadyPicked:
            if thisObject.attributes["shape"] == newShape:
                certainty += .1
                if thisObject.attributes["size"] == newShapeSize:
                    certainty += .1
                if thisObject.attributes["fill"] == newShapeFill:
                    certainty += .1

    
    tempProbability, tempTransformation, tempRotation, tempTrasformationValue, \
      tempRotationValue, O_populationChange = \
      mapTransitions(problem, C_attributes, O_attributes, "C", str(optionNumber))
    CO_Probability = tempProbability
    CO_Transition = tempTransformation
    CO_TransitionValue = tempTrasformationValue
    CO_Rotation = tempRotation
    CO_RotationValue = tempRotationValue
    
    if (O_populationChange == B_populationChange):
        certainty += .2

    i=0
    probability = 0
    for C_objectName in problem.figures["C"].objects:
        j=0
        for O_objectName in problem.figures[str(optionNumber)].objects:
            if CO_Probability[i][j] > probability:
                probability = CO_Probability[i][j]
                O_index = j
                bestobjectName = C_objectName
                bestTransition = CO_Transition[i][j]
                bestTransitionValue = CO_TransitionValue[i][j]
                bestRotation = CO_Rotation[i][j]
                bestRotationValue = CO_RotationValue[i][j]
                bestTranslation, bestTranslationValue = \
                  mapTranslations(problem, C_attributes, O_attributes, i, j, nodes)
            j += 1
        
        k=0
        m=0
        for node in nodes:
            if node.C_objectName == C_objectName:
                m=k
            k += 1
        
        if bestTransition == nodes[m].AB_transition:
            certainty += .1
            if bestTransitionValue == nodes[m].AB_transitionValue:
                certainty += .1
        if bestRotation == nodes[m].AB_rotation:
            certainty += .1
            if bestRotationValue == nodes[m].AB_rotationValue:
                certainty += .1
        if bestTranslation == nodes[m].AB_translation:
            certainty += .1
            if bestTranslationValue == nodes[m].AB_translationValue:
                certainty += .1

        i += 1
                        
    newShape = ""
    for node in nodes:
        if node.A_index == -1:
            if node.B_index != -1:
                for B_objectName in problem.figures["B"].objects:
                    thisObject = problem.figures["B"].objects[B_objectName]
                    if B_objectName == node.B_objectName:
                        newShape = thisObject.attributes["shape"]
                        print newShape

    for objectName in problem.figures[str(optionNumber)].objects:
        thisObject = problem.figures[str(optionNumber)].objects[objectName]
        if objectName not in alreadyPicked:
            if thisObject.attributes["shape"] == newShape:
                certainty += .2

    return certainty


class AttributeStruct():
    def __init__(self):
        self.name = []
        self.shape = []
        self.fill = []
        self.size = []
        self.angle = []
        self.inside = []
        self.above = []
        self.alignment = []
        self.overlaps = []
        
class NodeStruct():
    def __init__(self,A_index,B_index,C_index,\
                 AB_probability,AC_probability,\
                 A_objectName,B_objectName,C_objectName,\
                 AB_transition,AC_transition,AB_transitionValue,AC_transitionValue,\
                 AB_rotation,AC_rotation,AB_rotationValue,AC_rotationValue,\
                 AB_translation,AC_translation,AB_translationValue,AC_translationValue):
        self.A_index = A_index
        self.B_index = B_index
        self.C_index = C_index
        self.AB_probability = AB_probability
        self.AC_probability = AC_probability
        self.A_objectName = A_objectName
        self.B_objectName = B_objectName
        self.C_objectName = C_objectName
        self.AB_transition = AB_transition
        self.AC_transition = AC_transition
        self.AB_transitionValue = AB_transitionValue
        self.AC_transitionValue = AC_transitionValue
        self.AB_rotation = AB_rotation
        self.AC_rotation = AC_rotation
        self.AB_rotationValue = AB_rotationValue
        self.AC_rotationValue = AC_rotationValue
        self.AB_translation = AB_translation
        self.AC_translation = AC_translation
        self.AB_translationValue = AB_translationValue
        self.AC_translationValue = AC_translationValue
        
class GenericNodeStruct():
    def __init__(self,index, probability, objectName, transition,transitionValue,\
                 rotation,rotationValue, translation,translationValue):
        self.index = index
        self.probability = probability
        self.objectName = objectName
        self.transition = transition
        self.transitionValue = transitionValue
        self.rotation = rotation
        self.rotationValue = rotationValue
        self.translation = translation
        self.translationValue = translationValue
        
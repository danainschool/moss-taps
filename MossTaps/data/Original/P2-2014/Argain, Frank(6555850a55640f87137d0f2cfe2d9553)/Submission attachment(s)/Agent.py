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
        
        attributes = {}
        relationships = {}
        nodes = {}
        
        if problem.problemType == "2x2":
            neighbors = {'A':['B','C'],'B':['1','2','3','4','5','6'],\
                         'C':['1','2','3','4','5','6']}
            options = ['1','2','3','4','5','6']
            equivalentTranformations = {'B':[('A','C')], 'C':[('A','B')]}
            optionNeighbors = ["B","C"]
        else:
            neighbors = {'A':['B','D','C','G'],'B':['C','E'],'C':['F','1','2','3','4','5','6','7','8'],\
                         'D':['E','G'],'E':['F','H'],'F':['1','2','3','4','5','6','7','8'],\
                         'G':['H','1','2','3','4','5','6','7','8'],'H':['1','2','3','4','5','6','7','8'],}
            options = ['1','2','3','4','5','6','7','8']
            equivalentTranformations = {'F':[('E','H'),('C','F'),('D','G')], 'H':[('E','F'),('G','H'),('B','C')],\
                                        'C':[('A','G')], 'G':[('A','C')]}
#            equivalentTranformations = {'F':[('E','H'),('D','G')], 'H':[('E','F'),('B','C')]}
            optionNeighbors = ["F","H", "C", "G"]
        
        #populate attrubutes and define relationships between neighbors to the solution options
        for figureName in problem.figures:
            attributes = populateAttributes(problem, figureName, attributes)
            for optionNeighbor in optionNeighbors:
                if figureName in neighbors[optionNeighbor]:
                    relationships[(optionNeighbor,figureName)] = \
                      mapTranformations(problem, attributes, optionNeighbor, figureName)
                    relationships[(optionNeighbor,figureName)] = \
                      mapTranslations(problem, attributes, relationships[(optionNeighbor,figureName)],\
                                      optionNeighbor, figureName)
        
        #populate relationships
        for optionNeighbor in optionNeighbors:
            for equivalentTranformation in equivalentTranformations[optionNeighbor]:
                equivalentFigure = equivalentTranformation[0]
                relationships[(equivalentFigure, optionNeighbor)] = \
                  mapTranformations(problem, attributes, equivalentFigure, optionNeighbor)
                relationships[(equivalentFigure, optionNeighbor)] = \
                  mapTranslations(problem, attributes, relationships[(equivalentFigure, optionNeighbor)],\
                                  equivalentFigure, optionNeighbor)
        
        #populate relationships
        for figureName in problem.figures:
            if figureName not in options:
                for neighbor in neighbors[figureName]:
                    relationships[(figureName, neighbor)] = \
                      mapTranformations(problem, attributes, figureName, neighbor)
                    relationships[figureName,neighbor] = \
                      mapTranslations(problem, attributes, relationships[(figureName,neighbor)],\
                                      figureName, neighbor)

        #populate Semantic Net with likely equivalent objects in neighbor figures
        for optionNeighbor in optionNeighbors:
            for equivalentTranformation in equivalentTranformations[optionNeighbor]:
                equivalentFigure = equivalentTranformation[0]
                nodes[equivalentFigure, optionNeighbor] = \
                  populateSemanticNet(problem, attributes,\
                                      relationships[equivalentFigure, optionNeighbor],\
                                      equivalentFigure, optionNeighbor)
        
        #populate Semantic Net with likely equivalent objects in neighbor figures
        for figureName in problem.figures:
            if figureName not in options:
                for neighborFigure in neighbors[figureName]:
                    nodes[(figureName, neighborFigure)] = \
                      populateSemanticNet(problem, attributes,\
                                          relationships[(figureName,neighborFigure)],\
                                          figureName, neighborFigure)
                      
        bestLikelihood = -99.
        selection = 1

        #cycle through all options and choose the option with the highest score
        for optionNumber in options:
            likelihood = 0

            for optionNeighbor in optionNeighbors:
                for equivalentTranformation in equivalentTranformations[optionNeighbor]:
                    likelihood += \
                      compareOptions(problem, attributes, relationships, optionNumber, nodes,\
                                     equivalentTranformation, optionNeighbor)
              
            likelihood += \
              findPopulationChange(problem, equivalentTranformations, optionNumber,\
                                   optionNeighbors)
              
            likelihood += \
              findFillChange(problem, attributes, equivalentTranformations, optionNumber, optionNeighbors, neighbors)
              
            if bestLikelihood < likelihood:
                bestLikelihood = likelihood
                selection = optionNumber
                
        normalizedLikelihood = bestLikelihood / (len(problem.figures[str(selection)].objects)+1)
        
        print selection, "(", normalizedLikelihood,")", problem.checkAnswer(selection)
#        print selection
#        print
        print
        return selection
    
def populateAttributes(problem, figureName, attributes):
    sizeList = {"huge":6,"very large":5,"large":4,"medium":3,"small":2,"very small":1}
    
    for objectName in problem.figures[figureName].objects:
        figureObject = problem.figures[figureName].objects[objectName]

        objectAttributes = attributeStruct("","",0,0,0,0,"","","","")
        
        try:
            objectAttributes.shape = figureObject.attributes["shape"]
        except KeyError:
            objectAttributes.shape = "unknown"
        try:
            objectAttributes.fill = figureObject.attributes["fill"]
        except KeyError:
            objectAttributes.fill = "unknown"
        try:
            objectAttributes.size = sizeList[figureObject.attributes["size"]]
        except KeyError:
            objectAttributes.size = -1
        try:
            objectAttributes.height = sizeList[figureObject.attributes["height"]]
        except KeyError:
            objectAttributes.height = -1
        try:
            objectAttributes.width = sizeList[figureObject.attributes["width"]]
        except KeyError:
            objectAttributes.width = -1
        try:
            objectAttributes.angle = int(figureObject.attributes["angle"])
        except KeyError:
            objectAttributes.angle = -1
        try:
            objectAttributes.inside = figureObject.attributes["inside"]
        except KeyError:
            objectAttributes.inside = ""
        try:
            objectAttributes.above = figureObject.attributes["above"]
        except KeyError:
            objectAttributes.above = ""
        try:
            objectAttributes.leftOf = figureObject.attributes["left-of"]
        except KeyError:
            objectAttributes.leftOf = ""
        try:
            objectAttributes.overlaps = figureObject.attributes["overlaps"]
        except KeyError:
            objectAttributes.overlaps = ""
        try:
            objectAttributes.alignment = figureObject.attributes["alignment"]
        except KeyError:
            objectAttributes.alignment = "unknown"
        
        if objectAttributes.shape == "square":
            objectAttributes.shape = "rectangle"
            objectAttributes.size = -1
            objectAttributes.width = sizeList[figureObject.attributes["size"]]
            objectAttributes.height = sizeList[figureObject.attributes["size"]]
            
        attributes[(figureName,objectName)] = objectAttributes
            
    return attributes


def mapTranformations(problem, attributes, firstFigureName, secondFigureName):
    transformations = {}

    for firstObjectName in problem.figures[firstFigureName].objects:
        for secondObjectName in problem.figures[secondFigureName].objects:
            transformation = relationshipStruct(0, "", 0, "", 0, "", 0, [], 0)
            
            sizeChange = \
              calculateSizeChange(attributes[firstFigureName,firstObjectName], \
                                  attributes[secondFigureName,secondObjectName])
            
            if (attributes[firstFigureName,firstObjectName].shape == \
                attributes[secondFigureName,secondObjectName].shape):
                if (attributes[firstFigureName,firstObjectName].fill == \
                    attributes[secondFigureName,secondObjectName].fill) and\
                  (sizeChange == 0):
                    transformation.transformation = "unchanged"
                    transformation.transformationValue = 0
                    transformation.probability = 0.9
                elif (attributes[firstFigureName,firstObjectName].fill == \
                      attributes[secondFigureName,secondObjectName].fill) and\
                  (sizeChange != 0):
                    transformation.transformation = "size change"
                    transformation.transformationValue = sizeChange
                    transformation.probability = 0.8
                elif (attributes[firstFigureName,firstObjectName].fill != \
                      attributes[secondFigureName,secondObjectName].fill) and\
                  (sizeChange == 0):
                    transformation.transformation = "fill change"
                    if attributes[firstFigureName,firstObjectName].fill == "yes":
                        transformation.transformationValue = 1
                    else:
                        transformation.transformationValue = 0
                    transformation.probability = 0.7
                else:
                    transformation.transformation = "size and fill change"
                    transformation.transformationValue = sizeChange
                    transformation.probability = 0.6
            else: 
                if(attributes[firstFigureName,firstObjectName].fill == \
                   attributes[secondFigureName,secondObjectName].fill) and\
                (sizeChange == 0):
                    transformation.transformation = "shape change"
                    transformation.transformationValue = 0
                    transformation.probability = 0.5
                elif (attributes[firstFigureName,firstObjectName].fill != \
                      attributes[secondFigureName,secondObjectName].fill) and\
                (sizeChange == 0):
                    transformation.transformation = "shape and fill change"
                    if attributes[firstFigureName,firstObjectName].fill == "yes":
                        transformation.transformationValue = 1
                    else:
                        transformation.transformationValue = 0
                    transformation.probability = 0.4
                elif (attributes[firstFigureName,firstObjectName].fill == \
                      attributes[secondFigureName,secondObjectName].fill) and\
                (sizeChange != 0):
                    transformation.transformation = "shape and size change"
                    transformation.transformationValue = sizeChange
                    transformation.probability = 0.3
                else:
                    transformation.transformation = "shape fill and size change"
                    transformation.transformationValue = sizeChange
                    transformation.probability = 0.1
                
            if (attributes[firstFigureName,firstObjectName].angle == \
                attributes[secondFigureName,secondObjectName].angle) or\
            (attributes[firstFigureName,firstObjectName].angle == -1) or \
            (attributes[secondFigureName,secondObjectName].angle == -1):
                transformation.rotation = "unrotated"
                transformation.rotationValue = 0
                transformation.probability -= 0
            elif (abs(180 - attributes[firstFigureName,firstObjectName].angle) == \
                  abs(180 - attributes[secondFigureName,secondObjectName].angle)):
                transformation.rotation = "reflected"
                transformation.rotationValue = 1
                transformation.probability -= 0.03
            elif (abs(90 - attributes[firstFigureName,firstObjectName].angle) == \
                  abs(90 - attributes[secondFigureName,secondObjectName].angle)) or\
            (abs(270 - attributes[firstFigureName,firstObjectName].angle) == \
             abs(270 - attributes[secondFigureName,secondObjectName].angle)):
                transformation.rotation = "reflected"
                transformation.rotationValue = 0
                transformation.probability -= 0.03
            else:
                transformation.rotation = "rotated"
                transformation.rotationValue = \
                  attributes[firstFigureName,firstObjectName].angle - \
                    attributes[secondFigureName,secondObjectName].angle
                transformation.probability -= 0.06
                
            transformations[(firstObjectName, secondObjectName)] = transformation
        
    return transformations

def mapTranslations(problem, attributes, relationship, figureName, neighborFigure):
    for firstObjectName in problem.figures[figureName].objects:
        for secondObjectName in problem.figures[neighborFigure].objects:
            if attributes[figureName,firstObjectName].alignment == \
               attributes[neighborFigure,secondObjectName].alignment:
                relationship[firstObjectName, secondObjectName].translation = "unmoved"
                relationship[firstObjectName, secondObjectName].translationValue = [0,0]
            else:
                relationship[firstObjectName, secondObjectName].translation = "moved"
                relationship[firstObjectName, secondObjectName].translationValue = \
                  calculatePositionChange(attributes[figureName,firstObjectName].alignment,\
                                          attributes[neighborFigure,secondObjectName].alignment)

            relationship[firstObjectName, secondObjectName].relativePosition = []

            difference = compareNumberOfRelations(problem, attributes, \
                                                  attributes[figureName,firstObjectName].inside,\
                                                  attributes[neighborFigure,secondObjectName].inside)
            if difference == 0:
                relationship[firstObjectName, secondObjectName].relativePosition.append("insideUnchanged")
                relationship[firstObjectName, secondObjectName].probability += 0.1
            elif difference == 1:
                relationship[firstObjectName, secondObjectName].relativePosition.append("insideChange")
                relationship[firstObjectName, secondObjectName].probability += 0.05
            else:
                relationship[firstObjectName, secondObjectName].relativePosition.append("insideChange")
            
            difference = compareNumberOfRelations(problem, attributes, \
                                                  attributes[figureName,firstObjectName].overlaps,\
                                                  attributes[neighborFigure,secondObjectName].overlaps)
            if difference == 0:
                relationship[firstObjectName, secondObjectName].relativePosition.append("overlapUnchanged")
                relationship[firstObjectName, secondObjectName].probability += 0.1
            elif difference == 1:
                relationship[firstObjectName, secondObjectName].relativePosition.append("overlapChange")
                relationship[firstObjectName, secondObjectName].probability += 0.05
            else:
                relationship[firstObjectName, secondObjectName].relativePosition.append("overlapChange")
                  
            difference = compareNumberOfRelations(problem, attributes, \
                                                  attributes[figureName,firstObjectName].above,\
                                                  attributes[neighborFigure,secondObjectName].above)
            if difference == 0:
                relationship[firstObjectName, secondObjectName].relativePosition.append("aboveUnchanged")
                relationship[firstObjectName, secondObjectName].probability += 0.4
            elif difference == 1:
                relationship[firstObjectName, secondObjectName].relativePosition.append("aboveChange")
                relationship[firstObjectName, secondObjectName].probability += 0.2
            else:
                relationship[firstObjectName, secondObjectName].relativePosition.append("aboveChange")
                  
            difference = compareNumberOfRelations(problem, attributes, \
                                                  attributes[figureName,firstObjectName].leftOf,\
                                                  attributes[neighborFigure,secondObjectName].leftOf)
            if difference == 0:
                relationship[firstObjectName, secondObjectName].relativePosition.append("leftOfUnchanged")
                relationship[firstObjectName, secondObjectName].probability += 0.4
            elif difference == 1:
                relationship[firstObjectName, secondObjectName].relativePosition.append("leftOfChange")
                relationship[firstObjectName, secondObjectName].probability += 0.2
            else:
                relationship[firstObjectName, secondObjectName].relativePosition.append("leftOfChange")

    return relationship

def compareNumberOfRelations(problem, attributes, firstAttribute, secondAttribute):
    tempList1 = firstAttribute.split(",")
    tempList2 = secondAttribute.split(",")
    
    if tempList1 == ['']:
        length1 = 0
    else:
        length1 = len(tempList1)
        
    if tempList2 == ['']:
        length2 = 0
    else:
        length2 = len(tempList2)
    
    difference = abs(length1 - length2)
 
    return difference

def calculateSizeChange(firstObjectAttributes, secondObjectAttributes):
    sizeChange = secondObjectAttributes.size - firstObjectAttributes.size
    widthChange = secondObjectAttributes.width - firstObjectAttributes.width
    heightChange = secondObjectAttributes.height - firstObjectAttributes.height
    
    return sizeChange + widthChange + heightChange

def calculatePositionChange(firstPosition, secondPosition):
    alignmentList = {"top": [1,0], "top-center": [1,0], "top-right":[2,0], "top-left":[0,0],\
                     "bottom":[1,2], "bottom-center":[1,2], "bottom-right":[2,2], "bottom-left":[0,2],\
                     "right":[2,1], "center-right":[2,1], "left":[0,1], "center-left":[0,1],\
                     "unknown": [1,1]}

    return alignmentList[secondPosition][0] - alignmentList[firstPosition][0], \
           alignmentList[secondPosition][1] - alignmentList[firstPosition][1]
        
def populateSemanticNet(problem, attributes, relationships, figureName, neighborFigure):
    alreadyPickedList = []
    unmatchable = []
    figureNodes = {}
    unmatchedList = []
    for objectName in problem.figures[figureName].objects:
        unmatchedList.append(objectName)
    
    while len(unmatchedList) > 0:
        newUnmatchedList = deepcopy(unmatchedList)
        for objectName in unmatchedList:
            figureNodes[objectName] = nodeStruct("", 0)
            alreadyPickedList, newUnmatchedList, unmatchable, figureNodes = \
              findLikeObjects(problem, objectName, neighborFigure, relationships, \
                              alreadyPickedList, newUnmatchedList, unmatchable, figureNodes)
        unmatchedList = deepcopy(newUnmatchedList)
        
    return figureNodes

def findLikeObjects(problem, objectName, neighborFigure, relationships, alreadyPickedList,\
                    unmatchedList, unmatchable, figureNodes):
    tempNode = nodeStruct("",0)
    
    for neighborObject in problem.figures[neighborFigure].objects:
        if relationships[(objectName,neighborObject)].probability > tempNode.probability:
            tempNode.matchName = neighborObject
            tempNode.probability = relationships[(objectName,neighborObject)].probability
            if neighborObject in alreadyPickedList:
                otherObject = findOtherObject(figureNodes, neighborObject)
                if otherObject == "":
                    figureNodes[objectName] = deepcopy(tempNode)
                elif tempNode.probability > figureNodes[otherObject].probability:
                    unmatchedList.append(otherObject)
                    figureNodes[objectName] = deepcopy(tempNode)
                else:
                    tempNode.probability = figureNodes[objectName].probability
            else:
                figureNodes[objectName] = deepcopy(tempNode)

    unmatchedList.remove(objectName)
    if figureNodes[objectName].probability > 0:
        alreadyPickedList.append(figureNodes[objectName].matchName)
    else:
        unmatchable.append(objectName)
    
    return alreadyPickedList, unmatchedList, unmatchable, figureNodes

def findOtherObject(nodes, neighborObject):
    otherObject = ""
    
    for nodeName in nodes:
        node = nodes[nodeName]
        if node.matchName == neighborObject:
            otherObject = nodeName
            
    return otherObject

def compareOptions(problem, attributes, relationships, optionNumber, nodes,\
                  equivalentTranformation, figureName):
    likelihood = 0
    
    #cycle through the objects
    for objectName in problem.figures[figureName].objects:
        if nodes[figureName, optionNumber][objectName].matchName != "":
            optionRelationship = \
              relationships[figureName, optionNumber]\
                           [(objectName,nodes[figureName, optionNumber][objectName].matchName)]
            equivalentObjectName =\
              findEquivalentObject(problem, nodes, equivalentTranformation, figureName, objectName)
            if (equivalentObjectName != "") and \
               (nodes[equivalentTranformation][equivalentObjectName].matchName != ""):
                equivalentRelationship = \
                  relationships[equivalentTranformation]\
                               [(equivalentObjectName,\
                                 nodes[equivalentTranformation][equivalentObjectName].matchName)]
                
                if optionRelationship.transformation == equivalentRelationship.transformation:
                    likelihood += 0.1
                    if optionRelationship.transformationValue == equivalentRelationship.transformationValue:
                        likelihood += 0.1
                if optionRelationship.rotation == equivalentRelationship.rotation:
                    likelihood += 0.1
                    if optionRelationship.rotationValue == equivalentRelationship.rotationValue:
                        likelihood += 0.1
                if optionRelationship.translation == equivalentRelationship.translation:
                    likelihood += 0.1
                    if optionRelationship.translationValue == equivalentRelationship.translationValue:
                        likelihood += 0.1
                for relativePositionChange in optionRelationship.relativePosition:
                    if relativePositionChange in equivalentRelationship.relativePosition:
                        likelihood += 0.1
#                    if optionRelationship.relativePositionValue == equivalentRelationship.relativePositionValue:
#                        likelihood += 0.1
                                    
    likelihood += findAdditions(problem, nodes, optionNumber, equivalentTranformation, figureName)
                    
    return likelihood

def findAdditions(problem, nodes, optionNumber, equivalentTranformation, figureName):
    likelihood = 0
    deletion = 0

#    find any additions and see in they're in the option
    for objectName in problem.figures[equivalentTranformation[1]].objects:
        matchFound = 0
        for otherObjectName in problem.figures[equivalentTranformation[0]].objects:
            if nodes[equivalentTranformation][otherObjectName].matchName == objectName:
                matchFound = 1
            elif nodes[equivalentTranformation][otherObjectName].matchName == "":
                deletion += 1
                for optionObjectName in problem.figures[optionNumber].objects:
                    if problem.figures[optionNumber].objects[optionObjectName].attributes["shape"] == \
                       problem.figures[equivalentTranformation[1]].objects[objectName].attributes["shape"]:
                        likelihood -= 0.1
                        if problem.figures[optionNumber].objects[optionObjectName].attributes["fill"] == \
                           problem.figures[equivalentTranformation[1]].objects[objectName].attributes["fill"]:
                            likelihood -= 0.1
                            if problem.figures[optionNumber].objects[optionObjectName].attributes["size"] == \
                               problem.figures[equivalentTranformation[1]].objects[objectName].attributes["size"]:
                                likelihood -= 0.1
                
        if matchFound == 0:
            addedObject = ""
            for optionObjectName in problem.figures[optionNumber].objects:
                try:
                    optionObjectMatch = \
                      nodes[equivalentTranformation[1], optionNumber][optionObjectName].matchName
                except KeyError:
                    optionObjectMatch = ""
                if optionObjectMatch == objectName:
                    addedObject = optionObjectName
            if addedObject != "":
                if problem.figures[optionNumber].objects[addedObject].attributes["shape"] == \
                   problem.figures[equivalentTranformation[1]].objects[objectName].attributes["shape"]:
                    likelihood += 0.1
                    if problem.figures[optionNumber].objects[addedObject].attributes["fill"] == \
                       problem.figures[equivalentTranformation[1]].objects[objectName].attributes["fill"]:
                        likelihood += 0.1
                        if problem.figures[optionNumber].objects[addedObject].attributes["size"] == \
                           problem.figures[equivalentTranformation[1]].objects[objectName].attributes["size"]:
                            likelihood += 0.1

    return likelihood

def findEquivalentObject(problem, nodes, equivalentTranformation, figureName, objectName):
    equivalentObjectName = ""
    
    for parentObjectName in problem.figures[equivalentTranformation[0]].objects:
        if nodes[equivalentTranformation[0],figureName][parentObjectName].matchName == objectName:
            equivalentObjectName = parentObjectName
        
    return equivalentObjectName

def findPopulationChange(problem, equivalentTranformations, optionNumber, optionNeighbors):
    likelihood = 0
    
    firstFigureName = optionNeighbors[0]
    secondFigureName = optionNeighbors[1]
    
    for firstEquivalentTranformation in equivalentTranformations[firstFigureName]:
        for secondEquivalentTranformation in equivalentTranformations[secondFigureName]:
            firstEquivalentPopulationChange = \
              len(problem.figures[firstEquivalentTranformation[1]].objects) - \
                len(problem.figures[firstEquivalentTranformation[0]].objects)
            secondEquivalentPopulationChange = \
              len(problem.figures[secondEquivalentTranformation[1]].objects) - \
                len(problem.figures[secondEquivalentTranformation[0]].objects)
            optionPopulationChange = \
              (len(problem.figures[optionNumber].objects) - len(problem.figures[firstFigureName].objects)) + \
              (len(problem.figures[optionNumber].objects) - len(problem.figures[secondFigureName].objects))
            if abs(firstEquivalentPopulationChange + secondEquivalentPopulationChange - optionPopulationChange) == 0:
                likelihood += 0.4
            elif abs(firstEquivalentPopulationChange + secondEquivalentPopulationChange - optionPopulationChange) <= 1:
                likelihood += 0.2
            elif abs(firstEquivalentPopulationChange + secondEquivalentPopulationChange - optionPopulationChange) <= 2:
                likelihood += 0.1
            elif abs(firstEquivalentPopulationChange + secondEquivalentPopulationChange - optionPopulationChange) <= 3:
                likelihood += 0.05
    
    return likelihood

def findFillChange(problem, attributes, equivalentTranformations, optionNumber, optionNeighbors, neighbors):
    likelihood = 0
    fillCount = {}
    fillChange = {}
    
    for figureName in problem.figures:
        fillCount[figureName] = 0
        for objectName in problem.figures[figureName].objects:
            if attributes[figureName,objectName].fill == "yes":
                fillCount[figureName] += 1

    for figureName in neighbors:
        for neighbor in neighbors[figureName]:
            fillChange[figureName, neighbor] = fillCount[neighbor] - fillCount[figureName]

    for neighbor in optionNeighbors:
        for equivalentTranformation in equivalentTranformations[neighbor]:
            if fillChange[equivalentTranformation] == fillChange[neighbor, optionNumber]:
                likelihood += .25
            
    return likelihood
    
class attributeStruct():
    def __init__(self, shape, fill, size, width, height, angle, inside, above, alignment, overlaps):
        self.shape = shape
        self.fill = fill
        self.size = size
        self.width = width
        self.height = height
        self.angle = angle
        self.inside = inside
        self.above = above
        self.alignment = alignment
        self.overlaps = overlaps

class relationshipStruct():
    def __init__(self, probability, transformation, transformationValue,\
                 rotation,rotationValue, translation,translationValue,\
                 relativePosition, relativePositionValue):
        self.probability = probability
        self.transformation = transformation
        self.transformationValue = transformationValue
        self.rotation = rotation
        self.rotationValue = rotationValue
        self.translation = translation
        self.translationValue = translationValue
        self.relativePosition = relativePosition
        self.relativePositionValue = relativePositionValue

class nodeStruct():
    def __init__(self, matchName, probability):
        self.matchName = matchName
        self.probability = probability
        
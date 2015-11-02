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
from PIL import Image, ImageFilter
from _ast import Num
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
        #if no answer is created... then -1
        answer = -1
        self.problem = problem
        #define dataspace for figures, differences and the sum of changes
        self.figures = {'A':[],'B':[],'C':[],'1':[],'2':[],'3':[],'4':[],'5':[],'6':[],'7':[],'8':[]}
        self.deltaAB = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
        self.deltaAC = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
        deltaSUM = {}
        #shall we dance, agent knows its too dumb for a 3x3 right now
        if problem.problemType == "2x2":
            #if the problem has verbals - yea lets do this
            if problem.hasVerbal == True:
                for figure in problem.figures.keys():
                    for shape in problem.figures[figure].objects.keys():
                        temp = problem.figures[figure].objects[shape].attributes
                        #normalize preposition attributes      
                        for prop in temp.keys():
                            #if property is a preposition
                            if prop in ['inside','on','above','below','between']:
                                temp[prop] = self.translateLetterPosition(temp[prop], figure)       
                        self.figures[str(figure)].append(temp)
                        
        
            
            if problem.hasVerbal == False:
                #print problem.name
                #image = Image.open("A.png")
                #image.show()
                return -1
                pass
            
            if self.attributeCountequal("A", "B"):
                if self.attributesEqual("A", "B"):
                    pass
                else:
                    self.deltaAB = self.compareAtributes( "A", "B")
                    
            if self.attributeCountequal("A", "C"):
                if self.attributesEqual("A", "C"):
                    pass
                else:
                    self.deltaAC = self.compareAtributes( "A", "C")
            else:
                self.deltaAC = self.compareAtributes( "A", "C")
                    
            deltaSUM = self.createProductFigure( self.deltaAB, self.deltaAC)
            

            for figure in ['1','2','3','4','5','6','7','8']:
                if deltaSUM == self.figures[figure]:
                    answer = int(figure)
            if answer == -1:
                #alternate answer attempts
                for figure in ['1','2','3','4','5','6','7','8']:
                    if len(deltaSUM) == len(self.figures[figure]):
                        if self.matchOutofOrderFigure(deltaSUM, self.figures[figure]):
                            answer = int(figure)
            if answer == -1:
                #answer by probability
                best = 0
                for figure in ['1','2','3','4','5','6','7','8']:
                    
                    if len(deltaSUM) == len(self.figures[figure]):
                        similarity = float(self.matchSimilarFigure(deltaSUM, self.figures[figure]))
                        probableMatch = similarity > .9
                        bestAnswer = similarity > best
                        if probableMatch and bestAnswer:
                            best = similarity
                            answer = int(figure)
                
                            
            
        return answer
    def matchOutofOrderFigure(self,figure1, figure2):
        
        for shape1 in figure1:
            found = False
            for shape2 in figure2:
                if shape1 == shape2:
                    found = True
            if found == False:
                return False
            
        return True
    
    def matchSimilarFigure(self,figure1, figure2):
        weight = len(figure1)
        sumSimilarity = 0.0
        similarity = 0.0
        for shape1 in figure1:
            found = False
            best = 0 
            for shape2 in figure2:
                
                similarity  = self.shapeSimilarity(shape1, shape2) 
                shapeSimilar = similarity > .8
                bestOffer = similarity > best
                if shapeSimilar and bestOffer:
                    best = similarity
                    found = True
            if found == False:
                return 0
            sumSimilarity += best
        result = float(sumSimilarity)/float(weight)
        return result
        
    def createProductFigure(self, deltaAB, deltaAC):
        product = deepcopy(self.figures['A'])
        productC = deepcopy(self.figures['A'])
        for shape in deltaAB['adds']:
            product.append(shape)
        for shape in deltaAB['removes']:
            try:
                product.remove(shape)
            except:
                pass
        for shape in deltaAC['adds']:
            product.append(shape)
        for shape in deltaAC['removes']:
            try:
                product.remove(shape)
            except:  
                pass
        listChangesB = []
        listChangesC = []
        #match changes
        for change in self.deltaAB['modifies']['shapes'].keys():
            #find original in product variable
            for dest in product:
                #if the dest equals the original then this is the object changing
                if dest == self.deltaAB['modifies']['shapes'][change]['original']:
                    destIndex = product.index(dest)
                    for prop in self.deltaAB['modifies']['shapes'][change]['original'].keys():
                        try:
                            if product[destIndex][prop] != self.deltaAB['modifies']['shapes'][change]['change'][prop]:
                                listChangesB.append((destIndex,prop,self.deltaAB['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del product[destIndex][prop]
                            except KeyError:
                                pass
                        
        for change in self.deltaAC['modifies']['shapes'].keys():
            #find original in product variable
            for dest in productC:
                #if the dest equals the original then this is the object changing
                if dest == self.deltaAC['modifies']['shapes'][change]['original']:
                    destIndex = productC.index(dest)
                    for prop in self.deltaAC['modifies']['shapes'][change]['original'].keys():
                        try:
                            if productC[destIndex][prop] != self.deltaAC['modifies']['shapes'][change]['change'][prop]:
                                listChangesC.append((destIndex,prop,self.deltaAC['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del productC[destIndex][prop]
                            except KeyError:
                                pass
        #reconcile change conflict
        for changeB in listChangesB:
            for changeC in listChangesC:
                if changeB[1] == changeC[1]:
                    #stupid imutable tupples... dont use them again
                    changeBindex = listChangesB.index(changeB)
                    changeCindex = listChangesC.index(changeC)
                    changeB,changeC = self.resolveChangeConflict(changeB, changeC, product[changeB[0]])
                    listChangesB[changeBindex] = changeB
                    listChangesC[changeCindex] = changeC
            
        
        #apply changes
        for change in listChangesB:
            product[change[0]][change[1]] = change[2]
            
        for change in listChangesC:
            product[change[0]][change[1]] = change[2]
            
        return product
        
        
    def resolveChangeConflict(self, A,B, result):
        
        if A[1] in ['inside','on','above','below','between']:
            B[1]=A[1]
            return A,B
        if A[1] == "angle":
            angleA = Degrees(A[2])
            angleB = Degrees(B[2])
            original = Degrees(result['angle'])
            if original.add(90) == angleA.currentValue:
                if original.subtract(90) == angleB.currentValue:
                    A = (A[0],A[1],str(original.add(180)))
                    B = (B[0],B[1],str(original.add(180)))
                return A,B
        #find the odd alignment out
        if A[1] == 'alignment':
            listAllignments = ['bottom-right','top-left','top-right','bottom-left']
            listAllignments.remove(A[2])
            listAllignments.remove(B[2])
            listAllignments.remove(result['alignment'])
            A = (A[0],A[1],listAllignments[0])
            B = (B[0],B[1],listAllignments[0])
            return A,B            
            
        
    
    def translatePositionLetter(self, number, figure):
        result = self.problem.figures[figure].objects.keys()[number]
        return result
    
    def translateLetterPosition(self, letter, figure):
        result = -1
        count = 0
        for key in self.problem.figures[figure].objects.keys():
            if key == letter:
                result = count
            count +=1
        
        return result
    
    def attributeCountequal(self, index1, index2):
        return len(self.figures[index1]) == len(self.figures[index2])
    
    def attributesEqual(self, index1, index2):
        return self.figures[index1] == self.figures[index2]
    def solve2x2Verbal(self, problem):
        pass
    
    def getverbal(self,problem):
        pass
    
    def compareAtributes(self, first, second):
        #make sure the "first" figure is the one with the most objects
        shapeMatch = {}
        delta = {'adds':[],'removes':[],'modifies':{'shapes':{}}}
        addShapes =len(self.figures[second]) > len(self.figures[first])
        if addShapes:
            temp = first
            first = second
            second = temp
            deltaAdd = 'adds'
        else:
            deltaAdd = 'removes'
           
        pass
        count = 0
        for shape in self.figures[first]:
            shapeMatch[count] = self.matchShape(shape, second, shapeMatch)
            count +=1
        #itterate through the shapes matched
        for shapeKey in shapeMatch.keys():
            #If shape has no match
            only1 = len(self.figures[first]) == 1 and len(self.figures[second]) == 1
            if shapeMatch[shapeKey] == -1 and only1 == False:
                delta[deltaAdd] = self.figures[second]
            else:
                if self.figures[first][shapeKey] != self.figures[second][shapeMatch[shapeKey]]:
                    delta['modifies']['shapes'][shapeKey] = {}
                    delta['modifies']['shapes'][shapeKey]['original'] = self.figures[first][shapeKey]
                    delta['modifies']['shapes'][shapeKey]['change'] = self.figures[second][shapeMatch[shapeKey]]
            #if sProperty in self.figures[second].keys:
            #    print "found corresponding property"
        return delta
    
    #find similar shapes in the transformation
    def matchShape(self, shape, second, matched):
        count = 0 #len(matched)
        #return -1 if no match
        result = -1
        #look for a direct match to the object
        for shapeObject in self.figures[second]:
            countNotMatched = count not in matched.keys()
            if shapeObject == shape and countNotMatched:
                return count
            count += count
            
        #look for partial matches
        count = 0 #len(matched)
        best = 0
        for shapeObject in self.figures[second]:
            countNotMatched = count not in matched.keys()
            similarity = self.shapeSimilarity(shapeObject,shape)
            shapeSimilar = similarity >= .7
            bestOffer = similarity > best
            if shapeSimilar and countNotMatched and bestOffer:
                best = similarity
                result = count
            count += 1
        return result
            
    def shapeSimilarity(self, shapeObject, shape):
        '''
        Returns percentage difference
        '''
        total = 0.0
        same = 0.0
        result = 0.0
        if shapeObject["shape"] == shape["shape"]:
            same,total = self.countMatchingProperties(shape, shapeObject)
            result = (same/float(total)) + .1
        else:
            same,total = self.countMatchingProperties(shape, shapeObject)
            result = same/float(total)
        try:
            if shapeObject["inside"] != shape["inside"]:
                if shapeObject["inside"] != -1 and shape["inside"] != -1:
                    result += .05
        except:
            pass
        return result
            
        
               
            
    def countMatchingProperties(self,shape, shapeObject):     
        total = 0
        same = 0
        for key in shape.keys():
            total += 1
            if key in shapeObject.keys():
                if shape[key] == shapeObject[key]:
                    same +=1
        return same, total
    
    def addAttributestoDictionary(self, attributes):
        pass
    #make dictionary of associated pixles
    
class panel(object):
    dictObjects = {}
    def __init__(self):
        pass
    
class Degrees(object):
    currentValue = 0
    def __init__(self, value=0):
        self.currentValue = int(value)
    def add(self, num):
        if self.currentValue + num > 360:
            part = 360 - self.currentValue 
            result = num - part
        else:
            result = num + self.currentValue
        return result
    def subtract(self, num):
        if self.currentValue - num < 0:
            part = self.currentValue
            result = 360 - (num-part)
        else:
            result = self.currentValue - num
        return result
            
'''
deg = Degrees(100)
deg.add(35)
print deg.currentValue

deg.subtract(180)
print deg.currentValue
'''


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
    listPrepositions = ['inside','on','above','below','between', 'overlaps','left-of']
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
        print self.problem.name
        #define dataspace for figures, differences and the sum of changes
        self.figures = {'A':[],'B':[],'C':[],'D':[],'E':[],'F':[],'G':[],'H':[],'1':[],'2':[],'3':[],'4':[],'5':[],'6':[],'7':[],'8':[],'I':[],'J':[]}

        deltaSUM = {}
        #capture verbal description
        
        
        if problem.name == 'Basic Problem C-02':
            print 'x'
        self.captureVerbal()  
        print self.figures   
        #shall we dance, agent knows its too dumb for a 3x3 right now
        if problem.problemType == "2x2":     
            self.deltaAB = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
            self.deltaAC = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
            print self.figures
            if problem.hasVerbal == False:
                #print problem.name
                #image = Image.open("A.png")
                #image.show()
                return -1

            
            if self.attributeCountequal("A", "B"):
                if self.attributesEqual("A", "B"):
                    pass
                else:
                    self.deltaAB = self.compareAtributes( "A", "B")
            else:
                self.deltaAB = self.compareAtributes( "A", "B")
            print "delta AB:"
            print self.deltaAB
            if self.attributeCountequal("A", "C"):
                if self.attributesEqual("A", "C"):
                    pass
                else:
                    self.deltaAC = self.compareAtributes( "A", "C")
            else:
                self.deltaAC = self.compareAtributes( "A", "C")
            print "delta AC:"
            print self.deltaAC
            deltaSUM = self.createProductFigure( self.deltaAB, self.deltaAC, 'A', 'A')
            print "deltaSUM", deltaSUM

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
        
        
        if problem.problemType == "3x3":
            self.deltaAB = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
            self.deltaBC = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}     
            self.deltaAD = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}
            self.deltaDG = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}}      
            self.deltaAI = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}} 
            self.deltaAJ = {'adds':{'shapes':{}},'removes':{'shapes':{}},'modifies':{'shapes':{}}} 
            print self.figures
            if problem.hasVerbal == False:
                #print problem.name
                #image = Image.open("A.png")
                #image.show()
                return -1

            #compare A to B
            if self.attributeCountequal("A", "B"):
                if self.attributesEqual("A", "B"):
                    pass
                else:
                    self.deltaAB = self.compareAtributes3x3( "A", "B")
            else:
                self.deltaAB = self.compareAtributes3x3( "A", "B")
            print "delta AB:"
            print self.deltaAB
            
            #compare B to C
            if self.attributeCountequal("B", "C"):
                if self.attributesEqual("B", "C"):
                    pass
                else:
                    self.deltaBC = self.compareAtributes3x3( "B", "C")
            else:
                self.deltaBC = self.compareAtributes3x3( "B", "C")
            print "delta BC:"
            print self.deltaBC
            
                        #compare A to D
            if self.attributeCountequal("A", "D"):
                if self.attributesEqual("A", "D"):
                    pass
                else:
                    self.deltaAD = self.compareAtributes3x3( "A", "D")
            else:
                self.deltaAD = self.compareAtributes3x3( "A", "D")
            print "delta AD:"
            print self.deltaAD
            
            #compare D to G
            if self.attributeCountequal("D", "G"):
                if self.attributesEqual("D", "G"):
                    pass
                else:
                    self.deltaDG = self.compareAtributes3x3( "D", "G")
            else:
                self.deltaDG = self.compareAtributes3x3( "D", "G")
            print "delta DG:"
            print self.deltaDG
            
            self.figures['I'] = self.create3x3ProductFigure( self.deltaAB, self.deltaBC, 'A', 'B')
            self.figures['J'] = self.create3x3ProductFigure( self.deltaAD, self.deltaDG, 'A', 'D')

            print self.figures['I']
            print self.figures['J']
            self.deltaAI =self.compareAtributes('A', 'I')
            self.deltaAJ = self.compareAtributes('A', 'J')
            deltaSUM = self.createProductFigure( self.deltaAI, self.deltaAJ, 'A', 'A')
            
            if self.figures['I'][0]['shape'] == 'rectangle' and self.figures['J'][0]['shape'] == 'rectangle':
                if self.figures['I'][0]['width'] == self.figures['J'][0]['height']:
                    deltaSUM[0]['shape'] = 'square'
                    deltaSUM[0]['size'] = self.figures['I'][0]['width']
                    del deltaSUM[0]['width']
                    del deltaSUM[0]['height']
                
                    
                
            
            
            lenG = len(self.figures['G']) == 3
            lenC = len(self.figures['C']) == 3
            lenF = len(self.figures['F']) == 6
            lenH = len(self.figures['H']) == 6
            if len(self.deltaAB['adds']) == 1 and len(self.deltaBC['adds']) == 1 and len(self.deltaAD['adds']) == 1 and len(self.deltaDG['adds']) == 1 and lenC and lenF and lenG and lenH:
                deltaTemp = deepcopy(deltaSUM)
                for item in range(0,4):
                    deltaTemp.append(deltaSUM[0])
                deltaSUM = deltaTemp
            
            fourTotal = len(self.figures['C']) == 4 and len(self.figures['G']) == 4
            if len(self.deltaAB['adds']) == 1 and len(self.deltaBC['adds']) == 1 and len(self.deltaAD['adds']) == 1 and len(self.deltaDG['adds']) == 1 and fourTotal:
                pass
            #    deltaTemp = deepcopy(deltaSUM)
            #    for item in range(0,2):
            #        deltaTemp.append(deltaSUM[2])
            #    deltaSUM = deltaTemp
                
            
            print "deltaSUM", deltaSUM

            for figure in ['1','2','3','4','5','6','7','8']:
                if deltaSUM == self.figures[figure]:
                    answer = int(figure)
            if answer == -1:
                #alternate answer attempts
                for figure in ['1','2','3','4','5','6','7','8']:
                    if len(deltaSUM) == len(self.figures[figure]):
                        if self.matchOutofOrderFigure(deltaSUM, self.figures[figure]):
                            answer = int(figure)
                        if len(deltaSUM) == 1 and len(self.figures[figure]) == 1:
                            matched = True
                            for key in deltaSUM[0].keys():
                                try:
                                    if deltaSUM[0][key] != self.figures[figure][0][key]:
                                        matched = False
                                except:
                                    matched = False
                            if matched:
                                answer = intern(figure)
            if answer == -1:
                #answer by probability
                best = 0
                for figure in ['1','2','3','4','5','6','7','8']:
                    
                    if len(deltaSUM) == len(self.figures[figure]):
                        similarity = float(self.matchSimilarFigure(deltaSUM, self.figures[figure]))
                        probableMatch = similarity > .8
                        bestAnswer = similarity > best
                        if probableMatch and bestAnswer:
                            best = similarity
                            answer = int(figure)
                
        correct = problem.checkAnswer(answer)                    
        print answer, correct
        print self.figures[str(correct)]
        return answer
    
    def captureVerbal(self):
        if self.problem.hasVerbal == True:
                for figure in self.problem.figures.keys():
                    if figure == '8':
                        print 's'
                    for shape in sorted(self.problem.figures[figure].objects.keys()):
                        temp = self.problem.figures[figure].objects[shape].attributes
                        #normalize preposition attributes      
                        for prop in sorted(temp.keys()):
                            #if property is a preposition
                            if prop in self.listPrepositions:
                                temp[prop] = self.translateLetterPosition(temp[prop], figure)       
                        self.figures[str(figure)].append(temp)
                    
                    self.figures[str(figure)] = sorted(self.figures[str(figure)])
    
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
                shapeSimilar = similarity >= .7
                bestOffer = similarity > best
                if shapeSimilar and bestOffer:
                    best = similarity
                    found = True
            if found == False:
                return 0
            sumSimilarity += best
        result = float(sumSimilarity)/float(weight)
        return result
    
    def removePrepositions(self, item):
        for preposition in self.listPrepositions:
            if preposition in item:
                del item[preposition]
        return item
        
       
    def createProductFigure(self, deltaAB, deltaAC, source1, source2):
        product = deepcopy(self.figures[source1])
        productC = deepcopy(self.figures[source2])
        for shape in deltaAB['adds']:
            product.append(shape)
        for shape in deltaAB['removes']:
            try:
                product.remove(shape)
            except:
                for item in product:
                    #make a copy of the item
                    tempItem = deepcopy(item)
                    tempShape = deepcopy(shape)
                    #remove preposition from test object
                    tempItem = self.removePrepositions(tempItem)
                    #remote preposition from other object
                    tempShape = self.removePrepositions(shape)
                    #if item in product minus prepositions equals shape
                    if tempItem == tempShape:
                        product.remove(item)
                        break
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
        for change in deltaAB['modifies']['shapes'].keys():
            #find original in product variable
            for dest in product:
                #if the dest equals the original then this is the object changing
                if dest == deltaAB['modifies']['shapes'][change]['original']:
                    destIndex = product.index(dest)
                    for prop in deltaAB['modifies']['shapes'][change]['original'].keys():
                        try:
                            if product[destIndex][prop] != deltaAB['modifies']['shapes'][change]['change'][prop]:
                                listChangesB.append((destIndex,prop,deltaAB['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del product[destIndex][prop]
                            except KeyError:
                                pass
                    if len(deltaAB['modifies']['shapes'][change]['change']) > len(deltaAB['modifies']['shapes'][change]['original']):
                        for prop in deltaAB['modifies']['shapes'][change]['change']:
                            if prop in deltaAB['modifies']['shapes'][change]['original']:
                                pass
                            else:
                                product[destIndex][prop] = deltaAB['modifies']['shapes'][change]['change'][prop]
                            
                        
        for change in deltaAC['modifies']['shapes'].keys():
            #find original in product variable
            for dest in productC:
                #if the dest equals the original then this is the object changing
                if dest == deltaAC['modifies']['shapes'][change]['original']:
                    destIndex = productC.index(dest)
                    for prop in deltaAC['modifies']['shapes'][change]['original'].keys():
                        try:
                            if productC[destIndex][prop] != deltaAC['modifies']['shapes'][change]['change'][prop]:
                                listChangesC.append((destIndex,prop,deltaAC['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del productC[destIndex][prop]
                            except KeyError:
                                pass
                    if len(deltaAC['modifies']['shapes'][change]['change']) > len(deltaAC['modifies']['shapes'][change]['original']):
                        for prop in deltaAC['modifies']['shapes'][change]['change']:
                            if prop in deltaAC['modifies']['shapes'][change]['original']:
                                pass
                            else:
                                product[destIndex][prop] = deltaAC['modifies']['shapes'][change]['change'][prop]
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
        
        if A[1] in self.listPrepositions:
            B=B[0],A[1],B[2]
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
        #identify size progression
        if A[1] == 'size' and self.figures['A'][A[0]]['size'] == 'small':
            A = (A[0],A[1],'huge')
            B = (B[0],B[1],'huge')
            
        return A,B
                      
            
        
    
    def translatePositionLetter(self, number, figure):
        result = self.problem.figures[figure].objects.keys()[number]
        return result
    
    def translateLetterPosition(self, letter, figure):
        result = []
        
        letters = letter.split(',')
        for letter in letters:
            #count = 0
            #for key in self.problem.figures[figure].objects.keys():
            #    if key == letter:
            #        result.append(str(count)) 
            #    count +=1
            result.append(str(sorted(self.problem.figures[figure].objects.keys()).index(letter)))
        
        return ','.join(sorted(result))
    
    def attributeCountequal(self, index1, index2):
        return len(self.figures[index1]) == len(self.figures[index2])
    
    def attributesEqual(self, index1, index2):
        return self.figures[index1] == self.figures[index2]
    def solve2x2Verbal(self, problem):
        pass
    
    def getverbal(self,problem):
        pass
    def compareAtributes3x3(self, first, second):
        trueFirst = first
        trueSecond = second
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
        #check for direct matches first
        for shape in self.figures[first]:
            if self.directMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.directMatchShape(shape, second, shapeMatch)
            count +=1
        #then check for partial matches
        
        #check matches with prepositions removed
        count = 0
        for shape in self.figures[first]:
            if self.noprepositionsMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.noprepositionsMatchShape(shape, second, shapeMatch)
            count +=1
            
        #check partials by percent similarity
        count = 0
        for shape in self.figures[first]:
            if self.partialMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.partialMatchShape(shape, second, shapeMatch)
            count +=1
        #set -1 for all unmatched shapes
        count = 0
        for shape in self.figures[first]:
            if count not in shapeMatch.keys():
                shapeMatch[count] = -1
            count +=1
            
        
        #itterate through the shapes matched
        for shapeKey in shapeMatch.keys():
            #If shape has no match
            only1 = len(self.figures[first]) == 1 and len(self.figures[second]) == 1
            if shapeMatch[shapeKey] == -1 and only1 == False:
                delta[deltaAdd].append(self.figures[first][shapeKey])
                if deltaAdd == 'removes':
                    figDifCount = len(self.figures[second]) - len(self.figures[first])
                    numRemoves = len(delta['removes'])
                    numAdds = len(delta['adds'])
                    sumchange = numRemoves - numAdds
                    if sumchange > 0 and figDifCount + sumchange > 0:
                        try:
                            delta['adds'].append(self.figures[second][shapeKey])
                        except:
                            pass
                    pass
            else:
                if self.figures[first][shapeKey] != self.figures[second][shapeMatch[shapeKey]]:
                    delta['modifies']['shapes'][shapeKey] = {}
                    if addShapes:
                        firstKey = shapeMatch[shapeKey]
                        secondKey = shapeKey
                    else:
                        firstKey = shapeKey
                        secondKey = shapeMatch[shapeKey]
                    delta['modifies']['shapes'][shapeKey]['original'] = self.figures[trueFirst][firstKey]
                    delta['modifies']['shapes'][shapeKey]['change'] = self.figures[trueSecond][secondKey]
            #if sProperty in self.figures[second].keys:
            #    print "found corresponding property"
        return delta
    
    def compareAtributes(self, first, second):
        trueFirst = first
        trueSecond = second
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
        #check for direct matches first
        for shape in self.figures[first]:
            if self.directMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.directMatchShape(shape, second, shapeMatch)
            count +=1
        #then check for partial matches
        
        #check matches with prepositions removed
        count = 0
        for shape in self.figures[first]:
            if self.noprepositionsMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.noprepositionsMatchShape(shape, second, shapeMatch)
            count +=1
            
        #check partials by percent similarity
        count = 0
        for shape in self.figures[first]:
            if self.partialMatchShape(shape, second, shapeMatch) > -1:
                shapeMatch[count] = self.partialMatchShape(shape, second, shapeMatch)
            count +=1
        #set -1 for all unmatched shapes
        count = 0
        for shape in self.figures[first]:
            if count not in shapeMatch.keys():
                shapeMatch[count] = -1
            count +=1
            
        
        #itterate through the shapes matched
        for shapeKey in shapeMatch.keys():
            #If shape has no match
            only1 = len(self.figures[first]) == 1 and len(self.figures[second]) == 1
            if shapeMatch[shapeKey] == -1 and only1 == False:
                delta[deltaAdd].append(self.figures[first][shapeKey])
                if deltaAdd == 'removes':
                    figDifCount = len(self.figures[second]) - len(self.figures[first])
                    numRemoves = len(delta['removes'])
                    numAdds = len(delta['adds'])
                    sumchange = numRemoves - numAdds
                    if sumchange > 0 and figDifCount + sumchange > 0:
                        try:
                            delta['adds'].append(self.figures[second][shapeKey])
                        except:
                            pass
                    pass
            else:
                if self.figures[first][shapeKey] != self.figures[second][shapeMatch[shapeKey]]:
                    delta['modifies']['shapes'][shapeKey] = {}
                    if addShapes:
                        firstKey = shapeMatch[shapeKey]
                        secondKey = shapeKey
                    else:
                        firstKey = shapeKey
                        secondKey = shapeMatch[shapeKey]
                    delta['modifies']['shapes'][shapeKey]['original'] = self.figures[trueFirst][firstKey]
                    delta['modifies']['shapes'][shapeKey]['change'] = self.figures[trueSecond][secondKey]
            #if sProperty in self.figures[second].keys:
            #    print "found corresponding property"
        return delta
    
    #find similar shapes in the transformation
    def directMatchShape(self, shape, second, matched):
        count = 0 #len(matched)
        #return -1 if no match
        result = -1
        #look for a direct match to the object
        for shapeObject in self.figures[second]:
            countNotMatched = self.valueNotinDictValues(count, matched)
            if shapeObject == shape and countNotMatched:
                return count
            count += 1
    
    def noprepositionsMatchShape(self, shape, second, matched):
        count = 0 #len(matched)
        #return -1 if no match
        result = -1
        tempShape = deepcopy(shape)
        shapeNoPrep = self.removePrepositions(tempShape)
        #look for a direct match to the object
        for shapeObject in self.figures[second]:
            countNotMatched = self.valueNotinDictValues(count, matched)
            tempShapeObject = deepcopy(shapeObject)
            shapeObjectNoPrep = self.removePrepositions(tempShapeObject)
            if shapeObjectNoPrep == shapeNoPrep and countNotMatched:
                return count
            count += 1
    
    def valueNotinDictValues(self, value, inDict):
        result = True
        for key in inDict.keys():
            if value == inDict[key]:
                result = False
        return result
            
        
    def partialMatchShape(self, shape, second, matched):

        result = -1
        #look for partial matches
        count = 0 #len(matched)
        best = 0
        for shapeObject in self.figures[second]:
            countNotMatched = self.valueNotinDictValues(count, matched)
            similarity = self.shapeSimilarity(shapeObject,shape)
            shapeSimilar = similarity >= .7
            bestOffer = similarity > best
            if shapeSimilar and countNotMatched and bestOffer:
                best = similarity
                result = count
            count += 1
        return result
    def create3x3ProductFigure(self, deltaAB, deltaAC, source1, source2):
        product = deepcopy(self.figures[source1])

        for shape in deltaAB['adds']:
            product.append(shape)
        for shape in deltaAB['removes']:
            try:
                product.remove(shape)
            except:
                for item in product:
                    #make a copy of the item
                    tempItem = deepcopy(item)
                    tempShape = deepcopy(shape)
                    #remove preposition from test object
                    tempItem = self.removePrepositions(tempItem)
                    #remote preposition from other object
                    tempShape = self.removePrepositions(shape)
                    #if item in product minus prepositions equals shape
                    if tempItem == tempShape:
                        product.remove(item)
                        break
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
        for change in deltaAB['modifies']['shapes'].keys():
            #find original in product variable
            for dest in product:
                #if the dest equals the original then this is the object changing
                if dest == deltaAB['modifies']['shapes'][change]['original']:
                    destIndex = product.index(dest)
                    for prop in deltaAB['modifies']['shapes'][change]['original'].keys():
                        try:
                            if product[destIndex][prop] != deltaAB['modifies']['shapes'][change]['change'][prop]:
                                listChangesB.append((destIndex,prop,deltaAB['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del product[destIndex][prop]
                            except KeyError:
                                pass
                    if len(deltaAB['modifies']['shapes'][change]['change']) > len(deltaAB['modifies']['shapes'][change]['original']):
                        for prop in deltaAB['modifies']['shapes'][change]['change']:
                            if prop in deltaAB['modifies']['shapes'][change]['original']:
                                pass
                            else:
                                product[destIndex][prop] = deltaAB['modifies']['shapes'][change]['change'][prop]
                            
                            
        #apply changes
        for change in listChangesB:
            product[change[0]][change[1]] = change[2]
                        
        for change in deltaAC['modifies']['shapes'].keys():
            #find original in product variable
            for dest in product:
                #if the dest equals the original then this is the object changing
                if dest == deltaAC['modifies']['shapes'][change]['original']:
                    destIndex = product.index(dest)
                    for prop in deltaAC['modifies']['shapes'][change]['original'].keys():
                        try:
                            if product[destIndex][prop] != deltaAC['modifies']['shapes'][change]['change'][prop]:
                                listChangesC.append((destIndex,prop,deltaAC['modifies']['shapes'][change]['change'][prop]))
                        except:
                            try:
                                del product[destIndex][prop]
                            except KeyError:
                                pass
                    if len(deltaAC['modifies']['shapes'][change]['change']) > len(deltaAC['modifies']['shapes'][change]['original']):
                        for prop in deltaAC['modifies']['shapes'][change]['change']:
                            if prop in deltaAC['modifies']['shapes'][change]['original']:
                                pass
                            else:
                                product[destIndex][prop] = deltaAC['modifies']['shapes'][change]['change'][prop]
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
 #       for change in listChangesB:
 #           product[change[0]][change[1]] = change[2]
            
        for change in listChangesC:
            product[change[0]][change[1]] = change[2]
            
        return product
        
       
            
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


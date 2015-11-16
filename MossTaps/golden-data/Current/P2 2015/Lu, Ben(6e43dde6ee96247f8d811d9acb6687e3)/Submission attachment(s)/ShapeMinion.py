#Author: Audra Brown
#Revision date: 6/28/15
#
#This file contains the shape analysis test methods for the Raven's Progressive
#Matrices AI Agent 
#

def getAnalysis(figures, answers, scores):
    firstshapexcoors = {}
    firstshapeycoors = {}
    firstshapexcoorsA = {}
    firstshapeycoorsA = {}
    for k in figures.keys():
        x,y = findShapeXDown(figures[k], 0, 1)
        firstshapexcoors[k] = x
        firstshapeycoors[k] = y
        
        
        #shapecounts[k] = countShapes(figures[k])
    for j in answers.keys():
        x,y = findShapeXDown(answers[j], 0, 1)
        getShapeReading(figures[k], x, y)
        firstshapexcoorsA[j] = x
        firstshapeycoorsA[j] = y
        #shapecounts[k] = countShapes(answers[k])
    scores = xypatterns(firstshapexcoors, firstshapeycoors, firstshapexcoorsA, firstshapeycoorsA, scores)


    firstshapexcoors = {}
    firstshapeycoors = {}
    firstshapexcoorsA = {}
    firstshapeycoorsA = {}
    
    for k in figures.keys():
##        print k
        x,y = findShapeXUp(figures[k], 0, 1)
        firstshapexcoors[k] = x
        firstshapeycoors[k] = y
        
        #shapecounts[k] = countShapes(figures[k])
    for j in answers.keys():
##        print j
        x,y = findShapeXUp(answers[j], 0, 1)
        firstshapexcoorsA[j] = x
        firstshapeycoorsA[j] = y
        #shapecounts[k] = countShapes(answers[k])
    scores1 = xypatterns(firstshapexcoors, firstshapeycoors, firstshapexcoorsA, firstshapeycoorsA, scores)

    firstshapexcoors = {}
    firstshapeycoors = {}
    firstshapexcoorsA = {}
    firstshapeycoorsA = {}
    
    for k in figures.keys():
##        print k
        x,y = findShapeYLR(figures[k], 0, 1)
        firstshapexcoors[k] = x
        firstshapeycoors[k] = y
        
        #shapecounts[k] = countShapes(figures[k])
    for j in answers.keys():
##        print j
        x,y = findShapeYLR(answers[j], 0, 1)
        firstshapexcoorsA[j] = x
        firstshapeycoorsA[j] = y
        #shapecounts[k] = countShapes(answers[k])
    scores2 = xypatterns(firstshapexcoors, firstshapeycoors, firstshapexcoorsA, firstshapeycoorsA, scores)

    firstshapexcoors = {}
    firstshapeycoors = {}
    firstshapexcoorsA = {}
    firstshapeycoorsA = {}
    
    for k in figures.keys():
##        print k
        x,y = findShapeYRL(figures[k], 0, 1)
        firstshapexcoors[k] = x
        firstshapeycoors[k] = y
        
        #shapecounts[k] = countShapes(figures[k])
    for j in answers.keys():
##        print j
        x,y = findShapeYRL(answers[j], 0, 1)
        firstshapexcoorsA[j] = x
        firstshapeycoorsA[j] = y
        #shapecounts[k] = countShapes(answers[k])
    scores3 = xypatterns(firstshapexcoors, firstshapeycoors, firstshapexcoorsA, firstshapeycoorsA, scores)
   
    return addScores(addScores(addScores(scores, scores1), scores2),scores3)

#check for patterns of differnces in the impact cooridnates of the first found shapes
def xypatterns(xcoors, ycoors, xcoorsA, ycoorsA, scores):

##    horz1 = compareCoors(xcoors['A'],xcoors['B'],xcoors['C'])
##    horz2 = compareCoors(xcoors['A'],xcoors['B'],xcoors['C'])
    for k in xcoorsA.keys():
        test = compareCoors(xcoors['G'],xcoors['H'],xcoorsA[k],ycoors['G'],ycoors['H'],ycoorsA[k])
        scores[k] = scores[k] + test
        test = compareCoors(xcoors['A'],xcoors['E'],xcoorsA[k],ycoors['G'],ycoors['H'],ycoorsA[k])
    return scores

def compareFirstfoundShapesDownX(figures, answers, scores):
    readings  = {}
    for k in figures.keys():
        x,y = findShapeXDown(figures[k], 0, 1)
        readings[k] = getShapeReading(figures[k],x,y)
    for k in answers.keys():
        x,y = findShapeXDown(answers[k], 0, 1)
        readings[k] = getShapeReading(answers[k],x,y)
    #now we have a set of readings, let's compare
    #does the rows have the same shape?
    sabc = compare3shapes(readings['A'], readings['B'], readings['C'])
    sdef = compare3shapes(readings['D'], readings['E'], readings['F'])
    #columns?
    sadg = compare3shapes(readings['A'], readings['D'], readings['G'])
    sbeh = compare3shapes(readings['B'], readings['E'], readings['H'])
    #for each answer, see if it contains the shape as its fellows
    for k in answers.keys():
        #does it show the same shape as others in its row?
        row = compare3shapes(readings['G'], readings['H'], readings[k])
        #column?
        col =compare3shapes(readings['C'], readings['F'], readings[k])

        diag =compare3shapes(readings['A'], readings['E'], readings[k])

##        if row and sabc and sdef:
##            scores[k] = 3
##        if row and sabc or sdef:
##            scores[k] = 2
        if row and col and diag:
            scores[k] = 4
        if row and col:
            scores[k] = 3
        if row and diag:
            scores[k] = 3
        if col and diag:
            scores[k] = 3
        if row:
            scores[k] = 2

##        if col and sadg and sbeh:
##            scores[k] = 3
##        if col and sadg or sbeh:
##            scores[k] = 2
        if col:
            scores[k] = 2
        if diag:
            scores[k] = 2
        if sabc and sbeh and not diag and not row and not col:
            scores[k] = 0
##        if not sabc or not sdef or not sadg or not sbeh and not diag and not row and not col:
##            scores[k] = 1
##        if not row and not col and not diag:
##            scores[k] = 2
####
    print 'scores' ,scores
    return scores

def compareFirstfoundShapesUpX(figures, answers, scores):
    readings  = {}
    for k in figures.keys():
        x,y = findShapeXUp(figures[k], 0, 1)
        readings[k] = getShapeReading(figures[k],x,y)
    for k in answers.keys():
        x,y = findShapeXUp(answers[k], 0, 1)
        readings[k] = getShapeReading(answers[k],x,y)
    #now we have a set of readings, let's compare
    #does the rows have the same shape?
    sabc = compare3shapes(readings['A'], readings['B'], readings['C'])
    sdef = compare3shapes(readings['D'], readings['E'], readings['F'])
    #columns?
    sadg = compare3shapes(readings['A'], readings['D'], readings['G'])
    sbeh = compare3shapes(readings['B'], readings['E'], readings['H'])
    #for each answer, see if it contains the shape as its fellows
    for k in answers.keys():
        #does it show the same shape as others in its row?
        row = compare3shapes(readings['G'], readings['H'], readings[k])
        #column?
        col =compare3shapes(readings['C'], readings['F'], readings[k])

        diag =compare3shapes(readings['A'], readings['E'], readings[k])

##        if row and sabc and sdef:
##            scores[k] = 3
##        if row and sabc or sdef:
##            scores[k] = 2
        if row and col and diag:
            scores[k] = 4
        if row and col:
            scores[k] = 3
        if row and diag:
            scores[k] = 3
        if col and diag:
            scores[k] = 3
        if row:
            scores[k] = 2

##        if col and sadg and sbeh:
##            scores[k] = 3
##        if col and sadg or sbeh:
##            scores[k] = 2
        if col:
            scores[k] = 2
        if diag:
            scores[k] = 2
        if sabc and sbeh and not diag and not row and not col:
            scores[k] = 0
##        if not sabc or not sdef or not sadg or not sbeh and not diag and not row and not col:
##            scores[k] = 1
##        if not row and not col and not diag:
##            scores[k] = 2
####
    print 'scores' ,scores
    return scores

def compareFirstfoundShapesYLR(figures, answers, scores):
    readings  = {}
    for k in figures.keys():
        x,y = findShapeYLR(figures[k], 0, 1)
        readings[k] = getShapeReading(figures[k],x,y)
    for k in answers.keys():
        x,y = findShapeYLR(answers[k], 0, 1)
        readings[k] = getShapeReading(answers[k],x,y)
    #now we have a set of readings, let's compare
    #does the rows have the same shape?
    sabc = compare3shapes(readings['A'], readings['B'], readings['C'])
    sdef = compare3shapes(readings['D'], readings['E'], readings['F'])
    #columns?
    sadg = compare3shapes(readings['A'], readings['D'], readings['G'])
    sbeh = compare3shapes(readings['B'], readings['E'], readings['H'])
    #for each answer, see if it contains the shape as its fellows
    for k in answers.keys():
        #does it show the same shape as others in its row?
        row = compare3shapes(readings['G'], readings['H'], readings[k])
        #column?
        col =compare3shapes(readings['C'], readings['F'], readings[k])

        diag =compare3shapes(readings['A'], readings['E'], readings[k])

##        if row and sabc and sdef:
##            scores[k] = 3
##        if row and sabc or sdef:
##            scores[k] = 2
        if row and col and diag:
            scores[k] = 4
        if row and col:
            scores[k] = 3
        if row and diag:
            scores[k] = 3
        if col and diag:
            scores[k] = 3
        if row:
            scores[k] = 2

##        if col and sadg and sbeh:
##            scores[k] = 3
##        if col and sadg or sbeh:
##            scores[k] = 2
        if col:
            scores[k] = 2
        if diag:
            scores[k] = 2
        if sabc and sbeh and not diag and not row and not col:
            scores[k] = 0
##        if not sabc or not sdef or not sadg or not sbeh and not diag and not row and not col:
##            scores[k] = 1
##        if not row and not col and not diag:
##            scores[k] = 2
####
    print 'scores' ,scores
    return scores

def compareFirstfoundShapesYRL(figures, answers, scores):
    readings  = {}
    for k in figures.keys():
        x,y = findShapeYRL(figures[k], 0, 1)
        readings[k] = getShapeReading(figures[k],x,y)
    for k in answers.keys():
        x,y = findShapeYRL(answers[k], 0, 1)
        readings[k] = getShapeReading(answers[k],x,y)
    #now we have a set of readings, let's compare
    #does the rows have the same shape?
    sabc = compare3shapes(readings['A'], readings['B'], readings['C'])
    sdef = compare3shapes(readings['D'], readings['E'], readings['F'])
    #columns?
    sadg = compare3shapes(readings['A'], readings['D'], readings['G'])
    sbeh = compare3shapes(readings['B'], readings['E'], readings['H'])
    #for each answer, see if it contains the shape as its fellows
    for k in answers.keys():
        #does it show the same shape as others in its row?
        row = compare3shapes(readings['G'], readings['H'], readings[k])
        #column?
        col =compare3shapes(readings['C'], readings['F'], readings[k])

        diag =compare3shapes(readings['A'], readings['E'], readings[k])

##        if row and sabc and sdef:
##            scores[k] = 3
##        if row and sabc or sdef:
##            scores[k] = 2
        if row and col and diag:
            scores[k] = 4
        if row and col:
            scores[k] = 3
        if row and diag:
            scores[k] = 3
        if col and diag:
            scores[k] = 3
        if row:
            scores[k] = 2

##        if col and sadg and sbeh:
##            scores[k] = 3
##        if col and sadg or sbeh:
##            scores[k] = 2
        if col:
            scores[k] = 2
        if diag:
            scores[k] = 2
        if sabc and sbeh and not diag and not row and not col:
            scores[k] = 0
##        if not sabc or not sdef or not sadg or not sbeh and not diag and not row and not col:
##            scores[k] = 1
##        if not row and not col and not diag:
##            scores[k] = 2
####
    print 'scores' ,scores
    return scores

#takes shape readings, sees if they are the same
def compare3shapes(s1,s2,s3):
    if not equalityCheck(s1,s2) or not equalityCheck(s2,s3):
        return False
    else:
        return True
def equalityCheck(s1,s2):
    margin = 4
    #check lengths first, to eliminate
    if abs(len(s1) - len(s2)) > margin:
        return False
    #if the length are close, check values
    for i in range(0, min(len(s1), len(s2))):
        if abs(s1[i]-s2[i]) > margin:
            return False
    #if we make it through, call them equal
    return True

def compare2shapes(im1, x1,y1,im2,x2,y2):
    if getShapeReading(im1,x1,y1) == getShapeReading(im2,x2,y2):
        return True
    else:
        return False
        

def compareCoors(x1,y1,x2,y2,x3,y3):
    points = 0
    margin = 2
    if abs(x1 - x2) < margin and abs(x1 - x3) < margin:
        points = points + 2
    if abs(y1 - y2) < margin and abs(y1 - y3) < margin:
        points = points + 2
    
    if abs(x1 - x2) < margin:
        points = points + 1
    if abs(x1 - x3) < margin:
        points = points + 1
    if abs(x3 - x2) < margin:
        points = points + 1
    if abs(y1 - y2) < margin:
        points = points + 1
    if abs(y1 - y3) < margin:
        points = points + 1
    if abs(y2 - y2) < margin:
        points = points + 1
##    print "points: ", points
    return points

#

#reads disconnected shapes
def readSeperateShapes(im):
    x,y = findShapeXDown(im, 0, 1)
####    print 'x', x, y

##    x2,y2 = findShapeY(im, 0, 10)
##    print 'y', x2, y2
#    shapereading = getShapeReading(im, x, y)
    return x, y


def findShapeXDown(im, line, gridsize):
    if line > im.size[0]-1:
        return -1, -1
    reading = getAxisReading(im, 'x', line)
##    print reading
    #if the slice hit some black, return the leftmost x,y impact coordinates
    if not reading[0] == 184:
        return (reading[0], line)
    else: #if not, move the reading by the grid size
        line = line + gridsize
        return findShapeXDown(im, line+gridsize, gridsize)

def findShapeXUp(im, line, gridsize):
    if not line > 0:
        return -1, -1
    reading = getAxisReading(im, 'x', line)
##    print reading
    #if the slice hit some black, return the leftmost x,y impact coordinates
    if not reading[0] == 184:
        return (reading[0], line)
    else: #if not, move the reading by the grid size
        line = line - gridsize
        return findShapeXUp(im, line-gridsize, gridsize)

def findShapeYLR(im, line, gridsize):
    if line > im.size[0]-1:
        return -1, -1
    reading = getAxisReading(im, 'y', line)
##    print reading
    #if the slice hit some black, return the leftmost x,y impact coordinates
    if not reading[0] == 184:
        return (reading[0], line)
    else: #if not, move the reading by the grid size
        line = line + gridsize
        return findShapeYLR(im, line+gridsize, gridsize)

def findShapeYRL(im, line, gridsize):
    if not line > 0:
        return -1, -1
    reading = getAxisReading(im, 'y', line)
##    print reading
    #if the slice hit some black, return the rightmost x,y impact coordinates
    if not reading[0] == 184:
        return (reading[len(reading)-1], line)
    else: #if not, move the reading by the grid size
        line = line - gridsize
        return findShapeYRL(im, line-gridsize, gridsize)
    

def findTopmost(im, x, y):
    #given an encounter point for a shape, explore it

    return findShape(im, x-9, 1)

    
def getShapeReading(im, x, y):
    reading  = []
    spacingmargin = 5
    xmove = spacingmargin+1
    #get the first x value of the slice that found the shape
    if x == -1 and y ==-1:
        return []

    lastcut = getAxisReading(im, 'x',y)
    cut = lastcut
    xmove = 0
    while xmove < spacingmargin and not cut[0] == 184:
        y = y+1
        cut = getAxisReading(im,'x', y)
        if len(cut) > 1:
            val = cut[1]
            reading.append(val)
            xmove = abs(cut[0]-lastcut[0])
            lastcut = cut
####    print reading
    return reading


def getXlen(im, x, y):
    reading = getAxisReading(im, 'x', y)
    #if there is black in the reading, return the length of the black
    if not reading[0] == 184:
        return reading[1]

#adds score sheets together and normalizes the result
def addScores(s1, s2):
    scores = {}
    maxval = 0
    for k in s1.keys():
        scores[k] = s1[k] + s2[k]
        if scores[k] > maxval:
            maxval = scores[k]
            maxkey = k
    for j in scores.keys():
        scores[j] = scores[j] - maxval+2
    return scores
            

#takes an image and an equation of a line and along that line, 
#returns the progression of black to white shifts in the form of a list
def getAxisReading(img, axis, center):
    
    rangex = 0
    rangey = 0
    result = []
    counter = 0
    #value default starts as not black
    value = 1
    if axis == 'x':
        rangex = (0, 183)
    if axis == 'y':
        rangey = (0, img.size[1]-1)
    else: # an equation of axis, e.g. y = 1 or y = x+1
        nothing = None #implement later
    if rangex == 0:
        for y in range (0, img.size[1]-1):
            #run across a slice, counting the distance of each sequential interval of pixel value
            #So, if the current pixel is not black, as we expect along the edge of an image (!0)
            #Start counting, as long as it stays, not black, do not reset the counter
            if img.getpixel((center, y)) > 0:
                if value == 0: #if the last pixel was black
                    result.append(counter)
                    counter = 0
                    value = 1
                counter = counter + 1
            else: #but when it changes to black
                #If the change is from not black to black, store the count, reset the count, and change the value to 0
                if value == 1:
                    result.append(counter)
                    counter = 0
                    value = 0
                counter = counter + 1

    if rangey == 0:
        for x in range (0, img.size[0]-1):
            #run across the slice, counting the distance of each sequential interval of pixel value
            #So, if the current pixel is not black, as we expect along the edge of an image (!0)
            #Start counting, as long as it stays, not black, do not reset the counter
            if img.getpixel((x, center)) > 0:
                if value == 0: #if the last pixel was black
                    result.append(counter)
                    counter = 0
                    value = 1
                counter = counter + 1
            else: #but when it changes to black
                #If the change is from not black to black, store the count, reset the count, and change the value to 0
                if value == 1:
                    result.append(counter)
                    counter = 0
                    value = 0
                counter = counter + 1
    counter = counter + 1
    result.append(counter)
    return result

# VisualTest.py
# author: Audra Brown
# version date: 060715

#a visual-based agent for solving Raven's Progressive Matrices Problems
#the Agent.py class calls the visualSolve2x2() method
#all other methods are just helpers

from PIL import Image

#takes a RavensProblem and returns an answer based on visual reasoning
def visualSolve2x2(problem):
    
    #get the given figures and some measure of them

    a = convertToBWBinary(Image.open(problem.figures['A'].visualFilename))
    b = convertToBWBinary(Image.open(problem.figures['B'].visualFilename))
    c = convertToBWBinary(Image.open(problem.figures['C'].visualFilename))
    
    ax = extractImageData(a, 'x')
    bx = extractImageData(b, 'x')
    cx = extractImageData(c, 'x')

    ay = extractImageData(a, 'y')
    by = extractImageData(b, 'y')
    cy = extractImageData(c, 'y')

    ah = a.histogram()
    bh = b.histogram()
    ch = c.histogram()


    #do the same for the candidate answer figures

    one = convertToBWBinary(Image.open(problem.figures['1'].visualFilename))
    two = convertToBWBinary(Image.open(problem.figures['2'].visualFilename))
    three = convertToBWBinary(Image.open(problem.figures['3'].visualFilename))
    four = convertToBWBinary(Image.open(problem.figures['4'].visualFilename))
    five = convertToBWBinary(Image.open(problem.figures['5'].visualFilename))
    six = convertToBWBinary(Image.open(problem.figures['6'].visualFilename))
    
    onex = extractImageData(one, 'x')
    twox = extractImageData(two, 'x')
    threex = extractImageData(three, 'x')
    fourx = extractImageData(four, 'x')
    fivex = extractImageData(five, 'x')
    sixx = extractImageData(six, 'x')

    oney = extractImageData(convertToBWBinary(Image.open(problem.figures['1'].visualFilename)), 'y')
    twoy = extractImageData(convertToBWBinary(Image.open(problem.figures['2'].visualFilename)), 'y')
    threey = extractImageData(convertToBWBinary(Image.open(problem.figures['3'].visualFilename)), 'y')
    foury = extractImageData(convertToBWBinary(Image.open(problem.figures['4'].visualFilename)), 'y')
    fivey = extractImageData(convertToBWBinary(Image.open(problem.figures['5'].visualFilename)), 'y')
    sixy = extractImageData(convertToBWBinary(Image.open(problem.figures['6'].visualFilename)), 'y')

    h1 = one.histogram()
    h2 = two.histogram()
    h3 = three.histogram()
    h4 = four.histogram()
    h5 = five.histogram()
    h6 = six.histogram()

    #make places to store some settings and scores
    scores = getScoreSheet('2x2')

    #the coordinate of the primary axis testing line, currently set to center
    line = 92
    #the maximum number of candidates this method will guess from
    #any more and it will just return -1
    threshold = 1
    alphafirst = False
    alphafail = False
    betafail = False

    #reasoning direction alpha
    #estimate likelyhood of direction alpha to work
    if (abs(ah[0]-bh[0]) < 10 or abs(ah[0]-ch[0]) < 10) and compareChanges(ax, bx, ay, by) > 0:
        alphafirst = True
        #A::B  
        scores['1'] = scores['1'] + getScore(ax, bx, cx, onex)
        scores['2'] = scores['2'] + getScore(ax, bx, cx, twox)
        scores['3'] = scores['3'] + getScore(ax, bx, cx, threex)
        scores['4'] = scores['4'] + getScore(ax, bx, cx, fourx)
        scores['5'] = scores['5'] + getScore(ax, bx, cx, fivex)
        scores['6'] = scores['6'] + getScore(ax, bx, cx, sixx)

        scores['1'] = scores['1'] + getScore(ay, by, cy, oney)
        scores['2'] = scores['2'] + getScore(ay, by, cy, twoy)
        scores['3'] = scores['3'] + getScore(ay, by, cy, threey)
        scores['4'] = scores['4'] + getScore(ay, by, cy, foury)
        scores['5'] = scores['5'] + getScore(ay, by, cy, fivey)
        scores['6'] = scores['6'] + getScore(ay, by, cy, sixy)        
        
        scores['1'] = scores['1'] + getScore(ax, by, cx, oney)
        scores['2'] = scores['2'] + getScore(ax, by, cx, twoy)
        scores['3'] = scores['3'] + getScore(ax, by, cx, threey)
        scores['4'] = scores['4'] + getScore(ax, by, cx, foury)
        scores['5'] = scores['5'] + getScore(ax, by, cx, fivey)
        scores['6'] = scores['6'] + getScore(ax, by, cx, sixy)        
        
        scores['1'] = scores['1'] + compareWeightRelationship(ah, bh, ch, h1)
        scores['2'] = scores['2'] + compareWeightRelationship(ah, bh, ch, h2)
        scores['3'] = scores['3'] + compareWeightRelationship(ah, bh, ch, h3)
        scores['4'] = scores['4'] + compareWeightRelationship(ah, bh, ch, h4)
        scores['5'] = scores['5'] + compareWeightRelationship(ah, bh, ch, h5)
        scores['6'] = scores['6'] + compareWeightRelationship(ah, bh, ch, h6)        
        
        scores['1'] = scores['1'] + compareChanges(ax, bx, cx, onex)
        scores['2'] = scores['2'] + compareChanges(ax, bx, cx, twox)
        scores['3'] = scores['3'] + compareChanges(ax, bx, cx, threex)
        scores['4'] = scores['4'] + compareChanges(ax, bx, cx, fourx)
        scores['5'] = scores['5'] + compareChanges(ax, bx, cx, fivex)
        scores['6'] = scores['6'] + compareChanges(ax, bx, cx, sixx)        
        
        scores['1'] = scores['1'] + compareSingleAxis(a, b, c, one, line, 'x')
        scores['2'] = scores['2'] + compareSingleAxis(a, b, c, two, line, 'x')
        scores['3'] = scores['3'] + compareSingleAxis(a, b, c, three, line, 'x')
        scores['4'] = scores['4'] + compareSingleAxis(a, b, c, four, line, 'x')
        scores['5'] = scores['5'] + compareSingleAxis(a, b, c, five, line, 'x')
        scores['6'] = scores['6'] + compareSingleAxis(a, b, c, six, line, 'x')        

        scores['1'] = scores['1'] + compareSingleAxis(a, b, c, one, line, 'y')
        scores['2'] = scores['2'] + compareSingleAxis(a, b, c, two, line, 'y')
        scores['3'] = scores['3'] + compareSingleAxis(a, b, c, three, line, 'y')
        scores['4'] = scores['4'] + compareSingleAxis(a, b, c, four, line, 'y')
        scores['5'] = scores['5'] + compareSingleAxis(a, b, c, five, line, 'y')
        scores['6'] = scores['6'] + compareSingleAxis(a, b, c, six, line, 'y') 

        #A::C
        
        scores['1'] = scores['1'] + getScore(ax, cx, bx, onex)
        scores['1'] = scores['1'] + getScore(ay, cy, by, oney)
        scores['1'] = scores['1'] + getScore(ax, cy, bx, oney)
        scores['1'] = scores['1'] + compareWeightRelationship(ah, ch, bh, h1)
        scores['1'] = scores['1'] + compareChanges(ax, cx, bx, onex)
        scores['1'] = scores['1'] + compareSingleAxis(a, c, b, one, line, 'x')

        scores['2'] = scores['2'] + getScore(ax, cx, bx, twox)
        scores['2'] = scores['2'] + getScore(ay, cy, by, twoy)
        scores['2'] = scores['2'] + getScore(ax, cy, bx, twoy)
        scores['2'] = scores['2'] + compareWeightRelationship(ah, ch, bh, h2)
        scores['2'] = scores['2'] + compareChanges(ax, cx, bx, twox)
        scores['2'] = scores['2'] + compareSingleAxis(a, c, b, two, line, 'x')

        scores['3'] = scores['3'] + getScore(ax, cx, bx, threex)
        scores['3'] = scores['3'] + getScore(ay, cy, by, threey)
        scores['3'] = scores['3'] + getScore(ax, cy, bx, threey)
        scores['3'] = scores['3'] + compareWeightRelationship(ah, ch, bh, h3)
        scores['3'] = scores['3'] + compareChanges(ax, cx, bx, threex)
        scores['3'] = scores['3'] + compareSingleAxis(a, c, b, three, line, 'x')

        scores['4'] = scores['4'] + getScore(ax, cx, bx, fourx)
        scores['4'] = scores['4'] + getScore(ay, cy, by, foury)
        scores['4'] = scores['4'] + getScore(ax, cy, bx, foury)
        scores['4'] = scores['4'] + compareWeightRelationship(ah, ch, bh, h4)
        scores['4'] = scores['4'] + compareChanges(ax, cx, bx, fourx)
        scores['4'] = scores['4'] + compareSingleAxis(a, c, b, four, line, 'x')
        

        scores['5'] = scores['5'] + getScore(ax, cx, bx, fivex)
        scores['5'] = scores['5'] + getScore(ay, cy, by, fivey)
        scores['5'] = scores['5'] + getScore(ax, cy, bx, fivey)
        scores['5'] = scores['5'] + compareWeightRelationship(ah, ch, bh, h5)
        scores['5'] = scores['5'] + compareChanges(ax, cx, bx, fivex)
        scores['5'] = scores['5'] + compareSingleAxis(a, c, b, five, line, 'x')

        scores['6'] = scores['6'] + getScore(ax, cx, bx, sixx)
        scores['6'] = scores['6'] + getScore(ay, cy, by, sixy)
        scores['6'] = scores['6'] + getScore(ax, cy, bx, sixy)
        scores['6'] = scores['6'] + compareWeightRelationship(ah, ch, bh, h6)
        scores['6'] = scores['6'] + compareChanges(ax, cx, bx, sixx)
        scores['6'] = scores['6'] + compareSingleAxis(a, c, b, six, line, 'x')

        scores['1'] = scores['1'] + compareSingleAxis(a, b, c, one, line, 'y')
        scores['2'] = scores['2'] + compareSingleAxis(a, b, c, two, line, 'y')
        scores['3'] = scores['3'] + compareSingleAxis(a, b, c, three, line, 'y')
        scores['4'] = scores['4'] + compareSingleAxis(a, b, c, four, line, 'y')
        scores['5'] = scores['5'] + compareSingleAxis(a, b, c, five, line, 'y')
        scores['6'] = scores['6'] + compareSingleAxis(a, b, c, six, line, 'y')

        #go ahead and see if we have a good answer now
        bestmatch = getBestMatch(scores)
        if verifyMatch(bestmatch):
            pick = toAnswerOrNot(scores, bestmatch, threshold)
            if pick > -1:
                #we've found a single answer, and we feel pretty good about it
                return pick
            else:
                alphafail = True
                
        #if not, why not try the other fork in the road?

        #try another tack (beta)
    if not alphafirst or alphafail:
        scores['1'] = scores['1'] + compareChanges(ax, bx, cx, onex)
        scores['2'] = scores['2'] + compareChanges(ax, bx, cx, twox)
        scores['3'] = scores['3'] + compareChanges(ax, bx, cx, threex)
        scores['4'] = scores['4'] + compareChanges(ax, bx, cx, fourx)
        scores['5'] = scores['5'] + compareChanges(ax, bx, cx, fivex)        
        scores['6'] = scores['6'] + compareChanges(ax, bx, cx, sixx)
        
        scores['1'] = scores['1'] + compareWeightRelationship(ah, bh, ch, h1)
        scores['2'] = scores['2'] + compareWeightRelationship(ah, bh, ch, h2)
        scores['3'] = scores['3'] + compareWeightRelationship(ah, bh, ch, h3)
        scores['4'] = scores['4'] + compareWeightRelationship(ah, bh, ch, h4)
        scores['5'] = scores['5'] + compareWeightRelationship(ah, bh, ch, h5)
        scores['6'] = scores['6'] + compareWeightRelationship(ah, bh, ch, h6)

        scores['1'] = scores['1'] + compareSingleAxis(a, c, b, one, line, 'x')
        scores['2'] = scores['2'] + compareSingleAxis(a, c, b, two, line, 'x')
        scores['3'] = scores['3'] + compareSingleAxis(a, c, b, three, line, 'x')
        scores['4'] = scores['4'] + compareSingleAxis(a, c, b, four, line, 'x')
        scores['5'] = scores['5'] + compareSingleAxis(a, c, b, five, line, 'x')
        scores['6'] = scores['6'] + compareSingleAxis(a, c, b, six, line, 'x')        

        scores['1'] = scores['1'] + compareSingleAxis(a, c, b, one, line, 'y')
        scores['2'] = scores['2'] + compareSingleAxis(a, c, b, two, line, 'y')
        scores['3'] = scores['3'] + compareSingleAxis(a, c, b, three, line, 'y')
        scores['4'] = scores['4'] + compareSingleAxis(a, c, b, four, line, 'y')
        scores['5'] = scores['5'] + compareSingleAxis(a, c, b, five, line, 'y')
        scores['6'] = scores['6'] + compareSingleAxis(a, c, b, six, line, 'y')


        scores['1'] = scores['1'] + compareChanges(ax, cx, bx, onex)
        scores['2'] = scores['2'] + compareChanges(ax, cx, bx, twox)
        scores['3'] = scores['3'] + compareChanges(ax, cx, bx, threex)
        scores['4'] = scores['4'] + compareChanges(ax, cx, bx, fourx)
        scores['5'] = scores['5'] + compareChanges(ax, cx, bx, fivex)
        scores['6'] = scores['6'] + compareChanges(ax, cx, bx, sixx)
        
        scores['1'] = scores['1'] + compareWeightRelationship(ah, ch, bh, h1)
        scores['2'] = scores['2'] + compareWeightRelationship(ah, ch, bh, h2)
        scores['3'] = scores['3'] + compareWeightRelationship(ah, ch, bh, h3)
        scores['4'] = scores['4'] + compareWeightRelationship(ah, ch, bh, h4)
        scores['5'] = scores['5'] + compareWeightRelationship(ah, ch, bh, h5)
        scores['6'] = scores['6'] + compareWeightRelationship(ah, ch, bh, h6)

        scores['1'] = scores['1'] + compareSingleAxis(a, b, c, one, line, 'x')
        scores['2'] = scores['2'] + compareSingleAxis(a, b, c, two, line, 'x')
        scores['3'] = scores['3'] + compareSingleAxis(a, b, c, three, line, 'x')
        scores['4'] = scores['4'] + compareSingleAxis(a, b, c, four, line, 'x')
        scores['5'] = scores['5'] + compareSingleAxis(a, b, c, five, line, 'x')
        scores['6'] = scores['6'] + compareSingleAxis(a, b, c, six, line, 'x')        

        scores['1'] = scores['1'] + compareSingleAxis(a, b, c, one, line, 'y')
        scores['2'] = scores['2'] + compareSingleAxis(a, b, c, two, line, 'y')
        scores['3'] = scores['3'] + compareSingleAxis(a, b, c, three, line, 'y')
        scores['4'] = scores['4'] + compareSingleAxis(a, b, c, four, line, 'y')
        scores['5'] = scores['5'] + compareSingleAxis(a, b, c, five, line, 'y')
        scores['6'] = scores['6'] + compareSingleAxis(a, b, c, six, line, 'y')

        #go ahead and see if we have a good answer now
        bestmatch = getBestMatch(scores)
        if verifyMatch(bestmatch):
            pick = toAnswerOrNot(scores, bestmatch, threshold)
            if pick > -1:
                #we've found a single answer, and we feel pretty good about it
                return pick
            else:
                betafail = True
        #if not, and we started with beta tack, why not try the tests that were only in alpha?
        if  not alphafirst:
            scores['1'] = scores['1'] + getScore(ax, bx, cx, onex)
            scores['2'] = scores['2'] + getScore(ax, bx, cx, twox)
            scores['3'] = scores['3'] + getScore(ax, bx, cx, threex)
            scores['4'] = scores['4'] + getScore(ax, bx, cx, fourx)
            scores['5'] = scores['5'] + getScore(ax, bx, cx, fivex)
            scores['6'] = scores['6'] + getScore(ax, bx, cx, sixx)

            scores['1'] = scores['1'] + getScore(ax, cx, bx, onex)
            scores['2'] = scores['2'] + getScore(ax, cx, bx, twox)
            scores['3'] = scores['3'] + getScore(ax, cx, bx, threex)
            scores['4'] = scores['4'] + getScore(ax, cx, bx, fourx)
            scores['5'] = scores['5'] + getScore(ax, cx, bx, fivex)
            scores['6'] = scores['6'] + getScore(ax, cx, bx, sixx)

            if verifyMatch(bestmatch):
                pick = toAnswerOrNot(scores, bestmatch, threshold)
                if pick > -1:
                    #we've found a single answer, and we feel pretty good about it
                    return pick

            scores['1'] = scores['1'] + getScore(ay, by, cy, oney)
            scores['2'] = scores['2'] + getScore(ay, by, cy, twoy)
            scores['3'] = scores['3'] + getScore(ay, by, cy, threey)
            scores['4'] = scores['4'] + getScore(ay, by, cy, foury)
            scores['5'] = scores['5'] + getScore(ay, by, cy, fivey)
            scores['6'] = scores['6'] + getScore(ay, by, cy, sixy)        

            scores['1'] = scores['1'] + getScore(ay, cy, by, oney)
            scores['2'] = scores['2'] + getScore(ay, cy, by, twoy)
            scores['3'] = scores['3'] + getScore(ay, cy, by, threey)
            scores['4'] = scores['4'] + getScore(ay, cy, by, foury)
            scores['5'] = scores['5'] + getScore(ay, cy, by, fivey)
            scores['6'] = scores['6'] + getScore(ay, cy, by, sixy)

            if verifyMatch(bestmatch):
                pick = toAnswerOrNot(scores, bestmatch, threshold)
                if pick > -1:
                    #we've found a single answer, and we feel pretty good about it
                    return pick
            
            scores['1'] = scores['1'] + getScore(ax, by, cx, oney)
            scores['2'] = scores['2'] + getScore(ax, by, cx, twoy)
            scores['3'] = scores['3'] + getScore(ax, by, cx, threey)
            scores['4'] = scores['4'] + getScore(ax, by, cx, foury)
            scores['5'] = scores['5'] + getScore(ax, by, cx, fivey)
            scores['6'] = scores['6'] + getScore(ax, by, cx, sixy)
        
            scores['1'] = scores['1'] + getScore(ax, cy, bx, oney)
            scores['2'] = scores['2'] + getScore(ax, cy, bx, twoy)
            scores['3'] = scores['3'] + getScore(ax, cy, bx, threey)
            scores['4'] = scores['4'] + getScore(ax, cy, bx, foury)
            scores['5'] = scores['5'] + getScore(ax, cy, bx, fivey)
            scores['6'] = scores['6'] + getScore(ax, cy, bx, sixy)
    
    #go ahead and see if we have a good answer now
    #and if not, and we have narrowed it down to three, take a guess
    threshold = 3
    bestmatch = getBestMatch(scores)
    if verifyMatch(bestmatch):
        return toAnswerOrNot(scores, bestmatch, threshold)


    #if all else fails, return -1
    return -1

   
#compare the change points between figures, return a score
#uses the center slice
def compareChanges(d1, d2, dd1, dd2):
    dlen1 = len(d1[int(len(d1)/2)])
    dlen2 = len(d1[int(len(d2)/2)])
    ddlen1 = len(dd1[int(len(dd1)/2)])
    ddlen2 = len(dd2[int(len(dd2)/2)])

    #if r1 has same number of points and r2 has same number of points
    if dlen1 == dlen2 and ddlen1 == ddlen2 and dlen1 == ddlen1:
        return 2
    #if each r has same number of points with respect to itself
    if dlen1 == dlen2 and ddlen1 == ddlen2:
        return 2
        #if they have the same difference of points
    if abs(dlen1-dlen2) == abs(ddlen1-ddlen2):
        return 1
    else:
        return 0


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


#compare along axis
#Returns 0 if they are the same, or an integer score of the difference.
# currently defined as, the number of matching intervals 
def compareSingleAxis(im1, im2, imm1, imm2, line, axis):
    
    r1 = getAxisReading(im1, axis, line)
    r2 = getAxisReading(im2, axis, line)
    rr1 = getAxisReading(imm1, axis, line)
    rr2 = getAxisReading(imm2, axis, line)
    
    #if all is the same, that is very notable
    if r1 == r2 and rr2 == rr1:
        return 4
    #if the reverse of one set, matches the unchanged other set, a reflection is likely
    if r1 == reverseData(r2) and rr1 == reverseData(rr2):
        return 1
    #if they have the same number of changes
    if len(r1) == len(r2) and len(rr1) == len(rr2):
        return 2
    #if they hanve the same change in number of changes
    if abs(len(r1)-len(r2)) == abs(len(rr1)-len(rr2)):
        return 1
    #otherwise...
    else:
        return -1


#Convert the image to a binary black and whote image
def convertToBWBinary(img):
    return  img.convert('1')


# get a dictionary, referenced by relationship, to store points of comparision for each pair of figures
def getRelationDict():
    table = {}
    keys = ['A::B', 'A::C']# 'C::1', 'C::2', 'C::3', 'C::4', 'C::5', 'C::6', 'B::1', 'B::2', 'B::3', 'B::4', 'B::5', 'B::6']
    for x in keys:
            table[x] = {}
    return table


#Returns a score sheet for the problem type
def getScoreSheet(ptype):
    d = {}
    if (ptype == '2x2'):
        keys = ['1', '2', '3', '4', '5', '6']
        for k in keys:
            d[k] = 0
    return d


#check the scoresheet for acceptable matches
def getBestMatch(scores):
    bestmatch = '0'
    bestscore = -1
    lastbestscore = -1
    for a in scores.keys():
        if scores[a] > bestscore and scores[a] > lastbestscore:
            bestmatch = a
            lastbestscore = bestscore
            bestscore = scores[a]
                
    if lastbestscore == bestscore:
        return -1
    else: 
        return int(bestmatch)


#see how many candidate answers left
def toAnswerOrNot(scores, bestmatch, threshold):
    count = 0
    candidates = []
    for s in scores.keys():
        if scores[s] == scores[str(bestmatch)]:
            count = count + 1
            candidates.append(int(s))
    if count == 1:
        return bestmatch
    if count <= threshold:
        #if we have narrowed it down enough, take a guess.
        #whichever divides most evenly into the score
        #just cause [to be more human...]
        r = scores[str(bestmatch)]%bestmatch
        pick = bestmatch
        for a in candidates:
            if scores[str(a)]%a < r:
                r = scores[str(a)]%a
                pick = a

        return pick
    else:
        return -1
                    
    
#return True if the value is in the range (1,6)
def verifyMatch(match):
    if match > 0 and match < 7:
        return True
    else:
        return False
        

#compare two figures to see if they are a rotation of each other
def compareRotation(im1, im2, interval):
    theta = 0
    x = None
    y = None
    while theta < 360:
        if compareAxis(im1.copy().rotate(theta), im2, 'x') == 0:
            x = theta
        theta = theta + interval
    theta = 0
    while theta < 360:
        if compareAxis(im1.copy().rotate(theta), im2, 'y') == 0:
            y = theta
        theta = theta + interval
    if x == y:
        return x
    #if no match was found, return -1
    else:
        return -1


# get the lists of pixel change intervals
def extractImageData2(img, sampleSize, axis):
    data = []
    center = 92
    interval = 10
    for line in range (center-interval*sampleSize, center+sampleSize*interval, interval):
        
        data.append(getAxisReading(img, axis, line))

    return data


#the default image data extraction, with 2 samples to either side of center,per axis
def extractImageData(img, axis):
    return extractImageData2(img, 1, axis)
    

#take two list of lists and compare to see if they are the same. Returns True if elements are the same, in the same order.
def compareData(d1, d2):
    if d1 == d2:
        return True
    else:
        return False

    
# takes two pairs of data and return score
def compareRelationship(d1, d2, dd1, dd2):
    a = compareData(d1,d2)
    b = compareData(dd1,dd2)
    if a == True and b == True:
        return 1
    if a == False and b == False:
        return 0
    else:
        return -1

    
#returns a score of how alike two sets of relationships are
def getScore(d1,d2,dd1,dd2):
    #threshold = 5 #if a score reaches a certain number, then no more tests are to be run at this time.
    #the relationship tests can be addded here as they are developed
    score = 0
    #look for simple equality
    score = score + compareRelationship(d1, d2, dd1, dd2)
    # look for reflection
    score = score + compareRelationship(reverseData(d1), d2, reverseData(dd1), dd2)
    score = score + compareRelationship(doubleReverseData(d1), d2, doubleReverseData(dd1), dd2)
    score = score + bothContains(d1, d2, dd1, dd2)
    
    return score


#effectively rotate a data set 180 rather than relfecting it
def doubleReverseData(d):
    temp = []
    dd = []
    #reverse the order of each slice's elements
    for x in d: #for each slice
        for i in range(len(x)-1, -1, -1): #for each index in the slice, starting at the end
            #append the current element onto the temp list
            temp.append(x[i])
        #when finished with each slice, put it on the result list
        dd.append(temp)
    #finally, reverse the order the slices are stored
    result = reverseData(dd)
    return result
        

#compares two sets of data with contains method
def bothContains(d1, d2, dd1, dd2):
    a = contains(d1, d2)
    b = contains(dd1, dd2)
##    print a
##    print b
##    print '...'
    if a == b and a == 2:
        return 1
    if a == b and a == 1:
        return 0
    else:
        return -1


#see if a dataset contains another. return the length of the subseqence.
def contains(d1, d2):
    # see if (in order) d1 contains d2
    size = -1
    i2 = 0
    #for each slice
    for i in range (0, len(d1)-1):
        #compare elements
        for j in range (0, len(d1[i])-1):
            if size > -1 and d2[i][i2] == d1[i][j]:
                i2 = i2+1
            if d2[i][0] == d1[i][j] and d2[i][1] == d1[i][j+1] and size == -1:
                size = i
                i2 = i2+1
            else:
                #end of subsequence
                if size > 4:
                    return 2
                if size > 2:
                    return 1
        return 0
        

#reverse a data set
def reverseData(d):
    dd = []
    for i in range(len(d)-1, -1, -1):
        dd.append(d[i])
    return dd
    

#compare a pair of pixel weights, input two histograms, outputs a score for the relationship comparision
def compareWeightRelationship(h1, h2, hh1, hh2):
    dif1 = h1[0]-h2[0]
    dif2 = hh1[0]-hh2[0]

    if dif1 == dif2:
        return 2
    if h1 == hh1 or h2==hh2:
        return 1
    if h1[0] > h2[0] and hh1[0] > hh2[0] and abs(dif1) > 10 and abs(dif2) > 10:
        return 1
    if h1[0] < h2[0] and hh1[0] < hh2[0] and abs(dif1) > 10  and abs(dif2) > 10:
        return 1
    if abs(dif1 - dif2) > max(dif1, dif2)/2:
        return -2
    else:
        return 0  

#print the standard details of an image
def printDetails(img):
    print (img.format, img.size, img.mode)
    

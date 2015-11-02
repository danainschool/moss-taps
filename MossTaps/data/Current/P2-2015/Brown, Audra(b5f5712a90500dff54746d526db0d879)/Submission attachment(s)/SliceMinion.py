#Author: Audra Brown
#Revision date: 6/27/15
#
#This file contains the color change slice test methods for the Raven's Progressive
#Matrices AI Agent 
#

#takes a set of images to compare and a set of candidate answers
#returns a scoresheet
def testAxes(images, answers, scores, center):
    axis = 'x'
    line = center

    xabc = compare3(images['A'], images['B'], images['C'], line, axis)
    xdef = compare3(images['D'], images['E'], images['F'], line, axis)
    xadg = compare3(images['A'], images['D'], images['G'], line, axis)
    xbeh = compare3(images['B'], images['E'], images['H'], line, axis)

    if xabc == xdef and xabc > 0:
        for a in answers.keys():
            scores[a] = scores[a] + 1 + compare3(images['G'], images['H'], answers[a], line, axis)
    if xadg == xbeh and xadg > 0 :
        for a in answers.keys():
            scores[a] = scores[a] +compare3(images['C'], images['F'], answers[a], line, axis)

    #now, the y axis
    axis = 'y'
    xabc = compare3(images['A'], images['B'], images['C'], line, axis)
    xdef = compare3(images['D'], images['E'], images['F'], line, axis)
    xadg = compare3(images['A'], images['D'], images['G'], line, axis)
    xbeh = compare3(images['B'], images['E'], images['H'], line, axis)

    if xabc == xdef and xabc > 0 :
        for a in answers.keys():
            scores[a] = scores[a] + compare3(images['G'], images['H'], answers[a], line, axis)
    if xadg == xbeh and xadg > 0 :
        for a in answers.keys():
            scores[a] = scores[a] + 1 + compare3(images['C'], images['F'], answers[a], line, axis)

    return scores

def testScores(images, answers, scores):
    for k in answers.keys():
        scores[k] = scores[k] = scores3(images['A'], images['B'], answers['k'])
        return scores
                                        


def scores3(im1, im2, im3):
    return getScore(im1, im2, im2, im3)

#compare 3 figures along an axis
def compare3(im1, im2, im3, line, axis):
    return compareSingleAxis(im1, im2, im2, im3, line, axis)

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
    interval = center/sampleSize
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


#effectively rotate a data set 180 rather than reflecting it
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
        


    




#compare along axis
#Returns 4 if they are the same, or an integer score of the difference.
def compareSingleAxis(im1, im2, imm1, imm2, line, axis):
    
    r1 = getAxisReading(im1, axis, line)
    r2 = getAxisReading(im2, axis, line)
    rr1 = getAxisReading(imm1, axis, line)
    rr2 = getAxisReading(imm2, axis, line)
    #if nothing is there, not so discriminative
    if r1[0] >= line*2 or r2[0] >= line*2 or rr1[0] >= line*2 or rr2[0] >= line*2:
        return -1
    if len(r2) < 2 or len(r1) < 2 or len(rr1) < 2 or len(rr2) < 2:
        return 0
    #if all is the same, that is very notable
    if r1 == r2 and rr2 == rr1 and len(r1) > 1 and len(rr1) > 1:
        return 2
    #if the reverse of one set, matches the unchanged other set, a reflection is likely
    if r1 == reverseData(r2) and rr1 == reverseData(rr2):
        return 1
    #if they have the same number of changes > 0
    if len(r1) == len(r2) and len(rr1) == len(rr2):
        return 1
    #if they hanve the same change in number of changes
    if abs(len(r1)-len(r2)) == abs(len(rr1)-len(rr2)):
        return 1
    #otherwise...
    else:
        return -1

#reverse a data set
def reverseData(d):
    dd = []
    for i in range(len(d)-1, -1, -1):
        dd.append(d[i])
    return dd

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



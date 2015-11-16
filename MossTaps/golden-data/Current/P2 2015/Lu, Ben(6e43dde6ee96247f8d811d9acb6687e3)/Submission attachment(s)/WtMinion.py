#Author: Audra Brown
#Revision Date: 6/26/15
#
#This file contains the test methods for the Raven's Progressive
#Matrices AI Agent 
#

#Takes a dict of histograms, a scoresheet, and a margin
#Returns a scoresheet
def testWeight(hists, scoresheet, margin):
    printOn = False
    diagweight = True
    edgeweights = True
    scores = scoresheet
    #compare horizontal pixel-weights
    wtabc = compare3Weights(hists['ah'],hists['bh'],hists['ch'], margin)
    wtdef = compare3Weights(hists['dh'],hists['eh'],hists['fh'], margin)
    if printOn:
        print "horzwts, abc, def: ", wtabc, wtdef
    #If both the top row and the middle row show the same relationship,
    #look for an answer that shares the same relationship as well.
    if wtabc == wtdef and wtabc > 0:
        for i in scores.keys():
            h = 'h'+ i
            val = compare3Weights(hists['gh'],hists['hh'],hists[h], margin)

            if  val == wtabc and val == wtdef:
                if printOn:
                    print 'scores' ,i, '+3'
                scores[i] = scores[i] + 3
            if  val == wtabc or val == wtdef:
                if printOn:
                    print 'scores' ,i, '+2'
                scores[i] = scores[i] + 2
    else:
        diagweight = False

    #compare vertical pixel weights
    wtadg = compare3Weights(hists['ah'],hists['dh'],hists['gh'], margin)
    wtbeh = compare3Weights(hists['bh'],hists['eh'],hists['hh'], margin)
    if printOn:
        print "vertwts: ", wtadg, wtbeh
    if wtadg == wtbeh and wtadg > 0:
        for j in scores.keys():
            h = 'h'+ j
            val = compare3Weights(hists['ch'],hists['fh'],hists[h], margin)
            if wtadg == val:
                scores[j] = scores[j] + 2
                if printOn:
                    print j ,' scores ',2
    else:
        if True: # val == -1:
            diagweight = False
        
    if diagweight:

        for k in scores.keys():
            h = 'h'+k
            value = compare3Weights(hists['ah'],hists['eh'], hists[h], margin)
            if True: #wtadg == value:
                if value == 7:
                    scores[k] = scores[k] + 4
                    if printOn:
                        print k ,' scores ',4
                if value > 0 :
                    scores[k] = scores[k] + 1
                    if printOn:
                        print k ,' scores ',1

                if printOn:
                    print "diagedge"

    if edgeweights:
        for k in scores.keys():
            h = 'h'+k
            value = compare3Weights(hists['ch'],hists['fh'], hists[h], margin)
            if True:
                if value == 7:
                    if printOn:
                        print k ,' scores ',4
                    scores[k] = scores[k] + 4
                if value > 0 :
                    if printOn:
                        print k ,' scores ',1
                    scores[k] = scores[k] + 1
                if printOn:
                    print "vertedge"


        for k in scores.keys():
            h = 'h'+k
            value = compare3Weights(hists['gh'],hists['hh'], hists[h], margin)
            if True:
                if value == 7:
                    if printOn:
                        print k ,' scores ',4
                    scores[k] = scores[k] + 4
                if value > 0 :
                    if printOn:
                        print k ,' scores ',1
                    scores[k] = scores[k] + 1
                if printOn:
                    print "horzedge"


            if value == 7:
                if printOn:
                    print k ,' scores ',4
                scores[k] = scores[k] + 4
            if value > 0 :
                if printOn:
                    print k ,' scores ',1
                scores[k] = scores[k] + 1

####    print "testing the scores",scores
    return scores

#input three figure histograms and see if they have a similar weight relationship
#return score
def compare3Weights(a,b,c, margin):
    result = compareWeightRelationship(a,b,b,c, margin)
    return result

#compare a pair of pixel weights, input two histograms,
#returns a score for the relationship comparision
def compareWeightRelationship(h1, h2, hh1, hh2, margin):
    dif1 = h1[0]-h2[0]
    dif2 = hh1[0]-hh2[0]
    dif3 = h1[0]-hh2[0]
   

##    if dif1 == dif2 and dif3 == dif1:
##        return 4
    if h1[0] == h2[0] and hh1[0] == hh2[0]:
        return 7
    
##    if dif1 == dif3:
##        return 9

##    if abs(dif1-dif2) < margin:
##        return 12
##    if abs(dif1-dif3) < margin:
##        return 13
    ####
##    print dif1, dif2
##    print h1[0], h2[0], hh1[0], hh2[0]
    if h1 == hh1 or h2==hh2:
        return -1
    if h1[0] > h2[0] and hh1[0] > hh2[0] and abs(dif1) > margin and abs(dif2) > margin:
####        print 'test1'
        return 2
    if h1[0] < h2[0] and hh1[0] < hh2[0] and abs(dif1) > margin  and abs(dif2) > margin:
####        print 'test2'
        return 1
##    if h1[0] < h2[0] and hh1[0] < hh2[0]
    if abs(dif1 - dif2) > max(dif1, dif2)/2:
        return -1
    if h1[0] < h2[0] and hh1[0] > hh2[0]:
        return 5
    if h1[0] > h2[0] and hh1[0] < hh2[0]:
        return 6
##    if dif2 == dif3:
##        return 10
##    if abs(dif1-dif2) < margin and abs(dif1-dif3) < margin:
##        return 11
    else:
        return 0  

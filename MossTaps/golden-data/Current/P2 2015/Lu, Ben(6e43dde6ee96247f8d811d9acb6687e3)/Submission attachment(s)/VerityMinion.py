#Author: Audra Brown
#Revision date: 6/27/15
#
#This file contains the answer verification methods for the Raven's Progressive
#Matrices AI Agent 
#

#check the scoresheet for acceptable matches
def getBestMatch(scores):
    bestmatch = '0'
    bestscore = -1
    lastbestscore = -1
    for a in scores.keys():
        if scores[a] > bestscore:
            bestmatch = a
            bestscore = scores[a]
        else:
            if scores[a] > lastbestscore:
                lastbestscore = scores[a]
##    print scores
##    print 'bestmatch ', bestmatch
##    print '...'
    if lastbestscore == bestscore or lastbestscore+2 == bestscore:
        return -1
    if bestscore > 0:
        return int(bestmatch)
    else:
        return -1


def eliminateCandidates(candidates, scores, threshold):
    count = 0
    
    for k in candidates.keys():
        if scores[k] <= threshold:
            candidates[k] = False
            count = count + 1
            ##print 'eliminated ', k
    ##print 'eliminated: ', count, ' answers'

    if count == len(scores):
        for k in candidates.keys():
            candidates[k] = True
            ##scores[k] = 0
##            print 'reset', count
    return candidates

def youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers):
    #check that not all answers have been eliminated
    check = True
    for x in candidates.items():
        if x:
            check = False
    if not check:
        for k in candidates.keys():
            if not candidates[k]:
                del candidates[k]
                del scores[k]
                del cumulativeScores[k]
                del answers[k]
            else:
                scores[k] = 0
    if check:
        for k in candidates.keys():
            candidates[k] = True
            ##scores[k] = 0
##        print 'reset'
##    print 'answers left: ',answers.keys()                
    return candidates, scores, answers
    
#check if the answers have been anrrowed down to one
#if so, return true and the answer
#if not, return false and a the number of remaining answers
def finalAnswer(answers, printElimResults):
    if len(answers) == 1:
        if printElimResults:
            print  'SOLVED!'
        return True, answers.keys()[0]
    else:
        if printElimResults:
            print len(answers),' answers Remain', '\n' 

        return False, len(answers)

#return True if the value is in the range (1,8)
def verifyMatch(match):
    if match > 0 and match < 9:
        return True
    else:
        return False
    
#see how many candidate answers left
def toAnswerOrNot(scores, bestmatch, threshold):
    count = 0
    candidates = []
##    status = 'Undecided'
    for s in scores.keys():
        if scores[s] == scores[str(bestmatch)]:
            count = count + 1
            candidates.append(int(s))
    if count == 1:
##        print 'Confident'
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
##        print 'Taking a guess. Narrowed it down to ', count
        return pick
    else:
##        print 'Undecided ', count, ' answers remain.'
        return -1

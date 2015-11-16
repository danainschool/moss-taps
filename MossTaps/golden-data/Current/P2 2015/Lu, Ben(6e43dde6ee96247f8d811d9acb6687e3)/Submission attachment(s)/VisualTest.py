# VisualTest.py
# author: Audra Brown
# version date: 062215

#a visual-based agent for solving Raven's Progressive Matrices Problems
#the Agent.py class calls the visualSolve2x2() method
#all other methods are just helpers

from PIL import Image
import VisualTestLegacy
import WtMinion
import VerityMinion
import SliceMinion
import ShapeMinion

#takes a 3x3 RavensProblem and returns an answer
def solve3x3(problem):
    threshold = 1
    return getAnswer(problem.figures, problem.problemType)
##    print getAnswer(problem.figures)
##    answer, candidates = getAnswer(problem.figures)
##    if answer > -1:
##        return answer
##    else:
##        return VisualTestLegacy(problem.figures, candidates)

def solve2x2(problem):
    return VisualTestLegacy.visualSolve2x2(problem.figures, create2x2CandidateList(), problem.problemType)

def getAnswer(problemfigures, ptype):
    #get the given figures
    figures = {}
    
    figures['A'] = convertToBWBinary(Image.open(problemfigures['A'].visualFilename))
    figures['B'] = convertToBWBinary(Image.open(problemfigures['B'].visualFilename))
    figures['C'] = convertToBWBinary(Image.open(problemfigures['C'].visualFilename))
    figures['D'] = convertToBWBinary(Image.open(problemfigures['D'].visualFilename))
    figures['E'] = convertToBWBinary(Image.open(problemfigures['E'].visualFilename))
    figures['F'] = convertToBWBinary(Image.open(problemfigures['F'].visualFilename))
    figures['G'] = convertToBWBinary(Image.open(problemfigures['G'].visualFilename))
    figures['H'] = convertToBWBinary(Image.open(problemfigures['H'].visualFilename))

    answers = {}

    answers['1'] = convertToBWBinary(Image.open(problemfigures['1'].visualFilename))
    answers['2'] = convertToBWBinary(Image.open(problemfigures['2'].visualFilename))
    answers['3'] = convertToBWBinary(Image.open(problemfigures['3'].visualFilename))
    answers['4'] = convertToBWBinary(Image.open(problemfigures['4'].visualFilename))
    answers['5'] = convertToBWBinary(Image.open(problemfigures['5'].visualFilename))
    answers['6'] = convertToBWBinary(Image.open(problemfigures['6'].visualFilename))
    answers['7'] = convertToBWBinary(Image.open(problemfigures['7'].visualFilename))
    answers['8'] = convertToBWBinary(Image.open(problemfigures['8'].visualFilename))
    
    hists = {}                                  

    hists['h1'] = answers['1'].histogram()
    hists['h2'] = answers['2'].histogram()
    hists['h3'] = answers['3'].histogram()
    hists['h4'] = answers['4'].histogram()
    hists['h5'] = answers['5'].histogram()
    hists['h6'] = answers['6'].histogram()
    hists['h7'] = answers['7'].histogram()
    hists['h8'] = answers['8'].histogram()

    hists['ah'] = figures['A'].histogram()
    hists['bh'] = figures['B'].histogram()
    hists['ch'] = figures['C'].histogram()
    hists['dh'] = figures['D'].histogram()
    hists['eh'] = figures['E'].histogram()
    hists['fh'] = figures['F'].histogram()
    hists['gh'] = figures['G'].histogram()
    hists['hh'] = figures['H'].histogram()

    threshold = 1
    count = 0
    scores = getScoreSheet('3x3')
    answer, candidates = eliminateAnswers(hists, figures, answers, createCandidateList(),scores,getScoreSheet('3x3'), threshold, count)
    if answer > 0:
        return answer
    else:
##        return -1
        return VisualTestLegacy.visualSolve2x2(problemfigures, candidates, ptype)

def eliminateAnswers(hists, figures, answers, thecandidates, scoresheet , cumscores, threshold, count):

    scores = scoresheet
    candidates = thecandidates

    cumulativeScores = cumscores
    elimthreshold = 2 #lowestscore to keep
    #threshold = 1 #number of remaining answers to guess
    line = 92
    printOn = False#for all wts, all along
    printWtSummaryOn = True #print the remaining answers after the weight tests have been run
    foundfinalanswer = True
    printElimResults = False

    printAxesOn = False

    if count > 0:
        printWtSummaryOn = True

    #variables to turn on/turn off weight tests
    tightweights = True# True
    medweights = True
    wideweights = False
    hugeweights = False
    
    tightmargin = 10
    medmargin = 25
    widemargin = 100 #last working number 100
    hugemargin = 1000


    #
    #run the tight margin weight test
    #
    if tightweights:
        scores = WtMinion.testWeight(hists, scores, tightmargin)
        scores = normalizeScores(scores)
    if printOn and tightweights:
        print "scores",scores
        print "Checking Margin ", tightmargin, " Weights"
        print "scores: ", scores
        
        
##    #go ahead and see if we have a good answer now
##    bestmatch = VerityMinion.getBestMatch(scores)
##    if VerityMinion.verifyMatch(bestmatch):
##        pick = VerityMinion.toAnswerOrNot(scores, bestmatch, threshold)
##        if pick > -1 and candidates[str(pick)]:
##            #we've found a single answer, and we feel pretty good about it
##            print "I think the answer is: ", pick , "Returning it."
##            return pick, candidates
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    
    if printOn:
        print "remaining candidates: ", candidates
    #
    #Run the medium margin weight test
    #
    if medweights:
        scores = WtMinion.testWeight(hists, scores, medmargin)
        scores = normalizeScores(scores)
    if printOn and medweights:
        print "Checking Margin ", medmargin, " Weights"
        print "scores: ", scores
        
    
        
    #go ahead and see if we have a good answer now
    bestmatch = VerityMinion.getBestMatch(scores)
    if VerityMinion.verifyMatch(bestmatch):
        pick = VerityMinion.toAnswerOrNot(scores, bestmatch, threshold)
        if pick > -1 and candidates[str(pick)]:
            #we've found a single answer, and we feel pretty good about it
            print "I think the answer is: ", pick , "Returning it."
            return pick, candidates
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    
    if printOn:
        print "remaining candidates: ", candidates
    
        
    #
    #Run the wide margin weight test
    #
    if wideweights:
        scores = WtMinion.testWeight(hists, scores, widemargin)
        scores = normalizeScores(scores)
    if printOn and wideweights:
        print "Checking Margin ", widemargin, " Weights"
        print "scores: ", scores
        
    
        
    #go ahead and see if we have a good answer now
    bestmatch = VerityMinion.getBestMatch(scores)
    if VerityMinion.verifyMatch(bestmatch):
        pick = VerityMinion.toAnswerOrNot(scores, bestmatch, threshold)
        if pick > -1 and candidates[str(pick)]:
            #we've found a single answer, and we feel pretty good about it
            print "I think the answer is: ", pick , "Returning it."
            return pick, candidates
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    
    if printOn:
        print "remaining candidates: ", candidates


    #
    #Run the huge margin weight test
    #
    if hugeweights:
        scores = WtMinion.testWeight(hists, scores, hugemargin)
        scores = normalizeScores(scores)
    if printOn and hugeweights:
        print "Checking Margin ", hugemargin, " Weights"
        print "scores: ", scores
        
    
        
    #go ahead and see if we have a good answer now
    bestmatch = VerityMinion.getBestMatch(scores)
    if VerityMinion.verifyMatch(bestmatch):
        pick = VerityMinion.toAnswerOrNot(scores, bestmatch, threshold)
        if pick > -1 and candidates[str(pick)]:
            #we've found a single answer, and we feel pretty good about it
            print "I think the answer is: ", pick , "Returning it."
            return pick, candidates
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    
    if printOn:
        print "remaining candidates: ", candidates


    
    
    #######################################
    #Now testing axis color change slices.#
    #######################################
    elimthreshold = 1
    printElimResults = False
    printAxesSummaryOn = True
    printAxesOn = False

    scores1  = SliceMinion.testAxes(figures, answers, scores, line+20)
    scores2  = SliceMinion.testAxes(figures, answers, scores, line-20)
    scores = addScores(scores1, scores2)
    
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    if printAxesOn:
        print scores
        
    scores1  = SliceMinion.testAxes(figures, answers, scores, line-10)
    scores2  = SliceMinion.testAxes(figures, answers, scores, line+10)
    scores = addScores(scores1, scores2)
    
    #if not, see if we can narrow it down at least.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    if printAxesOn:
        print scores
##    
##    scores1  = SliceMinion.testAxes(figures, answers, scores, line-5)
##    scores2  = SliceMinion.testAxes(figures, answers, scores, line+5)
##    scores1 = addScores(scores1, scores2)    
##    scores2  = SliceMinion.testAxes(figures, answers, scores, line)
##    scores = addScores(scores1, scores2)
##    
##    #if not, see if we can narrow it down at least.
##    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
##    cumulativeScores = addScores(cumulativeScores, scores)
##    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
##    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    if printAxesOn:
        print scores
    if printAxesSummaryOn:
        print "remaining candidates: ", candidates.keys()


    #####################################
    #Now running shape comparison tests.#
    #####################################
    elimthreshold = 1
    printShapesOn = False
    printShapesSummaryOn = True
    scores = ShapeMinion.getAnalysis(figures, answers, scores)
    
    if printShapesOn:
        print scores
    #see if we can narrow it down.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates
    
    scores = ShapeMinion.compareFirstfoundShapesDownX(figures, answers, scores)

    if printShapesOn:
        print scores
    #see if we can narrow it down.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)
    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates


    scores = ShapeMinion.compareFirstfoundShapesUpX(figures, answers, scores)
    if printShapesOn:
        print scores
        
##    #see if we can narrow it down.
##    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
##    cumulativeScores = addScores(cumulativeScores, scores)
##
##    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
##    
##    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
##    if foundfinalanswer:
##        return finalanswer, candidates
##
##    scores = ShapeMinion.compareFirstfoundShapesYLR(figures, answers, scores)
##    if printShapesOn:
##        print scores
        
    #see if we can narrow it down.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)

    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates

    scores = ShapeMinion.compareFirstfoundShapesYRL(figures, answers, scores)
    if printShapesOn:
        print scores
        
    #see if we can narrow it down.
    candidates = VerityMinion.eliminateCandidates(candidates, scores, elimthreshold)
    cumulativeScores = addScores(cumulativeScores, scores)

    candidates, scores, answers = VerityMinion.youAreTheWeakestLinkGoodbye(cumulativeScores, candidates, scores, answers)
    
    foundfinalanswer, finalanswer = VerityMinion.finalAnswer(answers, printElimResults)
    if foundfinalanswer:
        return finalanswer, candidates




    if printShapesSummaryOn:
        print "remaining candidates: ", candidates.keys()
        print "answers left ", answers.keys()






        
    #scores = Minion.testScores(figures, candidates, scoares)

    #go ahead and see if we have a good answer now
##    bestmatch = VerityMinion.getBestMatch(cumulativeScores)
##    if VerityMinion.verifyMatch(bestmatch):
##        pick = VerityMinion.toAnswerOrNot(cumulativeScores, bestmatch, threshold)
##        if pick > -1 and candidates[str(pick)]:
##            #we've found a single answer, and we feel pretty good about it
##            print "I think the answer is: ", pick , "Returning it."
##            return pick, candidates

##    if count < 2:
##        print "running the reaming answers through again."
##        print "count" , count
####        print 'candidates, answers', candidates, answers
##        return eliminateAnswers(hists, figures, answers, candidates, scores, cumulativeScores, 1, count+1)
    print "candidates being sent to legacy: ", candidates.keys()
##    
    return -1, candidates
    #return VisualTestLegacy.visualSolve2x2(figures, candidates)


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
            



#Convert the image to a binary black and white image
def convertToBWBinary(img):
    return  img.convert('1')



#Returns a score sheet for the problem type
def getScoreSheet(ptype):
    d = {}
    if (ptype == '2x2'):
        keys = ['1', '2', '3', '4', '5', '6']
        for k in keys:
            d[k] = 5
    if (ptype == '3x3'):
        keys = ['1', '2', '3', '4', '5', '6', '7', '8']
        for k in keys:
            d[k] = 0
    return d        
        

#check to see if a figure is centered and symmetrical about x and y
#Returns True or False
def isCenteredAndSymmetric(img):
    xaxis = extractImageData(img, 'x')
    yaxis = extractImageData(img, 'y')
    if yaxis == xaxis and reverseData(xaxis) == xaxis and reverseData(yaxis) == yaxis:
        return True
    else:
        return False


   



#print the standard details of an image
def printDetails(img):
    print (img.format, img.size, img.mode)
    


def createCandidateList():
    result = {}
    for key in ['1','2','3','4','5','6','7','8']:
        result[key] = True
    return result

def create2x2CandidateList():
    result = {}
    for key in ['1','2','3','4','5','6']:
        result[key] = True
    return result

def normalizeScores(scores):
    maxval = 0
    for i in scores.keys():
        if scores[i] > maxval:
            maxval = scores[i]
    #reduce so that maxval is 3
    for k in scores.keys():
        scores[k] = scores[k]-maxval+3
    return scores
        






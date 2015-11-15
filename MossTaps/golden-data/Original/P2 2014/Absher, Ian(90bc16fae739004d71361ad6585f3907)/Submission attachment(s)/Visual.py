__author__ = 'Ian'

from PIL import Image, ImageDraw
import copy
import itertools
import math

def detectObjects(img):

    gray = img.convert('L')
    bw = gray.point(lambda x: 0 if x<128 else 255, '1')

    pix = bw.load()
    obj = 1
    objectNums = []
    for x in range(bw.size[0]):
        for y in range(bw.size[1]):
            if pix[x,y] not in objectNums and pix[x,y] != 255:
                ImageDraw.floodfill(bw, [x, y], obj)
                pix = bw.load()
                objectNums.append(obj)
                obj += 1

    pix = bw.load()
    objects = []
    for objNum in objectNums:

        Xmin = 9999999
        Xmax = 0
        Ymin = 9999999
        Ymax = 0
        count = 0
        for x in range(bw.size[0]):
            for y in range(bw.size[1]):
                if pix[x,y] == objNum:
                    count += 1
                    if x < Xmin:
                        Xmin = x
                    if x > Xmax:
                        Xmax = x
                    if y < Ymin:
                        Ymin = y
                    if y > Ymax:
                        Ymax = y


        if Xmax - Xmin > 2 and Ymax - Ymin > 2:
            XMeasurments = []
            XDensityMeasurments = []

            num_points = 9
            if (Xmax-Xmin)/num_points == 0:
                num_points = 2

            for x in range(Xmin, Xmax, (Xmax-Xmin)/num_points):
                start = 9999999
                stop = 0
                count = 0
                for y in range(bw.size[1]):
                    if pix[x,y] == objNum:
                        count += 1
                        if y < start:
                            start = y
                        if y > stop:
                            stop = y
                XDensityMeasurments.append(count)
                XMeasurments.append((stop-start)*1.0/(Xmax-Xmin))

            YMeasurments = []
            YDensityMeasurments = []
            for y in range(Ymin, Ymax, (Ymax-Ymin)/num_points):
                start = 9999999
                stop = 0
                count = 0
                for x in range(bw.size[0]):
                    if pix[x,y] == objNum:
                        count += 1
                        if x < start:
                            start = x
                        if x > stop:
                            stop = x
                YDensityMeasurments.append(count)
                YMeasurments.append((stop-start)*1.0/(Ymax-Ymin))

            while len(XMeasurments) < 10:
                XMeasurments.append(0)

            while len(YMeasurments) < 10:
                YMeasurments.append(0)

            while len(XDensityMeasurments) < 10:
                XDensityMeasurments.append(0)

            while len(YDensityMeasurments) < 10:
                YDensityMeasurments.append(0)

            while len(XMeasurments) > 10:
                del XMeasurments[-1]

            while len(YMeasurments) > 10:
                del YMeasurments[-1]

            while len(XDensityMeasurments) > 10:
                del XDensityMeasurments[-1]

            while len(YDensityMeasurments) > 10:
                del YDensityMeasurments[-1]

            objects.append({'Xmin': Xmin, 'Ymin': Ymin, 'Xlen': Xmax-Xmin, 'Ylen': Ymax-Ymin, 'count': count, 'XMeasurments': XMeasurments, 'YMeasurments': YMeasurments, 'XDensityMeasurments': XDensityMeasurments, 'YDensityMeasurments': YDensityMeasurments})

    return objects

def calculateObjectDelta(obj1, obj2):
    total = 0.0
    delta = {}
    for key in obj1:
        if 'Measurments' in key:
            delta[key] = []
            for x, y in zip(obj1[key], obj2[key]):
                delta[key].append(y - x)
                total += abs(y - x)
        else:
            delta[key] = obj2[key] - obj1[key]
            total += abs(obj2[key] - obj1[key])

    return delta, total

def applyObjectDelta(obj1, delta):

    for key in obj1:
        if 'Measurments' in key:
            for i in range( min(len(obj1[key]), len(delta[key]))):
                obj1[key][i] += delta[key][i]
        else:
            obj1[key] += delta[key]

    return obj1

def PickAnswer(Candidate, Answers):
    results = []
    for ans in Answers:
        sum = 0
        for obj1, obj2 in zip(Candidate, ans[1]):
            delta, total = calculateObjectDelta(obj1, obj2)
            sum += total
        results.append([sum, ans[0], ans[1]])

    return sorted(results)

def visualNew(A, B, C, problem):
    
    AObjs = detectObjects(Image.open(problem.figures[A].visualFilename))
    BObjs = detectObjects(Image.open(problem.figures[B].visualFilename))
    CObjs = detectObjects(Image.open(problem.figures[C].visualFilename))

    Answers = []
    num_Answers = 0
    if problem.problemType == '2x2':
        num_Answers = 6
    else:
        num_Answers = 8

    for i in range(1, num_Answers + 1):
        Answers.append([i, detectObjects(Image.open(problem.figures[str(i)].visualFilename))])

    Delta = []
    for obj1, obj2 in zip(AObjs, BObjs):
        Delta.append(calculateObjectDelta(obj1, obj2)[0])

    Candidate = []
    for obj1, delta in zip(CObjs, Delta):
        Candidate.append(applyObjectDelta(obj1, delta))

    solutions = PickAnswer(Candidate, Answers)
    for x in solutions:
        print x

    print

    return solutions[0][1]
    
def __main__(problem):
    
    if problem.problemType == '2x2':
        votes = []
        votes.append(visualNew('A', 'B', 'C', problem))
        votes.append(visualNew('A', 'C', 'B', problem))
        #print '------------------------------------------------------------------------------------NEW THING', votes, max(set(votes), key=votes.count), problem.correctAnswer
        return max(set(votes), key=votes.count)
    else:
        votes = []
        votes.append(visualNew('A', 'B', 'H', problem))
        votes.append(visualNew('B', 'C', 'H', problem))
        votes.append(visualNew('D', 'E', 'H', problem))
        votes.append(visualNew('E', 'F', 'H', problem))
        votes.append(visualNew('G', 'H', 'H', problem))
        votes.append(visualNew('A', 'D', 'F', problem))
        votes.append(visualNew('B', 'E', 'F', problem))
        votes.append(visualNew('C', 'F', 'F', problem))
        votes.append(visualNew('D', 'G', 'F', problem))
        votes.append(visualNew('E', 'H', 'F', problem))
        votes.append(visualNew('A', 'E', 'E', problem))
        votes.append(visualNew('D', 'H', 'E', problem))
        votes.append(visualNew('B', 'F', 'E', problem))
        #print '------------------------------------------------------------------------------------NEW THING', votes, max(set(votes), key=votes.count), problem.correctAnswer
        return max(set(votes), key=votes.count)

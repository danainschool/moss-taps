__author__ = 'nf682d'

import copy

def __main__(problem):
    if problem.problemType == '2x2':
        votes = []
        votes.append(SimpleVerbal('A', 'B', 'C', copy.deepcopy(problem), 6))
        votes.append(SimpleVerbal('A', 'C', 'B', copy.deepcopy(problem), 6))
        #print '------------------------------------------------------------------------------------NEW THING', votes, max(set(votes), key=votes.count), problem.correctAnswer
        return max(set(votes), key=votes.count)
    else:
        votes = []
        votes.append(SimpleVerbal('G', 'H', 'H', copy.deepcopy(problem), 8))
        votes.append(SimpleVerbal('C', 'F', 'F', copy.deepcopy(problem), 8))

        #print '------------------------------------------------------------------------------------NEW THING', votes, max(set(votes), key=votes.count), problem.correctAnswer
        if -1 not in votes:
            return max(set(votes), key=votes.count)
        else:
            return -1

def SimpleVerbal(A, B, C, problem, numProblems):
    AShapes = countShapes(problem.figures[A].objects)
    BShapes = countShapes(problem.figures[B].objects)

    ABdelta = delta(AShapes, BShapes)

    print ABdelta

    best = 999999
    answer = -1
    scores = []

    for i in range(1, numProblems+1):
        candidate = delta(countShapes(problem.figures[C].objects), countShapes(problem.figures[str(i)].objects))
        score = compairDelta(ABdelta, candidate)
        scores.append(score)
        print i, candidate, score
        if score < best:
            answer = i
            best = score

    if scores.count(min(scores)) > 1:
        return -1
    else:
        return answer

def countShapes(objects):
    shapes = {}
    for obj in objects:
        shape = objects[obj].attributes['shape']
        if shape in shapes:
            shapes[shape] += 1

        else:
            shapes[shape] = 1

    return shapes

def delta(AShapes, BShapes):
    delta = {}

    for x in AShapes:
        if x in BShapes:
            if BShapes[x] != AShapes[x]:
                delta[x] = BShapes[x] - AShapes[x]
        else:
            if x in delta:
                delta[x] -= 1
            else:
                delta[x] = -1

    for x in BShapes:
        if x in AShapes:
            if BShapes[x] != AShapes[x]:
                delta[x] = BShapes[x] - AShapes[x]
        else:
            if x in delta:
                delta[x] += 1
            else:
                delta[x] = 1

    return delta

def compairDelta(Adelta, Bdelta):
    score = 0

    for a in Adelta:
        if a in Bdelta:
            score += abs(Bdelta[a] - Adelta[a])
        else:
            score += abs(Adelta[a])

    for b in Bdelta:
        if b not in Adelta:
            score += abs(Bdelta[b])

    return score
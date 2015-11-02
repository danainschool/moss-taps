__author__ = 'Ian'

import copy
import itertools


def mapping_slow(Aobjects, Bobjects):

    CanadateMapping = {}
    mapping = {}

    # create an initial mapping
    for Akey, Bkey in zip(Aobjects, Bobjects):
        CanadateMapping[Akey] = Bkey

    AobjectsString = []
    for s in Aobjects:
        AobjectsString.append(s)

    BobjectsString = []
    for s in Bobjects:
        BobjectsString.append(s)

    best = 9999

    for A in itertools.permutations(AobjectsString, min(len(BobjectsString), len(AobjectsString))):
        for B in itertools.permutations(BobjectsString, min(len(BobjectsString), len(AobjectsString))):
            CanadateMapping = {}
            for a, b in zip(A, B):
                CanadateMapping[a] = b
            score = 0

            for obj in CanadateMapping:
                score += comapareObjects(CanadateMapping, Aobjects[obj], Bobjects[CanadateMapping[obj]])

            if score < best:
                mapping = copy.deepcopy(CanadateMapping)
                best = score

    return mapping

def mapping_fast(Aobjects, Bobjects):

    mapping = {}

    for Aobj in Aobjects:
        for Bobj in Bobjects:
            if Bobj not in mapping.values():
                if Aobj in mapping:
                    if comapareObjects(mapping, Aobjects[Aobj], Bobjects[Bobj]) < comapareObjects(mapping, Aobjects[Aobj], Bobjects[mapping[Aobj]]):
                        mapping[Aobj] = Bobj
                else:
                    mapping[Aobj] = Bobj


    return mapping

def mapping(Aobjects, Bobjects):

    if max(len(Aobjects), len(Bobjects)) > 5:
        return mapping_fast(Aobjects, Bobjects)
    else:
        return mapping_slow(Aobjects, Bobjects)

def findTransform(Aobjects, Bobjects, mapping):

    transform = []

    sizeLookup = {'very small': 1,
              'small': 2,
              'medium': 3,
              'large': 4,
              'very large': 5,
              'huge': 6 }

    # add objects
    if len(Aobjects) < len(Bobjects):
        for key in Bobjects:
            if key not in mapping.values():
                transform.append([key, 'add', Bobjects[key]])

    # remove objects
    if len(Aobjects) > len(Bobjects):
        for key in Aobjects:
            if key not in mapping:
                transform.append([key, 'remove'])

    for key in mapping:
        for AAttr in Aobjects[key].attributes:
            for BAttr in Bobjects[mapping[key]].attributes:
                if AAttr == BAttr:
                    if Aobjects[key].attributes[AAttr] != Bobjects[mapping[key]].attributes[BAttr]:

                        if AAttr == 'angle':
                            # mirror
                            mirrorAngle = abs(90 - (int(Aobjects[key].attributes[AAttr]) % 180))
                            mirrorAngle2 = 90 - mirrorAngle

                            Aangle = int(Bobjects[mapping[key]].attributes[BAttr])
                            Bangle = int(Aobjects[key].attributes[AAttr])

                            if Bangle == (Aangle + 2*mirrorAngle) % 360:
                                if Bangle - mirrorAngle == 0 or Bangle - mirrorAngle == 180:
                                    transform.append([key, 'mirror', 'H', 2*mirrorAngle])
                                else:
                                    transform.append([key, 'mirror', 'V', 2*mirrorAngle])

                            elif Bangle == (Aangle - 2*mirrorAngle) % 360:
                                if Bangle + mirrorAngle == 0 or Bangle + mirrorAngle == 180:
                                    transform.append([key, 'mirror', 'H', 2*mirrorAngle])
                                else:
                                    transform.append([key, 'mirror', 'V', 2*mirrorAngle])

                            elif Bangle == (Aangle + 2*mirrorAngle2) % 360:
                                if Bangle - mirrorAngle2 == 0 or Bangle - mirrorAngle2 == 180:
                                    transform.append([key, 'mirror', 'H', 2*mirrorAngle2])
                                else:
                                    transform.append([key, 'mirror', 'V', 2*mirrorAngle2])

                            elif Bangle == (Aangle + 2*mirrorAngle2) % 360:
                                if Bangle + mirrorAngle2 == 0 or Bangle + mirrorAngle2 == 180:
                                    transform.append([key, 'mirror', 'H', 2*mirrorAngle2])
                                else:
                                    transform.append([key, 'mirror', 'V', 2*mirrorAngle2])

                            else:
                                transform.append([key, 'rotation',  int(Bobjects[mapping[key]].attributes[BAttr]) - int(Aobjects[key].attributes[AAttr])])

                        elif AAttr in ['above', 'inside', 'left-of', 'right-of', 'below', 'overlaps']:
                            Aelements = Aobjects[key].attributes[AAttr].split(',')
                            Belements = Bobjects[mapping[key]].attributes[AAttr].split(',')
                            for a,b in zip(Aelements, Belements):
                                if a in mapping and mapping[a] != b:
                                    transform.append([key, 'move', Bobjects[mapping[key]].attributes[BAttr]])

                        elif AAttr == 'alignment':
                            a = Aobjects[key].attributes[AAttr].split('-')
                            b = Bobjects[mapping[key]].attributes[BAttr].split('-')

                            if a[0] != b[0]:
                                transform.append([key, 'translated', b[0]])
                            if a[1] != b[1]:
                                transform.append([key, 'translated', b[1]])

                        elif AAttr == 'size':
                            ASize = sizeLookup[Aobjects[key].attributes[AAttr]]
                            BSize = sizeLookup[Bobjects[mapping[key]].attributes[BAttr]]
                            transform.append([key, 'size_change', BSize - ASize])

                        elif AAttr in ['width', 'height']:
                            ASize = sizeLookup[Aobjects[key].attributes[AAttr]]
                            BSize = sizeLookup[Bobjects[mapping[key]].attributes[BAttr]]
                            transform.append([key, 'width_height_change', AAttr, BSize - ASize])

                        else:
                            transform.append([key, AAttr, Bobjects[mapping[key]].attributes[BAttr]])

    return transform

def applyTransform(start, transform, mapping):

    solution = start

    sizeLookup = {'very small': 1,
          'small': 2,
          'medium': 3,
          'large': 4,
          'very large': 5,
          'huge': 6 }

    for trans in transform:
        if trans[1] in 'add':
            solution[trans[0]] = trans[2]

        elif trans[0] in mapping and mapping[trans[0]] in solution:
            currentObj = mapping[trans[0]]
            if trans[1] in 'rotation':
                if 'angle' in solution[currentObj].attributes:
                    solution[currentObj].attributes['angle'] = str((int(solution[currentObj].attributes['angle']) + trans[2]) % 360)

            elif trans[1] in 'mirror':
                currentAngle = int(solution[currentObj].attributes['angle'])
                if trans[2] == 'H':

                    if 90 < currentAngle < 180 or currentAngle > 270:
                        solution[currentObj].attributes['angle'] = str((int(solution[currentObj].attributes['angle']) + trans[3]) % 360)

                    else:
                        solution[currentObj].attributes['angle'] = str((int(solution[currentObj].attributes['angle']) - trans[3]) % 360)

                else:
                    if currentAngle < 90 or 180 < currentAngle < 270:
                        solution[currentObj].attributes['angle'] = str((int(solution[currentObj].attributes['angle']) + trans[3]) % 360)

                    else:
                        solution[currentObj].attributes['angle'] = str((int(solution[currentObj].attributes['angle']) - trans[3]) % 360)

            elif trans[1] in 'remove':
                del solution[currentObj]

            elif trans[1] in 'translated':
                current = solution[currentObj].attributes['alignment'].split('-')

                if trans[2] in ['top', 'bottom']:
                    solution[currentObj].attributes['alignment'] = trans[2] + '-' + current[1]
                else:
                    solution[currentObj].attributes['alignment'] = current[0] + '-' + trans[2]

            elif trans[1] in 'size_change':
                current_size = sizeLookup[solution[currentObj].attributes['size']]
                new_size = current_size + trans[2]
                new_size_name = ''

                for key, value in sizeLookup.iteritems():
                    if value == new_size:
                        new_size_name = key

                solution[currentObj].attributes['size'] = new_size_name

            elif trans[1] in 'width_height_change':
                if trans[2] in solution[currentObj].attributes:
                    current_size = sizeLookup[solution[currentObj].attributes[trans[2]]]
                    new_size = current_size + trans[3]
                    new_size_name = ''

                    for key, value in sizeLookup.iteritems():
                        if value == new_size:
                            new_size_name = key

                    solution[currentObj].attributes[trans[2]] = new_size_name

                    if 'width' in solution[currentObj].attributes and 'height' in solution[currentObj].attributes:
                        if solution[currentObj].attributes['width'] == solution[currentObj].attributes['height']:
                            solution[currentObj].attributes['size'] = solution[currentObj].attributes['width']
                            del solution[currentObj].attributes['width']
                            del solution[currentObj].attributes['height']

            else:
                solution[currentObj].attributes[trans[1]] = trans[2]

    return solution

def comapareObjects(mapping, ob1, ob2):

    Attr_costs = {'shape': 7,
                  'size': 6,
                  'inside': 1,
                  'angle': 3,
                  'alignment': 5,
                  'above': 2,
                  'left-of': 1,
                  'right-of': 1,
                  'below': 1,
                  'overlaps': 1,
                  'width': 1,
                  'height': 1,
                  'fill': 2 }

    sizeLookup = {'very small': 1,
                  'small': 2,
                  'medium': 3,
                  'large': 4,
                  'very large': 5,
                  'huge': 6 }

    score = 0

    for key in ob1.attributes:
        if key not in ob2.attributes:
            score += 5

    for key in ob2.attributes:
        if key not in ob1.attributes:
            score += 5

    for key in ob1.attributes:
        if key in ob2.attributes and ob1.attributes[key] != ob2.attributes[key]:
            if key in ['above', 'inside', 'left-of', 'right-of', 'below', 'overlaps']:
                ob1Attr = ob1.attributes[key].split(',')
                ab2Attr = ob2.attributes[key].split(',')
                for attr in ob1Attr:
                    if attr in mapping:
                        if mapping[attr] not in ab2Attr:
                            score += Attr_costs[key]
                            break
                    else:
                        score += Attr_costs[key]
            elif key == 'size':
                score += abs(sizeLookup[ob1.attributes[key]] - sizeLookup[ob2.attributes[key]])*2

            else:
                score += Attr_costs[key]

    return score

def verbalNew(A, B, C, problem, numAnswers):

    # Find mapping between squares
    ABMapping = mapping(problem.figures[A].objects, problem.figures[B].objects)
    ACMapping = mapping(problem.figures[A].objects, problem.figures[C].objects)

    print "Mapping"
    print A + '-' + B, ABMapping
    print C + '-#', ACMapping

    # find the transform to match mapping
    Transform = findTransform(problem.figures[A].objects, problem.figures[B].objects, ABMapping)

    print "transform"
    print Transform

    # Apply Transform
    solution = applyTransform(problem.figures[C].objects, Transform, ACMapping)

    print 'solution'
    for key in solution:
        print key, ':', solution[key].attributes

    # compare to the answers

    answerScores = []

    for answ in range(1, numAnswers + 1):
        mappedSolution = mapping(solution, problem.figures[str(answ)].objects)
        print 'Answer', answ, 'Mapped solution', mappedSolution
        score = 0
        for keys in mappedSolution:
            score += comapareObjects(mappedSolution, solution[keys], problem.figures[str(answ)].objects[mappedSolution[keys]])

        # missing objects
        if len(solution) != len(problem.figures[str(answ)].objects):
            score += abs(len(solution) - len(problem.figures[str(answ)].objects)) * 10
        answerScores.append([score, answ])

    print sorted(answerScores)
    print 'answer ', sorted(answerScores)[0][1]
    #print 'correct answer ', problem.correctAnswer
    print
    return sorted(answerScores)[0][1]

def __main__(problem):

    if problem.problemType == '2x2':
        votes = []
        votes.append(verbalNew('A', 'B', 'C', copy.deepcopy(problem), 6))
        votes.append(verbalNew('A', 'C', 'B', copy.deepcopy(problem), 6))
        #print '------------------------------------------------------------------------------------', votes, max(set(votes), key=votes.count), problem.correctAnswer
        return max(set(votes), key=votes.count)
    else:
        votes = []
        votes.append(verbalNew('A', 'B', 'H', copy.deepcopy(problem), 8))
        votes.append(verbalNew('B', 'C', 'H', copy.deepcopy(problem), 8))
        votes.append(verbalNew('D', 'E', 'H', copy.deepcopy(problem), 8))
        votes.append(verbalNew('E', 'F', 'H', copy.deepcopy(problem), 8))
        votes.append(verbalNew('G', 'H', 'H', copy.deepcopy(problem), 8))
        votes.append(verbalNew('A', 'D', 'F', copy.deepcopy(problem), 8))
        votes.append(verbalNew('B', 'E', 'F', copy.deepcopy(problem), 8))
        votes.append(verbalNew('C', 'F', 'F', copy.deepcopy(problem), 8))
        votes.append(verbalNew('D', 'G', 'F', copy.deepcopy(problem), 8))
        votes.append(verbalNew('E', 'H', 'F', copy.deepcopy(problem), 8))
        votes.append(verbalNew('A', 'E', 'E', copy.deepcopy(problem), 8))
        votes.append(verbalNew('D', 'H', 'E', copy.deepcopy(problem), 8))
        votes.append(verbalNew('B', 'F', 'E', copy.deepcopy(problem), 8))
        #print '------------------------------------------------------------------------------------NEW THING', votes, max(set(votes), key=votes.count), problem.correctAnswer
        return max(set(votes), key=votes.count)

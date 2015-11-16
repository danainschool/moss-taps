import semanticNetwork
import json

def generate(problem):
    a = problem.figures["A"]
    b = problem.figures["B"]
    c = problem.figures["C"]
    s1 = problem.figures["1"]
    s2 = problem.figures["2"]
    s3 = problem.figures["3"]
    s4 = problem.figures["4"]
    s5 = problem.figures["5"]
    s6 = problem.figures["6"]

    attributes = set([key for figure in problem.figures for obj in problem.figures[figure].objects for key in problem.figures[figure].objects[obj].attributes.keys()])
    networks = {
        "a": {},
        "b": {},
        "c": {}
    }

    networks['a']['b'] = semanticNetwork.create(a, b, attributes)
    networks['a']['c'] = semanticNetwork.create(a, c, attributes)
    networks['b']['c'] = semanticNetwork.create(b, c, attributes)

    networks['b']['1'] = semanticNetwork.create(b, s1, attributes)
    networks['b']['2'] = semanticNetwork.create(b, s2, attributes)
    networks['b']['3'] = semanticNetwork.create(b, s3, attributes)
    networks['b']['4'] = semanticNetwork.create(b, s4, attributes)
    networks['b']['5'] = semanticNetwork.create(b, s5, attributes)
    networks['b']['6'] = semanticNetwork.create(b, s6, attributes)

    networks['c']['1'] = semanticNetwork.create(c, s1, attributes)
    networks['c']['2'] = semanticNetwork.create(c, s2, attributes)
    networks['c']['3'] = semanticNetwork.create(c, s3, attributes)
    networks['c']['4'] = semanticNetwork.create(c, s4, attributes)
    networks['c']['5'] = semanticNetwork.create(c, s5, attributes)
    networks['c']['6'] = semanticNetwork.create(c, s6, attributes)

    networks['a']['1'] = semanticNetwork.create(a, s1, attributes)
    networks['a']['2'] = semanticNetwork.create(a, s2, attributes)
    networks['a']['3'] = semanticNetwork.create(a, s3, attributes)
    networks['a']['4'] = semanticNetwork.create(a, s4, attributes)
    networks['a']['5'] = semanticNetwork.create(a, s5, attributes)
    networks['a']['6'] = semanticNetwork.create(a, s6, attributes)

    return networks

def test(networks):
    absig = json.dumps(networks['a']['b'])
    c1sig = json.dumps(networks['c']['1'])
    c2sig = json.dumps(networks['c']['2'])
    c3sig = json.dumps(networks['c']['3'])
    c4sig = json.dumps(networks['c']['4'])
    c5sig = json.dumps(networks['c']['5'])
    c6sig = json.dumps(networks['c']['6'])

    acsig = json.dumps(networks['a']['c'])
    b1sig = json.dumps(networks['b']['1'])
    b2sig = json.dumps(networks['b']['2'])
    b3sig = json.dumps(networks['b']['3'])
    b4sig = json.dumps(networks['b']['4'])
    b5sig = json.dumps(networks['b']['5'])
    b6sig = json.dumps(networks['b']['6'])

    bcsig = json.dumps(networks['b']['c'])
    a1sig = json.dumps(networks['a']['1'])
    a2sig = json.dumps(networks['a']['2'])
    a3sig = json.dumps(networks['a']['3'])
    a4sig = json.dumps(networks['a']['4'])
    a5sig = json.dumps(networks['a']['5'])
    a6sig = json.dumps(networks['a']['6'])

    solutions = [0, 0, 0, 0, 0, 0]

    if absig == c1sig:
        solutions[0] += 1
    if absig == c2sig:
        solutions[1] += 1
    if absig == c3sig:
        solutions[2] += 1
    if absig == c4sig:
        solutions[3] += 1
    if absig == c5sig:
        solutions[4] += 1
    if absig == c6sig:
        solutions[5] += 1

    if acsig == b1sig:
        solutions[0] += 1
    if acsig == b2sig:
        solutions[1] += 1
    if acsig == b3sig:
        solutions[2] += 1
    if acsig == b4sig:
        solutions[3] += 1
    if acsig == b5sig:
        solutions[4] += 1
    if acsig == b6sig:
        solutions[5] += 1

    if bcsig == a1sig:
        solutions[0] += 1
    if bcsig == a2sig:
        solutions[1] += 1
    if bcsig == a3sig:
        solutions[2] += 1
    if bcsig == a4sig:
        solutions[3] += 1
    if bcsig == a5sig:
        solutions[4] += 1
    if bcsig == a6sig:
        solutions[5] += 1

    print json.dumps(solutions)

    best = -1
    val = -1
    dup = False
    for solution, count in enumerate(solutions):
        if count > val:
            best = solution
            val = count
            dup = False
        elif count == val:
            dup = True
    if dup or val == -1 or val == 0:
        return 0
    else:
        return best + 1

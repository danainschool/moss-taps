import semanticNetwork
import json

def generate(problem):
    a = problem.figures["A"]
    b = problem.figures["B"]
    c = problem.figures["C"]
    d = problem.figures["D"]
    e = problem.figures["E"]
    f = problem.figures["F"]
    g = problem.figures["G"]
    h = problem.figures["H"]
    s1 = problem.figures["1"]
    s2 = problem.figures["2"]
    s3 = problem.figures["3"]
    s4 = problem.figures["4"]
    s5 = problem.figures["5"]
    s6 = problem.figures["6"]
    s7 = problem.figures["7"]
    s8 = problem.figures["8"]

    attributes = set([key for figure in problem.figures for obj in problem.figures[figure].objects for key in problem.figures[figure].objects[obj].attributes.keys()])

    networks = {
        "ver": [],
        "hor": [],
        "dia": [],
        1: {},
        2: {},
        3: {},
        4: {},
        5: {},
        6: {},
        7: {},
        8: {},
    }

    networks['hor'].append(semanticNetwork.create(a, b, attributes))
    networks['hor'].append(semanticNetwork.create(b, c, attributes))
    networks['hor'].append(semanticNetwork.create(d, e, attributes))
    networks['hor'].append(semanticNetwork.create(e, f, attributes))
    networks['hor'].append(semanticNetwork.create(g, h, attributes))

    networks['ver'].append(semanticNetwork.create(a, d, attributes))
    networks['ver'].append(semanticNetwork.create(d, g, attributes))
    networks['ver'].append(semanticNetwork.create(b, e, attributes))
    networks['ver'].append(semanticNetwork.create(e, h, attributes))
    networks['ver'].append(semanticNetwork.create(c, f, attributes))

    networks['dia'].append(semanticNetwork.create(a, e, attributes))
    networks['dia'].append(semanticNetwork.create(b, f, attributes))
    networks['dia'].append(semanticNetwork.create(d, h, attributes))

    networks[1]['hor'] = semanticNetwork.create(h, s1, attributes)
    networks[1]['ver'] = semanticNetwork.create(f, s1, attributes)
    networks[1]['dia'] = semanticNetwork.create(e, s1, attributes)
    networks[2]['hor'] = semanticNetwork.create(h, s2, attributes)
    networks[2]['ver'] = semanticNetwork.create(f, s2, attributes)
    networks[2]['dia'] = semanticNetwork.create(e, s2, attributes)
    networks[3]['hor'] = semanticNetwork.create(h, s3, attributes)
    networks[3]['ver'] = semanticNetwork.create(f, s3, attributes)
    networks[3]['dia'] = semanticNetwork.create(e, s3, attributes)
    networks[4]['hor'] = semanticNetwork.create(h, s4, attributes)
    networks[4]['ver'] = semanticNetwork.create(f, s4, attributes)
    networks[4]['dia'] = semanticNetwork.create(e, s4, attributes)
    networks[5]['hor'] = semanticNetwork.create(h, s5, attributes)
    networks[5]['ver'] = semanticNetwork.create(f, s5, attributes)
    networks[5]['dia'] = semanticNetwork.create(e, s5, attributes)
    networks[6]['hor'] = semanticNetwork.create(h, s6, attributes)
    networks[6]['ver'] = semanticNetwork.create(f, s6, attributes)
    networks[6]['dia'] = semanticNetwork.create(e, s6, attributes)
    networks[7]['hor'] = semanticNetwork.create(h, s7, attributes)
    networks[7]['ver'] = semanticNetwork.create(f, s7, attributes)
    networks[7]['dia'] = semanticNetwork.create(e, s7, attributes)
    networks[8]['hor'] = semanticNetwork.create(h, s8, attributes)
    networks[8]['ver'] = semanticNetwork.create(f, s8, attributes)
    networks[8]['dia'] = semanticNetwork.create(e, s8, attributes)

    return networks

def test(networks, transformer):
    solutions = [0, 0, 0, 0, 0, 0, 0, 0]

    for i in range(1, 9):
        for s in networks['hor']:
            if json.dumps(transformer(networks[i]['hor'])) == json.dumps(transformer(s)):
                solutions[i-1] += 1
        for s in networks['ver']:
            if json.dumps(transformer(networks[i]['ver'])) == json.dumps(transformer(s)):
                solutions[i-1] += 1
        for s in networks['dia']:
            if json.dumps(transformer(networks[i]['dia'])) == json.dumps(transformer(s)):
                solutions[i-1] += 1

    return solutions
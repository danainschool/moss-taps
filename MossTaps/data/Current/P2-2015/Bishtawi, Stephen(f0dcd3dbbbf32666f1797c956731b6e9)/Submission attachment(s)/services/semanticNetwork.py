import json

size = {
    "": 0,
    "very small": 1,
    "small": 2,
    "medium": 3,
    "large": 4,
    "very large": 5,
    "huge": 6,
    "very huge": 7
}

def create(figx, figy, attributes):
    #print figx.name + " : " + figy.name
    xMappings = {}
    yMappings = {}
    for xObjName in figx.objects:
        xObj = figx.objects[xObjName]
        xMappings[xObjName] = {}
        for yObjName in figy.objects:
            yObj = figy.objects[yObjName]
            if yObjName not in yMappings:
                yMappings[yObjName] = {}
            similarity = 0
            differences = []
            for attrName in attributes:
                xAttr = xObj.attributes.get(attrName, '')
                yAttr = yObj.attributes.get(attrName, '')
                #print attrName + ": " + xAttr + ' - ' + yAttr
                if xAttr == yAttr:
                    similarity += 2
                else:
                    if attrName == 'inside' or attrName == 'above' or attrName == 'left-of' or attrName == 'overlaps':
                        if xAttr != '' and yAttr != '' and len(xAttr.split(',')) == len(yAttr.split(',')):
                            similarity += 2
                        else:
                            differences.append(attrName)
                    elif attrName == 'angle':
                        if xAttr == '':
                            xAttr = '0'
                        if yAttr == '':
                            yAttr = '0'
                        if xAttr == yAttr:
                            similarity += 2
                        else:
                            x = int(xAttr)
                            y = int(yAttr)
                            if x % 90 == y % 90:
                                similarity += 1
                            differences.append(attrName)
                    elif attrName == 'alignment':
                        if xAttr != '' and yAttr != '':
                            x = xAttr.split('-')
                            y = yAttr.split('-')
                            if len(x) == len(y) and len(x) == 2 and (x[0] == y[0] or x[1] == y[1]):
                                similarity += 1
                        differences.append(attrName)
                    elif attrName == 'size' or attrName == 'height' or attrName == 'width':
                        xSize = size[xAttr]
                        ySize = size[yAttr]
                        differences.append(attrName + ':' + str(xSize < ySize))
                    elif attrName == 'fill':
                        differences.append(attrName + ':' + str(xAttr < yAttr))
                    else:
                        differences.append(attrName)
            mapping = {'sim': similarity, 'diff': differences}
            xMappings[xObjName][yObjName] = mapping
            yMappings[yObjName][xObjName] = mapping
    #print json.dumps(xMappings)
    #print json.dumps(yMappings)
    finalMapping = []
    while len(xMappings) or len(yMappings):
        if len(xMappings) == 0 and len(yMappings) > 0:
            for i in range(len(yMappings)):
                finalMapping.append({
                    'diff': ['add']
                })
            break
        elif len(xMappings) > 0 and len(yMappings) == 0:
            for i in range(len(xMappings)):
                finalMapping.append({
                    'diff': ['remove']
                })
            break

        best = -1
        candidateX = None
        candidateY = None
        for xname in xMappings:
            xMaps = xMappings[xname]
            for yname in xMaps:
                yMaps = xMaps[yname]
                if yMaps['sim'] > best or (yMaps['sim'] == best and len(yMaps['diff']) < len(xMappings[candidateX][candidateY]['diff'])):
                    best = yMaps['sim']
                    candidateX = xname
                    candidateY = yname
        if xMappings[candidateX][candidateY]['diff']:
            finalMapping.append(xMappings[candidateX][candidateY])
        del xMappings[candidateX]
        del yMappings[candidateY]
        for xname in xMappings:
            del xMappings[xname][candidateY]
        for yname in yMappings:
            del yMappings[yname][candidateX]
    finalMapping.sort()
    #print json.dumps(finalMapping)
    return finalMapping

def identity(network):
    return network

def simplify(network):
    simplified = json.loads(json.dumps(network))
    add = False
    remove = False
    dups = []
    for obj in simplified:
        if 'diff' in obj:
            if 'inside' in obj['diff']:
                obj['diff'].remove('inside')
            if 'above' in obj['diff']:
                obj['diff'].remove('above')
            if 'left-of' in obj['diff']:
                obj['diff'].remove('left-of')
            if 'overlaps' in obj['diff']:
                obj['diff'].remove('overlaps')
            if not obj['diff']:
                dups.append(obj)
            elif 'add' in obj['diff']:
                if add:
                    dups.append(obj)
                else:
                    add = True
            elif 'remove' in obj['diff']:
                if remove:
                    dups.append(obj)
                else:
                    remove = True
    for obj in dups:
        simplified.remove(obj)
    return simplified

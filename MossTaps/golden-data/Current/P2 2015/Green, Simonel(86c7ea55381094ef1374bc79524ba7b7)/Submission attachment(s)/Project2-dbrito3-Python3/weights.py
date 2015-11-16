class AttributeChangeWeights():
  _weights = {
    'shape': 10,
    'size': 2,
    'fill': 1,
    'alignment': 2,
    'above': 5,
    'overlaps': 5,
    'angle': 2,
    'inside': 5
  }
  default_weight = 1
  default_delta = 1

  def addAttribute(self, attribute, weight=default_weight):
    self._weights[attribute] = weight

  def getWeight(self, attribute):
    if attribute not in self._weights:
      self.addAttribute(attribute)

    return self._weights[attribute]

  def increaseWeight(attribute, add_amt=default_delta):
    self._weights[attribute] += add_amt

  def decreaseWeight(attribute, remove_amt=default_delta):
    self._weights[attribute] -= remove_amt

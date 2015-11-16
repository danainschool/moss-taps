# Things to try:
# For each square, check for exact match (shift cropping, pixel by pixel). Otherwise...
# Check for similarity with RMS? Fill shape comparison? size of difference?)
# see why combining transformations is failing
# add rotation of 45 and 90 in both directions

from PIL import Image
from PIL import ImageOps
from PIL import ImageChops
from PIL import ImageStat

import math
import statistics
import sys

from enum import Enum, unique

from ImageDetails import ImageDetails

this = sys.modules[__name__]

# removed adjacent duplicates
# def __remove_adjacent(seq):
#   i = 1
#   n = len(seq)
#   while i < n:
#     if seq[i] == seq[i-1]:
#       del seq[i]
#       n -= 1
#     else:
#       i += 1

def some(a, function):
  for item in a:
    if function(item):
      return True
  return False

def listsMatch(a, b):
  if len(a) != len(b):
    return False
  else:
    for i in range(len(a)):
      if a[i] != b[i]:
        return False
    return True

@unique
class TransformationAction(Enum):
  flip_v = 1
  flip_h = 2

class Transformation:
  actions = []

  def __init__(self, actions=[], action=None):
    self.actions = actions[:]
    if action:
      self.actions.append(action)
    self._simplifyActions()

  def _simplifyActionsR(self):
    # this method should collapse duplicate operations that result in no ops,
    # i.e. two flip_v in a row, or more complex sequences

    # detect adjacent duplicates
    a = self.actions
    i = 1
    n = len(a)
    while i < n:
      if a[i] == a[i-1]:
        if a[i] == TransformationAction.flip_h or a[i] == TransformationAction.flip_v:
          del a[i]
          n -= 1
        else:
          i += 1
      else:
        i += 1

    # detect adjacent duplicate pairs of flip_h and flip_v
    i = 0
    n = len(a) - 1
    while i + 1 < n:
      current_is_flip_h = a[i] == TransformationAction.flip_h
      next_is_flip_h = a[i + 1] == TransformationAction.flip_h
      adjacent_flip_h = current_is_flip_h and next_is_flip_h

      current_is_flip_v = a[i] == TransformationAction.flip_v
      next_is_flip_v = a[i + 1] == TransformationAction.flip_v
      adjacent_flip_v = current_is_flip_v and next_is_flip_v

      if adjacent_flip_h or adjacent_flip_v:
        del a[i]
        del a[i]
      else:
        i += 1

    # detect no op sequences. Maybe could detect repeating sequences instead?
    no_ops = [
      [
        TransformationAction.flip_h,
        TransformationAction.flip_v,
        TransformationAction.flip_h,
        TransformationAction.flip_v
      ],
      [
        TransformationAction.flip_v,
        TransformationAction.flip_h,
        TransformationAction.flip_v,
        TransformationAction.flip_h
      ]
    ]
    for no_op_sequence in no_ops:
      i = 0
      n = len(a) - 1
      seq_len = len(no_op_sequence)
      while i + seq_len < n:
        compare_to = a[i:seq_len]

        if listsMatch(no_op_sequence, compare_to):
          del a[i:seq_len]
        else:
          i += 1
    self.actions = a

  def _simplifyActions(self):
    initial = len(self.actions)
    while True:
      self._simplifyActionsR()
      results = len(self.actions)
      if results < initial:
        initial = results
        continue
      else:
        break

  def hasNoEffect(self):
    return len(self.actions) == 0

  def equals(self, transformation):
    if len(transformation.actions) != len(self.actions):
      return False
    else:
      for i in range(0, len(self.actions)):
        if transformation.actions[i] != self.actions[i]:
          return False
      return True

  def union(self, transformation):
    self.actions = self.actions + transformation.actions
    self._simplifyActions()
    return Transformation(self.actions + transformation.actions)

  def addAction(self, action):
    self.actions.append(action)
    self._simplifyActions()
    return self

  def addActions(self, actions):
    self.actions = self.actions + actions
    self._simplifyActions()
    return self

  def applyTo(self, image):
    for action in self.actions:
      if action == TransformationAction.flip_v:
        image = ImageOps.flip(image)

      elif action == TransformationAction.flip_h:
        image = ImageOps.mirror(image)

    return image

  def getComplexity(self):
    return len(self.actions)

  def __str__(self):
    return self.__repr__()[0:-1] + ': ' + str(self.actions) + ' ' + self.__repr__()[-1:]

def percentDifference(a, b):
  return abs(a - b) / ((a + b)/2)

def rootMeanSquare(a, b):
  diff_image = ImageChops.difference(a, b)
  histogram = diff_image.histogram() # just use the red channel
  squares = (value * ((index%256)**2) for index, value in enumerate(histogram))
  sum_of_squares = sum(squares)
  rms = math.sqrt(sum_of_squares / float(a.size[0] * a.size[1]))
  return rms

def imagesAreExactlyEqual(a, b):
  num_pixels = a.size[0] * a.size[1]
  diff_image = ImageChops.difference(ImageDetails(a).image_bw, ImageDetails(b).image_bw)
  return diff_image.histogram()[0] == num_pixels

def imagesAreMostlyEqual(a, b, threshold=0.005):
  a_details = ImageDetails(a)
  b_details = ImageDetails(b)

  for i in range(a_details.num_sectors_per_dim):
    for j in range(a_details.num_sectors_per_dim):
      percent_diff = this.percentDifference(a_details.black_px_by_sector[i][j], b_details.black_px_by_sector[i][j])
      if percent_diff > threshold:
        return False

  return True

  return pixels

def findMatchingImage(match, image_set):
  for image_name in image_set:
    if this.imagesAreMostlyEqual(match, image_set[image_name]):
      return image_name

  return None

def imagesAreFlippedVertically(a, b):
  return this.imagesAreMostlyEqual(ImageOps.flip(a), b)

def imagesAreFlippedHorizontally(a, b):
  return this.imagesAreMostlyEqual(ImageOps.mirror(a), b)

def getSingleStepTransformation(a, b):
  transformation = Transformation()

  if this.imagesAreExactlyEqual(a, b):
    return transformation

  elif this.imagesAreFlippedVertically(a, b):
    transformation.addAction(TransformationAction.flip_v)

  elif this.imagesAreFlippedHorizontally(a, b):
    transformation.addAction(TransformationAction.flip_h)

  else:
    return None

  return transformation

def generateTransformationCombinations(max_operations):
  transformations = [];
  for i in range(max_operations):
    if i == 0:
      for action in TransformationAction:
        transformations.append(Transformation(action=action))
    else:
      moar_transformations = []
      for transformation in transformations:
        for action in TransformationAction:
          moar_transformations.append(transformation.union(Transformation(action=action)))

      transformations = transformations + moar_transformations
  return transformations;


def getMultiStepTransformation(a, b, max_operations):
  transformations = this.generateTransformationCombinations(max_operations)

  matching_transformations = []
  for transformation in transformations:
    transformed_a = transformation.applyTo(a)
    if this.imagesAreMostlyEqual(transformed_a, b):
      matching_transformations.append(transformation)

  # choose the least complex one for now
  if len(matching_transformations) > 0:
    least_complex = matching_transformations[0]
    for i in range(0, len(matching_transformations) - 1):
      if matching_transformations[i].getComplexity() < least_complex.getComplexity():
        least_complex = matching_transformations[i]
    return least_complex

  else:
    return None

def getTransformation(a, b):
  transform = this.getSingleStepTransformation(a, b)
  if not transform:
    transform = this.getMultiStepTransformation(a, b, 3)

  if not transform:
    return None
  else:
    return transform

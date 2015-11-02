from PIL import Image
from PIL import ImageOps
from PIL import ImageChops
from PIL import ImageStat

import math
import statistics
import sys

from enum import Enum

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

class TransformationAction(Enum):
  flip_v = 1
  flip_h = 2

class Transformation:
  def __init__(self, actions=[], action=None):
    self.actions = actions
    if action:
      self.actions.append(action)
    self.__simplifyActions()

  def __simplifyActionsR(self):
    # this method should collapse duplicate operations that result in no ops,
    # i.e. two flip_v in a row, or more complex sequences

    # detect adjacent duplicates
    a = self.actions
    i = 1
    n = len(a)
    while i < n:
      if a[i] is a[i-1]:
        if a[i] is TransformationAction.flip_h or a[i] is TransformationAction.flip_v:
          del a[i]
          n -= 1
        else:
          i += 1
      else:
        i += 1

    # detect adjacent duplicate pairs of flip_h and flip_v
    i = 1
    n = len(a)
    while i < n:
      if a[n - 1] is TransformationAction.flip_h or a[n - 2] is TransformationAction.flip_v:
        if a[n - 1] is a[n - 3] and a[n - 2] is a[n - 4]:
          del a[n - 1]
          del a[n - 2]
          n -= 2
        else:
          i += 1
      else:
        i += 1

  def __simplifyActions(self):
    initial = len(self.actions)
    while True:
      self.__simplifyActionsR()
      results = len(self.actions)
      if results < initial:
        initial = results
        continue
      else:
        break

  def hasNoEffect(self):
    return len(self.actions) == 0

  def equals(self, transformation):
    if len(transformation.actions) is not len(self.actions):
      return False
    else:
      for i in range(0, len(self.actions)):
        if transformation.actions[i] is not self.actions[i]:
          return False
      return True

  def union(self, transformation):
    self.actions = self.actions + transformation.actions
    self.__simplifyActions()
    return Transformation(self.actions + transformation.actions)

  def addAction(self, action):
    self.actions.append(action)
    self.__simplifyActions()
    return self

  def applyTo(self, image):
    for action in self.actions:
      if action is TransformationAction.flip_v:
        image = ImageOps.flip(image)

      elif action is TransformationAction.flip_h:
        image = ImageOps.mirror(image)

    return image

  def getComplexity(self):
    return len(self.actions)

  def __str__(self):
    return self.__repr__()[0:-1] + ': ' + str(self.actions) + ' ' + self.__repr__()[-1:]

def rootMeanSquare(a, b):
  diff_image = ImageChops.difference(a, b)
  histogram = diff_image.histogram() # just use the red channel
  squares = (value * ((index%256)**2) for index, value in enumerate(histogram))
  sum_of_squares = sum(squares)
  rms = math.sqrt(sum_of_squares / float(a.size[0] * a.size[1]))
  return rms

def imagesAreExactlyEqual(a, b):
  a_pixels = a.load()
  b_pixels = b.load()

  for i in range(0, a.size[0]):
    for j in range(0, a.size[1]):
      # Images at the edges of shapes might be in between black and white due
      # to rendering, so better to compare dark to light than exactly black to
      # exactly white
      a_is_dark = this.isDarkPixel(a_pixels[i, j])
      b_is_dark = this.isDarkPixel(b_pixels[i, j])

      # could try converting to greyscale then scaling
      #bw = image.convet('L').point(lambda x: 0 if x<128 else 255, '1')
      # or a direct conversion
      #image.convert('1')

      if (a_is_dark != b_is_dark):
        return False

  return True

def imagesAreMostlyEqual(a, b):
  if this.imagesAreExactlyEqual(a, b):
    return True
  else:
    rms = this.rootMeanSquare(a, b)
    return rms < 70
  # return this.getDarkPixelDifference(a, b) < 5
  # return this.imagesAreExactlyEqual(a, b)
  # return this.rootMeanSquare(a, b) < 70

def getBlackAndWhitePixelMatrix(image):
  pixels = image.load()

  for i in range(0, image.size[0]):
    for j in range(0, image.size[1]):
      pixel_is_dark = this.isDarkPixel(pixels[i, j])
      if pixel_is_dark:
        pixels[i, j] = (0, 0, 0, 0)
      else:
        pixels[i, j] = (255, 255, 255, 255)

  return pixels

def getHistogramDifference(a, b):
  histogram_a = a.histogram()
  histogram_b = b.histogram()
  histogram_difference = 0
  for i in range(0, 256):
    both_nonzero = histogram_a[i] and histogram_b[i]
    #large_difference = abs(histogram_a[i] - histogram_b[i]) > 10
    if both_nonzero:
      histogram_difference += abs(histogram_a[i] - histogram_b[i])

  return histogram_difference

def getAvgHistogramDifference(a, b):
  histogram_a = a.histogram()
  histogram_b = b.histogram()
  diff_percentages = []
  # 256 to only check a single channel (R) as there's only black and white anyway
  for i in range(0, 256):
    both_nonzero = histogram_a[i] and histogram_b[i]
    if both_nonzero:
      diff_percentages.append(abs(1 - (histogram_a[i] / histogram_b[i])) * 100)
    else:
      diff_percentages.append(0)

  return statistics.mean(diff_percentages)


def getPixelLuminance(rgb_tuple):
  return 0.3 * rgb_tuple[0] + 0.59 * rgb_tuple[1] + 0.11 * rgb_tuple[2]

def isDarkPixel(rgb_tuple):
  return this.getPixelLuminance(rgb_tuple) > 127

def getDarkPixelDifference(a, b):
  dark_a = this.getDarkPixelCount(a)
  dark_b = this.getDarkPixelCount(b)
  ratio  = dark_a / dark_b
  diff   = abs(1 - ratio)
  return diff * 100

def getDarkPixelCount(image):
  pixels = image.load()
  count = 0
  for i in range(0, image.size[0]):
    for j in range(0, image.size[1]):
      if this.isDarkPixel(pixels[i, j]):
        count += 1
  return count

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
  for i in range(1, max_operations):
    if i is 1:
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
    return transformations

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
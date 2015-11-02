from enum import IntEnum, Enum, unique
from weights import AttributeChangeWeights
import itertools

class SemanticTransformation:
  """Figure transformation delta frame class"""
  _max_sets = 30000000

  correspondence = None
  def __init__(self, start_figure, end_figure, attribute_weights=AttributeChangeWeights()):
    self.start_figure = start_figure
    self.end_figure = end_figure
    self.attribute_weights = attribute_weights

    # Find Matching objects between the two figures
    self.generateCorrespondence()

  def generateCorrespondence(self, score_threshold=None):
    """
      Find and store the most likely set correspondences between the objects in
      each figure. The correspondence should be a frozenset of two-member tuples
      representing pairs of objects that should be matched up. When the length
      of the object lists don't match up, objects will be paired with None
    """

    possible_correspondences = set()
    num_start_obj = len(self.start_figure.objects)
    num_end_obj = len(self.end_figure.objects)

    if num_start_obj + num_end_obj <= 12:
      print('Generating all possible correspondence possibilities...')
      start_object_orders = itertools.permutations(self.start_figure.objects)
      end_object_orders = itertools.permutations(self.end_figure.objects)
      # order_pairings = set(itertools.product(start_object_orders, end_object_orders))
      for order_pairing in itertools.product(start_object_orders, end_object_orders):
        start_order = order_pairing[0]
        end_order = order_pairing[1]
        correspondence = []
        for pairing in itertools.zip_longest(start_order, end_order):
          correspondence.append(pairing)
        possible_correspondences.add(frozenset(correspondence))
    else:
      print('Exhaustive set extrememly large, falling back to heuristics.')
      # Go object by item, choosing lowest cost pairing until out of items,
      # and pair remaining with None
      correspondence = set()
      start_obj_pool = set(self.start_figure.objects)
      end_obj_pool = set(self.end_figure.objects)

      while len(start_obj_pool) > 0:
        start_obj_name = start_obj_pool.pop()
        start_obj = self.start_figure.objects[start_obj_name]

        if len(end_obj_pool) == 0:
          correspondence.add((start_obj.name, None))
        else:
          costs = {}
          for end_obj_name in end_obj_pool:
            end_obj = self.end_figure.objects[end_obj_name]
            transformation = ObjectTransformation(start_obj, end_obj)
            costs[end_obj_name] = transformation.getTransformationCost()

          min_cost = None
          best_match_name = None
          for end_obj_name in costs:
            cost = costs[end_obj_name]
            if not min_cost or cost < min_cost:
              min_cost = cost
              best_match_name = end_obj_name

          correspondence.add((start_obj_name, best_match_name))
          end_obj_pool.remove(best_match_name)

      # Add remaining correspondence pairs for objects in the end figure that
      # are not in the start figure
      if num_end_obj > num_start_obj:
        for remaining_obj_name in end_obj_pool:
          correspondence.add((None, remaining_obj_name))

      possible_correspondences.add(frozenset(correspondence))

    print('Generated', len(possible_correspondences), 'unique correspondence sets, finding best one.')
    lowest_score = None
    best_correspondence = None
    for correspondence in possible_correspondences:
      # print('\nCorrespondence:', correspondence)
      score = self.scoreCorrespondence(correspondence)
      if score_threshold is not None:
        if not lowest_score and score > score_threshold:
          lowest_score = score
          best_correspondence = correspondence
      else:
        if not lowest_score or score < lowest_score:
          lowest_score = score
          best_correspondence = correspondence

    self.correspondence = best_correspondence

  def scoreCorrespondence(self, correspondence):
    score = 0
    for pairing in correspondence:
      fig_a_obj = self.start_figure.objects.get(pairing[0])
      fig_b_obj = self.end_figure.objects.get(pairing[1])
      if not fig_a_obj and not fig_b_obj:
        print('Correspondence is', correspondence, ', pairing is', pairing)
        raise Exception('Faulty correspondence')
      score += ObjectTransformation(fig_a_obj, fig_b_obj).getTransformationCost()
    return score

  @property
  def combined_transformation_score(self):
    return self.scoreCorrespondence(self.correspondence)

  @property
  def score(self):
    return self.combined_transformation_score


  def nextCorrespondence(self):
    """
    Finds the next possible object correspondence mapping and updates the
    transformation using the new mapping.
    """
    self.generateCorrespondence(self.combined_transformation_score)

@unique
class Size(IntEnum):
  """An enumeration for working with object size attributes"""

  tiny = 1
  very_small = 2
  small = 3
  medium = 4
  large = 5
  very_large = 6
  huge = 7

  @staticmethod
  def fromStr(size_name_str):
    if not size_name_str:
      return None

    size_name_str = size_name_str.lower().replace(' ', '_')
    try:
      return Size[size_name_str]
    except e:
      return None

class Alignment():
  """A way of representing object alignment."""
  x = 0
  y = 0

  def __init__(self, alignment_str):
    self.alignment_str = alignment_str

    if 'top' in alignment_str:
      self.y = 1
    elif 'bottom' in alignment_str:
      self.y = -1

    if 'left' in alignment_str:
      self.x = -1
    elif 'right' in alignment_str:
      self.x = 1

  def __str__(self):
    vertical_location = ''
    if self.y < 0:
      vertical_location = 'bottom'
    elif self.y > 0:
      vertical_location = 'top'

    horizontal_location = ''
    if self.y < 0:
      horizontal_location = 'left'
    elif self.y > 0:
      horizontal_location = 'right'

    if vertical_location and horizontal_location:
      return vertical_location + '-' + horizontal_location
    elif horizontal_location:
      return horizontal_location
    else:
      return vertical_location

  def getDelta(self, alignment):
    return (alignment.x - self.x, alignment.y - self.y)

  def getDeltaFromStr(self, alignment_str):
    end_alignment = Alignment(alignment_str)
    return (end_alignment.x - self.x, end_alignment.y - self.y)

  def applyDelta(self, delta_tuple):
    self.x += delta_tuple[0]
    self.y += delta_tuple[1]

class ObjectTransformation:
  """Object transformation delta frame class"""

  object_deleted = False
  object_added = False

  unknown_attrs = []

  known_attrs = [
    'size',
    'alignment',
    'above',
    'angle',
    'overlaps',
    'fill',
    'shape',
    'inside'
  ]

  object_list_attrs = [
    'above',
    'overlaps',
    'inside'
  ]

  def __init__(self, start_object, end_object, attribute_weights=AttributeChangeWeights()):
    self.start_object = start_object
    self.attribute_weights = attribute_weights
    self.end_object = end_object

    if start_object and end_object:
      # populate unknown attribute information
      for attr_name in start_object.attributes:
        if attr_name not in self.known_attrs:
          self.unknown_attrs.append(attr_name)

      for attr_name in end_object.attributes:
        if attr_name not in self.known_attrs:
          self.unknown_attrs.append(attr_name)
    elif start_object and not end_object:
      self.object_deleted = True
    elif end_object and not start_object:
      self.object_added = True
    else:
      raise Exception('No objects provided for this transformation')

  @property
  def size_delta(self):
    start_size = Size.fromStr(self.start_object.attributes.get('size'))
    end_size = Size.fromStr(self.end_object.attributes.get('size'))
    if not start_size or not end_size:
      return 0
    return end_size - start_size

  @property
  def angle_delta(self):
    start_angle = self.start_object.attributes.get('angle')
    end_angle = self.end_object.attributes.get('angle')
    if start_angle and end_angle:
      return int(end_angle) - int(start_angle)
    else:
      return 0

  @property
  def alignment_delta(self):
    start_alignment = Alignment(self.start_object.attributes.get('alignment'))
    end_alignment = Alignment(self.end_object.attributes.get('alignment'))
    return start_alignment.getDelta(end_alignment)

  def attributeChanged(self, attr_name):
    if attr_name in self.object_list_attrs:
      return self.objectListAttrDelta(attr_name) != 0
    else:
      return self.end_object.attributes.get(attr_name) != self.start_object.attributes.get(attr_name)

  def objectListAttrDelta(self, attr_name):
    start_value = self.start_object.attributes.get(attr_name)
    end_value = self.end_object.attributes.get(attr_name)

    if start_value and end_value:
      start_num = len(start_value.split(','))
      end_num = len(end_value.split(','))
      return end_num - start_num
    else:
      return 0

  def getTransformationCost(self):
    """
    Calculate a number that can be compare the relative amount of change between
    one object transformation and another.
    """
    if not self.object_added and not self.object_deleted:
      weights = self.attribute_weights
      cost = 0

      # Handle unknown attributes
      for attr in self.unknown_attrs:
        if self.attributeChanged(attr):
          cost += 1 * weights.getWeight(attr)

      for attr in self.start_object.attributes:
        if attr not in self.unknown_attrs and self.attributeChanged(attr):
          attr_value = self.start_object.attributes[attr]
          if attr == 'angle':
            normalized_angle_delta = abs(self.angle_delta) / 45
            cost += normalized_angle_delta * weights.getWeight(attr)
          elif attr == 'size':
            cost += self.size_delta * weights.getWeight(attr)
          elif attr == 'alignment' or attr == 'fill' or attr == 'shape':
            cost += 1 * weights.getWeight(attr)
          elif attr in self.object_list_attrs:
            cost += self.objectListAttrDelta(attr) * weights.getWeight(attr)
          else:
            raise Exception('Impossible case occurred')

      return cost
    else:
      return 100

# Production rules
# Check for equality
# Try transforming the entire figure, choose an answer
# with over 95% similarity to trasformed full frames.
# Try to interpret the objects in the figure and transforming

# Make brain that loads JSON data of seed weigts for
# transformations and reflection amounts and such
# Allow API to change weight for a transformation type
# Store answer reasoning details, use brain to learn about
# answers and adjust

# Separate the problem and answers
# Load A, B, C, into image objects
# Check between A & B transformation (> 95% alike)
# check complex transformations (> 95% alike)
# store relationship in transform object/function
# do the same between A & C
# Add transform functions if they are different
# Apply transformation to B & C. Check both against all
# answers. If multiple possibilities (> 95% alike), then
# keep trying, or try backup transformations that could have
# been and use their weights to choose the best one
# store last likely answers to go back to find possibilities

from PIL import Image
from string import ascii_uppercase

import os
import errno

from collections import namedtuple

import ImageComparator as ic

tmp_file_path = os.path.join(os.getcwd(), 'tmp')

PossibleAnswer = namedtuple('PossibleAnswer', ['figure_name', 'confidence'])

def mkdir_p(path):
  try:
    os.makedirs(path)
  except OSError as exc: # Python >2.5
    if exc.errno == errno.EEXIST and os.path.isdir(path):
        pass
    else: raise

class Agent:
  def __init__(self):
    # Do stuff to prepare the agent.
    # Load the brain!
    pass

  def Solve(self, problem):
    print('\n\n\n\nStarting on', problem.name)
    possible_answers = []

    figure_images = Agent.getFigureImages(problem)
    answer_images = Agent.getAnswerImages(figure_images, problem.problemType)

    # Simple production rules for 2x2
    if problem.problemType == '2x2':
      a = figure_images['A']
      b = figure_images['B']
      c = figure_images['C']

      # Handle simple image transformations
      a_to_b = ic.getTransformation(a, b)
      print('a => b transformation is', a_to_b)

      a_to_c = ic.getTransformation(a, c)
      print('a => c transformation is', a_to_c)

      if a_to_b and a_to_c:
        complete_transform = a_to_b.union(a_to_c)
        print('a => # transformation is', complete_transform)

        candidate = complete_transform.applyTo(a)

        # Save generated answer image for debugging
        # save_dir = os.path.join(tmp_file_path, problem.name)
        # mkdir_p(save_dir)
        # image_path = os.path.join(save_dir, 'transformed.png')
        # candidate.save(image_path, 'png')

        matching_answer = ic.findMatchingImage(candidate, answer_images)
        if matching_answer:
          possible_answers.append(PossibleAnswer(matching_answer, 100))
        else:
          print('No answer matching combined transformation applied to a.')

      elif a_to_b:
        candidate = a_to_b.applyTo(c)
        matching_answer = ic.findMatchingImage(candidate, answer_images)
        if matching_answer:
          possible_answers.append(PossibleAnswer(matching_answer, 50))
        else:
          print('No answer image matching a => b applied to c.')

      elif a_to_c:
        candidate = a_to_c.applyTo(b)
        matching_answer = ic.findMatchingImage(candidate, answer_images)
        if matching_answer:
          possible_answers.append(PossibleAnswer(matching_answer, 50))
        else:
          print('No answer image matching a => c applied to b.')

      else:
        print('Transformation detection production rule failed.')

    if problem.hasVerbal:
      #To iterate over all the Objects in a RavensFigure (given name as above)
      #assuming a verbal representation
      # for objectName in thisFigure.objects:
      #   thisObject = thisFigure.objects[objectName]
      pass

    #   for attributeName in thisObject.attributes:
    #     attributeValue = thisObject.attributes[attributeName]

    most_likely_answer = self.getBestAnswer(possible_answers)

    # Todo, add check answer smarts here to better calibrate confidence threshold
    # maintain set of answers, if one that didn't make the cut was in the list,
    # bring the confidence threshold closer to the confidence of that one?
    if most_likely_answer and most_likely_answer.confidence > 45:
      print('Most likely answer to', problem, 'is', most_likely_answer)
      return int(most_likely_answer.figure_name)
    else:
      print('No likely answer, skipping.')
      return -1

  @staticmethod
  def getBestAnswer(answers_list):
    if len(answers_list) == 0:
      return None

    elif len(answers_list) == 1:
      return answers_list[0]

    else:
      answer_totals = {}
      for i in range(0, len(answers_list)):
        figure_name = answers_list[i].figure_name
        if answer_totals[figure_name]:
          answer_totals[figure_name] += answers_list[i].confidence
        else:
          answer_totals[figure_name] = answers_list[i].confidence
      print('Combined answer confidences:', answer_totals)

      highest_confidence = answers_list[0].confidence
      best_answer = answers_list[0]
      for answer, confidence in answer_totals:
        if confidence > highest_confidence:
          highest_confidence = confidence
          best_answer = answer

    return best_answer

  @staticmethod
  def getFigureImage(name, problem):
    return Image.open(problem.figures[name].visualFilename)

  @staticmethod
  def getFigureImages(problem):
    figure_images = {}
    for name in problem.figures:
      figure_images[name] = Agent.getFigureImage(name, problem)
    return figure_images

  @staticmethod
  def getAnswerImages(figure_images, problemType):
    num_answers = 8 if problemType == '3x3' else 6
    answers = {}

    for x in range(1, num_answers):
      name = str(x)
      answers[name] = figure_images[name]

    return answers

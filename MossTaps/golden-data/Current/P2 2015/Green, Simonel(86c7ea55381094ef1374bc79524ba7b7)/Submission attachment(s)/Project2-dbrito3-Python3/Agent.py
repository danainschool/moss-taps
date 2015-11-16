from PIL import Image
from string import ascii_uppercase

import os
import itertools

from collections import namedtuple

import ImageComparator as ic
from SemanticTransformation import SemanticTransformation
from weights import AttributeChangeWeights

tmp_file_path = os.path.join(os.getcwd(), 'tmp')

PossibleAnswer = namedtuple('PossibleAnswer', ['figure_name', 'confidence'])

class Agent:
  def __init__(self):
    # Prepare the brain!
    self.attribute_weights = AttributeChangeWeights()

  def Solve(self, problem):
    print('\n\n\n\nStarting on', problem.name)
    possible_answers = []


    figure_images = Agent.getFigureImages(problem)
    answer_images = Agent.getAnswerImages(figure_images, problem.problemType)

    # Simple production rules for 2x2
    if problem.problemType == '2x2':
      # a = figure_images['A']
      # b = figure_images['B']
      # c = figure_images['C']
      # # Handle simple image transformations
      # a_to_b = ic.getTransformation(a, b)
      # print('a => b transformation is', a_to_b)

      # a_to_c = ic.getTransformation(a, c)
      # print('a => c transformation is', a_to_c)

      # if a_to_b and a_to_c:
      #   complete_transform = a_to_b.union(a_to_c)
      #   print('a => # transformation is', complete_transform)

      #   candidate = complete_transform.applyTo(a)

      #   # Save generated answer image for debugging
      #   # save_dir = os.path.join(tmp_file_path, problem.name)
      #   # mkdir_p(save_dir)
      #   # image_path = os.path.join(save_dir, 'transformed.png')
      #   # candidate.save(image_path, 'png')

      #   matching_answer = ic.findMatchingImage(candidate, answer_images)
      #   if matching_answer:
      #     possible_answers.append(PossibleAnswer(matching_answer, 90))
      #   else:
      #     print('No answer matching combined transformation applied to a.')
      #     pass

      # elif a_to_b:
      #   candidate = a_to_b.applyTo(c)
      #   matching_answer = ic.findMatchingImage(candidate, answer_images)
      #   if matching_answer:
      #     possible_answers.append(PossibleAnswer(matching_answer, 30))
      #   else:
      #     print('No answer image matching a => b applied to c.')
      #     pass

      # elif a_to_c:
      #   candidate = a_to_c.applyTo(b)
      #   matching_answer = ic.findMatchingImage(candidate, answer_images)
      #   if matching_answer:
      #     possible_answers.append(PossibleAnswer(matching_answer, 30))
      #   else:
      #     print('No answer image matching a => c applied to b.')
      #     pass

      # else:
      #   print('Transformation detection production rule failed.')
      #   pass

      if problem.hasVerbal:
        fig_a = problem.figures['A']
        fig_b = problem.figures['B']
        fig_c = problem.figures['C']

        a_to_b = SemanticTransformation(fig_a, fig_b, self.attribute_weights)
        a_to_c = SemanticTransformation(fig_a, fig_c, self.attribute_weights)
        total_score = a_to_b.combined_transformation_score + a_to_c.combined_transformation_score

        answer_figures = Agent.getAnswerFigures(problem)
        best_answer = None
        smallest_score_diff = None
        for answer_figure_name in answer_figures:
          answer_figure = problem.figures[answer_figure_name]
          answer_transform = SemanticTransformation(fig_a, answer_figure, self.attribute_weights)
          answer_score = answer_transform.combined_transformation_score
          score_diff = abs(total_score - answer_score)
          if not best_answer or score_diff < smallest_score_diff:
            best_answer = answer_figure
            smallest_score_diff = score_diff
        possible_answers.append(PossibleAnswer(best_answer.name, 60));

    if problem.problemType == '3x3':
      if problem.hasVerbal:
        fig_a = problem.figures['A']
        fig_b = problem.figures['B']
        fig_c = problem.figures['C']
        fig_d = problem.figures['D']
        fig_e = problem.figures['E']
        fig_f = problem.figures['F']
        fig_g = problem.figures['G']
        fig_h = problem.figures['H']

        a_to_b = SemanticTransformation(fig_a, fig_b, self.attribute_weights)
        b_to_c = SemanticTransformation(fig_b, fig_c, self.attribute_weights)

        a_to_d = SemanticTransformation(fig_a, fig_d, self.attribute_weights)
        d_to_g = SemanticTransformation(fig_d, fig_g, self.attribute_weights)

        a_to_e = SemanticTransformation(fig_a, fig_e, self.attribute_weights)
        total_score = a_to_b.score + b_to_c.score + a_to_d.score + d_to_g.score + a_to_e.score

        answer_figures = Agent.getAnswerFigures(problem)
        best_answer = None
        smallest_score_diff = None
        for answer_figure_name in answer_figures:
          answer_figure = problem.figures[answer_figure_name]
          answer_transform = SemanticTransformation(fig_a, answer_figure, self.attribute_weights)
          answer_score = answer_transform.combined_transformation_score
          score_diff = abs(total_score - answer_score)
          if not best_answer or score_diff < smallest_score_diff:
            best_answer = answer_figure
            smallest_score_diff = score_diff
        possible_answers.append(PossibleAnswer(best_answer.name, 60));
    most_likely_answer = self.getBestAnswer(possible_answers)

    # Todo, add check answer smarts here to better calibrate confidence threshold
    # maintain set of answers, if one that didn't make the cut was in the list,
    # bring the confidence threshold closer to the confidence of that one?
    if most_likely_answer and most_likely_answer.confidence > 0:
      print('Most likely answer to', problem.name, 'is', most_likely_answer)
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
      for i in range(len(answers_list) - 1):
        figure_name = answers_list[i].figure_name
        if answer_totals.get(figure_name):
          answer_totals[figure_name] += answers_list[i].confidence
        else:
          answer_totals[figure_name] = answers_list[i].confidence
      print('Combined answer confidences:', answer_totals)

      highest_confidence = None
      for answer in answer_totals:
        confidence = answer_totals[answer]
        if not highest_confidence or confidence > highest_confidence:
          highest_confidence = confidence
          best_answer = answer

      return PossibleAnswer(best_answer, highest_confidence)

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

  @staticmethod
  def getAnswerFigures(problem):
    num_answers = 8 if problem.problemType == '3x3' else 6
    answers = {}

    for x in range(1, num_answers):
      name = str(x)
      answers[name] = problem.figures[name]

    return answers


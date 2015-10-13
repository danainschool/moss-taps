# Your Agent for solving Raven's Progressive Matrices. You MUST modify this file.
#
# You may also create and submit new files in addition to modifying this file.
#
# Make sure your file retains methods with the signatures:
# def __init__(self)
# def Solve(self,problem)
#
# These methods will be necessary for the project's main method to run.

# Install Pillow and uncomment this line to access image processing.
# from PIL import Image

import itertools
from ImageUtil import findMatchingCandidate, findImageRotation
from PIL import Image, ImageDraw
from PIL import ImageEnhance, ImageChops
from functools import reduce
import math, operator


class Agent:
    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().


    def __init__(self):
        pass



    # The primary method for solving incoming Raven's Progressive Matrices.
    # For each problem, your Agent's Solve() method will be called. At the
    # conclusion of Solve(), your Agent should return an integer representing its
    # answer to the question: "1", "2", "3", "4", "5", or "6". These integers
    # are also the Names of the individual RavensFigures, obtained through
    # RavensFigure.getName() (as Strings).
    #
    # In addition to returning your answer at the end of the method, your Agent
    # may also call problem.checkAnswer(int givenAnswer). The parameter
    # passed to checkAnswer should be your Agent's current guess for the
    # problem; checkAnswer will return the correct answer to the problem. This
    # allows your Agent to check its answer. Note, however, that after your
    # agent has called checkAnswer, it will *not* be able to change its answer.
    # checkAnswer is used to allow your Agent to learn from its incorrect
    # answers; however, your Agent cannot change the answer to a question it
    # has already answered.
    #
    # If your Agent calls checkAnswer during execution of Solve, the answer it
    # returns will be ignored; otherwise, the answer returned at the end of
    # Solve will be taken as your Agent's answer to this problem.
    #
    # Make sure to return your answer *as an integer* at the end of Solve().
    # Returning your answer as a string may cause your program to crash.
    def Solve(self, problem):

        print('==================================================')
        print('')
        print("Solving RPM Problem : ", problem.name , " (",problem.problemType,")")

        # Find the Visual Image from the given input data and output options

        imageA = Image.open(problem.figures['A'].visualFilename)
        imageB = Image.open(problem.figures['B'].visualFilename)
        imageC = Image.open(problem.figures['C'].visualFilename)
        image1 = Image.open(problem.figures['1'].visualFilename)
        image2 = Image.open(problem.figures['2'].visualFilename)
        image3 = Image.open(problem.figures['3'].visualFilename)
        image4 = Image.open(problem.figures['4'].visualFilename)
        image5 = Image.open(problem.figures['5'].visualFilename)
        image6 = Image.open(problem.figures['6'].visualFilename)

        # We clearly see that most common MATCHING PATTERN is - Images are exactly similar

        print(' Check if images are SIMILAR.')
        result = findMatchingCandidate(imageA, imageB, imageC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(imageA, imageC, imageB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result          

        print(' Check if one of the candidates matches with LEFT-RIGHT REFLECTION of input image')   
     
        flippedA = imageA.transpose(Image.FLIP_LEFT_RIGHT)
        flippedB = imageB.transpose(Image.FLIP_LEFT_RIGHT)
        flippedC = imageC.transpose(Image.FLIP_LEFT_RIGHT)

        result = findMatchingCandidate(flippedA, imageB, flippedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(flippedA, imageC, flippedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result

        print(' Check if one of the candidates matches with TOP-DOWN REFLECTION of input image')    
        flippedA = imageA.transpose(Image.FLIP_TOP_BOTTOM)
        flippedB = imageB.transpose(Image.FLIP_TOP_BOTTOM)
        flippedC = imageC.transpose(Image.FLIP_TOP_BOTTOM)

        result = findMatchingCandidate(flippedA, imageB, flippedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(flippedA, imageC, flippedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result  


        print(' Check if one of the candidates matches with ROTATED input image')    
        result = findImageRotation(imageA, imageB, imageC, image1, image2, image3, image4, image5, image6);    
        if result != None:
            return result
        result = findImageRotation(imageA, imageC, imageB, image1, image2, image3, image4, image5, image6);    
        if result != None:
            return result


        print(' Check if the input image SIMILAR to the candidate image after removing some noise')
        #result = findImageFractalDiff(imageA, imageB, imageC, image1, image2, image3, image4, image5, image6);
        #if result != None:
            #return result

        print(' Check if the IMAGE FILL pattern of input data matches with candidate options.')

        imageA_fill_pattern =imageA
        ImageDraw.floodfill(imageA_fill_pattern,(50,50),1)
        imageB_fill_pattern =imageB
        ImageDraw.floodfill(imageB_fill_pattern ,(50,50),1)
        imageC_fill_pattern =imageC
        ImageDraw.floodfill(imageC_fill_pattern,(50,50),1)

        #
        result = findMatchingCandidate(imageA_fill_pattern, imageB, imageC_fill_pattern, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(imageA_fill_pattern, imageC, imageB_fill_pattern , image1, image2, image3, image4, image5, image6)
        if result != None:
            return result


        return -1
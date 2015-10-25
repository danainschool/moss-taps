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
from PIL import Image, ImageDraw
from PIL import ImageEnhance
import ImageChops
import math, operator
RMSMATCH = 0.16


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

                #

        # if problem.name != "Basic Problem C-07":
        #     return -1



        # if problem.name != "Challenge Problem B-01":
		 #    return -1

        # test input
        print"Working on problems", problem.name

        print problem.problemType
        print problem.correctAnswer


        if problem.problemType == '2x2':
            global RMSMATCH
            RMSMATCH = 0.16
            return self.solveB(problem)
        elif problem.problemType == '3x3':
            global RMSMATCH
            RMSMATCH = 0.10
            return self.solveC(problem)

        # ...3,4,5,6....
        return -1

#solvent 3x3 problems

    def solveC(self, problem):
        # possible_obj =[A,B,C,one,two,three, four, five, six]
        RMSMATCH = 0.10
        A = problem.figures['A']
        B = problem.figures['B']
        C = problem.figures['C']
        D = problem.figures['D']
        E = problem.figures['E']
        F = problem.figures['F']
        G = problem.figures['G']
        H = problem.figures['H']
     #   C = problem.figures['I']
        one = problem.figures['1']
        two = problem.figures['2']
        three = problem.figures['3']
        four = problem.figures['4']
        five = problem.figures['5']
        six = problem.figures['6']
        seven = problem.figures['7']
        eight = problem.figures['8']
        # figureAImage = Image.open(A.visualFilename).transpose(Image.FLIP_TOP_BOTTOM)
        # figureAImage.show()
        # figureALoaded = figureAImage.load()
        figureAImage = Image.open(A.visualFilename)
        figureBImage = Image.open(B.visualFilename)
        figureCImage = Image.open(C.visualFilename)
        figureDImage = Image.open(D.visualFilename)
        figureEImage = Image.open(E.visualFilename)
        figureFImage = Image.open(F.visualFilename)
        figureGImage = Image.open(G.visualFilename)
        figureHImage = Image.open(H.visualFilename)

        figure1Image = Image.open(one.visualFilename)
        figure2Image = Image.open(two.visualFilename)
        figure3Image = Image.open(three.visualFilename)
        figure4Image = Image.open(four.visualFilename)
        figure5Image = Image.open(five.visualFilename)
        figure6Image = Image.open(six.visualFilename)
        figure7Image = Image.open(seven.visualFilename)
        figure8Image = Image.open(eight.visualFilename)

        #print self.imageCheckidentical(figureHImage,figure1Image)

        weight = {"unchanged":5,"reflected":4, "rotated":3, "scaled":2, "deleted":1, "shapechange":0}

        answer = []
        print 'guess the imaging identical'
        guessanswer = self.checkimageanswer3x3(figureFImage, figureEImage, figureHImage, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image, figure7Image,figure8Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer3x3(figureAImage, figureDImage, figureFImage, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image, figure7Image,figure8Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer3x3(figureAImage, figureEImage, figureEImage, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image, figure7Image,figure8Image)
        if guessanswer != None:
            return guessanswer


 #       fliptopbottomB = figureBImage.transpose(Image.FLIP_TOP_BOTTOM)
        fliptopbottomE = figureEImage.transpose(Image.FLIP_TOP_BOTTOM)
        fliptopbottomD = figureDImage.transpose(Image.FLIP_TOP_BOTTOM)
        #   figureAImage.show()
        #    fliptopbottomA.show()
        #     figureCImage.show()
        #print self.imageCheckidentical(fliptopbottomA, figureDImage)
        fliptopbottomF = figureFImage.transpose(Image.FLIP_TOP_BOTTOM)
        fliptopbottomH = figureFImage.transpose(Image.FLIP_TOP_BOTTOM)
        #   fliptopbottomB.show()
        fliptopbottomB = figureBImage.transpose(Image.FLIP_TOP_BOTTOM)
        # fliptopbottomC.show()
        #   figure3Image.show()
        #   subtractinAto3 = ImageChops.invert(ImageChops.difference(fliptopbottomB,figure3Image)).convert('1')
        #   subtractinAto3.show()
        guessanswer = self.checkimageanswer3x3(fliptopbottomD, figureGImage, fliptopbottomF, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image,figure6Image,figure7Image,figure8Image,)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer3x3(fliptopbottomE, figureFImage, fliptopbottomH, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image,figure7Image,figure8Image,)
        if guessanswer != None:
            return guessanswer
        print 'FLIP_LEFT_RIGHT'
        # guessanswer = self.checkimageanswer(A, B, C, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(A, C, B, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        fliptopbottomD = figureDImage.transpose(Image.FLIP_LEFT_RIGHT)
        #        figureAImage.show()
        #        fliptopbottomA.show()
        #        figureCImage.show()
        print self.imageCheckidentical(fliptopbottomD, figureFImage)
        fliptopbottomG = figureGImage.transpose(Image.FLIP_LEFT_RIGHT)
        #        fliptopbottomB.show()
        # fliptopbottomC = figureCImage.transpose(Image.FLIP_LEFT_RIGHT)
        #       fliptopbottomC.show()
        guessanswer = self.checkimageanswer3x3(fliptopbottomD, figureFImage, fliptopbottomG, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image,figure6Image,figure7Image,figure8Image,)
        if guessanswer != None:
            return guessanswer
        # guessanswer = self.checkimageanswer3x3(fliptopbottomE, figureFImage, fliptopbottomH, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image,figure7Image,figure8Image,)
        # if guessanswer != None:
        # print 'rotate 90'
        # # guessanswer = self.checkimageanswer(A, B, C, one, two, three, four, five, six)
        # # if guessanswer != None:
        # #     return guessanswer
        # # guessanswer = self.checkimageanswer(A, C, B, one, two, three, four, five, six)
        # # if guessanswer != None:
        # #     return guessanswer
        # fliptopbottomA = figureAImage.transpose(Image.ROTATE_90)
        # #        figureAImage.show()
        # #        fliptopbottomA.show()
        # #        figureCImage.show()
        # #        print self.imageCheckidentical(fliptopbottomA,figureCImage)
        # fliptopbottomB = figureBImage.transpose(Image.ROTATE_90)
        # #        fliptopbottomB.show()
        # fliptopbottomC = figureCImage.transpose(Image.ROTATE_90)
        # #       fliptopbottomC.show()
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        # print 'rotate 180'
        # fliptopbottomA = figureAImage.transpose(Image.ROTATE_180)
        # #        figureAImage.show()
        # #        fliptopbottomA.show()
        # #        figureCImage.show()
        # print self.imageCheckidentical(fliptopbottomA, figureCImage)
        # fliptopbottomB = figureBImage.transpose(Image.ROTATE_180)
        # #        fliptopbottomB.show()
        # fliptopbottomC = figureCImage.transpose(Image.ROTATE_180)
        # #       fliptopbottomC.show()
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        # print 'rotate 270'
        # fliptopbottomA = figureAImage.transpose(Image.ROTATE_270)
        # #        figureAImage.show()
        # #        fliptopbottomA.show()
        # #        figureCImage.show()
        # print self.imageCheckidentical(fliptopbottomA, figureCImage)
        # fliptopbottomB = figureBImage.transpose(Image.ROTATE_270)
        # #        fliptopbottomB.show()
        # fliptopbottomC = figureCImage.transpose(Image.ROTATE_270)
        # #       fliptopbottomC.show()
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
        #                                     figure3Image, figure4Image, figure5Image, figure6Image)
        # if guessanswer != None:
        #     return guessanswer
        print 'image subtraction'
        subtractinFtoE = ImageChops.invert(ImageChops.difference(figureFImage, figureEImage)).convert('1')
        #subtractinAtoC = ImageChops.invert(ImageChops.difference(figureAImage, figureCImage)).convert('1')
        # figureFImage.show()
        # figureEImage.show()
        subtractinFtoE = self.imagesubnoise(subtractinFtoE)
        # subtractinFtoE.show()

        guessanswer = self.checkimageanswer3x3(subtractinFtoE,
                                            subtractinFtoE,
                                            subtractinFtoE,
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure1Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure2Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure3Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure4Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure5Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure6Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure7Image, figureHImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure8Image, figureHImage)))

                                            )
        if guessanswer != None:
            return guessanswer

    #ration of A/C
        #
        # ratioanswer =[self.blackpixelratio(figure1Image)/(1.0+self.blackpixelratio(figureHImage)),self.blackpixelratio(figure2Image)/(1.0+self.blackpixelratio(figureHImage)),
        #               self.blackpixelratio(figure3Image)/(1.0+self.blackpixelratio(figureHImage)),
        #               self.blackpixelratio(figure4Image)/(1.0+self.blackpixelratio(figureHImage)),self.blackpixelratio(figure5Image)/(1.0+self.blackpixelratio(figureHImage)),
        #               self.blackpixelratio(figure6Image)/(1.0+self.blackpixelratio(figureHImage)),self.blackpixelratio(figure7Image)/(1.0+self.blackpixelratio(figureHImage)),
        #               self.blackpixelratio(figure8Image)/(1.0+self.blackpixelratio(figureHImage))]
        #
        # answer = 1+min(range(len(ratioanswer)), key=lambda i: abs(ratioanswer[i]-(1.0*self.blackpixelratio(figureFImage))/(1.0+self.blackpixelratio(figureEImage))))
        # print answer
        # return answer
    #ration of A/C
        #
        avgHF= (self.blackpixelratio(figureFImage)+self.blackpixelratio(figureHImage))*0.5+1
        ratioanswer =[self.blackpixelratio(figure1Image)/avgHF,self.blackpixelratio(figure2Image)/avgHF,
                      self.blackpixelratio(figure3Image)/avgHF,
                      self.blackpixelratio(figure4Image)/avgHF,self.blackpixelratio(figure5Image)/avgHF,
                      self.blackpixelratio(figure6Image)/avgHF,self.blackpixelratio(figure7Image)/avgHF,
                      self.blackpixelratio(figure8Image)/avgHF]

        answer = 1+min(range(len(ratioanswer)), key=lambda i: abs(ratioanswer[i]-avgHF/(1.0+self.blackpixelratio(figureEImage))))
        print answer
        return answer

        return "-1"









#solve 2x2 problems

    def solveB(self, problem):
        # possible_obj =[A,B,C,one,two,three, four, five, six]
        A = problem.figures['A']
        B = problem.figures['B']
        C = problem.figures['C']
        one = problem.figures['1']
        two = problem.figures['2']
        three = problem.figures['3']
        four = problem.figures['4']
        five = problem.figures['5']
        six = problem.figures['6']
        # figureAImage = Image.open(A.visualFilename).transpose(Image.FLIP_TOP_BOTTOM)
        # figureAImage.show()
        # figureALoaded = figureAImage.load()
        figureAImage = Image.open(A.visualFilename)
        figureBImage = Image.open(B.visualFilename)
        figureCImage = Image.open(C.visualFilename)
        figure1Image = Image.open(one.visualFilename)
        figure2Image = Image.open(two.visualFilename)
        figure3Image = Image.open(three.visualFilename)
        figure4Image = Image.open(four.visualFilename)
        figure5Image = Image.open(five.visualFilename)
        figure6Image = Image.open(six.visualFilename)
        answer = []

        print 'guess the imaging identical'
        guessanswer = self.checkimageanswer(figureAImage, figureBImage, figureCImage, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(figureAImage, figureCImage, figureBImage, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'guess imaging transformation #flip top and bottom'
        #    figureAImage.show()
        fliptopbottomA = figureAImage.transpose(Image.FLIP_TOP_BOTTOM)
        #   figureAImage.show()
        #    fliptopbottomA.show()
        #     figureCImage.show()
        print self.imageCheckidentical(fliptopbottomA, figureCImage)
        fliptopbottomB = figureBImage.transpose(Image.FLIP_TOP_BOTTOM)
        #   fliptopbottomB.show()
        fliptopbottomC = figureCImage.transpose(Image.FLIP_TOP_BOTTOM)
        # fliptopbottomC.show()
        #   figure3Image.show()
        #   subtractinAto3 = ImageChops.invert(ImageChops.difference(fliptopbottomB,figure3Image)).convert('1')
        #   subtractinAto3.show()
        guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'FLIP_LEFT_RIGHT'
        # guessanswer = self.checkimageanswer(A, B, C, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(A, C, B, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        fliptopbottomA = figureAImage.transpose(Image.FLIP_LEFT_RIGHT)
        #        figureAImage.show()
        #        fliptopbottomA.show()
        #        figureCImage.show()
        print self.imageCheckidentical(fliptopbottomA, figureCImage)
        fliptopbottomB = figureBImage.transpose(Image.FLIP_LEFT_RIGHT)
        #        fliptopbottomB.show()
        fliptopbottomC = figureCImage.transpose(Image.FLIP_LEFT_RIGHT)
        #       fliptopbottomC.show()
        guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'rotate 90'
        # guessanswer = self.checkimageanswer(A, B, C, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(A, C, B, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        fliptopbottomA = figureAImage.transpose(Image.ROTATE_90)
        # figureAImage.show()
        # fliptopbottomA.show()
        #        figureCImage.show()
        #        print self.imageCheckidentical(fliptopbottomA,figureCImage)
        fliptopbottomB = figureBImage.transpose(Image.ROTATE_90)
        # fliptopbottomB.show()
        fliptopbottomC = figureCImage.transpose(Image.ROTATE_90)
        # fliptopbottomC.show()
        guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'rotate 180'
        fliptopbottomA = figureAImage.transpose(Image.ROTATE_180)
        #        figureAImage.show()
        #        fliptopbottomA.show()
        #        figureCImage.show()
        print self.imageCheckidentical(fliptopbottomA, figureCImage)
        fliptopbottomB = figureBImage.transpose(Image.ROTATE_180)
        #        fliptopbottomB.show()
        fliptopbottomC = figureCImage.transpose(Image.ROTATE_180)
        #       fliptopbottomC.show()
        guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'rotate 270'
        fliptopbottomA = figureAImage.transpose(Image.ROTATE_270)
        #        figureAImage.show()
        #        fliptopbottomA.show()
        #        figureCImage.show()
        print self.imageCheckidentical(fliptopbottomA, figureCImage)
        fliptopbottomB = figureBImage.transpose(Image.ROTATE_270)
        #        fliptopbottomB.show()
        fliptopbottomC = figureCImage.transpose(Image.ROTATE_270)
        #       fliptopbottomC.show()
        guessanswer = self.checkimageanswer(fliptopbottomA, figureBImage, fliptopbottomC, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(fliptopbottomA, figureCImage, fliptopbottomB, figure1Image, figure2Image,
                                            figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        print 'image subtraction'
        subtractinAtoB = ImageChops.invert(ImageChops.difference(figureBImage, figureAImage)).convert('1')
        subtractinAtoC = ImageChops.invert(ImageChops.difference(figureAImage, figureCImage)).convert('1')
        #        figureAImage.show()
        #       figureCImage.show()
        subtractinAtoC = self.imagesubnoise(subtractinAtoC)

        guessanswer = self.checkimageanswer(subtractinAtoC,
                                            subtractinAtoC,
                                            subtractinAtoC,
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure1Image, figureBImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure2Image, figureBImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure3Image, figureBImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure4Image, figureBImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure5Image, figureBImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure6Image, figureBImage))))
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(subtractinAtoB,
                                            subtractinAtoB,
                                            subtractinAtoB,
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure1Image, figureCImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure2Image, figureCImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure3Image, figureCImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure4Image, figureCImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure5Image, figureCImage))),
                                            self.imagesubnoise(
                                                ImageChops.invert(ImageChops.difference(figure6Image, figureCImage))))
        if guessanswer != None:
            return guessanswer
            #        # guessanswer = self.checkimageanswer(subtractinAtoB,subtractinAtoB,subtractinAtoB, ImageChops.difference(figure1Image,figureCImage), ImageChops.difference(figure2Image,figureCImage),
        #                                     ImageChops.difference(figure3Image,figureCImage), ImageChops.difference(figure4Image,figureCImage), ImageChops.difference(figure5Image,figureCImage),
        #                             ImageChops.difference(figure6Image,figureCImage))
        print 'FILLED SHAPE'
        # guessanswer = self.checkimageanswer(A, B, C, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        # guessanswer = self.checkimageanswer(A, C, B, one, two, three, four, five, six)
        # if guessanswer != None:
        #     return guessanswer
        # ImageChops.invert(ImageChops.difference(figureBImage,figureAImage)).convert('1')
        #        figureAImage.show()
        figureAImage_filled = figureAImage
        ImageDraw.floodfill(figureAImage_filled, (50, 50), 1)
        figureBImage_filled = figureBImage
        ImageDraw.floodfill(figureBImage_filled, (50, 50), 1)
        figureCImage_filled = figureCImage
        ImageDraw.floodfill(figureCImage_filled, (50, 50), 1)
        #        figureAImage.show()
        #       fliptopbottomC.show()
        guessanswer = self.checkimageanswer(figureAImage_filled, figureBImage, figureCImage_filled, figure1Image,
                                            figure2Image, figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer
        guessanswer = self.checkimageanswer(figureAImage_filled, figureCImage, figureBImage_filled, figure1Image,
                                            figure2Image, figure3Image, figure4Image, figure5Image, figure6Image)
        if guessanswer != None:
            return guessanswer

        # ratioanswer =[self.blackpixelratio(figure1Image)/(1.0+self.blackpixelratio(figureBImage)),self.blackpixelratio(figure2Image)/(1.0+self.blackpixelratio(figureBImage)),
        #               self.blackpixelratio(figure3Image)/(1.0+self.blackpixelratio(figureBImage)),
        #               self.blackpixelratio(figure4Image)/(1.0+self.blackpixelratio(figureBImage)),self.blackpixelratio(figure5Image)/(1.0+self.blackpixelratio(figureBImage)),
        #               self.blackpixelratio(figure6Image)/(1.0+self.blackpixelratio(figureBImage))]
        #
        # answer = 1+min(range(len(ratioanswer)), key=lambda i: abs(ratioanswer[i]-(1.0*self.blackpixelratio(figureCImage)/(1.0+self.blackpixelratio(figureAImage)))))
        # print answer
        # return answer

        ratioanswer =[self.blackpixelratio(figure1Image)/(1.0+self.blackpixelratio(figureCImage)),self.blackpixelratio(figure2Image)/(1.0+self.blackpixelratio(figureCImage)),
                      self.blackpixelratio(figure3Image)/(1.0+self.blackpixelratio(figureCImage)),
                      self.blackpixelratio(figure4Image)/(1.0+self.blackpixelratio(figureCImage)),self.blackpixelratio(figure5Image)/(1.0+self.blackpixelratio(figureCImage)),
                      self.blackpixelratio(figure6Image)/(1.0+self.blackpixelratio(figureCImage))]

        answer = 1+min(range(len(ratioanswer)), key=lambda i: abs(ratioanswer[i]-(1.0*self.blackpixelratio(figureBImage)/(1.0+self.blackpixelratio(figureAImage)))))
        print answer
        return answer

        return "-1"



    # check Figure identical or not
    def imageCheckidentical(self,figureAImage, figureBImage):
        # load figures

        #figureAImage = Image.open(A.visualFilename)
        #figureAImage.show()
        #figureALoaded = figureAImage.load()

        #figureBImage = Image.open(B.visualFilename)
        #figureBImage.show()
        #figureBLoaded = figureBImage.load()



        if list(figureAImage.getdata()) == list(figureBImage.getdata())or self.rmsdiff(figureAImage, figureBImage)<=RMSMATCH :
            # print "Identical"
            # figureAImage.show()
            # figureBImage.show()
            # print self.rmsdiff(figureAImage, figureBImage)
            # print figureAImage, figureBImage, True
            return True
        else:
            # print "Different"
            # print '***',self.rmsdiff(figureAImage, figureBImage)
            # figureAImage.show()
            # figureBImage.show()
            return False

        # def ImageCheckfliptopbottom(self, A, B):
        # # load figures
        #
        #     #
        #     #figureBImage.show()
        #     #figureBLoaded = figureBImage.load()
        #
        #     if list(figureAImage.getdata()) == list(figureBImage.getdata()):
        #         #            print "Identical"
        #         return True
        #     else:
        #         # print "Different"
        #         return False

    def checkimageanswer(self,A, Bmatch, Ctomatch, one, two, three, four, five, six):

        if self.imageCheckidentical(A, Bmatch):
            if self.imageCheckidentical(Ctomatch, one):
                return "1"
            if self.imageCheckidentical(Ctomatch, two):
                return "2"
            if self.imageCheckidentical(Ctomatch, three):
                return "3"
            if self.imageCheckidentical(Ctomatch, four):
                return "4"
            if self.imageCheckidentical(Ctomatch, five):
                return "5"
            if self.imageCheckidentical(Ctomatch, six):
                return "6"
    def checkimageanswer3x3(self,A, Bmatch, Ctomatch, one, two, three, four, five, six,seven,eight):

        if self.imageCheckidentical(A, Bmatch):
            if self.imageCheckidentical(Ctomatch, one):
                return "1"
            if self.imageCheckidentical(Ctomatch, two):
                return "2"
            if self.imageCheckidentical(Ctomatch, three):
                return "3"
            if self.imageCheckidentical(Ctomatch, four):
                return "4"
            if self.imageCheckidentical(Ctomatch, five):
                return "5"
            if self.imageCheckidentical(Ctomatch, six):
                return "6"
            if self.imageCheckidentical(Ctomatch, seven):
                return "7"
            if self.imageCheckidentical(Ctomatch, eight):
                return "8"


    def rmsdiff(self, image1, image2):
        # image1 = Image.open(file1)
        # image2 = Image.open(file2)
        # image1 = self.imagesubnoise(image1)
        h1 = list(image1.getdata())
        h1one=[]
        numofblack =0.01
        for pixel in h1:
            if pixel ==(255,255,255,255):

                h1one.append(0)
            else:
                h1one.append(1.0)
                numofblack = numofblack+1
        #print h1one
        # image2 = self.imagesubnoise(image2)
        h2 = list(image2.getdata())

        h2one=[]
        for pixel in h2:
            if pixel ==(255,255,255,255):

                h2one.append(0)
            else:
                h2one.append(1.0)
    #    print h1one
     #   print h1
     #   print h2
        rms = math.sqrt(reduce(operator.add,
                               map(lambda a,b: (a-b)**2, h1one, h2one))/(2.0*numofblack+len(h1one)))
        return rms


    def imagesubnoise(self, subtractinAtoC):
        figureALoaded = subtractinAtoC.load()
        # image_two = Image.open ("image_two.bmp")
        image_two = subtractinAtoC.convert("RGBA")
        pixels = image_two.load()
        for y in xrange(1,image_two.size[1],1):
            for x in xrange(1,image_two.size[0],1):
                if y+1<image_two.size[1] and x+1 <image_two.size[0]:
                    if pixels[x+1, y+1] == (0, 0, 0, 255) and pixels[x, y] == (0, 0, 0, 255) and pixels[x, y+1 ] == \
                            (0, 0, 0, 255) and pixels[x+1 , y ] == (0, 0, 0, 255):
                        pixels[x, y] = (0, 0, 0, 255)
                    else:
                        pixels[x, y] = (255, 255, 255, 255)
        return image_two


    def blackpixelratio(self, image):
        # figureALoaded = subtractinAtoC.load()
        # # image_two = Image.open ("image_two.bmp")
        # blackpixelcount =0
        # whitepixelcount =0
        # image_two = subtractinAtoC.convert("RGBA")
        # pixels = image_two.load()
        # for y in xrange(image_two.size[1]):
        #     for x in xrange(image_two.size[0]):
        #
        #         if pixels[x, y] == (0, 0, 0, 255):
        #
        #                 blackpixelcount = blackpixelcount + 1
        #
        #         else:
        #                 whitepixelcount = blackpixelcount + 1
        # print 1.0*blackpixelcount/(whitepixelcount + blackpixelcount)
        # return 1.0*blackpixelcount/(whitepixelcount + blackpixelcount)
        img = image.convert('1')
        if len(img.getcolors())>1:
            black, white = img.getcolors()

            print black[0]
            print white[0]
            print black[0]/(1.0+black[0]+white[0])
        else:
            black=[0]
            white=[len(img.getcolors())]

        return black[0]/(1.0+black[0]+white[0])


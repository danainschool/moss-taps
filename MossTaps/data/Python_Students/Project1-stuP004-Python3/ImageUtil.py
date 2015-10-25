
import itertools
from PIL import Image, ImageDraw
from PIL import ImageEnhance, ImageChops
from functools import reduce
import math, operator

# Check if 2 given Images are similar
def findSimilarObjects(firstImageToCompare, secondImageToCompare):
    if list(firstImageToCompare.getdata()) == list(secondImageToCompare.getdata()) or findImageRootMeanSquareDiff(firstImageToCompare, secondImageToCompare)<=0.16 :
        return True
    else:
        return False


def findImageRootMeanSquareDiff(firstImageToCompare, secondImageToCompare):
    #binaryList1 = convertRGBtoBinary(firstImageToCompare)
    #binaryList2 = convertRGBtoBinary(secondImageToCompare)
    #print('		Find RMS.')
    pixelData1 = list(firstImageToCompare.getdata())
    binaryData1 = []
    blackPixels = 0
    for pixel in pixelData1:
        if pixel ==(255,255,255,255):
            binaryData1.append(0)
        else:
            binaryData1.append(1.0)
            blackPixels += 1

    pixelData2 = list(secondImageToCompare.getdata())
    binaryData2 = []

    for pixel in pixelData2:
        if pixel ==(255,255,255,255):
            binaryData2.append(0)
        else:
            binaryData2.append(1.0)        


    rms = math.sqrt(reduce(operator.add,
                           map(lambda a,b: (a-b)**2, binaryData1, binaryData2))/(2.0*blackPixels+len(binaryData2)))
    return rms 


def findMatchingCandidate(A_, B_, C_, imageOne, imageTwo, imageThree, imageFour, imageFive, imageSix):
    if findSimilarObjects(A_, B_):
        if findSimilarObjects(C_, imageOne):
            return "1"
        if findSimilarObjects(C_, imageTwo):
            return "2"
        if findSimilarObjects(C_, imageThree):
            return "3"
        if findSimilarObjects(C_, imageFour):
            return "4"
        if findSimilarObjects(C_, imageFive):
            return "5"
        if findSimilarObjects(C_, imageSix):
            return "6"


def findImageRotation(imageA,imageB,imageC,image1,image2,image3,image4,image5,image6):


        #print('		45 Rotation')
        rotatedA = imageA.rotate(45)
        rotatedB = imageB.rotate(45)
        rotatedC = imageC.rotate(45)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result


        #print('		90 Rotation')
        rotatedA = imageA.transpose(Image.ROTATE_90)
        rotatedB = imageB.transpose(Image.ROTATE_90)
        rotatedC = imageC.transpose(Image.ROTATE_90)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result


        #print('		135 Rotation')
        rotatedA = imageA.rotate(135)
        rotatedB = imageB.rotate(135)
        rotatedC = imageC.rotate(135)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result    

        #print('		180 Rotation')
        rotatedA = imageA.transpose(Image.ROTATE_180)
        rotatedB = imageB.transpose(Image.ROTATE_180)
        rotatedC = imageC.transpose(Image.ROTATE_180)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result

        #print('		225 Rotation')
        rotatedA = imageA.rotate(225)
        rotatedB = imageB.rotate(225)
        rotatedC = imageC.rotate(225)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result        


        #print('		270 Rotation')
        rotatedA = imageA.transpose(Image.ROTATE_270)
        rotatedB = imageB.transpose(Image.ROTATE_270)
        rotatedC = imageC.transpose(Image.ROTATE_270)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result


        #print('		315 Rotation')
        rotatedA = imageA.rotate(315)
        rotatedB = imageB.rotate(315)
        rotatedC = imageC.rotate(315)

        result = findMatchingCandidate(rotatedA, imageB, rotatedC, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result
        result = findMatchingCandidate(rotatedA, imageC, rotatedB, image1, image2, image3, image4, image5, image6)
        if result != None:
            return result        

        return None    

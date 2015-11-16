from ObjectTransformation import ObjectTransformation
from FigureTransformation import FigureTransformation
from RavensFigure import RavensFigure
from RavensObject import RavensObject

class Agent3x3:
    def __init__(self):
        pass

    def Solve(self, problem):
        A = problem.figures['A']
        B = problem.figures['B']
        C = problem.figures['C']
        D = problem.figures['D']
        E = problem.figures['E']
        F = problem.figures['F']
        G = problem.figures['G']
        H = problem.figures['H']

        E_to_F = FigureTransformation(E, F)
        E_to_H = FigureTransformation(E, H)

        answer = -1

        for i in range(1, 8+1):
            option = problem.figures[str(i)]

            H_to_X = FigureTransformation(H, option)
            F_to_X = FigureTransformation(F, option)

            if E_to_F == H_to_X and E_to_H == F_to_X:
                answer = i  # found the answer!
                break


        if answer > 0:
            problem.checkAnswer(answer)
            result = problem.getCorrect()
            print "Answer: %s. %s" % (answer, result)
            return answer

        # not sure what the right answer is
        print "Skipping"
        return -1

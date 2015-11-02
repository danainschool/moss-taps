__author__ = 'pbirmingham'

def detect_progressive_addition(problem):
    count = {}
    figure_names = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
    shape_name = first_shape_name(problem.figures['A'])
    if not shape_name:
        return False
    for figure_name in figure_names:
        figure = problem.figures[figure_name]
        if not figure_is_homogeneous(figure):
            return False
        count[figure.name] = count_shape(figure, shape_name)
        if count[figure.name] == 0:
            return False
    #Now find the GCF for the first and second rows
    gcf_ac = greatest_common_factor([count['A'], count['B'], count['C']])
    gcf_df = greatest_common_factor([count['D'], count['E'], count['F']])
    if count['F'] != count['D'] + gcf_df / gcf_ac * (count['C'] - count['A']):
        return False
    gcf_gh = greatest_common_factor([count['G'], count['H']])
    return (count ['G'] + gcf_gh * (count['F'] - count['D']) / gcf_df, shape_name)


def figure_is_homogeneous(figure):
    return (count_shape(figure, first_shape_name(figure)) == len(figure.objects))


def first_shape_name(figure):
    try:
        name = figure.objects[list(figure.objects.keys())[0]].attributes['shape']
        return name
    except Exception:
        return False


def greatest_common_factor(numbers):
    numbers = sorted(numbers)
    for i in range(numbers[0], 0, -1):
        disqualified = False
        for number in numbers:
            if number % i != 0:
                disqualified = True
        if not disqualified:
            return i

def count_shape(figure, shape_name):
    count = 0
    for object in figure.objects.values():
        if object.attributes['shape'] == shape_name:
            count += 1
    return count
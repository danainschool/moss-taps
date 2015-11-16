__author__ = 'pbirmingham'
from math import pi, sin, cos

DELETION_DIFF = 4
# 0 unchanged
# 1 reflected
# 2 rotated
# 3 scaled
# 4 deleted
# 5 shape changed
difference_weights = {'above': .5, 'alignment': 1, 'angle': 1.5, 'fill': .5, 'inside': .5, 'overlaps': .5,
                      'shape': 5, 'size': 3, 'height': 2, 'width': 2, 'left-of': .5}
relationship_names = difference_weights.keys()
sizes = {'very small': 1, 'small': 2, 'medium': 3, 'large': 4, 'very large': 5, 'huge': 6}


def deg_to_rad(angle):
    return angle * pi / 180


def build_networks(initial_figure, final_figure):
    mapping = find_best_mapping(initial_figure, final_figure)
    network = build_transformation_subnetwork(initial_figure, final_figure, mapping)
    frame_wide = build_frame_transformation_network(initial_figure, final_figure, mapping)
    if frame_wide:
        network += frame_wide

    return network


def build_relationship_subnetwork(figure):
    network = []
    # build the intra-figure relationships
    for object_name in figure.objects:
        for attribute_name in figure.objects[object_name].attributes:
            if attribute_name in ['inside', 'above', 'overlaps']:
                for object_ref in figure.objects[object_name].attributes[attribute_name].split(','):
                    network.append((object_name, attribute_name, object_ref))
    return network


def build_transformation_subnetwork(initial_figure, final_figure, mapping):
    network = []
    for this_tuple in mapping:
        initial_object_name = this_tuple[0]
        final_object_name = this_tuple[1]
        if initial_object_name == 'none':
            network.append(('added', final_object_name))
        elif final_object_name == 'none':
            network.append(('deleted', initial_object_name))
        else:
            initial_object = initial_figure.objects[initial_object_name]
            final_object = final_figure.objects[final_object_name]
            vertical_move = object_vertical_movement(initial_object_name, initial_figure, final_figure, mapping)
            if vertical_move:
                network.append(("move-" + vertical_move, initial_object_name))
            horizontal_move = object_horizontal_movement(initial_object_name, initial_figure, final_figure, mapping)
            if horizontal_move:
                network.append(("move-" + horizontal_move, initial_object_name))
            reflection = evaluate_object_reflection(initial_object, final_object)
            if reflection:
                network.append((reflection, initial_object_name))
            else:
                rotation = evaluate_object_rotation(initial_object, final_object)
                if rotation:
                    network.append((rotation, initial_object_name))
            for attribute_name in initial_object.attributes:
                if attribute_name in final_object.attributes:
                    if initial_object.attributes[attribute_name] != final_object.attributes[attribute_name]:
                        if attribute_name == 'shape':
                            # special case for squares and rectangles
                            morph = square_rectangle_morph(initial_object, final_object)
                            if morph:
                                network.append(morph)
                            else:
                                network.append(('shape_change', initial_object_name))
                        if attribute_name == 'size':
                            if sizes[initial_object.attributes['size']] > sizes[
                                final_object.attributes['size']]:
                                network.append(('shrink', initial_object_name))
                            else:
                                network.append(('grow', initial_object_name))
                        if attribute_name == 'height':
                            network.append(('scale-v', initial_object_name))
                        if attribute_name == 'width':
                            network.append(('scale-h', initial_object_name))
                        if attribute_name == 'fill':
                            network.append(('color_change', initial_object_name))
                            # if attribute_name == 'overlaps':
                            #     print("Processing overlaps")
                            #     initial_overlap_list = initial_object.attributes[attribute_name].split(',')
                            #     final_overlap_list = final_object.attributes[attribute_name].split(',')
                            #     for overlap in initial_overlap_list:
                            #         if matched_object_name(overlap, tuples) not in final_overlap_list: # These objects don't overlap any more.
                            #             if 'above' in initial_object.attributes:
                            #                 above_list = initial_object.attributes['above'].split(',')
                            #                 if matched_object_name(overlap, tuples) in above_list:
                            #                     network.append(('v-separation', initial_object_name))
                            #             if 'left-of' in initial_object.attributes:
                            #                 left_list = initial_object.attributes['left-of'].split(',')
                            #                 if matched_object_name(overlap, tuples) in left_list:
                            #                     network.append(('h-separation', initial_object_name))

                            # if attribute_name == 'inside':
                            #     print("Not currently handling relational attributes in transformations")
                            # if attribute_name == 'overlaps':
                            #     print("Not currently handling relational attributes in transformations")
                            # if attribute_name == 'above':
                            #     print("Not currently handling relational attributes in transformations")
                            # if attribute_name == 'left-of':
                            #     print("Not currently handling relational attributes in transformations")

    return [network]


def object_horizontal_ranking(figure):
    ranking_list = []
    for object in figure.objects.values():
        left_of_count = len(object.attributes['left-of'].split(','))
        ranking_list.append((object.name, left_of_count))
    return sorted(ranking_list, key=lambda item: -item[1])  # left to right


def object_vertical_ranking(figure):
    ranking_list = []
    for object in figure.objects.values():
        above_count = len(object.attributes['above'].split(','))
        ranking_list.append((object.name, above_count))
    return sorted(ranking_list, key=lambda item: -item[1])  # top to bottom


def square_rectangle_morph(initial_object, final_object):
    initial_shape = initial_object.attributes['shape']
    final_shape = final_object.attributes['shape']
    stretched_square = initial_shape == 'square' and final_shape == 'rectangle'
    squashed_rectangle = initial_shape == 'rectangle' and final_shape == 'square'
    if stretched_square or squashed_rectangle:
        if initial_shape == 'square':
            if sizes[initial_object.attributes['size']] != sizes[final_object.attributes['height']]:
                return 'scale-v', initial_object.name
            if sizes[initial_object.attributes['size']] != sizes[final_object.attributes['width']]:
                return 'scale-h', initial_object.name
        else:
            if sizes[initial_object.attributes['height']] != sizes[final_object.attributes['size']]:
                return 'scale-v', initial_object.name
            if sizes[initial_object.attributes['width']] != sizes[final_object.attributes['size']]:
                return 'scale-h', initial_object.name
    else:
        return False


def objects_nearly_match(initial_object, final_object, unmatched_attribute_name):
    ignored_attributes = ['overlaps', 'inside', 'above', 'left-of']
    for attribute_name in initial_object.attributes:
        if attribute_name != unmatched_attribute_name and attribute_name in final_object.attributes and attribute_name not in ignored_attributes:
            if initial_object.attributes[attribute_name] != final_object.attributes[attribute_name]:
                return False
    return True


def evaluate_object_rotation(initial_object, final_object):
    if not objects_nearly_match(initial_object, final_object, 'angle'):
        return False
    if 'angle' in initial_object.attributes and 'angle' in final_object.attributes:
        rotation = int(final_object.attributes['angle']) - int(initial_object.attributes['angle'])
        while rotation < 0:
            rotation += 360
        if rotation > 0:
            return 'rotate-' + str(rotation)
        else:
            return False
    else:
        return False


def build_frame_transformation_network(initial_figure, final_figure, mapping):
    framewide_transformations = []
    # Rotated?
    framewide_rotation = build_framewide_rotation_network(initial_figure, final_figure, mapping)
    if framewide_rotation:
        framewide_transformations.append(framewide_rotation)
    framewide_reflection = build_framewide_reflection_network(initial_figure, final_figure, mapping)
    if framewide_reflection:
        framewide_transformations.append(framewide_reflection)

    if len(framewide_transformations) > 0:
        return framewide_transformations
    else:
        return False


def build_framewide_rotation_network(initial_figure, final_figure, mapping):
    rotation = None
    for initial_name in initial_figure.objects:
        final_name = matched_object_name(initial_name, mapping)
        if initial_name != 'none' and final_name != 'none':
            initial_object = initial_figure.objects[initial_name]
            final_object = final_figure.objects[final_name]
            if 'angle' in initial_object.attributes and 'angle' in final_object.attributes:
                temp_rotation = evaluate_object_rotation(initial_object, final_object)
                if rotation is not None and temp_rotation != rotation:
                    return False
                else:
                    rotation = temp_rotation
        else:
            return False  # objects added or deleted, don't try and calculate
    if rotation:
        return [("frame_" + str(rotation), 'frame')]
    else:
        return False


def build_framewide_reflection_network(initial_figure, final_figure, mapping):
    reflection = None
    for initial_name in initial_figure.objects:
        final_name = matched_object_name(initial_name, mapping)
        if final_name != 'none' and initial_name != 'none':
            initial_object = initial_figure.objects[initial_name]
            final_object = final_figure.objects[final_name]
            if not objects_nearly_match(initial_object, final_object,
                                        'angle') and not objects_nearly_match(initial_object,
                                                                              final_object,
                                                                              'alignment'):
                return False
            reflect = evaluate_object_reflection(initial_object, final_object)
        else:
            return False  # objects added or deleted, don't try and calculate
        if reflection is not None and reflection != reflect:
            return False
        else:
            reflection = reflect
    if not reflection:
        return False
    else:
        return [("frame_" + reflect, "frame")]


def list_all_tuples(initial_figure, final_figure):
    return_list = []
    initial_keys = list(initial_figure.objects.keys())
    final_keys = list(final_figure.objects.keys())
    for i in range(len(initial_keys)):
        for j in range(len(final_keys)):
            return_list.append((initial_keys[i],
                                final_keys[j],
                                object_difference(initial_figure.objects[initial_keys[i]],
                                                  final_figure.objects[final_keys[j]])
                                ))
    return return_list


def matched_object_name(object1_name, mapping):
    for tuple in mapping:
        if tuple[0] == object1_name:
            return tuple[1]
        if tuple[1] == object1_name:
            return tuple[0]

            # Simple matching method: sort the candidate matches in order of increasing difference between the objects
            # Move the first one (it will be the one with the greatest similarity) to the final list and remove from the candidate
            # list all pairings that have either of the objects in the pairing we selected.  Also remove them from a list of all
            # the object names.  When we are out of pairings, go through the remaining objects and assign them to "none"


def find_best_mapping(initial_figure, final_figure):
    tuple_list = sorted(list_all_tuples(initial_figure, final_figure), key=lambda pair: -pair[2])
    initial_keys = list(initial_figure.objects.keys())
    final_keys = list(final_figure.objects.keys())
    key_list = initial_keys + final_keys
    return_list = []

    while len(tuple_list) > 0:
        top_tuple = tuple_list.pop()
        return_list.append(top_tuple)
        key_list.remove(top_tuple[0])
        key_list.remove(top_tuple[1])

        tuple_list = list(filter(lambda x: not tuples_have_common_members(x, top_tuple), tuple_list))

    for key in key_list:
        if key in initial_figure.objects.keys():
            return_list.append((key, 'none', DELETION_DIFF))
        else:
            return_list.append(('none', key, DELETION_DIFF))

    return return_list


def tuples_have_common_members(tuple1, tuple2):
    for i in range(2):
        for j in range(2):
            if tuple1[i] == tuple2[j]:
                return True
    else:
        return False


def object_horizontal_movement(initial_object_name, initial_figure, final_figure, mapping):
    movement = False
    initial_object = initial_figure.objects[initial_object_name]
    final_object_name = matched_object_name(initial_object_name, mapping)
    final_object = final_figure.objects[final_object_name]
    if leftof_count(final_object) > leftof_count(initial_object):
        movement = 'left'
    if leftof_count(final_object) < leftof_count(initial_object):
        movement = 'right'
    return movement


def object_vertical_movement(initial_object_name, initial_figure, final_figure, mapping):
    movement = False
    initial_object = initial_figure.objects[initial_object_name]
    final_object_name = matched_object_name(initial_object_name, mapping)
    final_object = final_figure.objects[final_object_name]
    if above_count(final_object) > above_count(initial_object):
        movement = 'up'
    if above_count(final_object) < above_count(initial_object):
        movement = 'down'
    return movement


def overlap_count(object):
    if 'overlaps' not in object.attributes:
        return 0
    else:
        return len(object.attributes['overlaps'].split(','))


def above_count(object):
    if 'above' not in object.attributes:
        return 0
    else:
        return len(object.attributes['above'].split(','))


def leftof_count(object):
    if 'left-of' not in object.attributes:
        return 0
    else:
        return len(object.attributes['left-of'].split(','))


def evaluate_object_reflection(initial_object, final_object):
    if 'alignment' in initial_object.attributes and 'alignment' in final_object.attributes:
        # alignment attributes follow the pattern 'vertical_descriptor-horizontal_descriptor' e.g. 'lower-right'
        initial_alignment = initial_object.attributes['alignment'].split('-')
        final_alignment = final_object.attributes['alignment'].split('-')
        if initial_alignment[0] == final_alignment[0] and initial_alignment[1] != final_alignment[1]:
            return 'reflect-v'
        elif initial_alignment[0] != final_alignment[0] and initial_alignment[1] == final_alignment[1]:
            return 'reflect-h'
    if 'angle' in initial_object.attributes and 'angle' in final_object.attributes and initial_object.attributes[
        'angle'] != final_object.attributes['angle']:
        initial_angle = initial_object.attributes['angle']
        final_angle = final_object.attributes['angle']
        initial_rad = deg_to_rad(int(initial_angle))
        final_rad = deg_to_rad(int(final_angle))
        initial_cos = cos(initial_rad)
        final_cos = cos(final_rad)
        initial_sin = sin(initial_rad)
        final_sin = sin(final_rad)
        if abs(initial_cos + final_cos) < .000001 and abs(initial_sin - final_sin) < .000001:
            return 'reflect-v'
        if abs(initial_sin + final_sin) < .000001 and abs(initial_cos - final_cos) < .000001:
            return 'reflect-h'
    return False


def object_difference(obj1, obj2):
    diff = 0
    for attrName in obj1.attributes:
        if attrName in obj2.attributes:
            attrLen1 = len(obj1.attributes[attrName].split(','))
            attrLen2 = len(obj2.attributes[attrName].split(','))
            if attrLen1 > 1 or attrLen2 > 1:
                diff += difference_weights[attrName] * abs(attrLen1 - attrLen2)
            elif obj1.attributes[attrName] != obj2.attributes[attrName]:
                diff += difference_weights[attrName]
        else:
            diff += difference_weights[attrName]
    for attrName in obj2.attributes:
        if attrName not in obj1.attributes:
            diff += difference_weights[attrName]

    return diff

from collections import defaultdict
import sys

def default_factory():
    return 1

similarity_weights = defaultdict()
similarity_weights.update(
    {'deleted': 2, 'added': 2, 'shape_change': 1, 'rotate': 4, 'frame_rotate': 4, 'reflect': 5, 'frame_reflect': 5,
     'shrink': 3, 'grow': 3, 'scale': 3,
     'color_change': 5, 'move': 5})
IDENTITY_WEIGHT = 6

def score_networks(networks_a, networks_b):
    # return flat_scoring(networks_a, networks_b)
    # return weighted_flat_scoring(networks_a, networks_b)
    # return weighted_flat_scoring_with_penalties(networks_a, networks_b)
    return weighted_proportional_scoring(networks_a, networks_b)

def weighted_proportional_scoring(networks_a, networks_b):
    max_score = - sys.maxsize
    for network_a in networks_a:
        for network_b in networks_b:
            score = 0
            if len(network_a) == len(network_b):
                score += 1
            if network_a == [] and network_b == []:
                score += IDENTITY_WEIGHT
            for relationship in network_a:
                relationship_names = relationship[0].split('-')
                weight = similarity_weights[relationship_names[0]]
                count_a = count_relationship_occurrences(relationship[0], network_a)
                count_b = count_relationship_occurrences(relationship[0], network_b)
                score += weight * (((count_a + count_b) - abs(count_a - count_b)) / (count_a + count_b))
            if score > max_score:
                max_score = score
    return max_score

def weighted_flat_scoring(networks_a, networks_b):
    max_score = - sys.maxsize
    for network_a in networks_a:
        for network_b in networks_b:
            score = 0
            if len(network_a) == len(network_b):
                score += 1
            if network_a == [] and network_b == []:
                score += IDENTITY_WEIGHT
            counts = defaultdict(int)
            for relationship in network_a:
                counts[relationship[0]] += 1
            for relationship in network_b:
                counts[relationship[0]] -= 1
            for key in counts:
                if counts[key] == 0:
                    relationship_names = key.split('-')
                    weight = similarity_weights[relationship_names[0]]
                    score += weight
            if score > max_score:
                max_score = score
    return max_score

def flat_scoring(networks_a, networks_b):
    max_score = - sys.maxsize
    for network_a in networks_a:
        for network_b in networks_b:
            score = 0
            if len(network_a) == len(network_b):
                score += 1
            if network_a == [] and network_b == []:
                score += IDENTITY_WEIGHT
            counts = defaultdict(int)
            for relationship in network_a:
                counts[relationship[0]] += 1
            for relationship in network_b:
                counts[relationship[0]] -= 1
            for key in counts:
                if counts[key] == 0:
                    relationship_names = key.split('-')
                    weight = similarity_weights[relationship_names[0]]
                    score += weight
            if score > max_score:
                max_score = score
    return max_score

def weighted_flat_scoring_with_penalties(networks_a, networks_b):
    max_score = 0
    for network_a in networks_a:
        for network_b in networks_b:
            score = 0
            if len(network_a) == len(network_b):
                score += 1
            if network_a == [] and network_b == []:
                score += IDENTITY_WEIGHT
            counts = defaultdict(int)
            for relationship in network_a:
                counts[relationship[0]] += 1
            for relationship in network_b:
                counts[relationship[0]] -= 1
            for key in counts:
                relationship_names = key.split('-')
                weight = similarity_weights[relationship_names[0]]
                if counts[key] == 0:
                    score += weight
                else:
                    score -= weight
            if score > max_score:
                max_score = score
    return max_score

def count_relationship_occurrences(relationship_name, network):
    count = 0
    for relationship in network:
        if relationship[0] == relationship_name:
            count += 1
    return count

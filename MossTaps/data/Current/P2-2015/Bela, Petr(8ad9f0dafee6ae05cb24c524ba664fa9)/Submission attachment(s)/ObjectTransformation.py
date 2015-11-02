# Describes transformation between two objects.
class ObjectTransformation:
    # @param name the name of the object
    def __init__(self, source, target):
        # Source object from the source figure
        self.source = source

        # Target object from the target figure
        self.target = target

        # A dictionary of name-value pairs representing the attributes that the transformation changes.
        # The key for these changes is the name of the attribute, and the value is
        # a pair of from-value and to-value.
        # For example, a transformation from large to small size and rotation from 0 to 90 degrees would be stored as:
        # "size: {from: large, to: small}", "rotation: {from: 0, to: 90}".
        self.changes = {}

    def __str__(self):
        return "changes from %s to %s: %s" % (self.source.name, self.target.name, self.changes)

    def __repr__(self):
        return self.__str__()

    def __eq__(self, other):
        if type(other) is type(self):
            return self.changes == other.changes
        else:
            return False

    def cost(self):
        total = 0
        for attr in self.changes:
            total += 1 # Transformation.costs[attr]
        return total

    # A dictionary of transformation costs
    costs = {
        'delete': 6,
        'rotate': 5,
        'reflect': 4,
        'resize': 3,
        'fill': 2,
        'translate': 1
    }

    attributes = {
        # independent
        'shape': ['circle', 'triangle', 'right triangle', 'square', 'rectangle', 'diamond', 'pentagon', 'octagon', 'plus', 'star', 'pac-man', 'heart'],

        # ordered
        'size': ['very small', 'small', 'medium', 'large', 'very large', 'huge'],
        'height': ['small', 'large', 'huge'],
        'width': ['small', 'large', 'huge'],
        'angle': ['0', '45', '90', '135', '180', '225', '270', '315'],

        # complementary
        'fill': ['no', 'yes', 'right-half', 'left-half', 'bottom-half', 'top-half'],
        'alignment': ['bottom-right', 'top-right', 'bottom-left', 'top-left'],

        # relational
        'overlaps': [],
        'inside': [],
        'left-of': [],
        'above': [],
    }

    attributeTypes = {
        'independent': ['shape'],
        'ordered': ['size', 'height', 'width', 'angle'],
        'complementary': ['fill', 'alignment'],
        'relational': ['overlaps', 'inside', 'left-of', 'above']
    }

# Describes transformation between two objects.
class Transformation:
    # @param name the name of the object
    def __init__(self, source, target):
        # A dictionary of name-value pairs representing the attributes that the transformation changes.
        # The key for these changes is the name of the attribute, and the value is
        # a pair of from-value and to-value.
        # For example, a transformation from large to small size and rotation from 0 to 90 degrees would be stored as:
        # "size: {from: large, to: small}", "rotation: {from: 0, to: 90}".
        self.changes = {}

        # Source object from the source figure
        self.source = source

        # Target object from the target figure
        self.target = target

    def __str__(self):
        return "changes from %s to %s: %s" % (self.source.name, self.target.name, self.changes)

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

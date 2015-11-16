from ObjectTransformation import ObjectTransformation

# Describes transformation between two figures.
class FigureTransformation:
    # @param name the name of the object
    def __init__(self, source, target):
        # Source figure
        self.source = source

        # Target figure
        self.target = target

        # Mappings between source objects and target objects
        self.find_mappings()

        # List of object transformations
        self.transformations = self.calculate_transformations()

    def __str__(self):
        return "transformations from %s to %s: %s" % (self.source.name, self.target.name, self.transformations)

    def __repr__(self):
        return self.__str__()

    def __eq__(self, other):
        if type(other) is type(self):
            for t in self.transformations:
                if t not in other.transformations:
                    return False
            for t in other.transformations:
                if t not in self.transformations:
                    return False
            return True
        else:
            return False

    # Calculate transformations from source figure to target figure
    def calculate_transformations(self):
        transformations = {}

        for source_obj_name, source_obj in self.source.objects.iteritems():
            for target_obj_name, target_obj in self.target.objects.iteritems():
                transformation = ObjectTransformation(source_obj, target_obj)

                for attr in source_obj.attributes:
                    # removed attribute
                    if attr not in target_obj.attributes:
                        if attr in ObjectTransformation.attributeTypes['relational']:
                            from_value = sorted(source_obj.attributes[attr].split(','))
                            transformation.changes[attr] = {'from': from_value}
                        else:
                            transformation.changes[attr] = {'from': source_obj.attributes[attr]}

                    # moved
                    elif attr in ObjectTransformation.attributeTypes['relational']:
                        from_value = sorted(source_obj.attributes[attr].split(','))
                        to_value = sorted(map(lambda x: self.target_mapping[x], target_obj.attributes[attr].split(',')))
                        if from_value != to_value:
                            transformation.changes[attr] = {'from': from_value, 'to': to_value}

                    # transformed
                    elif source_obj.attributes[attr] != target_obj.attributes[attr]:
                        from_value = source_obj.attributes[attr]
                        to_value = target_obj.attributes[attr]
                        transformation.changes[attr] = {'from': from_value, 'to': to_value}
                        if attr in ObjectTransformation.attributeTypes['ordered']:
                            transformation.changes[attr] = {'diff': ObjectTransformation.attributes[attr].index(to_value) - ObjectTransformation.attributes[attr].index(from_value)}

                for attr in target_obj.attributes:
                    # added attribute
                    if attr not in source_obj.attributes:
                        if attr in ObjectTransformation.attributeTypes['relational']:
                            to_value = sorted(target_obj.attributes[attr].split(','))
                            transformation.changes[attr] = {'to': to_value}
                        else:
                            transformation.changes[attr] = {'to': target_obj.attributes[attr]}

                if source_obj_name not in transformations or transformation.cost() < transformations[source_obj_name].cost():
                    transformations[source_obj_name] = transformation

        return transformations.values()

    # Determine mapping between objects in the source and target figure
    def find_mappings(self):
        self.source_mapping = {}
        self.target_mapping = {}

        for source_obj_name, source_obj in self.source.objects.iteritems():
            for target_obj_name, target_obj in self.target.objects.iteritems():

                similarRelations = True
                for attr in ObjectTransformation.attributeTypes['relational']:
                    if attr in source_obj.attributes and attr in target_obj.attributes:
                        if len(source_obj.attributes[attr].split(',')) != len(source_obj.attributes[attr].split(',')):
                            similarRelations = False

                if similarRelations and source_obj_name not in self.source_mapping and target_obj_name not in self.target_mapping:
                    self.source_mapping[source_obj_name] = target_obj_name
                    self.target_mapping[target_obj_name] = source_obj_name

        # the rest don't map to anything
        for source_obj_name in self.source.objects:
            if source_obj_name not in self.source_mapping:
                self.source_mapping[source_obj_name] = ''
        for target_obj_name in self.target.objects:
            if target_obj_name not in self.target_mapping:
                self.target_mapping[target_obj_name] = ''


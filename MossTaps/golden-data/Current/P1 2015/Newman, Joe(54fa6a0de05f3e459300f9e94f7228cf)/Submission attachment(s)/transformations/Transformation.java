package ravensproject.transformations;

import java.util.Map;

public interface Transformation extends HasCost {
    Map<String, String> transform(Map<String, String> toTransform);
}

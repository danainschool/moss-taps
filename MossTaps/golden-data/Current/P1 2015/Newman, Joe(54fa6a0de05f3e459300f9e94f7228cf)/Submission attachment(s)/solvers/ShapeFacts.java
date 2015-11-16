package ravensproject.solvers;

import java.util.*;

public final class ShapeFacts {
    private ShapeFacts() {
        //Namespace for static functions, so hide constructor
    }

    public static final List<String> SIZES = Collections.unmodifiableList(Arrays.asList(
            "microscopic", "tiny", "very small", "small", "medium", "large", "very large", "huge", "enormous"
    ));

    public static final Set<String> SHAPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "circle", "diamond", "heart", "octagon", "pac-man", "pentagon", "plus", "rectangle", "right triangle",
            "square", "star", "triangle"
    )));

    public static final Set<String> FILLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "no", "yes", "bottom-half", "left-half", "right-half", "top-half"
    )));

    public static int normalizeAngle(int degrees) {
        while(degrees < 0) {
            degrees += 360;
        }
        while(degrees >= 360) {
            degrees -= 360;
        }
        return degrees;
    }
}

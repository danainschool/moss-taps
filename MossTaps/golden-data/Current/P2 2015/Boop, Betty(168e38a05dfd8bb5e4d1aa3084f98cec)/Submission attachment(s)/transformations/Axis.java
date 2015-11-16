package ravensproject.transformations;

public enum Axis {
    HORIZONTAL("left-of", "hOrder"),
    VERTICAL("above", "vOrder"),
    NESTING("inside", "nesting"),
    OVERLAP("overlaps", "overlapIndex");

    public final String preKey, postKey;

    Axis(final String preKey, final String postKey) {
        this.preKey = preKey;
        this.postKey = postKey;
    }
}

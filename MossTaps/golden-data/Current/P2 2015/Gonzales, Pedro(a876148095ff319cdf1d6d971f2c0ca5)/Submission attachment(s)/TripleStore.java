package ravensproject;

/**
 * This class is used to describe relationships between objects within a ravens figure
 */
public class TripleStore {
    private final RavensObject primary;
    private final String relationship;
    private final RavensObject related;

    public TripleStore(RavensObject primaryObj, String relation, RavensObject relatedObj) {
        this.primary = primaryObj;
        this.relationship = relation;
        this.related = relatedObj;
    }

    public RavensObject getPrimary() {
        return primary;
    }

    public String getRelationship() {
        return relationship;
    }

    public RavensObject getRelated() {
        return related;
    }
}

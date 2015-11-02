package ravensproject;

import com.sun.istack.internal.NotNull;

/**
 * Created by jblac_000 on 6/2/2015.
 */
public class Transition {

    @NotNull
    private TransitionType transitionType;
    private String value;

    public Transition(TransitionType type, String value){
        this.transitionType = type;
        this.value = value;
    }

    public Transition(TransitionType type){
        this.transitionType = type;
        this.value = null;
    }

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (transitionType != that.transitionType) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = transitionType.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}

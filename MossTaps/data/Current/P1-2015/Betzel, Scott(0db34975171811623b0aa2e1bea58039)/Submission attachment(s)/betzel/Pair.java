package ravensproject.betzel;

/**
 * Created by scott betzel on 6/7/15.
 */
public class Pair<A, B> {
    public A fst;
    public B snd;

    public Pair(A first, B second) {
        super();
        this.fst = first;
        this.snd = second;
    }

    public int hashCode() {
        int hashFirst = fst != null ? fst.hashCode() : 0;
        int hashSecond = snd != null ? snd.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return
                    ((  this.fst == otherPair.fst ||
                            ( this.fst != null && otherPair.fst != null &&
                                    this.fst.equals(otherPair.fst))) &&
                            (	this.snd == otherPair.snd ||
                                    ( this.snd != null && otherPair.snd != null &&
                                            this.snd.equals(otherPair.snd))) );
        }

        return false;
    }

    public String toString()
    {
        return "(" + fst + ", " + snd + ")";
    }
}

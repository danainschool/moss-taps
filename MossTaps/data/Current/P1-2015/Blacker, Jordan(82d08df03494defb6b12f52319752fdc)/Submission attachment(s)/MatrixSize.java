package ravensproject;

/**
 * Created by jblac_000 on 6/1/2015.
 */
public enum MatrixSize {
    TWOSQUARE (2),
    THREESQUARE (3);

    private int size;
    MatrixSize(int square) { this.size = square; }
    public int getSize() { return this.size; }
}

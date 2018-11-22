public class Centipede extends Drawable{

    boolean dir; // Direction that the centipede is moving.
    public Centipede next; // The next segment after the current.
    boolean head; // Indicates segment start.

    public Centipede(int x, int y, Centipede n, boolean h) {
        dir = true;
        speed = 5;
        row = x;
        col = y;
        next = n;
        head = h;
    }

    @Override
    public String toString() { return head ? "\u263A" : "\u25CF"; }

    public void move() {

    }
}

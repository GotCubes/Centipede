public class Mushroom extends Drawable{

    public Mushroom(int x, int y) {
        speed = 1;
        row = x;
        col = y;
    }

    @Override
    public String toString() { return "\u25A0"; }
}

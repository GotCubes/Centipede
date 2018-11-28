public class Mushroom extends Drawable{

    public Mushroom(int x, int y) {
        durability = 3;
        row = x;
        col = y;
    }

    @Override
    public String toString() { return "\u25A0"; }
}

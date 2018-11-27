public class Bullet extends Drawable {
    public int dy;

    public Bullet(int x, int y) {
        dy = 25;
        row = x;
        col = y;
    }

    @Override
    public String toString() { return "\u007C"; }
}

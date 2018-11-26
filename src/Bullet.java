public class Bullet extends Drawable {
    public Bullet(int x, int y) {
        speed = 1;
        row = x;
        col = y;
    }

    @Override
    public String toString() { return "\u007C"; }
}

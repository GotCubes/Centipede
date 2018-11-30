public class Bullet extends Drawable {

    public Bullet(int x, int y) {
        row = x;
        col = y;
    }

    public boolean move() {
        col -= 15;

        // Delete bullets that go out of bounds.
        return col < 50;
    }
}

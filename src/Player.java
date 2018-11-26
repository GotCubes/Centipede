public class Player extends Drawable{
    public Player(int x, int y) {
        speed = 1;
        row = x;
        col = y;
    }

    @Override
    public String toString() { return "\uD83D\uDE80"; }
}

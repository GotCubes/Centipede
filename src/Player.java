public class Player extends Drawable{
    public int lives, score;
    public boolean hit;

    public Player(int x, int y) {
        speed = 1;
        row = x;
        col = y;
        lives = 3;
        score = 0;
        hit = false;
    }

    @Override
    public String toString() { return "\uD83D\uDE80"; }
}

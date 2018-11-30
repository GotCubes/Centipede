import java.awt.*;

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

    public void move(Point q) {
        // Get mouse location.
        Point p = MouseInfo.getPointerInfo().getLocation();

        // Constrain within the game area.
        row = Math.min(Math.max(p.x - q.x - 7, 35), 615);
        col = Math.min(Math.max(p.y - q.y - 30, 60), 640);
    }
}

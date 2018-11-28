public class Spider extends Drawable {
    public int dx, dy;

    public Spider(int x, int y) {
        speed = 20;
        durability = 2;
        row = x;
        col = y;
    }

    public void move() {
        if(frameCnt == 0) {
            int x = 25 + (int) (Math.random() * ((605 - 25) + 1));
            int y = 315 + (int) (Math.random() * ((630 - 315) + 1));

            dx = x > row ? 1 + (int) (Math.random() * ((10 - 1) + 1)) : -10 + (int) (Math.random() * ((-1 + 10) + 1));
            dy = y > col ? 1 + (int) (Math.random() * ((10 - 1) + 1)) : -10 + (int) (Math.random() * ((-1 + 10) + 1));
        }

        row = Math.min(Math.max(row + dx, 25), 605);
        col = Math.min(Math.max(col + dy, 315), 630);
    }
}

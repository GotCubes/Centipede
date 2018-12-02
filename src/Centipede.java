class Centipede extends Drawable {

    boolean dir; // Direction that the centipede is moving.
    boolean head; // Indicates segment start.

    Centipede(int x, int y, boolean h) {
        dir = false;
        speed = 3;
        durability = 2;
        row = x;
        col = y;
        head = h;
    }

    void move(Centipede parent) {
        if(frameCnt == 0) {
            // Get next location if head.
            if (head) {
                // Test for collision.
                int nxtCol = col + (dir ? 1 : -1);

                if (nxtCol < 0 || nxtCol > 29 || Game.board[row][nxtCol] instanceof Mushroom) {
                    if (row != 27)
                        row = row + 1;
                    dir = !dir;
                } else
                    col = nxtCol;
                // Otherwise follow parent.
            } else {
                dir = parent.dir;
                row = parent.row;
                col = parent.col;
            }
        }
        frameCnt = (frameCnt + 1) % speed;
    }
}
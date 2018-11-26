public class Centipede extends Drawable{

    boolean dir; // Direction that the centipede is moving.
    public Centipede next; // The next segment after the current.
    boolean head; // Indicates segment start.

    public Centipede(int x, int y, Centipede n, boolean h) {
        dir = true;
        speed = 5;
        row = x;
        col = y;
        next = n;
        head = h;
        maxFrames = 3;
    }

    @Override
    public String toString() { return head ? "\u263A" : "\u25CF"; }

    public void move(Centipede parent) {
        // Default next location.
        int nxtRow = row;
        int nxtCol =  dir ? col + 1 : col - 1;

        // Get next location if head.
        if(head) {
            // Test for collision.
            if(nxtCol < 0 || nxtCol > 29 || Window.board[row][nxtCol] instanceof Mushroom) {
                nxtRow = row + 1;
                nxtCol = col;
                dir = !dir;
            }
        // Otherwise follow parent.
        } else {
            nxtRow = parent.row;
            nxtCol = parent.col;
        }

        // Recursively move all segments in the centipede, and update board.
        if(next != null) {
            next.move(this);
            Window.board[nxtRow][nxtCol] = this;
        } else
            Window.board[row][col] = Window.blank;

        // Update segment location.
        row = nxtRow;
        col = nxtCol;
    }
}

public class Centipede extends Drawable{

    boolean dir; // Direction that the centipede is moving.
    public Centipede next, prev; // The next segment after the current.
    boolean head; // Indicates segment start.

    public Centipede(int x, int y, Centipede n, boolean h) {
        dir = true;
        speed = 5;
        durability = 1;
        row = x;
        col = y;
        next = n;
        head = h;
    }

    @Override
    public String toString() { return head ? "\u263A" : "\u25CF"; }

    public void move() {
        // Default next location.
        int nxtRow = row;
        int nxtCol = dir ? col + 1 : col - 1;

        // Get next location if head.
        if(head) {
            // Test for collision.
            if(nxtCol < 0 || nxtCol > 29 || Window.board[row][nxtCol] instanceof Mushroom || Window.board[row][nxtCol] instanceof Centipede) {
                if(row == 27) {
                    reverse();
                    return;
                } else {
                    nxtRow = row + 1;
                    nxtCol = col;
                    dir = !dir;
                }
            }
        // Otherwise follow parent.
        } else {
            dir = prev.dir;
            nxtRow = prev.row;
            nxtCol = prev.col;
        }

        // Recursively move all segments in the centipede, and update board.
        if(next != null) {
            next.move();
            Window.board[nxtRow][nxtCol] = this;
        } else
            Window.board[row][col] = Window.blank;

        // Update segment location.
        row = nxtRow;
        col = nxtCol;
    }

    public void reverse() {
        Centipede curr = this;
        curr.head = false;

        while(curr != null) {
            curr.dir = !curr.dir;
            Centipede temp = curr.next;
            curr.next = curr.prev;
            curr.prev = temp;

            if(curr.prev == null) {
                curr.head = true;
                Window.heads.remove(this);
                Window.heads.add(curr);
            }

            curr = curr.prev;
        }
    }
}

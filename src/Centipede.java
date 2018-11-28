public class Centipede extends Drawable{

    boolean dir; // Direction that the centipede is moving.
    public Centipede next, prev; // The next segment after the current.
    boolean head; // Indicates segment start.

    public Centipede(int x, int y, Centipede n, boolean h) {
        dir = true;
        speed = 4;
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
            if(nxtCol < 0 || nxtCol > 29 || Window.board[row][nxtCol] instanceof Mushroom) {
                if(row == 27) {
                    Window.toReverse.add(this);
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
        if(this.next == null && this.prev == null) {
            dir = !dir;
            if(!Window.heads.contains(this))
                Window.heads.add(this);
        } else {
            Window.heads.remove(this);
            Centipede curr = this;
            Centipede temp;
            curr.head = false;

            while (curr.next != null) {
                curr.dir = !curr.dir;
                temp = curr.next;
                curr.next = curr.prev;
                curr.prev = temp;
                curr = curr.prev;
            }

            curr.head = true;
            curr.dir = !curr.dir;
            temp = curr.next;
            curr.next = curr.prev;
            curr.prev = temp;

            if(!Window.heads.contains(curr))
                Window.heads.add(curr);;
        }
    }
}

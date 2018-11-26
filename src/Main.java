import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Main {

    public static Drawable[][] board = new Drawable[30][30];
    public static Drawable blank = new Drawable();
    public static int density;
    public static ArrayList heads = new ArrayList();

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java Main density");
            System.exit(-1);
        }

        density = Integer.valueOf(args[0]);
        if(density < 1 || density > 5) {
            System.out.println("Density must be a value between 1 and 5.");
            System.exit(-2);
        }

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++)
                board[i][j] = blank;
        }

        initCentipede();
        placeShrooms();

        while(true) {

            try {
                Thread.sleep(100);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            Iterator it = heads.iterator();
            while(it.hasNext()) {
                Centipede head = (Centipede) it.next();
                if(head.frameCnt == 0) {
                    head.move(null);
                    printBoard();
                }
                head.frameCnt = (head.frameCnt + 1) % head.maxFrames;
            }
        }
    }

    public static void printBoard() {
        StringBuilder ret = new StringBuilder();
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++)
                ret.append(board[i][j]);
            ret.append("\n");
        }
        System.out.print(ret.toString());
    }

    // Initialize the centipede.
    public static void initCentipede() {
        board[0][0] = new Centipede(0, 0, null, false);
        for(int i = 1; i < 11; i++)
            board[0][i] = new Centipede(0, i, (Centipede) board[0][i - 1], false);
        board[0][11] = new Centipede(0, 11, (Centipede) board[0][10], true);
        heads.add(board[0][11]);
    }

    // Place mushrooms on the board according to the density.
    public static void placeShrooms() {
        ArrayList valCols = new ArrayList();
        for(int i = 1; i < (board[0].length - 1); i++) valCols.add(i);

        // Iterate through each row in the placeable range.
        for(int row = 1; row < (board.length  - (board.length / 10)); row++) {
            // Get each valid column in the row.
            Iterator it = valCols.iterator();
            
            ArrayList nxtVals = new ArrayList();
            for(int i = 1; i < (board[0].length - 1); i++) nxtVals.add(i);

            // Iterate through each valid column.
            while(it.hasNext()) {
                int col = (Integer) it.next();
                if(getXinYChance(density, 15)) {
                    board[row][col] = new Mushroom(row, col);
                    nxtVals.remove(Integer.valueOf(col - 1));
                    nxtVals.remove(Integer.valueOf(col + 1));
                }
            }

            // Update list of valid columns.
            valCols = nxtVals;
        }
    }

    // Return true with a num in den chance.
    public static boolean getXinYChance(int num, int den) {
        Random rand = new Random();
        return rand.nextInt(den) < num;
    }
}

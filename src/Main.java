import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;

public class Main {

    public static Object[][] board = new Object[30][30];
    public static int density;

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

        placeShrooms();
    }

    // Place mushrooms on the board according to the density.
    public static void placeShrooms() {
        ArrayList valCols = new ArrayList();
        for(int i = 1; i < (board[0].length - 1); i++) valCols.add(i);

        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");

        // Iterate through each row in the placeable range.
        for(int row = (board.length / 10); row < (board.length  - (board.length / 10)); row++) {
            // Get each valid column in the row.
            Iterator it = valCols.iterator();
            //System.out.println(valCols);
            
            ArrayList valCopy = new ArrayList();
            for(int i = 1; i < (board[0].length - 1); i++) valCopy.add(i);

            // Iterate through each valid column.
            char[] disp = new char[30];
            Arrays.fill(disp, '-');
            while(it.hasNext()) {
                int ind = (Integer) it.next();
                if(getXinYChance(density, 15)) {
                    disp[ind] = 'X';
                    valCopy.remove(Integer.valueOf(ind - 1));
                    valCopy.remove(Integer.valueOf(ind));
                    valCopy.remove(Integer.valueOf(ind + 1));
                }
            }

            System.out.println(disp);
            valCols = valCopy;
        }

        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
    }

    // Return true with a num in den chance.
    public static boolean getXinYChance(int num, int den) {
        Random rand = new Random();
        return rand.nextInt(den) < num;
    }
}

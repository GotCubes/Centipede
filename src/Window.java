import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Window extends JFrame implements ActionListener{

    public static Drawable[][] board = new Drawable[30][30];
    public static Drawable blank = new Drawable();
    public static ArrayList heads = new ArrayList();
    public static ArrayList bullets = new ArrayList();
    public static Player player;
    public static int density;

    public Window() {
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 675);
        getContentPane().setBackground(Color.black);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        getContentPane().setCursor(blankCursor);
    }

    public void actionPerformed(ActionEvent e) {
        // Repaint the panel.
        //printBoard();
        repaint();

        // Update player location.
        Point p = MouseInfo.getPointerInfo().getLocation();
        if(player.frameCnt == 0) {
            player.row = Math.min(Math.max(p.x - 13, 25), 605);
            player.col = Math.min(Math.max(p.y - 36, 25), 605);
        }
        player.frameCnt = (player.frameCnt + 1) % player.speed;


        // Update all centipedes.
        Iterator it = heads.iterator();
        while(it.hasNext()) {
            Centipede head = (Centipede) it.next();
            if(head.frameCnt == 0)
                head.move(null);
            head.frameCnt = (head.frameCnt + 1) % head.speed;
        }

        // Update all bullets.
        it = bullets.iterator();
        ArrayList toRemove = new ArrayList();
        while(it.hasNext()) {
            Bullet bullet = (Bullet) it.next();

            if(bullet.frameCnt == 0)
                bullet.col -= 15;
            bullet.frameCnt = (bullet.frameCnt + 1) % bullet.speed;

            if(bullet.col < 25)
                toRemove.add(bullet);
        }
        bullets.removeAll(toRemove);
    }

    public static void main(String[] args) {
        // Verify correct usage.
        if(args.length != 1) {
            System.out.println("Usage: java Main density");
            System.exit(-1);
        }

        // Verify proper density.
        density = Integer.valueOf(args[0]);
        if(density < 1 || density > 5) {
            System.out.println("Density must be a value between 1 and 5.");
            System.exit(-2);
        }

        // Initialize empty board.
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++)
                board[i][j] = blank;
        }

        // Place centipede and mushrooms.
        initPlayer();
        initCentipede();
        placeShrooms();

        // Initialize window and panel.
        Window window = new Window();
        JPanel panel = new JPanel() {
            // Paint objects onto the panel.
            public void paintComponent(Graphics g) {
                // Set background scenery.
                Graphics2D g2d = (Graphics2D) g;
                setOpaque(false);
                setSize(650, 650);
                setBackground(Color.black);
                g2d.setColor(Color.red);
                g2d.drawRect(25, 25, getWidth() - 50, getHeight() - 50);

                // Draw all grid-bound objects.
                for(int i = 0; i < board.length; i++) {
                    for(int j = 0; j < board[0].length; j++) {
                        if(board[i][j] instanceof Centipede) {
                            g2d.setColor(((Centipede) board[i][j]).head ? Color.blue : Color.green);
                            g2d.fillOval(j * 20 + 25, i * 20 + 25, 20, 20);
                        } else if(board[i][j] instanceof Mushroom) {
                            g2d.setColor(new Color(118, 85, 43));
                            g2d.fillRect(j * 20 + 25, i * 20 + 25, 20, 20);
                        }
                    }
                }

                // Draw player.
                g2d.setColor(Color.pink);
                g2d.fillOval(player.row, player.col, 20, 20);

                // Draw bullets.
                Iterator it = bullets.iterator();
                while(it.hasNext()) {
                    Bullet bullet = (Bullet) it.next();
                    g2d.setStroke(new BasicStroke(3));
                    g2d.setColor(Color.yellow);
                    g2d.drawLine(bullet.row, bullet.col, bullet.row, bullet.col + 10);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Bullet bullet = new Bullet(player.row + 10, player.col);
                bullets.add(bullet);
            }
        });

        window.add(panel);

        Timer timer = new Timer(17, window);
        timer.setRepeats(true);
        timer.start();
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

    // Initialize the player.
    public static void initPlayer() {
        player = new Player(325, 605);
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
                if(getXinYChance(density, 30)) {
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

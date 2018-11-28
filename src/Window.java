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
    public static ArrayList segments = new ArrayList();
    public static ArrayList mushrooms = new ArrayList();
    public static ArrayList bullets = new ArrayList();
    public static Player player;
    public static ArrayList toReverse = new ArrayList();
    public static int density, score, lives;
    public static JLabel scr, lvs;

    public Window() {
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(665, 710);
        getContentPane().setBackground(Color.black);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
        getContentPane().setCursor(blankCursor);
    }

    public void testCollisions() {
        ArrayList btoRemove = new ArrayList();
        ArrayList mtoRemove = new ArrayList();
        ArrayList stoSplit = new ArrayList();

        Iterator bull = bullets.iterator();
        while(bull.hasNext()) {
            Bullet b = (Bullet) bull.next();

            // Test all mushrooms to see if they were hit.
            Iterator it = mushrooms.iterator();
            while(it.hasNext()) {
                Mushroom m = (Mushroom) it.next();
                int clb = (m.row * 20) + 50;
                int cub = (m.row * 20) + 50 + 20;
                int rlb = (m.col * 20) + 25;
                int rub = (m.col * 20) + 25 + 20;
                if(b.col <= cub && b.col >= clb) {
                    if(b.row <= rub && b.row >= rlb) {
                        btoRemove.add(b);

                        if(++m.hitCnt == m.durability) { // Destroyed
                            score += 5;
                            mtoRemove.add(m);
                            board[m.row][m.col] = blank;
                        } else // Hit
                            score += 1;
                    }
                }
            }
            mushrooms.removeAll(mtoRemove);

            // Test all centipede segments to see if they were hit.
            it = segments.iterator();
            while(it.hasNext()) {
                Centipede c = (Centipede) it.next();
                int clb = (c.row * 20) + 50;
                int cub = (c.row * 20) + 50 + 20;
                int rlb = (c.col * 20) + 25;
                int rub = (c.col * 20) + 25 + 20;
                if(b.col < cub && b.col >= clb) {
                    if(b.row < rub && b.row >= rlb) {
                        btoRemove.add(b);

                        // Score points for hitting centipede.
                        if(++c.hitCnt == c.durability) { // Destroyed
                            score += 5;
                            stoSplit.add(c);
                            board[c.row][c.col] = blank;
                        } else // Hit
                            score += 2;
                    }
                }
            }

            // Split all destroyed segments.
            it = stoSplit.iterator();
            while(it.hasNext()) {
                Centipede c = (Centipede) it.next();

                if(c.next != null || c.prev != null) { // Single segment.
                    if(c.prev == null) { // Leading segment.
                        c.next.head = true;
                        c.next.prev = null;

                        if(!heads.contains(c.next))
                            heads.add(c.next);
                    } else if(c.next == null) { // Ending segment.
                        c.prev.next = null;
                    } else { // Middle segment.
                        c.next.head = true;
                        c.next.prev = null;

                        c.next.reverse();
                        c.prev.next = null;
                    }
                }

                board[c.row][c.col] = blank;
                segments.remove(c);
                heads.remove(c);
                c = null;
            }
        }
        bullets.removeAll(btoRemove);
    }

    public void actionPerformed(ActionEvent e) {
        // Repaint the panel.
        testCollisions();
        revalidate();
        repaint();

        // Update player location.
        Point p = MouseInfo.getPointerInfo().getLocation();
        Point q = getLocationOnScreen();

        // Constrain within the game area.
        player.row = Math.min(Math.max(p.x - q.x - 13, 25), 605);
        player.col = Math.min(Math.max(p.y - q.y - 36, 50), 630);

        // Update all centipedes.
        toReverse = new ArrayList();
        Iterator it = heads.iterator();
        while(it.hasNext()) {
            Centipede head = (Centipede) it.next();
            if(head.frameCnt == 0)
                head.move();
            head.frameCnt = (head.frameCnt + 1) % head.speed;
        }

        // Reverse all centipedes that require it.
        it = toReverse.iterator();
        while(it.hasNext()) {
            Centipede c = (Centipede) it.next();
            c.reverse();
        }

        // Update all bullets.
        it = bullets.iterator();
        ArrayList toRemove = new ArrayList();
        while(it.hasNext()) {
            Bullet bullet = (Bullet) it.next();
            bullet.col -= bullet.dy;

            if(bullet.col < 50)
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
        score = 0;
        lives = 3;

        // Initialize window and panel.
        Window window = new Window();
        JPanel panel = new JPanel() {
            // Paint objects onto the panel.
            public void paintComponent(Graphics g) {
                // Set background scenery.
                Graphics2D g2d = (Graphics2D) g;
                setOpaque(false);
                setSize(650, 700);
                setBackground(Color.black);
                g2d.setColor(Color.red);
                g2d.drawRect(25, 50, 600, 600);

                scr.setText("Score: " + score);
                lvs.setText("Lives: " + lives);

                // Draw centipede segments.
                Iterator it = segments.iterator();
                while(it.hasNext()) {
                    Centipede seg = (Centipede) it.next();
                    g2d.setColor(seg.head ? Color.blue : Color.green);
                    g2d.fillOval(seg.col * 20 + 25, seg.row * 20 + 50, 20, 20);
                }

                // Draw mushrooms.
                it = mushrooms.iterator();
                while(it.hasNext()) {
                    Mushroom mush = (Mushroom) it.next();
                    g2d.setColor(new Color(118, 85, 43));

                    if(mush.hitCnt == 0)
                        g2d.fillRect(mush.col * 20 + 25, mush.row * 20 + 50, 20, 20);
                    else if(mush.hitCnt == 1)
                        g2d.fillRect(mush.col * 20 + 25, mush.row * 20 + 50, 20, 15);
                    else if(mush.hitCnt == 2)
                        g2d.fillRect(mush.col * 20 + 25, mush.row * 20 + 50, 20, 10);
                }

                // Draw player.
                g2d.setColor(Color.pink);
                g2d.fillOval(player.row, player.col, 20, 20);

                // Draw bullets.
                it = bullets.iterator();
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

        scr = new JLabel("Score: " + score);
        scr.setBounds(25, 25, 300, 25);
        scr.setLocation(25, 25);
        scr.setFont(new Font("Serif", Font.PLAIN, 25));
        scr.setHorizontalAlignment(SwingConstants.LEFT);
        scr.setForeground(Color.red);

        lvs = new JLabel("Lives: " + lives);
        lvs.setFont(new Font("Serif", Font.PLAIN, 25));
        lvs.setBounds(325, 25, 300, 25);
        lvs.setLocation(325, 25);
        lvs.setHorizontalAlignment(SwingConstants.RIGHT);
        lvs.setForeground(Color.red);

        panel.setLayout(null);
        panel.add(scr);
        panel.add(lvs);
        window.add(panel);

        // Initialize game loop.
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
        player = new Player(325, 630);
    }

    // Initialize the centipede.
    public static void initCentipede() {
        board[0][0] = new Centipede(0, 0, null, false);
        segments.add(board[0][0]);
        for(int i = 1; i < 11; i++) {
            board[0][i] = new Centipede(0, i, (Centipede) board[0][i - 1], false);
            segments.add(board[0][i]);
        }
        board[0][11] = new Centipede(0, 11, (Centipede) board[0][10], true);
        segments.add(board[0][11]);

        Centipede c = (Centipede) board[0][11];
        c.prev = null;
        for(int i = 10; i >= 0; i--) {
            c = (Centipede) board[0][i];
            c.prev = (Centipede) board[0][i + 1];
        }
        c = (Centipede) board[0][0];
        c.prev = (Centipede) board[0][1];

        heads.add(board[0][11]);
    }

    // Place mushrooms on the board according to the density.
    public static void placeShrooms() {
        ArrayList valCols = new ArrayList();
        for(int i = 1; i < (board[0].length - 1); i++) valCols.add(i);

        // Iterate through each row in the placeable range.
        for(int row = 1; row < (board.length  - 4); row++) {
            // Get each valid column in the row.
            Iterator it = valCols.iterator();

            ArrayList nxtVals = new ArrayList();
            for(int i = 1; i < (board[0].length - 1); i++) nxtVals.add(i);

            // Iterate through each valid column.
            while(it.hasNext()) {
                int col = (Integer) it.next();
                if(getXinYChance(density, 30)) {
                    board[row][col] = new Mushroom(row, col);
                    mushrooms.add(board[row][col]);
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

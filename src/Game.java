import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game extends JFrame implements ActionListener{

    public static Drawable blank = new Drawable();
    public static Drawable[][] board = new Drawable[30][30];
    public static ArrayList centipedes = new ArrayList();
    public static ArrayList mushrooms = new ArrayList();
    public static ArrayList bullets = new ArrayList();
    public static ArrayList spiders = new ArrayList();
    public static Player player;
    public static Timer gameTimer;
    public static boolean restart;
    public static int density;

    // Initialize game.
    public Game() {
        // Set window behavior.
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(665, 710);
        getContentPane().setBackground(Color.black);

        // Set game screen.
        Screen screen = new Screen();
        add(screen);
    }

    // Initialize game parameters and connections.
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
        initSpider();
        placeShrooms();

        // Initialize game and screen.
        Game game = new Game();

        // Initialize game loop;
        restart = true;
        gameTimer = new Timer(17, game);
        gameTimer.setRepeats(true);
        gameTimer.start();
    }

    // Update game at 60fps.
    public void actionPerformed(ActionEvent e) {
        // Pause temporarily if restarting.
        if(restart) {
            // Default cursor for adjustment.
            getContentPane().setCursor(Cursor.getDefaultCursor());

            // Allow player to adjust.
            try {
                Thread.sleep(1000);
            } catch(Exception exc) { System.out.println("Thread failed to sleep."); }

            // Reset invisible cursor.
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
            getContentPane().setCursor(blankCursor);
            restart = false;
        }

        // Repaint the panel.
        testCollisions();
        updateLocations();
        revalidate();
        repaint();
    }

    // Test to see if any collisions occurred in the frame.
    public void testCollisions() {
        ArrayList btoRemove = new ArrayList();
        ArrayList mtoRemove = new ArrayList();
        ArrayList stoRemove = new ArrayList();
        ArrayList ctoSplit = new ArrayList();
        ArrayList itoSplit = new ArrayList();
        boolean used = false;

        // Iterate through all existing bullets.
        Iterator iter = bullets.iterator();
        while(iter.hasNext() && !used) {
            Bullet b = (Bullet) iter.next();

            // Test all mushrooms to see if they were hit.
            Iterator it = mushrooms.iterator();
            while(it.hasNext() && !used) {
                Mushroom m = (Mushroom) it.next();
                int clb = (m.row * 20) + 50;
                int cub = (m.row * 20) + 50 + 20;
                int rlb = (m.col * 20) + 25;
                int rub = (m.col * 20) + 25 + 20;
                if(b.col <= cub && b.col >= clb) {
                    if(b.row <= rub && b.row >= rlb) {
                        // Mushroom was hit.
                        if(++m.hitCnt == m.durability) { // Destroyed
                            player.score += 5;
                            mtoRemove.add(m);
                            board[m.row][m.col] = blank;
                        } else // Hit
                            player.score += 1;

                        used = true;
                        btoRemove.add(b);
                    }
                }
            }
            mushrooms.removeAll(mtoRemove);

            it = centipedes.iterator();
            while(it.hasNext() && !used) {
                ArrayList c = (ArrayList) it.next();
                Iterator it2 = c.iterator();
                while(it2.hasNext() && !used) {
                    Centipede s = (Centipede) it2.next();
                    int clb = (s.row * 20) + 50;
                    int cub = (s.row * 20) + 50 + 20;
                    int rlb = (s.col * 20) + 25;
                    int rub = (s.col * 20) + 25 + 20;
                    if(b.col <= cub && b.col >= clb) {
                        if(b.row <= rub && b.row >= rlb) {
                            // Score points for hitting centipede.
                            if(++s.hitCnt == s.durability) { // Destroyed
                                if(centipedes.size() == 1 && c.size() == 1) {
                                    initCentipede();
                                    initSpider();
                                    if(spiders.size() == 0)
                                        initSpider();
                                    player.score += 600; // Destroyed entire centipede.
                                } else
                                    player.score += 5;
                                ctoSplit.add(c);
                                itoSplit.add(c.indexOf(s));
                            } else // Hit
                                player.score += 2;

                            used = true;
                            btoRemove.add(b);
                        }
                    }
                }
            }

            // Split all destroyed segments.
            it = ctoSplit.iterator();
            Iterator it2 = itoSplit.iterator();
            while(it.hasNext() && it2.hasNext()) {
                ArrayList c = (ArrayList) it.next();
                int index = (int) it2.next();

                Centipede s = (Centipede) c.get(index);
                if(s.head) { // Head
                    if(c.size() == 1) { // Single segment.
                        c.clear();
                        centipedes.remove(c);
                    } else { // Head of existing centipede.
                        s = (Centipede) c.get(index - 1);
                        s.head = true;
                        c.remove(index);
                    }
                } else if(index == 0) // Tail
                    c.remove(index);
                else { // Middle
                    s = (Centipede) c.get(index - 1);
                    s.head = true;
                    s.dir = !s.dir;

                    ArrayList c2 = new ArrayList(c.subList(0, index));
                    centipedes.add(c2);

                    c.subList(0, index + 1).clear();
                }
            }

            // Test the spider to see if it was hit.
            it = spiders.iterator();
            while(it.hasNext() && !used) {
                Spider s = (Spider) it.next();
                if(b.col <= s.col + 20 && b.col >= s.col) {
                    if(b.row <= s.row + 20 && b.row >= s.row) {
                        // Score points for hitting spider.
                        if(++s.hitCnt == s.durability) { // Destroyed
                            player.score += 600;
                            stoRemove.add(s);
                        } else // Hit
                            player.score += 100;

                        used = true;
                        btoRemove.add(b);
                    }
                }
            }
            spiders.removeAll(stoRemove);
        }
        bullets.removeAll(btoRemove);

        // Test all segments to see if they hit the player.
        player.hit = false;
        iter = centipedes.iterator();
        while(iter.hasNext()) {
            ArrayList c = (ArrayList) iter.next();
            Iterator iter2 = c.iterator();
            while(iter2.hasNext()) {
                Centipede s = (Centipede) iter2.next();
                int cx = (s.col * 20) + 25 + 10;
                int cy = (s.row * 20) + 50 + 10;

                // Player runs into a centipede segment.
                if (Math.pow(cx - player.row, 2) + Math.pow(cy - player.col, 2) < 400)
                    player.hit = true;
            }
        }

        // Test to see if the spider hit the player.
        iter = spiders.iterator();
        while(iter.hasNext()) {
            Spider s = (Spider) iter.next();

            // Player runs into the spider.
            if (Math.pow((s.row + 10) - player.row, 2) + Math.pow((s.col + 10) - player.col, 2) < 400)
                player.hit = true;
        }

        // Process getting hit.
        if(player.hit) {
            if(--player.lives > 0) { // Player has lives remaining.
                // Reset the screen.
                restart = true;

                // Move components back to their starting locations.
                bullets.clear();
                initCentipede();
                initSpider();
                restoreShrooms();
            }else // Game ends.
                gameTimer.stop();
        }
    }

    // Update the locations of all the items.
    public void updateLocations() {
        // Update player location.
        Point q = getLocationOnScreen();
        player.move(q);

        // Update all centipede segments.
        Iterator it = centipedes.iterator();
        while(it.hasNext()) {
            ArrayList c = (ArrayList) it.next();
            Iterator it2 = c.iterator();
            while(it2.hasNext()) {
                Centipede s = (Centipede) it2.next();
                Centipede parent = s.head ? null : (Centipede) c.get(c.indexOf(s) + 1);
                s.move(parent);
            }
        }

        // Update the spider.
        it = spiders.iterator();
        while(it.hasNext()) {
            Spider s = (Spider) it.next();
            s.move();
        }

        // Update all bullets.
        it = bullets.iterator();
        ArrayList toRemove = new ArrayList();
        while(it.hasNext()) {
            Bullet bullet = (Bullet) it.next();

            // Move bullets up the screen.
            if(bullet.move())
                toRemove.add(bullet);
        }
        bullets.removeAll(toRemove);
    }

    // Initialize the player.
    public static void initPlayer() {
        player = new Player(325, 630);
    }

    // Initialize the centipede.
    public static void initCentipede() {
        centipedes.clear();
        ArrayList c = new ArrayList();

        for(int i = 0; i < 12; i++)
            c.add(new Centipede(0, 29 - i,i == 11));
        centipedes.add(c);
    }

    // Initialize the spider.
    public static void initSpider() {
        spiders.clear();
        spiders.add(new Spider(45, 590));
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

    // Restore all decayed mushrooms to full health.
    public static void restoreShrooms() {
        Iterator it = mushrooms.iterator();
        while(it.hasNext()) {
            Mushroom m = (Mushroom) it.next();
            if(m.hitCnt != 0) {
                m.hitCnt = 0;
                player.score += 10;
            }
        }
    }

    // Return true with a num in den chance.
    public static boolean getXinYChance(int num, int den) {
        Random rand = new Random();
        return rand.nextInt(den) < num;
    }
}

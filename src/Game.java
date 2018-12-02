import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

class Game extends JFrame implements ActionListener{

    // Declare all drawable objects.
    static Player player;
    static Drawable[][] board = new Drawable[30][30];
    static ArrayList<ArrayList<Centipede>> centipedes = new ArrayList<>();
    static ArrayList<Mushroom> mushrooms = new ArrayList<>();
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Spider> spiders = new ArrayList<>();

    // Declare game parameters.
    static SoundManager sounds = new SoundManager();
    private static Timer gameTimer;
    private static boolean restart;
    private static int density;

    // Initialize game.
    private Game() {
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

        // Place centipede and mushrooms.
        initPlayer();
        initCentipede();
        initSpider();
        placeShrooms();

        // Initialize game and screen.
        Game game = new Game();

        // Initialize game loop;
        gameTimer = new Timer(17, game);
        gameTimer.setRepeats(true);
        gameTimer.start();
    }

    // Update game at 60fps.
    public void actionPerformed(ActionEvent e) {
        // Pause temporarily if restarting.
        if(restart) {
            // Allow player to adjust.
            try {
                Thread.sleep(1000);
            } catch(Exception exc) { System.out.println("Thread failed to sleep."); }

            // Resume game.
            restart = false;
        }

        // Reset invisible cursor.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
        getContentPane().setCursor(blankCursor);

        // Repaint the panel.
        testCollisions();
        updateLocations();
        revalidate();
        repaint();
    }

    // Test to see if any collisions occurred in the frame.
    private void testCollisions() {
        ArrayList<Bullet> btoRemove = new ArrayList<>();
        ArrayList<Mushroom> mtoRemove = new ArrayList<>();
        ArrayList<Spider> stoRemove = new ArrayList<>();
        ArrayList<ArrayList<Centipede>> ctoSplit = new ArrayList<>();
        ArrayList<Integer> itoSplit = new ArrayList<>();
        boolean used = false;

        // Iterate through all existing bullets.
        Iterator<Bullet> b_it = bullets.iterator();
        while(b_it.hasNext() && !used) {
            Bullet b = b_it.next();

            // Test all mushrooms to see if they were hit.
            for(Mushroom m : mushrooms) {
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
                            board[m.row][m.col] = null;
                        } else // Hit
                            player.score += 1;

                        used = true;
                        btoRemove.add(b);
                    }
                }
            }
            mushrooms.removeAll(mtoRemove);

            // Test all centipedes to see if they wee hit.
            Iterator<ArrayList<Centipede>> c_it = centipedes.iterator();
            while(c_it.hasNext() && !used) {
                ArrayList<Centipede> c = c_it.next();

                // Test all centipede segments to see if they were hit.
                Iterator<Centipede> s_it = c.iterator();
                while(s_it.hasNext() && !used) {
                    Centipede s = s_it.next();

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
                                    player.score += 600; // Destroyed entire centipede.
                                } else
                                    player.score += 5;
                                sounds.centipedeDead.play();

                                ctoSplit.add(c);
                                itoSplit.add(c.indexOf(s));
                            } else {// Hit
                                player.score += 2;
                                sounds.centipedeHit.play();
                            }

                            used = true;
                            btoRemove.add(b);
                        }
                    }
                }
            }

            // Split all destroyed segments.
            Iterator<ArrayList<Centipede>> x_it = ctoSplit.iterator();
            Iterator<Integer> i_it = itoSplit.iterator();
            while(x_it.hasNext() && i_it.hasNext()) {
                ArrayList<Centipede> c = x_it.next();
                int index = i_it.next();

                Centipede s = c.get(index);
                if(s.head) { // Head
                    if(c.size() == 1) { // Single segment.
                        c.clear();
                        centipedes.remove(c);
                    } else { // Head of existing centipede.
                        s = c.get(index - 1);
                        s.head = true;
                        c.remove(index);
                    }
                } else if(index == 0) // Tail
                    c.remove(index);
                else { // Middle
                    s = c.get(index - 1);
                    s.head = true;
                    s.dir = !s.dir;

                    ArrayList<Centipede> c2 = new ArrayList<>(c.subList(0, index));
                    centipedes.add(c2);

                    c.subList(0, index + 1).clear();
                }
            }

            // Test the spider to see if it was hit.
            for(Spider s : spiders) {
                if(b.col <= s.col + 20 && b.col >= s.col) {
                    if(b.row <= s.row + 20 && b.row >= s.row) {
                        // Score points for hitting spider.
                        if(++s.hitCnt == s.durability) { // Destroyed
                            player.score += 600;
                            stoRemove.add(s);
                            sounds.spiderDead.play();
                        } else {// Hit
                            player.score += 100;
                            sounds.spiderHit.play();
                        }

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
        for(ArrayList<Centipede> c : centipedes) {
            for(Centipede s : c) {
                int cx = (s.col * 20) + 25 + 10;
                int cy = (s.row * 20) + 50 + 10;

                // Player runs into a centipede segment.
                if (Math.pow(cx - player.row, 2) + Math.pow(cy - player.col, 2) < 400)
                    player.hit = true;
            }
        }

        // Test to see if the spider hit the player.
        for(Spider s : spiders) {
            // Player runs into the spider.
            if (Math.pow((s.row + 10) - player.row, 2) + Math.pow((s.col + 10) - player.col, 2) < 400)
                player.hit = true;
        }

        // Process getting hit.
        if(player.hit) {
            // Default cursor for adjustment.
            getContentPane().setCursor(Cursor.getDefaultCursor());

            if(--player.lives > 0) { // Player has lives remaining.
                // Reset the screen.
                restart = true;
                sounds.playerHit.play();

                // Move components back to their starting locations.
                bullets.clear();
                initCentipede();
                initSpider();
                restoreShrooms();
            } else { // Game ends.
                gameTimer.stop();
                sounds.playerDead.play();
            }
        }
    }

    // Update the locations of all the items.
    private void updateLocations() {
        // Update player location.
        Point q = getLocationOnScreen();
        player.move(q);

        // Update all centipede segments.
        for(ArrayList<Centipede> c : centipedes) {
            for(Centipede s : c) {
                Centipede parent = s.head ? null : c.get(c.indexOf(s) + 1);
                s.move(parent);
            }
        }

        // Update the spider.
        for(Spider s : spiders)
            s.move();

        // Update all bullets.
        ArrayList<Bullet> toRemove = new ArrayList<>();
        for(Bullet b : bullets) {
            // Move bullets up the screen.
            if(b.move())
                toRemove.add(b);
        }
        bullets.removeAll(toRemove);
    }

    // Initialize the player.
    private static void initPlayer() {
        player = new Player(325, 630);
    }

    // Initialize the centipede.
    private static void initCentipede() {
        centipedes.clear();
        ArrayList<Centipede> c = new ArrayList<>();

        for(int i = 0; i < 12; i++)
            c.add(new Centipede(0, 29 - i,i == 11));
        centipedes.add(c);
    }

    // Initialize the spider.
    private static void initSpider() {
        spiders.clear();
        spiders.add(new Spider(45, 590));
    }

    // Place mushrooms on the board according to the density.
    private static void placeShrooms() {
        ArrayList<Integer> valCols = new ArrayList<>();
        for(int i = 1; i < (board[0].length - 1); i++) valCols.add(i);

        // Iterate through each row in the placeable range.
        for(int row = 1; row < (board.length  - 3); row++) {
            // Get each valid column in the row.
            Iterator<Integer> it = valCols.iterator();

            ArrayList<Integer> nxtVals = new ArrayList<>();
            for(int i = 1; i < (board[0].length - 1); i++) nxtVals.add(i);

            // Iterate through each valid column.
            while(it.hasNext()) {
                int col = it.next();
                int rand = (int) (Math.random() * (30 + 1));

                if(rand < density) {
                    board[row][col] = new Mushroom(row, col);
                    mushrooms.add((Mushroom) board[row][col]);
                    nxtVals.remove(Integer.valueOf(col - 1));
                    nxtVals.remove(Integer.valueOf(col + 1));
                }
            }

            // Update list of valid columns.
            valCols = nxtVals;
        }
    }

    // Restore all decayed mushrooms to full health.
    private static void restoreShrooms() {
        for(Mushroom m : mushrooms) {
            if(m.hitCnt != 0) {
                m.hitCnt = 0;
                player.score += 10;
            }
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.io.File;
import javax.sound.sampled.*;
import javax.imageio.*;

public class Window extends JFrame implements ActionListener{

    public static Drawable[][] board = new Drawable[30][30];
    public static Drawable blank = new Drawable();
    public static ArrayList centipedes = new ArrayList();
    public static ArrayList mushrooms = new ArrayList();
    public static ArrayList bullets = new ArrayList();
    public static ArrayList spiders = new ArrayList();
    public static Player player;
    public static int density;
    public static JLabel scr, lvs, gameover;
    public static Timer gameTimer;
    public static boolean restart;
    public static Clip pew;
    public static BufferedImage bodySprite, bulletSprite, headSprite, mushSprite0, mushSprite1, mushSprite2, playerSprite, spiderSprite;

    // Initialize window.
    public Window() {
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(665, 710);
        getContentPane().setBackground(Color.black);
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

                            btoRemove.add(b);
                            used = true;
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
            getContentPane().setCursor(Cursor.getDefaultCursor());
            if(--player.lives > 0) { // Player has lives remaining.
                // Reset the screen.
                restart = true;

                // Move components back to their starting locations.
                bullets.clear();
                initCentipede();
                initSpider();
                restoreShrooms();
            }else { // Game ends.
                gameTimer.stop();
                gameover.setVisible(true);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        // Pause temporarily if restarting.
        if(restart) {
            // Allow player to adjust.
            try { Thread.sleep(1000); }catch(Exception exc){}

            // Reset invisible cursor.
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
            getContentPane().setCursor(blankCursor);

            // De-assert restart flag.
            restart = false;
        }

        // Repaint the panel.
        testCollisions();
        revalidate();
        repaint();

        // Update player location.
        Point p = MouseInfo.getPointerInfo().getLocation();
        Point q = getLocationOnScreen();

        // Constrain within the game area.
        player.row = Math.min(Math.max(p.x - q.x - 7, 35), 615);
        player.col = Math.min(Math.max(p.y - q.y - 30, 60), 640);

        Iterator it = centipedes.iterator();
        while(it.hasNext()) {
            ArrayList c = (ArrayList) it.next();
            Iterator it2 = c.iterator();
            while(it2.hasNext()) {
                Centipede s = (Centipede) it2.next();
                Centipede parent = s.head ? null : (Centipede) c.get(c.indexOf(s) + 1);
                if(s.frameCnt == 0)
                    s.move(parent);
                s.frameCnt = (s.frameCnt + 1) % s.speed;
            }
        }

        // Update the spider.
        it = spiders.iterator();
        while(it.hasNext()) {
            Spider s = (Spider) it.next();
            s.move();
            s.frameCnt = (s.frameCnt + 1) % s.speed;
        }

        // Update all bullets.
        it = bullets.iterator();
        ArrayList toRemove = new ArrayList();
        while(it.hasNext()) {
            // Move bullets up the screen,
            Bullet bullet = (Bullet) it.next();
            bullet.col -= bullet.dy;

            // Delete bullets that go out of bounds.
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
        initSpider();
        placeShrooms();

        // Get audio sound clip.
        try {
            File pewFile = new File("oof.wav");
            AudioInputStream pewStream = AudioSystem.getAudioInputStream(pewFile);
            pew = AudioSystem.getClip();
            pew.open(pewStream);
            FloatControl volume = (FloatControl) pew.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-20.0f);
        } catch(Exception e) {}

        // Load sprites.
        try {
            bodySprite = ImageIO.read(new File("bodySprite.png"));
            bulletSprite = ImageIO.read(new File("bulletSprite.png"));
            headSprite = ImageIO.read(new File("headSprite.png"));
            mushSprite0 = ImageIO.read(new File("mushSprite0.png"));
            mushSprite1 = ImageIO.read(new File("mushSprite1.png"));
            mushSprite2 = ImageIO.read(new File("mushSprite2.png"));
            playerSprite = ImageIO.read(new File("playerSprite.png"));
            spiderSprite = ImageIO.read(new File("spiderSprite.png"));
        } catch (Exception e) {}

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

                scr.setText("Score: " + player.score);
                lvs.setText("Lives: " + player.lives);

                Iterator it = centipedes.iterator();
                while(it.hasNext()) {
                    ArrayList c = (ArrayList) it.next();
                    Iterator it2 = c.iterator();
                    while(it2.hasNext()) {
                        Centipede s = (Centipede) it2.next();
                        g2d.drawImage(s.head ? headSprite : bodySprite, s.col * 20 + 25, s.row * 20 + 50, this);
                    }
                }

                // Draw mushrooms.
                it = mushrooms.iterator();
                while(it.hasNext()) {
                    Mushroom mush = (Mushroom) it.next();

                    if(mush.hitCnt == 0)
                        g2d.drawImage(mushSprite0, mush.col * 20 + 25, mush.row * 20 + 50, this);
                    else if(mush.hitCnt == 1)
                        g2d.drawImage(mushSprite1, mush.col * 20 + 25, mush.row * 20 + 50, this);
                    else if(mush.hitCnt == 2)
                        g2d.drawImage(mushSprite2, mush.col * 20 + 25, mush.row * 20 + 50, this);
                }

                // Draw spider;
                it = spiders.iterator();
                while(it.hasNext()) {
                    Spider s = (Spider) it.next();
                    g2d.drawImage(spiderSprite, s.row, s.col, this);
                }

                // Draw bullets.
                it = bullets.iterator();
                while(it.hasNext()) {
                    Bullet bullet = (Bullet) it.next();
                    g2d.drawImage(bulletSprite, bullet.row - 3, bullet.col, this);
                }

                // Draw player.
                if(!restart)
                    g2d.drawImage(playerSprite, player.row - 10, player.col - 10, this);
                else
                    g2d.drawImage(playerSprite, 325, 630, this);
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(player.lives > 0) {
                    if (pew.isRunning())
                        pew.stop();
                    pew.setFramePosition(0);
                    pew.start();

                    Bullet bullet = new Bullet(player.row, player.col - 10);
                    bullets.add(bullet);
                }
            }
        });

        scr = new JLabel("Score: " + player.score);
        scr.setBounds(25, 25, 300, 25);
        scr.setLocation(25, 25);
        scr.setFont(new Font("Arial", Font.PLAIN, 25));
        scr.setHorizontalAlignment(SwingConstants.LEFT);
        scr.setForeground(Color.red);

        lvs = new JLabel("Lives: " + player.lives);
        lvs.setFont(new Font("Arial", Font.PLAIN, 25));
        lvs.setBounds(325, 25, 300, 25);
        lvs.setLocation(325, 25);
        lvs.setHorizontalAlignment(SwingConstants.RIGHT);
        lvs.setForeground(Color.red);

        gameover = new JLabel("GAME OVER!");
        gameover.setFont(new Font("Arial Black", Font.PLAIN, 25));
        gameover.setBounds(25, 25, 600, 25);
        gameover.setLocation(25, 25);
        gameover.setHorizontalAlignment(SwingConstants.CENTER);
        gameover.setForeground(Color.red);
        gameover.setVisible(false);

        panel.setLayout(null);
        panel.add(scr);
        panel.add(lvs);
        panel.add(gameover);
        window.add(panel);

        // Initialize game loop;
        restart = true;
        gameTimer = new Timer(17, window);
        gameTimer.setRepeats(true);
        gameTimer.start();
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
        centipedes.clear();
        ArrayList c = new ArrayList();

        for(int i = 0; i < 12; i++)
            c.add(new Centipede(0, 29 - i,i == 11));
        centipedes.add(c);
    }

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

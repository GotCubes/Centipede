import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class Screen extends JPanel {

    private JLabel scr, lvs, gameOver;
    private SpriteManager sprites = new SpriteManager();

    Screen() {
        super();

        setLayout(null);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(Game.player.lives > 0) {
                    Bullet bullet = new Bullet(Game.player.row, Game.player.col - 10);
                    Game.bullets.add(bullet);
                    Game.sounds.bullet.play();
                }
            }
        });

        scr = new JLabel("Score: " + Game.player.score);
        scr.setBounds(25, 25, 300, 25);
        scr.setLocation(25, 25);
        scr.setFont(new Font("Arial", Font.PLAIN, 25));
        scr.setHorizontalAlignment(SwingConstants.LEFT);
        scr.setForeground(Color.red);
        add(scr);

        lvs = new JLabel("Lives: " + Game.player.lives);
        lvs.setFont(new Font("Arial", Font.PLAIN, 25));
        lvs.setBounds(325, 25, 300, 25);
        lvs.setLocation(325, 25);
        lvs.setHorizontalAlignment(SwingConstants.RIGHT);
        lvs.setForeground(Color.red);
        add(lvs);

        gameOver = new JLabel("GAME OVER!");
        gameOver.setFont(new Font("Arial Black", Font.PLAIN, 25));
        gameOver.setBounds(25, 25, 600, 25);
        gameOver.setLocation(25, 25);
        gameOver.setHorizontalAlignment(SwingConstants.CENTER);
        gameOver.setForeground(Color.red);
        gameOver.setVisible(false);
        add(gameOver);
    }

    // Paint objects onto the panel.
    public void paintComponent(Graphics g) {
        // Set background scenery.
        Graphics2D g2d = (Graphics2D) g;
        setOpaque(false);
        setSize(650, 700);
        setBackground(Color.black);
        g2d.setColor(Color.red);
        g2d.drawRect(25, 50, 600, 600);

        scr.setText("Score: " + Game.player.score);
        lvs.setText("Lives: " + Game.player.lives);

        if(Game.player.lives == 0)
            gameOver.setVisible(true);

        for(ArrayList<Centipede> c : Game.centipedes) {
            for(Centipede s : c)
                g2d.drawImage(s.head ? sprites.headSprite : sprites.bodySprite, s.col * 20 + 25, s.row * 20 + 50, this);
        }

        // Draw mushrooms.
        for(Mushroom m : Game.mushrooms) {
            if(m.hitCnt == 0)
                g2d.drawImage(sprites.mushSprite0, m.col * 20 + 25, m.row * 20 + 50, this);
            else if(m.hitCnt == 1)
                g2d.drawImage(sprites.mushSprite1, m.col * 20 + 25, m.row * 20 + 50, this);
            else if(m.hitCnt == 2)
                g2d.drawImage(sprites.mushSprite2, m.col * 20 + 25, m.row * 20 + 50, this);
        }

        // Draw spider;
        for(Spider s : Game.spiders)
            g2d.drawImage(sprites.spiderSprite, s.row, s.col, this);

        // Draw bullets.
        for(Bullet b : Game.bullets)
            g2d.drawImage(sprites.bulletSprite, b.row - 3, b.col, this);

        // Draw player.
        g2d.drawImage(sprites.playerSprite, Game.player.row - 10, Game.player.col - 10, this);
    }
}

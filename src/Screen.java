import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class Screen extends JPanel {

    public JLabel scr, lvs, gameOver;
    public SpriteManager sprites = new SpriteManager();

    public Screen() {
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

        Iterator it = Game.centipedes.iterator();
        while(it.hasNext()) {
            ArrayList c = (ArrayList) it.next();
            Iterator it2 = c.iterator();
            while(it2.hasNext()) {
                Centipede s = (Centipede) it2.next();
                g2d.drawImage(s.head ? sprites.headSprite : sprites.bodySprite, s.col * 20 + 25, s.row * 20 + 50, this);
            }
        }

        // Draw mushrooms.
        it = Game.mushrooms.iterator();
        while(it.hasNext()) {
            Mushroom mush = (Mushroom) it.next();

            if(mush.hitCnt == 0)
                g2d.drawImage(sprites.mushSprite0, mush.col * 20 + 25, mush.row * 20 + 50, this);
            else if(mush.hitCnt == 1)
                g2d.drawImage(sprites.mushSprite1, mush.col * 20 + 25, mush.row * 20 + 50, this);
            else if(mush.hitCnt == 2)
                g2d.drawImage(sprites.mushSprite2, mush.col * 20 + 25, mush.row * 20 + 50, this);
        }

        // Draw spider;
        it = Game.spiders.iterator();
        while(it.hasNext()) {
            Spider s = (Spider) it.next();
            g2d.drawImage(sprites.spiderSprite, s.row, s.col, this);
        }

        // Draw bullets.
        it = Game.bullets.iterator();
        while(it.hasNext()) {
            Bullet bullet = (Bullet) it.next();
            g2d.drawImage(sprites.bulletSprite, bullet.row - 3, bullet.col, this);
        }

        // Draw player.
        if(!Game.restart)
            g2d.drawImage(sprites.playerSprite, Game.player.row - 10, Game.player.col - 10, this);
        else
            g2d.drawImage(sprites.playerSprite, 325, 630, this);
    }
}

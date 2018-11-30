import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SpriteManager {

    public BufferedImage    bodySprite,
                            bulletSprite,
                            headSprite,
                            mushSprite0,
                            mushSprite1,
                            mushSprite2,
                            playerSprite,
                            spiderSprite;

    public SpriteManager() {
        try {
            bodySprite = ImageIO.read(new File("sprites/bodySprite.png"));
            bulletSprite = ImageIO.read(new File("sprites/bulletSprite.png"));
            headSprite = ImageIO.read(new File("sprites/headSprite.png"));
            mushSprite0 = ImageIO.read(new File("sprites/mushSprite0.png"));
            mushSprite1 = ImageIO.read(new File("sprites/mushSprite1.png"));
            mushSprite2 = ImageIO.read(new File("sprites/mushSprite2.png"));
            playerSprite = ImageIO.read(new File("sprites/playerSprite.png"));
            spiderSprite = ImageIO.read(new File("sprites/spiderSprite.png"));
        } catch (Exception e) {}
    }

}

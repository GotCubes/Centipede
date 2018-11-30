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
            bodySprite = ImageIO.read(new File("bodySprite.png"));
            bulletSprite = ImageIO.read(new File("bulletSprite.png"));
            headSprite = ImageIO.read(new File("headSprite.png"));
            mushSprite0 = ImageIO.read(new File("mushSprite0.png"));
            mushSprite1 = ImageIO.read(new File("mushSprite1.png"));
            mushSprite2 = ImageIO.read(new File("mushSprite2.png"));
            playerSprite = ImageIO.read(new File("playerSprite.png"));
            spiderSprite = ImageIO.read(new File("spiderSprite.png"));
        } catch (Exception e) {}
    }

}

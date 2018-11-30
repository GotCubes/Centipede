public class SoundManager {

    public Sound    bulletSound,
                    hitSound,
                    deadSound;

    public SoundManager() {
        bulletSound = new Sound("sounds/bulletSound.wav");
        hitSound = new Sound("sounds/hitSound.wav");
        deadSound = new Sound("sounds/deadSound.wav");
    }
}

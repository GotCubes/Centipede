import javax.sound.sampled.*;
import java.io.File;

public class Sound {
    public Clip clip;

    public Sound(String path) {
        try {
            File file = new File(path);
            AudioInputStream pewStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(pewStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-20.0f);
        } catch(Exception e) {}
    }

    public void play() {
        if (clip.isRunning())
            clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}

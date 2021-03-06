import javax.sound.sampled.*;
import java.io.File;

class Sound {

    private Clip clip;

    Sound(String path) {
        try {
            File file = new File(path);
            AudioInputStream pewStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(pewStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
        } catch(Exception e) { System.out.println("Failed to load sound " + path); }
    }

    void play() {
        if (clip.isRunning())
            clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}

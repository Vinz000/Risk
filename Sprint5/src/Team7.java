package src;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Team7 implements Bot {

    private final BoardAPI board;
    private final PlayerAPI player;

    Team7(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;
    }

    public String getName() {

        try {
            playFallenKingdom();
        } catch (Exception e) {
            // Catch all exceptions here, if our bot failed
            // because of this it would be *tragic*.
            e.printStackTrace();
        }

        return "Team7";
    }

    private void playFallenKingdom() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = getAudioInputStream();
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        setVolumeToMaximum(clip);
    }

    private AudioInputStream getAudioInputStream() throws IOException, UnsupportedAudioFileException {

        // Manually created direct-download link, hosted on Google Drive.
        String path = "https://drive.google.com/uc?id=1Bq8Mh4a_gGEVGeDqF-uT_gFJdi9x2_mQ&export=download";
        URL url = new URL(path);

        return AudioSystem.getAudioInputStream(url);
    }

    private void setVolumeToMaximum(Clip clip) {
        float maxVolumeDecibels = 6.0f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(maxVolumeDecibels);
    }

    public String getReinforcement() {
        String command = "";
        // put your code here
        command = GameData.COUNTRY_NAMES[(int) (Math.random() * GameData.NUM_COUNTRIES)];
        command = command.replaceAll("\\s", "");
        command += " 1";
        return (command);
    }

    public String getPlacement(int forPlayer) {
        String command = "";
        // put your code here
        command = GameData.COUNTRY_NAMES[(int) (Math.random() * GameData.NUM_COUNTRIES)];
        command = command.replaceAll("\\s", "");
        return (command);
    }

    public String getCardExchange() {
        String command = "";
        // put your code here
        command = "skip";
        return (command);
    }

    public String getBattle() {
        String command = "";
        // put your code here
        command = "skip";
        return (command);
    }

    public String getDefence(int countryId) {
        String command = "";
        // put your code here
        command = "1";
        return (command);
    }

    public String getMoveIn(int attackCountryId) {
        String command = "";
        // put your code here
        command = "0";
        return (command);
    }

    public String getFortify() {
        String command = "";
        // put code here
        command = "skip";
        return (command);
    }

}

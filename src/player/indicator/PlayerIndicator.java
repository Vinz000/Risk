package player.indicator;

import common.Component;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import player.Player;
import player.PlayerModel;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class PlayerIndicator extends HBox implements Observer, Component {
    private final Circle playerColorIndicator = new Circle();
    private final Label playerNameIndicator = new Label();

    public PlayerIndicator() {
        setCssId();
        observe();
        build();
    }

    @Override
    public void build() {
        setSpacing(10);

        setMinSize(PLAYER_INDICATOR_WIDTH, PLAYER_INDICATOR_HEIGHT);
        setMaxSize(PLAYER_INDICATOR_WIDTH, PLAYER_INDICATOR_HEIGHT);

        setTranslateX(PLAYER_INDICATOR_X);
        setTranslateY(PLAYER_INDICATOR_Y);

        playerColorIndicator.setRadius(PLAYER_COLOR_INDICATOR_RADIUS);

        playerNameIndicator.setTextFill(Color.WHITE);
        playerNameIndicator.setTranslateY(10);

        getChildren().addAll(playerColorIndicator, playerNameIndicator);

        setVisible(false);
    }

    @Override
    public void setCssId() {
        setId("player-indicator");
    }

    @Override
    public void observe() {
        PlayerModel playerModel = PlayerModel.getInstance();
        playerModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!isVisible()) {
            setVisible(true);
        }

        Player currentPlayer = (Player) arg;

        playerColorIndicator.setFill(currentPlayer.getColor());
        playerNameIndicator.setText(currentPlayer.getName());
    }
}

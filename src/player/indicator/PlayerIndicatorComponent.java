package player.indicator;

import common.BaseComponent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import player.Player;
import player.model.PlayerModel;
import player.model.PlayerModelArg;

import java.util.Observable;

import static common.Constants.*;

public class PlayerIndicatorComponent extends HBox {
    private final Circle playerColor = new Circle();
    private final Label playerName = new Label();

    public PlayerIndicatorComponent() {
        BaseComponent.build(this::build, this::setCssId, this::modelsToObserve, this::update);
    }

    public void build() {
        setSpacing(5);

        setMinSize(PLAYER_INDICATOR_WIDTH, PLAYER_INDICATOR_HEIGHT);
        setMaxSize(PLAYER_INDICATOR_WIDTH, PLAYER_INDICATOR_HEIGHT);

        setTranslateX(PLAYER_INDICATOR_X);
        setTranslateY(PLAYER_INDICATOR_Y);

        playerColor.setRadius(PLAYER_COLOR_INDICATOR_RADIUS);

        playerName.setTranslateY(PLAYER_NAME_Y);

        getChildren().addAll(playerColor, playerName);

        setVisible(false);
    }

    public void setCssId() {
        setId(ComponentIds.PLAYER_INDICATOR);
    }

    public Observable[] modelsToObserve() {
        return new Observable[]{
                PlayerModel.getInstance()
        };
    }

    public void update(Observable o, Object arg) {
        PlayerModelArg updateArg = (PlayerModelArg) arg;

        switch (updateArg.updateType) {
            case VISIBLE:
                setVisible(true);
                break;
            case CHANGED_PLAYER:
                Player currentPlayer = (Player) updateArg.arg;

                playerColor.setFill(currentPlayer.getColor());
                playerName.setText(currentPlayer.getName());
                break;
        }
    }
}

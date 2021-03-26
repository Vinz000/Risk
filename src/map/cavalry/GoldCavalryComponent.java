package map.cavalry;

import common.Component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import map.model.MapModel;
import map.model.MapModelArg;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class GoldCavalryComponent extends StackPane implements Observer, Component {
    private final Circle goldenCavalryIndicator = new Circle();
    private final Text bonus = new Text("0");

    public GoldCavalryComponent() {
        setCssId();
        observe();
        build();
    }

    private void increaseBonus() {
        int bonusSize = Integer.parseInt(bonus.getText());
        
        if (bonusSize < MAX_CAVALRY_BONUS) {
            int increment = bonusSize < STARTING_BONUS_LIMIT ? INIT_CAVALRY_BONUS : CAVALRY_BONUS;

            bonusSize += increment;

            bonus.setText(String.valueOf(bonusSize));
        }
    }

    public int getBonus() {
        int currentBonus = Integer.parseInt(bonus.getText());
        increaseBonus();
        return currentBonus;
    }

    @Override
    public void build() {
        increaseBonus();

        goldenCavalryIndicator.setFill(Paint.valueOf("#06577D"));

        bonus.setId(ComponentIds.GOLD_CAVALRY_TEXT);

        setTranslateX(GOLD_CAVALRY_X);
        setTranslateY(GOLD_CAVALRY_Y);
        setVisible(false);

        goldenCavalryIndicator.setRadius(GOLD_CAVALRY_RADIUS);

        getChildren().addAll(goldenCavalryIndicator, bonus);
    }

    @Override
    public void setCssId() {
        setId(ComponentIds.GOLD_CAVALRY);
    }

    @Override
    public void observe() {
        MapModel mapModel = MapModel.getInstance();
        mapModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        MapModelArg updateArg = (MapModelArg) arg;

        switch (updateArg.updateType) {
            case SHOW_GOLD_CAVALRY:
                setVisible(true);
                break;
        }
     }

}

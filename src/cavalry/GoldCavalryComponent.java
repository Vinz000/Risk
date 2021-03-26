package cavalry;

import cavalry.model.GoldCavalryModel;
import cavalry.model.GoldCavalryModelArg;

import common.BaseComponent;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.Observable;

import static common.Constants.*;

public class GoldCavalryComponent extends StackPane {
    private final Circle goldenCavalryIndicator = new Circle();
    private final Tooltip tooltip = new Tooltip();
    private final Text bonus = new Text(String.valueOf(STARTING_CAVALRY_BONUS));

    public GoldCavalryComponent() {
        BaseComponent.build(this::build, this::setCssId, this::modelsToObserve, this::update);
    }

    public void build() {
        setOnMouseMoved(this::onMouseMoved);
        setOnMouseExited(this::onMouseExited);

        tooltip.setText("GOLD CAVALRY COUNT");

        goldenCavalryIndicator.setFill(Paint.valueOf(GOLD_CAVALRY_FILL));
        goldenCavalryIndicator.setStroke(Paint.valueOf(GOLD_CAVALRY_STROKE));
        goldenCavalryIndicator.setStrokeType(StrokeType.INSIDE);
        goldenCavalryIndicator.setStrokeWidth(4);

        bonus.setId(ComponentIds.GOLD_CAVALRY_TEXT);

        setTranslateX(GOLD_CAVALRY_X);
        setTranslateY(GOLD_CAVALRY_Y);
        setVisible(false);

        goldenCavalryIndicator.setRadius(GOLD_CAVALRY_RADIUS);

        getChildren().addAll(goldenCavalryIndicator, bonus);
    }

    private void onMouseExited(MouseEvent mouseEvent) {
        tooltip.hide();
    }

    private void onMouseMoved(MouseEvent mouseEvent) {
        double tooltipX = mouseEvent.getScreenX() + 15;
        double tooltipY = mouseEvent.getScreenY() + 15;

        if (!tooltip.isShowing()) tooltip.show(getParent(), tooltipX, tooltipY);

        tooltip.setAnchorX(tooltipX);
        tooltip.setAnchorY(tooltipY);
    }

    public void setCssId() {
        setId(ComponentIds.GOLD_CAVALRY);
    }

    public Observable[] modelsToObserve() {
        return new Observable[]{
                GoldCavalryModel.getInstance()
        };
    }

    public void update(Observable o, Object arg) {
        GoldCavalryModelArg updateArg = (GoldCavalryModelArg) arg;

        switch (updateArg.updateType) {
            case VISIBLE:
                setVisible(true);
                break;
            case BONUS:
                bonus.setText(String.valueOf(updateArg.arg));
                break;
        }

     }

}

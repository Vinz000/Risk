package map;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import common.Constants;
import shell.Player;

import java.util.Observable;
import java.util.Observer;

/**
 * So we keep Country as just a data structure
 * Change color of node
 */

public class CountryComponent extends StackPane implements Observer {
    private final CountryNode countryNode;
    private final Text armyCount = new Text();
    private final Circle countryMarker = new Circle();
    private final Tooltip tooltip = new Tooltip();

    public CountryComponent(CountryNode countryNode) {
        this.countryNode = countryNode;
        this.countryNode.addObserver(this);
        build();
    }

    private void build() {
        countryMarker.setRadius(Constants.COUNTRY_NODE_RADIUS);
        countryMarker.setId(Constants.ComponentIds.NEUTRAL_PLAYER);

        Pane tooltipPane = installTooltip();

        setTranslateX(countryNode.getCoords().getX() - Constants.COUNTRY_NODE_RADIUS);
        setTranslateY(countryNode.getCoords().getY() - Constants.COUNTRY_NODE_RADIUS);

        getChildren().addAll(countryMarker, armyCount, tooltipPane);
        setText();
    }

    private Pane installTooltip() {
        tooltip.setText(countryNode.getCountryName());

        Pane pane = new Pane();
        Tooltip.install(pane, tooltip);

        return pane;
    }

    public void setText() {
        armyCount.setText(Integer.toString(countryNode.getArmy()));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Player) {
            Color playerColor = ((Player) arg).getColor();
            countryMarker.setFill(playerColor);

            String toolTipText = String.format("%s\nOwner: %s",
                    countryNode.getCountryName(),
                    ((Player) arg).getName()
            );
            tooltip.setText(toolTipText);
        } else {
            armyCount.setText(arg.toString());
        }
    }
}

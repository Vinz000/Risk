package map;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    private final Text text = new Text();
    private Circle circle = new Circle();
    private Tooltip tooltip = new Tooltip();

    public CountryComponent(CountryNode countryNode) {
        this.countryNode = countryNode;
        this.countryNode.addObserver(this);

        circle.setRadius(Constants.COUNTRY_NODE_RADIUS);
        circle.setId("neutral-player");
        tooltip.setText(countryNode.getCountryName());
        Pane pane = new Pane();
        Tooltip.install(pane, tooltip);


        setTranslateX(countryNode.getCoord()[0] - Constants.COUNTRY_NODE_RADIUS);
        setTranslateY(countryNode.getCoord()[1] - Constants.COUNTRY_NODE_RADIUS);

        getChildren().addAll(circle, text, pane);
        setText();
    }

    public void setText() {
        text.setText(Integer.toString(countryNode.getArmy()));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Player) {
            circle.setId(((Player) arg).getPlayerID());
        } else {
            text.setText(arg.toString());
        }
    }
}

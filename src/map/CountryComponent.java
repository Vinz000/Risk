package map;

import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import common.Constants;
import shell.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static common.Constants.COUNTRY_COORDS;
import static common.Constants.MAP_WIDTH;

/**
 * So we keep Country as just a data structure
 * Change color of node
 */

public class CountryComponent extends StackPane implements Observer {
    private final CountryNode countryNode;
    private final Text armyCount = new Text();
    private final Circle countryMarker = new Circle();
    private final Tooltip tooltip = new Tooltip();
    private final List<Line> countryLinks = new ArrayList<>();

    public CountryComponent(CountryNode countryNode, MapModel mapModel) {
        this.countryNode = countryNode;
        armyCount.setId(Constants.ComponentIds.TEXT);
        this.countryNode.addObserver(this);
        createLinks();
        build(mapModel);
    }

    private void build(MapModel mapModel) {
        countryMarker.setRadius(Constants.COUNTRY_NODE_RADIUS);
        countryMarker.setId(Constants.ComponentIds.NEUTRAL_PLAYER);

        setOnMouseEntered(mouseEvent -> {
            if (!tooltip.isShowing()) tooltip.show(getParent(), countryNode.getCoords().getX() + 150,
                    countryNode.getCoords().getY() + 150);
            setEffect(new DropShadow());
            setCursor(Cursor.HAND);
            mapModel.updateLinks(countryLinks);
        });

        setOnMouseExited(mouseEvent -> {
            if (tooltip.isShowing()) tooltip.hide();
            setEffect(null);
            setCursor(Cursor.MOVE);
            mapModel.updateLinks(countryLinks);
        });

        tooltip.setText(countryNode.getCountryName());

        setTranslateX(countryNode.getCoords().getX() - Constants.COUNTRY_NODE_RADIUS);
        setTranslateY(countryNode.getCoords().getY() - Constants.COUNTRY_NODE_RADIUS);

        getChildren().addAll(countryMarker, armyCount);
        setText();
    }

    public void createLinks() {
        for (int adj : countryNode.getAdjCountries()) {
            Line line = new Line();
            line.setStartX(countryNode.getCoords().getX());
            line.setStartY(countryNode.getCoords().getY());

            boolean isAlaskaLeft = countryNode.getCountryName().equals("Alaska") && adj == 22;
            boolean isKamchatkaRight = countryNode.getCountryName().equals("Kamchatka") && adj == 8;

            if (isAlaskaLeft) {
                line.setEndX(0);
                line.setEndY(countryNode.getCoords().getY());

                countryLinks.add(getExtraLine(adj));
            } else if (isKamchatkaRight) {
                line.setEndX(MAP_WIDTH - 30);
                line.setEndY(countryNode.getCoords().getY());

                countryLinks.add(getExtraLine(adj));
            } else {
                line.setEndX(COUNTRY_COORDS[adj][0]);
                line.setEndY(COUNTRY_COORDS[adj][1]);
            }
            line.setStrokeWidth(1.5);
            countryLinks.add(line);
        }

    }

    private Line getExtraLine(int countryId) {
        Line line = new Line();
        line.setStartX(COUNTRY_COORDS[countryId][0]);
        line.setStartY(COUNTRY_COORDS[countryId][1]);

        if (countryId == 22) {
            line.setEndX(MAP_WIDTH - 30);
        } else {
            line.setEndX(0);
        }

        line.setEndY(COUNTRY_COORDS[countryId][1]);
        line.setStrokeWidth(1.5);
        return line;
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

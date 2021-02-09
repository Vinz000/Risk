package map;

import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
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

import static common.Constants.*;

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

    public CountryComponent(CountryNode countryNode) {
        this.countryNode = countryNode;
        armyCount.setId(Constants.ComponentIds.TEXT);
        this.countryNode.addObserver(this);
        createLinks();
        build();
    }

    private void build() {
        countryMarker.setRadius(Constants.COUNTRY_NODE_RADIUS);
        countryMarker.setId(Constants.ComponentIds.NEUTRAL_PLAYER);

        setOnMouseMoved(this::onMouseMoved);
        setOnMouseExited(this::onMouseExited);

        tooltip.setText(countryNode.getCountryName());

        setTranslateX(countryNode.getCoords().getX() - Constants.COUNTRY_NODE_RADIUS);
        setTranslateY(countryNode.getCoords().getY() - Constants.COUNTRY_NODE_RADIUS);

        getChildren().addAll(countryMarker, armyCount);
        updateArmyCount(String.valueOf(countryNode.getArmy()));
    }

    private void onMouseMoved(MouseEvent mouseEvent) {
        double tooltipX = mouseEvent.getScreenX() + 15;
        double tooltipY = mouseEvent.getScreenY() + 15;

        if (!tooltip.isShowing()) tooltip.show(getParent(), tooltipX, tooltipY);

        tooltip.setAnchorX(tooltipX);
        tooltip.setAnchorY(tooltipY);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(4);
        setEffect(dropShadow);
        setCursor(Cursor.HAND);

        countryLinks.forEach(link -> link.setVisible(true));
    }

    private void onMouseExited(MouseEvent mouseEvent) {
        tooltip.hide();
        setEffect(null);
        setCursor(Cursor.MOVE);
        countryLinks.forEach(link -> link.setVisible(false));
    }

    private void createLinks() {
        for (int adj : countryNode.getAdjCountries()) {
            Line line = new Line();
            line.setStartX(countryNode.getCoords().getX());
            line.setStartY(countryNode.getCoords().getY());

            boolean isAlaskaLeft = countryNode.getCountryName().equals("Alaska") && adj == 22;
            boolean isKamchatkaRight = countryNode.getCountryName().equals("Kamchatka") && adj == 8;

            if (isAlaskaLeft) {
                line.setEndX(0);
                line.setEndY(countryNode.getCoords().getY());

                countryLinks.add(getHelperLine(adj));
            } else if (isKamchatkaRight) {
                line.setEndX(MAP_WIDTH - 30);
                line.setEndY(countryNode.getCoords().getY());

                countryLinks.add(getHelperLine(adj));
            } else {
                line.setEndX(COUNTRY_COORDS[adj][0]);
                line.setEndY(COUNTRY_COORDS[adj][1]);
            }
            countryLinks.add(line);
        }

        countryLinks.forEach(link -> {
            link.setStrokeWidth(2.5);
            link.setVisible(false);
        });
    }

    private Line getHelperLine(int countryId) {
        Line line = new Line();
        line.setStartX(COUNTRY_COORDS[countryId][0]);
        line.setStartY(COUNTRY_COORDS[countryId][1]);

        line.setEndX((countryId == 22) ? MAP_WIDTH - 30 : 0);

        line.setEndY(COUNTRY_COORDS[countryId][1]);
        return line;
    }

    public List<Line> getCountryLinks() {
        return countryLinks;
    }

    private void updateArmyCount(String text) {
        armyCount.setText(text);
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
            updateArmyCount(arg.toString());
        }
    }
}

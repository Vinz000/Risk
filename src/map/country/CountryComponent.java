package map.country;

import common.Component;
import common.Constants;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import map.model.MapModel;
import map.model.MapModelArg;
import player.Player;
import shell.model.ShellModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class CountryComponent extends StackPane implements Observer, Component {
    private final Country country;
    private final Text armyCount = new Text();
    private final Circle countryMarker = new Circle();
    private final Tooltip tooltip = new Tooltip();
    private final List<Line> countryLinks = new ArrayList<>();

    public CountryComponent(Country country) {
        this.country = country;

        observe();
        build();
    }

    @Override
    public void build() {
        createLinks();

        countryMarker.setStroke(Color.BLACK);
        countryMarker.setStrokeType(StrokeType.INSIDE);
        countryMarker.setStrokeWidth(0);
        countryMarker.setRadius(Constants.COUNTRY_NODE_RADIUS);

        setOnMouseMoved(this::onMouseMoved);
        setOnMouseExited(this::onMouseExited);
        setOnMouseClicked(this::onMouseClicked);

        tooltip.setText(country.getCountryName());

        setTranslateX(country.getCoords().getX() - Constants.COUNTRY_NODE_RADIUS);
        setTranslateY(country.getCoords().getY() - Constants.COUNTRY_NODE_RADIUS);

        armyCount.setId(Constants.ComponentIds.TEXT);
        getChildren().addAll(countryMarker, armyCount);
        updateArmyCount(String.valueOf(country.getArmyCount()));

        setVisible(false);
    }

    @Override
    public void setCssId() {
    }

    @Override
    public void observe() {
        MapModel mapModel = MapModel.getInstance();
        mapModel.addObserver(this);
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.setInputLineText(country.getCountryName());
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
        for (int adj : country.getAdjCountries()) {
            Line line = new Line();
            line.setStartX(country.getCoords().getX());
            line.setStartY(country.getCoords().getY());

            boolean isAlaskaLeft = country.getCountryName().equals("Alaska") && adj == 22;
            boolean isKamchatkaRight = country.getCountryName().equals("Kamchatka") && adj == 8;

            if (isAlaskaLeft) {
                line.setEndX(0);
                line.setEndY(country.getCoords().getY());

                countryLinks.add(getHelperLine(adj));
            } else if (isKamchatkaRight) {
                line.setEndX(MAP_WIDTH - 30);
                line.setEndY(country.getCoords().getY());

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
        line.setEndX(countryId == 22 ? MAP_WIDTH - 30 : 0);
        line.setEndY(COUNTRY_COORDS[countryId][1]);

        return line;
    }

    public List<Line> getCountryLinks() {
        return countryLinks;
    }

    private void updateArmyCount(String text) {
        armyCount.setText(text);
    }

    private void toggleHighlight(boolean enable) {
        int radiusBonus = enable ? 2 : 0;
        countryMarker.setRadius(COUNTRY_NODE_RADIUS + radiusBonus);
    }

    @Override
    public void update(Observable o, Object arg) {

        MapModelArg updateArg = (MapModelArg) arg;
        boolean isThisCountry = updateArg.arg.equals(country);

        if (isThisCountry) {

            switch (updateArg.updateType) {

                case ARMY_COUNT:
                    String armyCount = String.valueOf(((Country) updateArg.arg).getArmyCount());
                    updateArmyCount(armyCount);
                    break;
                case OCCUPIER:
                    Player countryOccupier = ((Country) updateArg.arg).getOccupier();
                    countryMarker.setFill(countryOccupier.getColor());

                    String toolTipText = String.format("%s\nOwner: %s",
                            country.getCountryName(),
                            countryOccupier.getName()
                    );
                    tooltip.setText(toolTipText);
                    break;
                case VISIBLE:
                    setVisible(true);
                    break;
                case HIGHLIGHT:
                    boolean enable = countryMarker.getRadius() == COUNTRY_NODE_RADIUS;
                    toggleHighlight(enable);
                    break;
            }
        }
    }
}

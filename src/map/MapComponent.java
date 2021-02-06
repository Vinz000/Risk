package map;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class MapComponent extends Pane implements Observer {

    public MapComponent(MapModel mapModel) {
        drawBoard(mapModel);
        drawContinentPane();
        setId(ComponentIds.MAP);
    }

    private void drawBoard(MapModel mapModel) {
        for (CountryNode countryNode : mapModel.getCountries()) {
            CountryComponent countryComponent = new CountryComponent(countryNode);
            drawLinks(countryNode);
            getChildren().add(countryComponent);
        }

    }

    private void drawContinentPane() {
        VBox continentPane = new VBox();
        continentPane.setPrefWidth(CONTINENT_PANE_WIDTH);
        continentPane.setPrefHeight(CONTINENT_PANE_HEIGHT);
        continentPane.setTranslateX(10);
        continentPane.setTranslateY(390);
        continentPane.setSpacing(3);

        for (int i = 0; i < NUM_CONTINENTS; i++) {
            String prettyContinentName = CONTINENT_NAMES[i].replace("_", ". ");
            Text text = new Text(prettyContinentName + " " + CONTINENT_VALUES[i]);
            text.setId(CONTINENT_NAMES[i]);
            continentPane.getChildren().add(text);
        }

        continentPane.setId(ComponentIds.CONTINENT_PANE);
        getChildren().add(continentPane);
    }

    private void drawLinks(CountryNode countryNode) {

        for (int adj : countryNode.getAdjCountries()) {
            Line line = new Line();
            line.setStartX(countryNode.getCoords().getX());
            line.setStartY(countryNode.getCoords().getY());

            boolean isAlaskaLeft = countryNode.getCountryName().equals("Alaska") && adj == 22;
            boolean isKamchatkaRight = countryNode.getCountryName().equals("Kamchatka") && adj == 8;

            if (isAlaskaLeft) {
                line.setEndX(0);
                line.setEndY(countryNode.getCoords().getY());
            } else if (isKamchatkaRight) {
                line.setEndX(MAP_WIDTH - 30);
                line.setEndY(countryNode.getCoords().getY());
            } else {
                line.setEndX(COUNTRY_COORDS[adj][0]);
                line.setEndY(COUNTRY_COORDS[adj][1]);
            }
            line.setStrokeWidth(1);
            getChildren().add(line);
            line.toBack();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}

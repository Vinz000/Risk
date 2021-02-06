package map;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class MapComponent extends Pane implements Observer {

    public MapComponent(MapModel mapModel) {
        drawBoard(mapModel);
        drawContinentPane();
        setId("map-component");
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
        continentPane.setMinWidth(140);
        continentPane.setMinHeight(180);
        continentPane.setMaxWidth(140);
        continentPane.setMaxHeight(180);
        continentPane.setTranslateX(10);
        continentPane.setTranslateY(390);

        continentPane.setSpacing(3);

//        for(String continent : CONTINENT_NAMES) {
//            Text text = new Text(continent + " " + );
//            text.setId(continent);
//            continentPane.getChildren().add(text);
//        }

        for (int i = 0; i < NUM_CONTINENTS; i++) {
            Text text = new Text(CONTINENT_NAMES[i].replace("_",". ") + " " + CONTINENT_VALUES[i]);
            text.setId(CONTINENT_NAMES[i]);
            continentPane.getChildren().add(text);
        }
        continentPane.setId("continent-pane");
        this.getChildren().add(continentPane);
    }

    private void drawLinks(CountryNode countryNode) {
//        for (int i = 0; i < NUM_COUNTRIES; i++) {
//            for (int adj : ADJACENT[i]) {
//                Line line = new Line();
//                line.setStartX(COUNTRY_COORD[i][0]);
//                line.setStartY(COUNTRY_COORD[i][1]);
//                line.setEndX(COUNTRY_COORD[adj][0]);
//                line.setEndY(COUNTRY_COORD[adj][1]);
//                line.setFill(Color.BLACK);
//                line.setStrokeWidth(1);
//                getChildren().add(line);
//            }
//        }

        for (int adj : countryNode.getAdjCountries()) {
            Line line = new Line();
            if (countryNode.getCountryName() == "Alaska" && adj == 22 || countryNode.getCountryName() == "Kamchatka" && adj == 8) {

                if (adj == 22) {
                    line.setStartX(countryNode.getCoord()[0]);
                    line.setStartY(countryNode.getCoord()[1]);
                    line.setEndX(0);
                    line.setEndY(countryNode.getCoord()[1]);

                    // second line
                    Line secondLine = new Line();
                    secondLine.setStartX(COUNTRY_COORD[adj][0]);
                    secondLine.setStartY(COUNTRY_COORD[adj][1]);
                    secondLine.setEndX(MAP_WIDTH - 30);
                    secondLine.setEndY(countryNode.getCoord()[1]);
                    secondLine.setStrokeWidth(1);
                    getChildren().add(secondLine);
                    secondLine.toBack();
                }

            } else {
                line.setStartX(countryNode.getCoord()[0]);
                line.setStartY(countryNode.getCoord()[1]);
                line.setEndX(COUNTRY_COORD[adj][0]);
                line.setEndY(COUNTRY_COORD[adj][1]);
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

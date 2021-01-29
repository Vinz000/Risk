package map;

import static common.Constants.COUNTRY_COORD;
import static common.Constants.ADJACENT;
import static common.Constants.NUM_COUNTRIES;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class MapComponent extends Pane {
    private MapModel mapModel = new MapModel();
    private Group mapGroup = new Group();

    public MapComponent() {
        drawLinks();
        drawBoard();
    }

    //called once
    private void drawBoard() {
        getChildren().addAll(mapModel.getCountryNodes());
    }

    //called once
    private void drawLinks() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            for (int adj : ADJACENT[i]) {
                Line line = new Line();
                line.setStartX(COUNTRY_COORD[i][0]);
                line.setStartY(COUNTRY_COORD[i][1]);
                line.setEndX(COUNTRY_COORD[adj][0]);
                line.setEndY(COUNTRY_COORD[adj][1]);
                line.setFill(Color.BLACK);
                line.setStrokeWidth(1);
                getChildren().add(line);
            }
        }
    }

}

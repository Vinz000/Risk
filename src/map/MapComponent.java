package map;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class MapComponent extends Pane implements Observer {

    public MapComponent(MapModel mapModel) {
        drawBoard(mapModel);
        setId(ComponentIds.MAP);
    }

    private void drawBoard(MapModel mapModel) {
        for (CountryNode countryNode : mapModel.getCountries()) {
            CountryComponent countryComponent = new CountryComponent(countryNode, mapModel);
            getChildren().add(countryComponent);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List) {
            if (getChildren().containsAll((List) arg)) {
                getChildren().removeAll((List) arg);
            } else {
                getChildren().addAll((List) arg);
                ((List<Line>) arg).forEach(i -> i.toBack());
            }
        }
    }
}

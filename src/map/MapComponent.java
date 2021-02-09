package map;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

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
            List<Line> lineLinks = (List) arg;

            if (getChildren().containsAll(lineLinks)){
                getChildren().removeAll(lineLinks);
            } else {
                getChildren().addAll(lineLinks);
                lineLinks.forEach(i -> i.toBack());
            }
        }
    }
}

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
            CountryComponent countryComponent = new CountryComponent(countryNode);
            getChildren().add(countryComponent);

            List<Line> countryLinks = countryComponent.getCountryLinks();
            getChildren().addAll(countryLinks);
            countryLinks.forEach(link -> link.toBack());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}

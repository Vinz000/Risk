package map.component;

import common.Component;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import cavalry.GoldCavalryComponent;
import map.country.Country;
import map.country.CountryComponent;
import map.model.MapModel;
import player.indicator.PlayerIndicatorComponent;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static common.Constants.ComponentIds;

public class MapComponent extends Pane implements Observer, Component {

    public MapComponent() {
        setCssId();
        observe();
        build();
    }

    @Override
    public void build() {
        MapModel mapModel = MapModel.getInstance();

        for (Country country : mapModel.getCountries()) {
            CountryComponent countryComponent = new CountryComponent(country);
            getChildren().add(countryComponent);

            List<Line> countryLinks = countryComponent.getCountryLinks();
            getChildren().addAll(countryLinks);
            countryLinks.forEach(Node::toBack);
        }

        PlayerIndicatorComponent playerIndicatorComponent = new PlayerIndicatorComponent();

        GoldCavalryComponent goldCavalryComponent = new GoldCavalryComponent();

        getChildren().addAll(playerIndicatorComponent, goldCavalryComponent);
    }

    @Override
    public void setCssId() {
        setId(ComponentIds.MAP);
    }

    @Override
    public void observe() {
        MapModel mapModel = MapModel.getInstance();
        mapModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}

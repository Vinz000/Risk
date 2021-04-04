package map.component;

import cavalry.GoldCavalryComponent;
import common.BaseComponent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import map.country.Country;
import map.country.CountryComponent;
import map.model.MapModel;
import player.indicator.PlayerIndicatorComponent;

import java.util.List;

import static common.Constants.ComponentIds;

public class MapComponent extends Pane {

    public MapComponent() {
        BaseComponent.build(this::build, this::setCssId);
    }

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

    public void setCssId() {
        setId(ComponentIds.MAP);
    }
}

package game;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * So we keep Country as just a data structure
 * Change color of node
 */

public class CountryNode extends StackPane {
    private final Country country;
    private final Text text = new Text();

    public CountryNode(String countryName, int[] adjCountries, int continent_id, int[] coord, String continent) {
        country = new Country(countryName, adjCountries, continent_id);

        //hard coded circle size
        int radius = 10;

        //
        Circle circle = new Circle();
        circle.setRadius(radius);
        circle.setId(continent);

        setTranslateX(coord[0] - radius);
        setTranslateY(coord[1] - radius);

        getChildren().addAll(circle,text);
        setText();
    }

    public void setText() {
        text.setText(Integer.toString(country.getArmy()));
    }

}

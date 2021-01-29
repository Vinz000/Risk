package game;

import static common.Constants.COUNTRY_NAMES;
import static common.Constants.ADJACENT;
import static common.Constants.CONTINENT_IDS;
import static common.Constants.COUNTRY_COORD;
import static common.Constants.CONTINENT_NAMES;

import java.util.ArrayList;
import java.util.List;

public class MapModel {
    private List<CountryNode> countryNodes = new ArrayList<>();

    public MapModel() {
        initializeCountries();
    }

    private void initializeCountries() {
        for (int i = 0; i < COUNTRY_NAMES.length; i++) {
            countryNodes.add(new CountryNode(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORD[i], CONTINENT_NAMES[CONTINENT_IDS[i]]));

        }
    }

    public List<CountryNode> getCountryNodes() {
        return countryNodes;
    }
}


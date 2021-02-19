package map;

import player.Player;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static common.Constants.*;

public class MapModel extends Observable {
    private final List<Country> countries = new ArrayList<>();
    private static MapModel instance;

    private MapModel() {
        createCountries();
    }

    public static MapModel getInstance() {
        if (instance == null) {
            return instance = new MapModel();
        }

        return instance;
    }

    private void createCountries() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            Country newCountry = new Country(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORDS[i]);
            countries.add(newCountry);
        }
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Optional<Country> getCountryByName(String countryName) {
        return countries
                .stream()
                .filter(countryNode -> countryNode.getCountryName().toLowerCase().contains(countryName))
                .findFirst();
    }

    public void setCountryArmyCount(Country country, int army) {
        country.setArmy(army);
        MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.ARMY_COUNT);
        setChanged();
        notifyObservers(mapModelArg);
    }

    public void setCountryCurrentPlayer(Country country, Player player) {
        country.setCurrentPlayer(player);
        MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.CURRENT_PLAYER);
        setChanged();
        notifyObservers(mapModelArg);
    }
}


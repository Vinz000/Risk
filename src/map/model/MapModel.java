package map.model;

import map.country.Country;
import player.Player;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.function.Predicate;

import static common.Constants.*;

public class MapModel extends Observable {
    private static MapModel instance;
    private final List<Country> countries = new ArrayList<>();

    private MapModel() {
        createCountries();
    }

    public static synchronized MapModel getInstance() {
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
        Predicate<Country> hasSameName = country -> country
                .getCountryName()
                .toLowerCase()
                .contains(countryName.toLowerCase().trim());

        return countries
                .stream()
                .filter(hasSameName)
                .findFirst();
    }

    public void setCountryArmyCount(Country country, int armyCount) {
        country.setArmyCount(armyCount);
        MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.ARMY_COUNT);
        setChanged();
        notifyObservers(mapModelArg);
    }

    public void setCountryOccupier(Country country, Player player) {
        country.setOccupier(player);
        MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.OCCUPIER);
        setChanged();
        notifyObservers(mapModelArg);
    }

    public void showCountryComponents() {
        for (Country country : countries) {
            MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.VISIBLE);
            setChanged();
            notifyObservers(mapModelArg);
        }
    }
}


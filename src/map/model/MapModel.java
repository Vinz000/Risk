package map.model;

import map.Continent;
import javafx.application.Platform;
import map.country.Country;
import player.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.Constants.*;

public class MapModel extends Observable {
    private static MapModel instance;
    private final List<Country> countries = new ArrayList<>();
    private final List<Continent> continents = new ArrayList<>();
    private final List<Country> combatantInfo = new ArrayList<Country>(2);

    private MapModel() {
        createCountries();
        createContinents();
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

    private void createContinents() {
        for (int i = 0; i < NUM_CONTINENTS; i++) {
            int continentId = i;
            List<Country> ownedCountries = countries.stream().filter(country -> country.getContinentID() == continentId).collect(Collectors.toList());
            Continent newContinent = new Continent(CONTINENT_NAMES[continentId], CONTINENT_VALUES[continentId], continentId, ownedCountries);
            continents.add(newContinent);
        }
    }

    public List<Continent> getContinents() {
        return continents;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Optional<Country> getCountryByName(String countryName) {
        if (countryName.isEmpty()) {
            return Optional.empty();
        }

        Predicate<Country> hasSameName = country -> country
                .getCountryName()
                .toLowerCase()
                .contains(countryName.toLowerCase().trim());

        return countries
                .stream()
                .filter(hasSameName)
                .findFirst();
    }

    public void updateCountryArmyCount(Country country, int armyCount) {
        country.updateArmyCount(armyCount);
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

    private void highlightCountry(Country country) {
        MapModelArg mapModelArg = new MapModelArg(country, MapModelUpdateType.HIGHLIGHT);
        setChanged();
        notifyObservers(mapModelArg);
    }

    public void highlightCountries(List<Country> countries) {
        for (Country country : countries) {
            Platform.runLater(() -> highlightCountry(country));
        }
    }

    public void highlightCountries(int[] countryIds) {
        for (int countryId : countryIds) {
            Optional<Country> nullableAdjacentCountry = getCountryByName(COUNTRY_NAMES[countryId]);
            nullableAdjacentCountry.ifPresent(adjacentCountry ->
                    Platform.runLater(() -> highlightCountry(adjacentCountry)));
        }
    }

    public List<Country> getCombatantInfo() {
        return combatantInfo;
    }

    public void addCombatant(Country country) {

        combatantInfo.add(country);
    }

    public void clearCombatants() {
        combatantInfo.clear();
    }

    public Country getAttackingCountry() {
        return combatantInfo.get(0);
    }

    public Country getDefendingCountry() {
        return combatantInfo.get(1);
    }
}


package map;

import map.country.Country;

import java.util.List;

public class Continent {
    private final String name;
    private final int bonusReinforcement;
    private final int continentId;
    private final List<Country> countries;

    public Continent(String name, int bonusReinforcement, int continentId, List<Country> countries) {
        this.name = name;
        this.bonusReinforcement = bonusReinforcement;
        this.continentId = continentId;
        this.countries = countries;
    }

    public String getName() {
        return name;
    }

    public int getBonusReinforcement() {
        return bonusReinforcement;
    }

    public int getContinentId() {
        return continentId;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
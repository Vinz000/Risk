package game;

/**
 * TODO:
 *  add player variable
 *
 */

public class Country {
    private final String countryName;
    private final int[] adjCountries;
    private final int continent_id;
    private int army = 0;

    // not yet implemented
    // private Player currentPlayer;

    public Country(String countryName, int[] adjCountries, int continent_id) {
        this.countryName = countryName;
        this.adjCountries = adjCountries;
        this.continent_id = continent_id;
    }

    public String getCountryName() {
        return countryName;
    }

    public int[] getAdjCountries() {
        return adjCountries;
    }

    public int getContinent_id() {
        return continent_id;
    }

    public int getArmy() {
        return army;
    }

    public void setArmy(int army) {
        this.army = army;
    }
}

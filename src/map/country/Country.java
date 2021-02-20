package map.country;

import player.Player;
import javafx.geometry.Point2D;

public class Country {
    private final String countryName;
    private final int[] adjCountries;
    private final int continentID;
    private final Point2D coords;
    private int armyCount = 0;
    private Player occupier;

    public Country(String countryName, int[] adjCountries, int continentID, int[] coords) {
        this.countryName = countryName;
        this.adjCountries = adjCountries;
        this.continentID = continentID;
        this.coords = new Point2D(coords[0], coords[1]);
    }

    public Point2D getCoords() {
        return coords;
    }

    public String getCountryName() {
        return countryName;
    }

    public int[] getAdjCountries() {
        return adjCountries;
    }

    public int getContinentID() {
        return continentID;
    }

    public int getArmyCount() {
        return armyCount;
    }

    public void updateArmyCount(int armyCount) {
        if (this.armyCount + armyCount < 0) {
            throw new IllegalArgumentException("ArmyCount cannot be less than 0.");
        }
        this.armyCount += armyCount;
    }

    public Player getOccupier() {
        return occupier;
    }

    public void setOccupier(Player player) {
        this.occupier = player;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Country &&
                ((Country) obj).getCountryName().equals(countryName) &&
                ((Country) obj).getContinentID() == continentID;
    }
}

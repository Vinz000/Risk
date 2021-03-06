package map.country;

import javafx.geometry.Point2D;
import player.Player;

import java.util.Objects;

public class Country {
    private final String countryName;
    private final int[] adjCountries;
    private final int continentID;
    private final int id;
    private final Point2D coords;
    private int armyCount = 0;
    private int forceCount = 0;
    private Player occupier;

    public Country(String countryName, int[] adjCountries, int continentID, int id, int[] coords) throws IllegalArgumentException {
        assert continentID >= 0 : "ContinentId cannot be negative, but was " + continentID;

        this.countryName = Objects.requireNonNull(countryName);
        this.adjCountries = Objects.requireNonNull(adjCountries);
        Objects.requireNonNull(coords);

        this.continentID = continentID;
        this.id = id;
        this.coords = new Point2D(coords[0], coords[1]);
    }

    public Point2D getCoords() {
        return coords;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getId() {
        return id;
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
        assert this.armyCount + armyCount >= 0 : "ArmyCount cannot be less than 0.";
        this.armyCount += armyCount;
    }

    public void setArmyCount(int armyCount) {
        assert armyCount >= 0 : "ArmyCount cannot be less than 0.";
        this.armyCount = armyCount;
    }

    public Player getOccupier() {
        return occupier;
    }

    public void setOccupier(Player player) {
        this.occupier = Objects.requireNonNull(player);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Country &&
                ((Country) obj).getCountryName().equals(countryName) &&
                ((Country) obj).getContinentID() == continentID;
    }

    public int getForceCount() {
        return forceCount;
    }

    public void updateForceCount(int forceCount) {
        assert this.forceCount + forceCount >= 0 : "Force cannot be less than 0.";
        this.forceCount += forceCount;
    }

    public void emptyForceCount() {
        this.forceCount = 0;
    }

    public void destroyedUnit() {
        assert this.forceCount - 1 >= 0 : "Force cannot be less than 0.";
        this.forceCount -= 1;
    }
}

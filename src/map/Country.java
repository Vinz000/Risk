package map;

import player.Player;
import javafx.geometry.Point2D;

public class Country {
    private final String countryName;
    private final int[] adjCountries;
    private final int continentID;
    private final Point2D coords;
    private int army = 0;
    private Player currentPlayer;

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

    public int getArmy() {
        return army;
    }

    public void setArmy(int army) {
        this.army = army;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Country &&
                ((Country) obj).getCountryName().equals(countryName) &&
                ((Country) obj).getContinentID() == continentID;
    }
}

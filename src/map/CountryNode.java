package map;

import java.util.Observable;

import shell.Player;

/**
 * TODO:
 * add player variable
 */

public class CountryNode extends Observable {
    private final String countryName;
    private final int[] adjCountries;
    private final int continentID;
    private final int[] coord;
    private int army = 0;
    private Player currentPlayer;

    public CountryNode(String countryName, int[] adjCountries, int continentID, int[] coord) {
        this.countryName = countryName;
        this.adjCountries = adjCountries;
        this.continentID = continentID;
        this.coord = coord;
    }

    public int[] getCoord() {
        return coord;
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
        setChanged();
        notifyObservers(army);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        setChanged();
        notifyObservers(player);
    }


}

package map;

import java.util.Observable;

import player.Player;
import javafx.geometry.Point2D;

public class CountryNode extends Observable {
    private final String countryName;
    private final int[] adjCountries;
    private final int continentID;
    private final Point2D coords;
    private int army = 0;
    private Player currentPlayer;

    public CountryNode(String countryName, int[] adjCountries, int continentID, int[] coords) {
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
        setChanged();
        notifyObservers(army);
    }

    public void incrementArmy(int army) {
        setArmy(this.army + army);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player humanPlayer) {
        this.currentPlayer = humanPlayer;
        setChanged();
        notifyObservers(currentPlayer);
    }


}

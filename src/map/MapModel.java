package map;

import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static common.Constants.*;

public class MapModel extends Observable {
    private final List<CountryNode> countries = new ArrayList<>();

    private final List<Player> humanPlayers = new ArrayList<>(NUM_HUMAN_PLAYERS);
    private final List<Player> neutralPlayers = new ArrayList<>(NUM_NEUTRAL_PLAYERS);
    private static MapModel instance;
    private int currentPlayerIndex = 0;

    private MapModel() {
        initializeCountries();
    }

    public static MapModel getInstance() {
        if (instance == null) {
            return instance = new MapModel();
        }

        return instance;
    }

    private void initializeCountries() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            countries.add(new CountryNode(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORDS[i]));
        }
    }

    public List<CountryNode> getCountries() {
        return countries;
    }

    public List<Player> getHumanPlayers() {
        return humanPlayers;
    }

    public void setCountryArmyCount(CountryNode country, int army) {
        for (CountryNode countryNode : countries) {
            if (countryNode.equals(country)) {
                countryNode.setArmy(army);

                MapModelArg mapModelArg = new MapModelArg(countryNode, MapModelUpdateType.ARMY_COUNT);

                setChanged();
                notifyObservers(mapModelArg);

                break;
            }
        }
    }

    public void setCurrentPlayer(CountryNode country, Player player) {
        for (CountryNode countryNode : countries) {
            if (countryNode.equals(country)) {
                countryNode.setCurrentPlayer(player);

                MapModelArg mapModelArg = new MapModelArg(countryNode, MapModelUpdateType.CURRENT_PLAYER);
                setChanged();
                notifyObservers(mapModelArg);

                break;
            }
        }
    }

    public void addPlayer(HumanPlayer humanPlayer) {
        humanPlayers.add(humanPlayer);
    }

    private void setUpNeutrals() {
        for (int i = 0; i < NUM_NEUTRAL_PLAYERS; i++) {
            neutralPlayers.add(new NeutralPlayer(String.valueOf(i + 1), Colors.NEUTRAL_PLAYER));
        }
    }

    public void initializeGame() {
        setUpNeutrals();

        int currentPlayerIndex = 0;
        int playerControlledCountries = 0;

        // While playerControlledCountries is less than the initial starting requirement continue to iterate.
        while (playerControlledCountries < INIT_COUNTRIES_PLAYER * 2) {

            // Iterates through the players hand positions and gets the country name on the card.
            for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
                Player currentPlayer = humanPlayers.get(currentPlayerIndex);
                String playerCountryName = currentPlayer.getHand().get(i).getCountryName();

                // Iterates through the country list to find a matching country name.
                for (CountryNode country : countries) {
                    if (playerCountryName.equals(country.getCountryName())) {

                        // Sets that country as a players if a match is found.
                        setCurrentPlayer(country, currentPlayer);
                        playerControlledCountries++;
                    }
                    setCountryArmyCount(country, 1);
                }
            }
            currentPlayerIndex++;
        }
    }

    public void forEachPlayer(int maxIndex, Consumer<Player> callback) {
        for (int i = 0; i < maxIndex; i++) {
            Player currentPlayer = getPlayer(i);
            callback.accept(currentPlayer);
        }
    }

    public Player getPlayer(int i) {
        return humanPlayers.get(i);
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayerIndex);
    }

    public void changeTurn() {
        currentPlayerIndex++;
        currentPlayerIndex %= NUM_HUMAN_PLAYERS;
    }

    public List<Player> getNeutralPlayers() {
        return neutralPlayers;
    }

    // Helper function to change string to CountryNode.
    public Optional<CountryNode> fetchCountry(String input) {
        return countries
                .stream()
                .filter(countryNode -> countryNode.getCountryName().toLowerCase().contains(input))
                .findFirst();
    }
}


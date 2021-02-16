package map;

import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;

import java.util.*;
import java.util.function.Consumer;

import static common.Constants.*;

public class MapModel extends Observable {
    private final static MapModel mapModel = new MapModel();

    private final List<CountryNode> countries = new ArrayList<>();

    private final List<Player> humanPlayers = new ArrayList<>(NUM_HUMAN_PLAYERS);
    private final List<Player> neutralPlayers = new ArrayList<>(NUM_NEUTRAL_PLAYERS);

    private int currentPlayerIndex = 0;

    private MapModel() {
        initializeCountries();
    }

    public static MapModel getMapModel() {
        return mapModel;
    }

    public List<Player> getHumanPlayers() {
        return humanPlayers;
    }

    private void initializeCountries() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            countries.add(new CountryNode(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORDS[i]));
        }
    }

    public List<CountryNode> getCountries() {
        return countries;
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
                String playerCountryName = humanPlayers.get(currentPlayerIndex).getHand().get(i).getCountryName();

                // Iterates through the country list to find a matching country name.
                for (CountryNode country : countries) {
                    if (playerCountryName.equals(country.getCountryName())) {
                        //Sets that country as a players if a match is found.
                        country.setCurrentPlayer(humanPlayers.get(currentPlayerIndex));
                        playerControlledCountries++;
                    }
                    country.setArmy(1);
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
        return mapModel
                .getCountries()
                .stream()
                .filter(countryNode -> countryNode.getCountryName().toLowerCase().contains(input))
                .findFirst();
    }
}


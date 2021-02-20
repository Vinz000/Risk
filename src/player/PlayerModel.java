package player;

import common.Constants;
import map.country.Country;
import map.model.MapModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;

import static common.Constants.*;
import static common.Constants.INIT_COUNTRIES_PLAYER;

public class PlayerModel extends Observable {
    private final List<Player> humanPlayers = new ArrayList<>(NUM_HUMAN_PLAYERS);
    private final List<Player> neutralPlayers = new ArrayList<>(NUM_NEUTRAL_PLAYERS);
    private int currentPlayerIndex = 0;

    private static PlayerModel instance;

    private PlayerModel() {
        createNeutralPlayers();
    }

    public static PlayerModel getInstance() {
        if (instance == null) {
            return instance = new PlayerModel();
        }

        return instance;
    }

    public List<Player> getHumanPlayers() {
        return humanPlayers;
    }

    public Player getHumanPlayer(int i) {
        return humanPlayers.get(i);
    }

    public Player getCurrentHumanPlayer() {
        return getHumanPlayer(currentPlayerIndex);
    }

    public List<Player> getNeutralPlayers() {
        return neutralPlayers;
    }

    public void addHumanPlayer(HumanPlayer humanPlayer) {
        humanPlayers.add(humanPlayer);
    }

    private void createNeutralPlayers() {
        for (int i = 0; i < NUM_NEUTRAL_PLAYERS; i++) {
            Player newPlayer = new NeutralPlayer(String.valueOf(i + 1), Constants.Colors.NEUTRAL_PLAYER);
            neutralPlayers.add(newPlayer);
        }
    }

    public void changeTurn() {
        currentPlayerIndex++;
        currentPlayerIndex %= NUM_HUMAN_PLAYERS;
    }

    public void forEachPlayer(int maxIndex, Consumer<Player> callback) {
        for (int i = 0; i < maxIndex; i++) {
            Player currentPlayer = getHumanPlayer(i);
            callback.accept(currentPlayer);
        }
    }

    public void assignInitialCountries() {

        MapModel mapModel = MapModel.getInstance();
        int currentPlayerIndex = 0;
        int playerControlledCountries = 0;

        // While playerControlledCountries is less than the initial starting requirement continue to iterate.
        while (playerControlledCountries < INIT_COUNTRIES_PLAYER * 2) {
            Player currentPlayer = humanPlayers.get(currentPlayerIndex++);

            // Iterates through the players hand positions and gets the country name on the card.
            for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
                String playerCountryName = currentPlayer.getHand().get(i).getCountryName();

                // Iterates through the country list to find a matching country name.
                for (Country country : mapModel.getCountries()) {
                    if (playerCountryName.equals(country.getCountryName())) {

                        // Sets that country as a players if a match is found.
                        mapModel.setCountryOccupier(country, currentPlayer);
                        playerControlledCountries++;
                    }
                    mapModel.setCountryArmyCount(country, 1);
                }
            }
        }
    }
}

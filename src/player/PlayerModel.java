package player;

import card.Card;
import card.Deck;
import common.Constants;
import map.country.Country;
import map.model.MapModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.function.Consumer;

import static common.Constants.*;

public class PlayerModel extends Observable {
    private static PlayerModel instance;
    private final List<Player> humanPlayers = new ArrayList<>(NUM_HUMAN_PLAYERS);
    private final List<Player> neutralPlayers = new ArrayList<>(NUM_NEUTRAL_PLAYERS);
    private int currentPlayerIndex = 0;

    private PlayerModel() {
        createNeutralPlayers();
    }

    public static synchronized PlayerModel getInstance() {
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

    public void forEachHumanPlayer(Consumer<Player> callback) {
        for (Player player : humanPlayers) {
            callback.accept(player);
        }
    }

    public void forEachNeutralPlayer(Consumer<Player> callback) {
        for (Player player : neutralPlayers) {
            callback.accept(player);
        }
    }

    private void assignInitialCountries(List<Player> players) {

        MapModel mapModel = MapModel.getInstance();
        Deck deck = Deck.getInstance();

        players.forEach(player -> {
            for (Card card : player.getHand()) {
                String playerCountryName = card.getCountryName();
                Optional<Country> nullableCountry = mapModel.getCountryByName(playerCountryName);
                nullableCountry.ifPresent(country -> {
                    mapModel.setCountryOccupier(country, player);
                    mapModel.updateCountryArmyCount(country, 1);

                    player.addCountry(country);

                });
            }
        });
    }

    public void assignAllInitialCountries() {
        assignInitialCountries(humanPlayers);
        assignInitialCountries(neutralPlayers);
    }

}

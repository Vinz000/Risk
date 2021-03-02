package player.model;

import common.Constants;
import deck.Card;
import deck.Deck;
import javafx.application.Platform;
import map.country.Country;
import map.model.MapModel;
import player.NeutralPlayer;
import player.Player;

import java.util.*;

import static common.Constants.NUM_HUMAN_PLAYERS;
import static common.Constants.NUM_NEUTRAL_PLAYERS;

public class PlayerModel extends Observable {

    /**
     * Order of Players:
     * H1 -> N1 -> N2 -> N3 -> N4 -> H2/BOT
     */

    private static PlayerModel instance;
    private final List<Player> players = new ArrayList<>(NUM_HUMAN_PLAYERS + NUM_NEUTRAL_PLAYERS);
    private int currentPlayerIndex = 0;

    private PlayerModel() {
    }

    public static synchronized PlayerModel getInstance() {
        if (instance == null) {
            return instance = new PlayerModel();
        }

        return instance;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void createNeutralPlayers() {
        for (int i = 0; i < NUM_NEUTRAL_PLAYERS; i++) {
            Player newPlayer = new NeutralPlayer(String.valueOf(i + 1), Constants.Colors.NEUTRAL_PLAYER);
            players.add(newPlayer);
        }
    }

    // TODO: Change turn
    public void changeTurn() {
        currentPlayerIndex++;

        if (currentPlayerIndex == players.size() - 1) {
            currentPlayerIndex = 0;

            // Swap the last and first players
            Collections.swap(players, 0, players.size() - 1);

            // Only update playerIndicator when swap happens
            Platform.runLater(this::updatePlayerIndicator);
        }
    }

    public void updatePlayerIndicator() {
        PlayerModelArg playerModelArg = new PlayerModelArg(getCurrentPlayer(), PlayerModelUpdateType.CHANGED_PLAYER);
        setChanged();
        notifyObservers(playerModelArg);
    }

    public void showPlayerIndicator() {
        PlayerModelArg playerModelArg = new PlayerModelArg(null, PlayerModelUpdateType.VISIBLE);
        setChanged();
        notifyObservers(playerModelArg);
    }

    public void assignInitialCountries() {

        MapModel mapModel = MapModel.getInstance();
        Deck deck = Deck.getInstance();

        players.forEach(player -> {
            List<Card> cards = player.getCards();
            while (cards.size() > 0) {
                String playerCountryName = cards.get(0).getCountryName();
                Optional<Country> nullableCountry = mapModel.getCountryByName(playerCountryName);
                nullableCountry.ifPresent(country -> {
                    mapModel.setCountryOccupier(country, player);
                    mapModel.updateCountryArmyCount(country, 1);

                    player.addCountry(country);
                    deck.add(player.removeTopCard());
                });
            }
        });
    }
}

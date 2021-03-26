package player.model;

import common.Constants;
import common.validation.Validators;
import javafx.application.Platform;
import map.Continent;
import map.country.Country;
import map.model.MapModel;
import player.NeutralPlayer;
import player.Player;
import shell.model.ShellModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.function.Predicate;

import static common.Constants.*;

public class PlayerModel extends Observable {

    private static PlayerModel instance;
    private final List<Player> players = new ArrayList<>(NUM_PLAYERS);
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

    public void removePlayer(Player player) {
        players.remove(player);
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
            Collections.swap(players, 0, currentPlayerIndex);

            currentPlayerIndex = 0;

            updatePlayerIndicator();
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

    public void calculateReinforcements(Player player) {
        int availableReinforcements = 0;

        availableReinforcements += (player.getOwnedCountries().size() / 3);

        MapModel mapModel = MapModel.getInstance();

        for (Continent continent : mapModel.getContinents()) {
            boolean ownsContinent = continent
                    .getCountries()
                    .stream()
                    .allMatch(country -> country.getOccupier().equals(player));

            if (ownsContinent) {
                availableReinforcements += continent.getBonusReinforcement();
            }
        }

        // Available Reinforcements is always a min of 3
        availableReinforcements = Math.max(availableReinforcements, DEFAULT_REINFORCEMENT);

        player.setReinforcements(availableReinforcements);
    }

    public boolean currentPlayerCanAttack() {
        Player currentPlayer = getCurrentPlayer();
        ShellModel shellModel = ShellModel.getInstance();

        Predicate<Country> countryCanAttack = country -> Validators
                .hasAdjacentOpposingCountry
                .apply(country.getCountryName())
                .isValid() && country.getArmyCount() != 1;

        boolean canAttack = currentPlayer
                .getOwnedCountries()
                .stream()
                .anyMatch(countryCanAttack);

        if (!canAttack) {
            shellModel.notify("You do not have the appropriate forces to attack with.");
        }

        return canAttack;
    }

}

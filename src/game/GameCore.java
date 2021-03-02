package game;

import common.Constants;
import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import javafx.application.Platform;
import javafx.concurrent.Task;
import map.country.Country;
import map.model.MapModel;
import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static common.Constants.*;

public class GameCore extends Task<Void> {

    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final MapModel mapModel = MapModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    private String response;

    private void uiAction(Runnable action) {
        Platform.runLater(action);
    }

    @Override
    protected Void call() {

        uiAction(() -> shellModel.notify(Constants.Notifications.WELCOME));
        Deck deck = Deck.getInstance();
        Dice dice = new Dice();

        /*
        Creating players
         */
        uiAction(() -> shellModel.notify(Constants.Notifications.NAME + "(P1)"));
        response = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerOne = new HumanPlayer(response, Constants.Colors.PLAYER_1_COLOR);
        playerModel.addPlayer(humanPlayerOne);
        uiAction(() -> shellModel.notify("Welcome " + humanPlayerOne.getName()));

        playerModel.createNeutralPlayers();

        uiAction(() -> shellModel.notify(Constants.Notifications.NAME + "(P2)"));
        response = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerTwo = new HumanPlayer(response, Constants.Colors.PLAYER_2_COLOR);
        playerModel.addPlayer(humanPlayerTwo);
        uiAction(() -> shellModel.notify("Welcome " + humanPlayerTwo.getName()));

        /*
        Drawing initial territory cards
         */
        uiAction(() -> shellModel.notify(Constants.Notifications.TERRITORY));
        uiAction(() -> shellModel.notify(Constants.Notifications.TERRITORY_OPTION));
        response = shellModel.prompt(Validators.yesNo);
        boolean showDraw = response.toLowerCase().contains("y");

        for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
            boolean neutralsMissingCards = i < INIT_COUNTRIES_NEUTRAL;

            for (Player player : playerModel.getPlayers()) {
                if (neutralsMissingCards || player instanceof HumanPlayer) {
                    Optional<Card> nullableCard = deck.drawCard();

                    nullableCard.ifPresent(drawnCard -> {
                        player.addCard(drawnCard);
                        if (showDraw) {
                            String drawnCountryName = drawnCard.getCountryName();
                            String playerName = player.getName();
                            uiAction(() -> shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName));
                        }
                    });
                }
            }
        }

        playerModel.assignInitialCountries();
        deck.addWildcards();
        deck.shuffle();
        mapModel.showCountryComponents();

        /*
        Roll dice to determine which player chooses countries to reinforce first
         */
        uiAction(() -> shellModel.notify("\n" + "The dice will now decide who goes first."));

        int playerOneDiceSum;
        int playerTwoDiceSum;
        List<Player> players = playerModel.getPlayers();

        Function<Player, String> createRolledNotification = (player) -> player.getName() + " "
                + Constants.Notifications.ROLLED + dice.toString();

        do {
            playerOneDiceSum = dice.getRollSum(2);
            String playerOneRolledNotification = createRolledNotification.apply(players.get(0));
            uiAction(() -> shellModel.notify(playerOneRolledNotification));

            playerTwoDiceSum = dice.getRollSum(2);
            String playerTwoRolledNotification = createRolledNotification.apply(players.get(players.size() - 1));
            uiAction(() -> shellModel.notify(playerTwoRolledNotification));

            if (playerOneDiceSum == playerTwoDiceSum) {
                uiAction(() -> shellModel.notify("\nRolled the same number\nRolling again!"));
            } else if (playerOneDiceSum < playerTwoDiceSum) {
                // player One goes first by default but if player One rolls a lower sum,
                // then we change the turn so that the current player is now player Two.
                Collections.swap(players, 0, players.size() - 1);
            }

            uiAction(playerModel::updatePlayerIndicator);
            uiAction(playerModel::showPlayerIndicator);

        } while (playerOneDiceSum == playerTwoDiceSum);

        String currentPlayerName = players.get(0).getName();
        String finalCurrentPlayerName1 = currentPlayerName;
        uiAction(() -> shellModel.notify(String.format("%s rolled higher, so is going first", finalCurrentPlayerName1)));

        /*
        Initial reinforcement of countries
         */
        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1) * 9; i++) {
            Player player = playerModel.getCurrentPlayer();

            // Before reinforcing
            boolean isNeutral = (player instanceof NeutralPlayer);

            String prefixMessage = isNeutral ? "Choose country Owned by " : "\nYour Turn ";
            String message = prefixMessage + player.getName();

            uiAction(() -> shellModel.notify(message));
            uiAction(() -> shellModel.notify("Place down reinforcements."));

            // Toggle highlight (on)
            for (Country ownedCountry : player.getOwnedCountries()) {
                uiAction(() -> mapModel.highlightCountry(ownedCountry));
            }

            // Reinforcing
            response = shellModel.prompt(Validators.currentPlayerOccupies);
            Optional<Country> country = mapModel.getCountryByName(response);
            int reinforcement = player.getReinforcement();

            country.ifPresent(validCountry -> uiAction(() -> mapModel.updateCountryArmyCount(validCountry, reinforcement)));

            uiAction(() -> shellModel.notify("Successfully placed reinforcements."));

            // Toggle highlight (off)
            for (Country ownedCountry : player.getOwnedCountries()) {
                uiAction(() -> mapModel.highlightCountry(ownedCountry));
            }

            playerModel.changeTurn();
        }

        /*
        Roll dice to determine which player has the first turn
         */
        uiAction(() -> shellModel.notify("\n" + "The dice will now decide who goes first."));

        players = playerModel.getPlayers();

        do {
            playerOneDiceSum = dice.getRollSum(2);
            String playerOneRolledNotification = createRolledNotification.apply(players.get(0));
            uiAction(() -> shellModel.notify(playerOneRolledNotification));

            playerTwoDiceSum = dice.getRollSum(2);
            String playerTwoRolledNotification = createRolledNotification.apply(players.get(players.size() - 1));
            uiAction(() -> shellModel.notify(playerTwoRolledNotification));

            if (playerOneDiceSum == playerTwoDiceSum) {
                uiAction(() -> shellModel.notify("\nRolled the same number\nRolling again!"));
            } else if (playerOneDiceSum < playerTwoDiceSum) {
                // player One goes first by default but if player One rolls a lower sum,
                // then we change the turn so that the current player is now player Two.
                Collections.swap(players, 0, players.size() - 1);
            }

            uiAction(playerModel::updatePlayerIndicator);
            uiAction(playerModel::showPlayerIndicator);

        } while (playerOneDiceSum == playerTwoDiceSum);

        currentPlayerName = players.get(0).getName();
        String finalCurrentPlayerName = currentPlayerName;
        uiAction(() -> shellModel.notify(String.format("%s rolled higher, so is going first", finalCurrentPlayerName)));

        /*
        Game loop
         */
        while (true) {
            uiAction(() -> shellModel.notify("Please enter something"));
            response = shellModel.prompt(Validators.nonEmpty);
            uiAction(() -> shellModel.notify("Your response: " + response));
        }
    }

    // Game logic sequence
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}

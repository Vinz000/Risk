package shell.prompt;

import common.Constants;
import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
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
import java.util.function.Supplier;

import static common.Constants.INIT_COUNTRIES_NEUTRAL;
import static common.Constants.INIT_COUNTRIES_PLAYER;

public class ShellPromptFactory {
    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final MapModel mapModel = MapModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    public ShellPromptFactory() {
    }

    public ShellPrompt createPlayerOne() {
        return new ShellPrompt(input -> {
            Player humanPlayerOne = new HumanPlayer(input, Constants.Colors.PLAYER_1_COLOR);
            playerModel.addPlayer(humanPlayerOne);

            shellModel.notify("Welcome " + humanPlayerOne.getName());
            shellModel.notify(Constants.Notifications.NAME + "(P2)");

            // Create the neutralPlayers
            playerModel.createNeutralPlayers();
        }, Validators.nonEmpty);
    }

    public ShellPrompt createPlayerTwo() {
        return new ShellPrompt(input -> {
            Player humanPlayerTwo = new HumanPlayer(input, Constants.Colors.PLAYER_2_COLOR);
            playerModel.addPlayer(humanPlayerTwo);

            shellModel.notify("Welcome " + humanPlayerTwo.getName() + "\n");

            shellModel.notify(Constants.Notifications.TERRITORY);
            shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
        }, Validators.nonEmpty);
    }

    public ShellPrompt drawTerritories() {
        return new ShellPrompt(input -> {
            Deck deck = Deck.getInstance();

            final boolean showDraw = input.toLowerCase().contains("y");

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
                                shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                            }
                        });
                    }
                }
            }

            playerModel.assignInitialCountries();

            deck.addWildcards();
            deck.shuffle();

            mapModel.showCountryComponents();

            shellModel.notify("\n" + Constants.Notifications.DICE_ROLL);
        }, Validators.yesNo);
    }

    public ShellPrompt selectFirstPlayer() {
        return new ShellPrompt(input -> {
            Dice dice = new Dice();
            int playerOneDiceSum;
            int playerTwoDiceSum;

            List<Player> players = playerModel.getPlayers();

            Function<Player, String> createRolledNotification = (player) -> player.getName() + " "
                    + Constants.Notifications.ROLLED + dice.toString();

            do {
                playerOneDiceSum = dice.getRollSum(2);
                String playerOneRolledNotification = createRolledNotification.apply(players.get(0));

                shellModel.notify(playerOneRolledNotification);

                playerTwoDiceSum = dice.getRollSum(2);
                String playerTwoRolledNotification = createRolledNotification.apply(players.get(players.size() - 1));

                shellModel.notify(playerTwoRolledNotification);

                if (playerOneDiceSum == playerTwoDiceSum) {
                    shellModel.notify("\nRolled the same number\nRolling again!");
                } else if (playerOneDiceSum < playerTwoDiceSum) {
                    // player One goes first by default but if player One rolls a lower sum,
                    // then we change the turn so that the current player is now player Two.

                    Collections.swap(players, 0, players.size() - 1);
                }

                playerModel.updatePlayerIndicator();
                playerModel.showPlayerIndicator();

            } while (playerOneDiceSum == playerTwoDiceSum);

            String currentPlayerName = players.get(0).getName();
            shellModel.notify(String.format("%s rolled higher, so is going first", currentPlayerName));
            shellModel.notify("Please press Enter to continue");
        }, Validators.alwaysValid);
    }

    public ShellPrompt beforeReinforcingCountry() {
        return new ShellPrompt(input -> {
            Player player = playerModel.getCurrentPlayer();
            boolean isNeutral = (player instanceof NeutralPlayer);

            String prefixMessage = isNeutral ? "Choose country Owned by " : "\nYour Turn ";
            String message = prefixMessage + player.getName();

            shellModel.notify(message);
            shellModel.notify("Place down reinforcements.");

            player.getOwnedCountries().forEach(mapModel::highlightCountry);
        }, Validators.alwaysValid);
    }

    public ShellPrompt reinforcingCountry() {
        return new ShellPrompt(input -> {
            Player player = playerModel.getCurrentPlayer();
            Optional<Country> country = mapModel.getCountryByName(input);
            int reinforcement = player.getReinforcement();

            country.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, reinforcement));

            shellModel.notify("Successfully placed reinforcements.");

            // Undo highlight
            player.getOwnedCountries().forEach(mapModel::highlightCountry);

            playerModel.changeTurn();
        }, Validators.currentPlayerOccupies);
    }

    public ShellPrompt enterGameLoop(Supplier<Boolean> gameLoop) {
        return new ShellPrompt(input -> {
            boolean continuePlaying = gameLoop.get();

            if (continuePlaying) shellModel.prompt(enterGameLoop(gameLoop));
        }, Validators.alwaysValid);
    }
}

package game;

import card.Card;
import card.Deck;
import common.Constants;
import common.Dice;
import common.validation.Validators;
import map.country.Country;
import map.model.MapModel;
import player.HumanPlayer;
import player.Player;
import player.PlayerModel;
import shell.model.ShellModel;
import shell.model.ShellModel.ShellPrompt;

import java.util.Collections;
import java.util.Optional;

import static common.Constants.NUM_NEUTRAL_PLAYERS;

public class GameCore {

    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final MapModel mapModel = MapModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    private static final ShellPrompt playerOnePrompt = new ShellPrompt(input -> {
        HumanPlayer humanPlayerOne = new HumanPlayer(input, Constants.Colors.PLAYER_1_COLOR);
        playerModel.addHumanPlayer(humanPlayerOne);

        shellModel.notify(Constants.Notifications.NAME + "(P2)\n");

    }, Validators.nonEmpty);

    private static final ShellPrompt playerTwoPrompt = new ShellPrompt(input -> {

        HumanPlayer humanPlayerTwo = new HumanPlayer(input, Constants.Colors.PLAYER_2_COLOR);
        playerModel.addHumanPlayer(humanPlayerTwo);

        shellModel.notify(Constants.Notifications.TERRITORY);
        shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
    }, Validators.nonEmpty);

    // Deciding which player goes first
    private static final ShellPrompt selectFirstPlayer = new ShellPrompt(input -> {
        Dice dice = new Dice();
        int playerOneDiceSum;
        int playerTwoDiceSum;

        do {
            playerOneDiceSum = dice.getRollSum(2);
            String playerOneDiceNotification = playerModel.getHumanPlayer(0).getName() + " "
                    + Constants.Notifications.ROLLED + dice.printRoll();

            shellModel.notify(playerOneDiceNotification);

            playerTwoDiceSum = dice.getRollSum(2);
            String playerTwoDiceNotification = playerModel.getHumanPlayer(1).getName() + " "
                    + Constants.Notifications.ROLLED + dice.printRoll();

            shellModel.notify(playerTwoDiceNotification);

            if (playerOneDiceSum == playerTwoDiceSum) {
                shellModel.notify("\nRolled the same number\nRolling again!");
            } else if (playerOneDiceSum < playerTwoDiceSum) {
                // player One goes first by default but if player One rolls a lower sum,
                // then we change the turn so that the current player is now player Two.

                playerModel.changeTurn();
            }

        } while (playerOneDiceSum == playerTwoDiceSum);

        String currentPlayerName = playerModel.getCurrentHumanPlayer().getName();
        shellModel.notify(String.format("%s rolled higher, so is going first\n", currentPlayerName));
        shellModel.notify("Your turn " + currentPlayerName);
        shellModel.notify("Please press Enter to continue");
    }, Validators.alwaysValid);

    // Choosing own country to reinforce
    private static final ShellPrompt chooseOwnCountry = new ShellPrompt(input -> {
        // Place down 3 armies in corresponding country
        Optional<Country> country = mapModel.getCountryByName(input);
        country.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, 3));

        // Undo highlight of owned countries
        playerModel.getCurrentHumanPlayer().getOwnedCountries().forEach(mapModel::highlightCountry);

        // Change turn
        playerModel.changeTurn();

        shellModel.notify("Successfully placed armies down");
        shellModel.notify("Please press Enter to continue");
    }, Validators.currentPlayerOccupies);

    // Prompts user before choosing own country
    private static final ShellPrompt beforeChoosingOwnCountry = new ShellPrompt(input -> {

        playerModel.getCurrentHumanPlayer().getOwnedCountries().forEach(mapModel::highlightCountry);

        shellModel.notify(
                String.format("%s choose country that you own to place 3 armies.", playerModel.getCurrentHumanPlayer().getName())
        );

    }, Validators.alwaysValid);

    // Choosing neutral countries to reinforce
    private static final ShellPrompt chooseNeutral = new ShellPrompt(input -> {
        Optional<Country> country = mapModel.getCountryByName(input);
        country.ifPresent(validCountry -> {
            int currentArmyCount = validCountry.getArmyCount();
            mapModel.updateCountryArmyCount(validCountry, currentArmyCount + 1);
        });

        shellModel.notify("Successfully placed army.");

        // Undo highlight
        playerModel.getNeutralPlayers().get(0).getOwnedCountries().forEach(mapModel::highlightCountry);

        Collections.rotate(playerModel.getNeutralPlayers(), -1);
    }, Validators.neutralPlayerOccupies);

    // Message before choosing a neutral country
    private static final ShellPrompt beforeChoosingNeutrals = new ShellPrompt(input -> {
        Player currentNeutralPlayer = playerModel.getNeutralPlayers().get(0);

        currentNeutralPlayer.getOwnedCountries().forEach(mapModel::highlightCountry);

        shellModel.notify(
                String.format("Place one army owned by %s", playerModel.getNeutralPlayers().get(0).getName())
        );
    }, Validators.alwaysValid);

    // Assigning countries at the start of the game
    private static final ShellPrompt drawingTerritories = new ShellPrompt(input -> {
        Deck deck = new Deck();

        if (input.toLowerCase().contains("y")) {
            for (int i = 0; i < 9; i++) {
                playerModel.forEachHumanPlayer(player -> {
                    Optional<Card> nullableCard = deck.drawCard();

                    nullableCard.ifPresent(drawnCard -> {
                        player.addCard(drawnCard);
                        String drawnCountryName = drawnCard.getCountryName();
                        String playerName = player.getName();
                        shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                    });
                });
            }

            for (int j = 0; j < 6; j++) {
                playerModel.forEachNeutralPlayer(player -> {
                    Optional<Card> nullableCard = deck.drawCard();
                    nullableCard.ifPresent(drawnCard -> {
                        player.addCard(drawnCard);
                        String drawnCountryName = drawnCard.getCountryName();
                        String playerName = player.getName();
                        shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                    });

                });
            }

        } else {
            for (int i = 0; i < 9; i++) {
                playerModel.forEachHumanPlayer(player -> {
                    Optional<Card> nullableCard = deck.drawCard();
                    nullableCard.ifPresent(player::addCard);
                });
            }

            for (int j = 0; j < 6; j++) {
                playerModel.forEachNeutralPlayer(player -> {
                    Optional<Card> nullableCard = deck.drawCard();
                    nullableCard.ifPresent(player::addCard);
                });
            }
        }

        playerModel.assignAllInitialCountries();

        for (int i = 0; i < 9; i++) {
            playerModel.forEachHumanPlayer(player -> {
                Card refillDeck = player.removeCard();
                deck.add(refillDeck);
            });
        }

        for (int j = 0; j < 6; j++) {
            playerModel.forEachNeutralPlayer(player -> {
                Card refillDeck = player.removeCard();
                deck.add(refillDeck);
            });
        }

        deck.addWildcards();
        deck.shuffle();

        mapModel.showCountryComponents();

        shellModel.notify(Constants.Notifications.DICE_ROLL);
    }, Validators.yesNo);

    // Game logic sequence
    public static void start() {
        shellModel.notify(Constants.Notifications.WELCOME);

        shellModel.notify(Constants.Notifications.NAME + "(P1)");

        shellModel.prompt(playerOnePrompt);
        shellModel.prompt(playerTwoPrompt);
        shellModel.prompt(drawingTerritories);
        shellModel.prompt(selectFirstPlayer);

        // Each player takes 12 turns
        for (int i = 0; i < 24; i++) {
            shellModel.prompt(beforeChoosingOwnCountry);
            shellModel.prompt(chooseOwnCountry);

            for (int j = 0; j < NUM_NEUTRAL_PLAYERS; j++) {
                shellModel.prompt(beforeChoosingNeutrals);
                shellModel.prompt(chooseNeutral);
            }
        }
    }
}

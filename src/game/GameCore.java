package game;

import card.Card;
import card.Deck;
import common.Constants;
import common.Dice;
import common.Validators;
import map.country.Country;
import map.model.MapModel;
import player.HumanPlayer;
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
            playerOneDiceSum = dice.getNextDice(2).getRollSum();
            String playerOneDiceNotification = playerModel.getHumanPlayer(0).getName() + " "
                    + Constants.Notifications.ROLLED + dice.printRoll();

            shellModel.notify(playerOneDiceNotification);

            playerTwoDiceSum = dice.getNextDice(2).getRollSum();
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
        // Place down 3 armies in corresponding countryNode
        Optional<Country> countryNode = mapModel.getCountryByName(input);
        countryNode.ifPresent(node -> {
            int currentArmyCount = node.getArmy();
            mapModel.setCountryArmyCount(node, currentArmyCount + 3);
        });

        shellModel.notify("Successfully placed armies down");
        shellModel.notify("Please press Enter to continue");
    }, Validators.currentPlayerOwns);

    // Prompts user before choosing own country
    private static final ShellPrompt beforeChoosingOwnCountry = new ShellPrompt(input -> shellModel.notify(
            String.format("%s choose country that you own to place 3 army.", playerModel.getCurrentHumanPlayer().getName())
    ), Validators.alwaysValid);

    // Choosing neutral countries to reinforce
    private static final ShellPrompt chooseNeutral = new ShellPrompt(input -> {
        Optional<Country> countryNode = mapModel.getCountryByName(input);
        countryNode.ifPresent(node -> {
            int currentArmyCount = node.getArmy();
            mapModel.setCountryArmyCount(node, currentArmyCount + 1);
        });

        shellModel.notify("Successfully placed army.");

        Collections.rotate(playerModel.getNeutralPlayers(), -1);
    }, Validators.neutralPlayerOwns);

    // Message before choosing a neutral country
    private static final ShellPrompt beforeChoosingNeutrals = new ShellPrompt(input -> shellModel.notify(
            String.format("Place one army owned by %s", playerModel.getNeutralPlayers().get(0).getName())
    ), Validators.alwaysValid);

    // Assigning countries at the start of the game
    private static final ShellPrompt drawingTerritories = new ShellPrompt(input -> {
        Deck deck = new Deck();

        if (input.toLowerCase().contains("y")) {
            for (int i = 0; i < 9; i++) {
                playerModel.forEachPlayer(2, player -> {
                    Card drawnCard = deck.drawCard();
                    player.addCard(drawnCard);
                    String drawnCountryName = drawnCard.getCountryName();
                    String playerName = player.getName();
                    shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                });
            }
        } else {
            for (int i = 0; i < 9; i++) {
                playerModel.forEachPlayer(2, player -> {
                    Card drawnCard = deck.drawCard();
                    player.addCard(drawnCard);
                });
            }
        }

        playerModel.assignInitialCountries();

        for (int i = 0; i < 9; i++) {
            playerModel.forEachPlayer(2, player -> {
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
package game;

import cards.Card;
import cards.Deck;
import common.Constants;
import Player.HumanPlayer;
import common.Validators;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import map.CountryNode;
import map.MapComponent;
import map.MapModel;
import shell.ShellComponent;
import shell.ShellModel;
import shell.ShellPrompt;

import java.util.Collections;
import java.util.Optional;

import static common.Constants.*;

public class GameRoot extends BorderPane {

    // Create models
    private final ShellModel shellModel = new ShellModel();
    private final MapModel mapModel = MapModel.getMapModel();

    public GameRoot() {
        setId(ComponentIds.GAME_ROOT);

        // Create top-level components
        MapComponent mapComponent = new MapComponent(mapModel);
        ShellComponent shellComponent = new ShellComponent(shellModel);
        mapModel.addObserver(mapComponent);
        shellModel.addObserver(shellComponent);
        shellModel.addObserver(mapComponent);

        // BorderPane configuration
        setRight(shellComponent);
        setCenter(mapComponent);
        setMargin(shellComponent, new Insets(10));
        setMargin(mapComponent, new Insets(10, 0, 10, 10));
    }

    public void start() {
        shellModel.notify(Notifications.WELCOME);

        /*
         * Getting player names
         */

        shellModel.notify(Notifications.NAME + "(P1)");

        ShellPrompt playerOnePrompt = new ShellPrompt(input -> {
            HumanPlayer humanPlayerOne = new HumanPlayer(input, Colors.PLAYER_1_COLOR);
            mapModel.addPlayer(humanPlayerOne);

            // Send message for next prompt
            shellModel.notify(Notifications.NAME + "(P2)\n");

        }, Validators.nonEmpty);

        ShellPrompt playerTwoPrompt = new ShellPrompt(input -> {

            HumanPlayer humanPlayerTwo = new HumanPlayer(input, Colors.PLAYER_2_COLOR);
            mapModel.addPlayer(humanPlayerTwo);

            shellModel.notify(Notifications.DICE_ROLL);

            shellModel.notify(Constants.Notifications.TERRITORY);
            shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
        }, Validators.nonEmpty);

        ShellPrompt selectFirstPlayer = new ShellPrompt(input -> {
            Dice dice = new Dice();
            int playerOneDiceSum;
            int playerTwoDiceSum;

            do {
                playerOneDiceSum = dice.getNextDice(2).getRollSum();
                String playerOneDiceNotification = mapModel.getPlayer(0).getName() + " "
                        + Notifications.ROLLED + dice.printRoll();

                shellModel.notify(playerOneDiceNotification);

                playerTwoDiceSum = dice.getNextDice(2).getRollSum();
                String playerTwoDiceNotification = mapModel.getPlayer(1).getName() + " "
                        + Notifications.ROLLED + dice.printRoll();

                shellModel.notify(playerTwoDiceNotification);

                if (playerOneDiceSum == playerTwoDiceSum) {
                    shellModel.notify("\nRolled the same number\nRolling again!");
                } else if (playerOneDiceSum < playerTwoDiceSum) {
                    // Player One goes first by default but if player One rolls a lower sum,
                    // then we change the turn so that the current player is now player Two.

                    mapModel.changeTurn();
                }

            } while (playerOneDiceSum == playerTwoDiceSum);

            shellModel.notify(String.format("%s rolled higher, so is going first\n", mapModel.getCurrentPlayer().getName()));
            shellModel.notify("Your turn " + mapModel.getCurrentPlayer().getName());
            shellModel.notify("Please choose country to reinforce.");
        }, Validators.alwaysValid);

        ShellPrompt chooseOwnCountry = new ShellPrompt(input -> {
            // Place down 3 armies in corresponding countryNode
            Optional<CountryNode> countryNode = mapModel.fetchCountry(input);
            countryNode.ifPresent(node -> node.incrementArmy(3));

            shellModel.notify("Successfully placed armies down");
            shellModel.notify("Please press Enter to continue");
        }, Validators.canReinforceOwn);

        ShellPrompt chooseNeutral = new ShellPrompt(input -> {
            Optional<CountryNode> countryNode = mapModel.fetchCountry(input);
            countryNode.ifPresent(node -> node.incrementArmy(1));

            shellModel.notify("Successfully placed army.");

            Collections.rotate(mapModel.getNeutralPlayers(), -1);
        }, Validators.canReinforceNeutral);

        ShellPrompt beforeChoosingNeutrals = new ShellPrompt(input -> shellModel.notify(
                String.format("Place one army owned by %s", mapModel.getNeutralPlayers().get(0).getName())
        ), Validators.alwaysValid);

        /*
         * Whatever comes after we get player names
         */

        ShellPrompt drawingTerritories = new ShellPrompt(input -> {
            Deck deck = new Deck();

            if (input.toLowerCase().contains("y")) {
                for (int i = 0; i < 9; i++) {
                    mapModel.forEachPlayer(2, player -> {
                        Card drawnCard = deck.drawCard();
                        player.getHand().add(drawnCard);
                        String drawnCountryName = drawnCard.getCountryName();
                        String playerName = player.getName();
                        shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                    });
                }
            } else {
                for (int i = 0; i < 9; i++) {
                    mapModel.forEachPlayer(2, player -> {
                        Card drawnCard = deck.drawCard();
                        player.getHand().add(drawnCard);
                    });
                }
            }

            mapModel.initializeGame();


            for (int i = 0; i < 9; i++) {
                mapModel.forEachPlayer(2, player -> {
                    Card refillDeck = player.removeCard();
                    deck.add(refillDeck);
                });
            }

            deck.addWildcards();
            deck.shuffle();
        }, Validators.yesNo);

        shellModel.prompt(playerOnePrompt);
        shellModel.prompt(playerTwoPrompt);
        shellModel.prompt(drawingTerritories);
        shellModel.prompt(selectFirstPlayer);

        for (int i = 0; i < 24; i++) {
            shellModel.prompt(chooseOwnCountry);
            for (int j = 0; j < NUM_NEUTRALS; j++) {
                shellModel.prompt(beforeChoosingNeutrals);
                shellModel.prompt(chooseNeutral);
            }
        }
    }
}

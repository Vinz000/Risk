package game;

import cards.Card;
import cards.Deck;
import common.Constants;
import common.Validators;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import map.MapComponent;
import map.MapModel;
import shell.Player;
import shell.ShellComponent;
import shell.ShellModel;
import shell.ShellPrompt;


public class GameRoot extends BorderPane {

    // Create models
    private final ShellModel shellModel = new ShellModel();
    private final MapModel mapModel = new MapModel();

    public GameRoot() {
        setId(Constants.ComponentIds.GAME_ROOT);

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
        shellModel.notify(Constants.Notifications.WELCOME);

        /*
         * Getting player names
         */

        shellModel.notify(Constants.Notifications.NAME + "(P1)");
        ShellPrompt playerOnePrompt = new ShellPrompt(input -> {
            Player playerOne = new Player(input, Color.valueOf("#0b7540"));
            mapModel.addPlayer(playerOne);

            // Send message for next prompt
            shellModel.notify(Constants.Notifications.NAME + "(P2)");

        }, Validators.nonEmpty);
        shellModel.prompt(playerOnePrompt);

        ShellPrompt playerTwoPrompt = new ShellPrompt(input -> {
            Player playerTwo = new Player(input, Color.valueOf("#ba131d"));
            mapModel.addPlayer(playerTwo);

            shellModel.notify(Constants.Notifications.TERRITORY);
            shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
        }, Validators.nonEmpty);
        shellModel.prompt(playerTwoPrompt);

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
        shellModel.prompt(drawingTerritories);


    }
}

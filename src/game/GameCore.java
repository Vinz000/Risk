package game;

import cavalry.model.GoldCavalryModel;
import common.Constants;
import common.validation.Validators;
import deck.Card;
import game.module.ClaimTerritories;
import game.module.SetUp;
import javafx.concurrent.Task;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.List;

public class GameCore extends Task<Void> {

    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final GoldCavalryModel goldCavalryModel = GoldCavalryModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    @Override
    protected Void call() {
        ClaimTerritories claimTerritories = new ClaimTerritories();
        SetUp setUp = new SetUp();

        shellModel.notify(Constants.Notifications.WELCOME);

        setUp.getPlayerOne();
        setUp.getPlayerTwo();

        claimTerritories.assignInitialCountries();

        setUp.selectFirstPlayer();

        for (int i = 0; i < 2 * Constants.INIT_REINFORCEMENT_TURNS * (Constants.NUM_PLAYERS - 1); i++) {
            Player currentPlayer = playerModel.getCurrentPlayer();

            currentPlayer.startReinforcement();
            currentPlayer.initReinforce();

            playerModel.changeTurn();
        }

        setUp.selectFirstPlayer();

        goldCavalryModel.showGoldCavalry();

        while (true) {
            Player currentPlayer = playerModel.getCurrentPlayer();

            currentPlayer.startTurn();
            List<Card> cards = currentPlayer.getCards();
            while (cards.size() >= 3 && Validators.validCombinations(cards)) {
                currentPlayer.cardUsage();
            }

            currentPlayer.reinforce();

            if (currentPlayer.combat()) {
                shellModel.notify("Game over: " + currentPlayer.getName() + " has won the game.");
                break;
            }

            currentPlayer.fortify();

            playerModel.changeTurn();
        }

        return null;
    }

    // Game logic sequence
    public void start() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println("Caught " + e));

        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}

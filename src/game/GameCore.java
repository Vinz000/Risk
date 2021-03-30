package game;

import cavalry.model.GoldCavalryModel;
import common.Constants;
import game.module.ClaimTerritories;
import game.module.SetUp;
import javafx.concurrent.Task;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

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

 //       2 * Constants.INIT_REINFORCEMENT_TURNS * (Constants.NUM_PLAYERS - 1)
        for (int i = 0; i < 5; i++) {
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
           currentPlayer.reinforce();
           if (currentPlayer.combat()) {
                shellModel.notify("Game over: " + currentPlayer.getName() + " has won the game.");
                break;
            }
            if (currentPlayer.hasCards()) {
                currentPlayer.cardUsage();
            }

           currentPlayer.fortify();

            playerModel.changeTurn();
        }
        return null;
    }

    // Game logic sequence
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}

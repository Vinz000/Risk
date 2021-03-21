package game;

import common.Constants;
import game.module.ClaimTerritories;
import game.module.SetUp;
import javafx.concurrent.Task;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

public class GameCore extends Task<Void> {

    private static final ShellModel shellModel = ShellModel.getInstance();
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

        for (int i = 0; i < 5; i++) {
            Player currentPlayer = playerModel.getCurrentPlayer();

            currentPlayer.startReinforcement();
            currentPlayer.initReinforce();

            playerModel.changeTurn();
        }

        setUp.selectFirstPlayer();

        while (true) {
            Player currentPlayer = playerModel.getCurrentPlayer();

            currentPlayer.startTurn();
            currentPlayer.reinforce();

            currentPlayer.combat();

            currentPlayer.fortify();

            playerModel.changeTurn();
        }
    }

    // Game logic sequence
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}

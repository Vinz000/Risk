package game;

import common.Constants;
import shell.model.ShellModel;
import shell.prompt.ShellPromptFactory;

import static common.Constants.NUM_NEUTRAL_PLAYERS;

public class GameCore {

    private static final ShellModel shellModel = ShellModel.getInstance();

    // Game logic sequence
    public static void start() {
        // TODO: Place following logic into a welcomePrompt
        ShellPromptFactory shellPromptFactory = new ShellPromptFactory();
        shellModel.notify(Constants.Notifications.WELCOME);
        shellModel.notify(Constants.Notifications.NAME + "(P1)");

        shellModel.prompt(shellPromptFactory.createPlayerOne());
        shellModel.prompt(shellPromptFactory.createPlayerTwo());
        shellModel.prompt(shellPromptFactory.drawTerritories());
        shellModel.prompt(shellPromptFactory.selectFirstPlayer());

        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1) * 9; i++) {
            shellModel.prompt(shellPromptFactory.beforeReinforcingCountry());
            shellModel.prompt(shellPromptFactory.reinforcingCountry());
        }

        shellModel.prompt(shellPromptFactory.selectFirstPlayer());
    }
}

package game;

import common.Constants;
import shell.prompt.ShellPromptFactory;
import shell.prompt.ShellPromptType;
import shell.model.ShellModel;

import static common.Constants.*;

public class GameCore {

    private static final ShellModel shellModel = ShellModel.getInstance();

    // Game logic sequence
    public static void start() {
        // TODO: Place following logic into a welcomePrompt
        ShellPromptFactory shellPromptFactory = new ShellPromptFactory();
        shellModel.notify(Constants.Notifications.WELCOME);
        shellModel.notify(Constants.Notifications.NAME + "(P1)");

        shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.GET_PLAYER_ONE));
        shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.GET_PLAYER_TWO));
        shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.DRAW_TERRITORIES));
        shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.SELECT_FIRST_PLAYER));

        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1) * 9; i++) {
            shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.BEFORE_REINFORCING_COUNTRY));
            shellModel.prompt(shellPromptFactory.getPrompt(ShellPromptType.REINFORCING_COUNTRY));
        }
    }
}

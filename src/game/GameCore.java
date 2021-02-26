package game;

import common.Constants;
import common.validation.Validators;
import shell.model.ShellModel;
import shell.prompt.ShellPrompt;
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

        // TODO: I would recommend commenting out all these prompts
        //       for debugging/testing purposes.
//        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1) * 9; i++) {
//            shellModel.prompt(shellPromptFactory.beforeReinforcingCountry());
//            shellModel.prompt(shellPromptFactory.reinforcingCountry());
//        }
//
//        shellModel.prompt(shellPromptFactory.selectFirstPlayer());

        shellModel.prompt(shellPromptFactory.enterGameLoop(GameCore::gameLoop));
    }

    // TODO: boys this is an example game loop, run
    //       the application and see how this behaves
    //       to help you develop this game loop going forward!
    //       (Then delete this comment)
    //       As per the new usual, create the prompts in the
    //       factory and prompt here, just like the GameCore.start method!
    private static boolean gameLoop() {

        // Determine whether or not the game
        // is over, if so, return FALSE. This will exit the game loop.
        if (isGameOver()) return false;

        shellModel.notify("New turn baby");
        shellModel.notify("Please enter something.");

        ShellPrompt first = new ShellPrompt(notification -> {
            shellModel.notify(notification);
            shellModel.notify("Thanks for entering something.");
            shellModel.notify("Next up, please enter yes or no...");
        }, Validators.nonEmpty);
        ShellPrompt second = new ShellPrompt(notification -> {
            shellModel.notify(notification);
            shellModel.notify("Thanks for entering yes or no!");
            shellModel.notify("That is all for this turn, moving on! (Press enter)");
        }, Validators.yesNo);

        shellModel.prompt(first);
        shellModel.prompt(second);

        return true;
    }

    private static boolean isGameOver() {
        // TODO: use models etc. to determine whether or
        //       not the game is over. This currently loops forever.
        return false;
    }
}

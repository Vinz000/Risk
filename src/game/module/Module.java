package game.module;

import javafx.application.Platform;
import map.model.MapModel;
import player.model.PlayerModel;
import shell.model.ShellModel;

public abstract class Module {
    protected ShellModel shellModel = ShellModel.getInstance();
    protected PlayerModel playerModel = PlayerModel.getInstance();
    protected MapModel mapModel = MapModel.getInstance();

    protected void uiAction(Runnable action) {
        Platform.runLater(action);
    }
}

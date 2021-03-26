package game.module;

import map.model.MapModel;
import player.model.PlayerModel;
import shell.model.ShellModel;

public abstract class Module {
    protected ShellModel shellModel = ShellModel.getInstance();
    protected PlayerModel playerModel = PlayerModel.getInstance();
    protected MapModel mapModel = MapModel.getInstance();
}

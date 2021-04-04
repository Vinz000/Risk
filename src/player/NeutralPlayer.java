package player;

import game.module.Reinforcing;
import javafx.scene.paint.Color;
import shell.model.ShellModel;

import static common.Constants.INIT_NEUTRAL_PLAYER_REINFORCEMENTS;

public class NeutralPlayer extends Player {

    public NeutralPlayer(String name, Color color) {
        super("Neutral player " + name, color);
    }

    @Override
    public void initReinforce() {
        Reinforcing reinforcing = new Reinforcing();
        reinforcing.reinforceInitialCountries(this, INIT_NEUTRAL_PLAYER_REINFORCEMENTS);
    }

    @Override
    public void startTurn() {

    }

    @Override
    public void startReinforcement() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.notify("Choose country owned by " + getName());
    }

    @Override
    public void reinforce() {

    }

    @Override
    public boolean combat() {
        return false;
    }

    @Override
    public void cardUsage() {

    }

    @Override
    public void fortify() {

    }
}

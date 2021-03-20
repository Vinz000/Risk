package player;

import game.module.Reinforcing;
import javafx.scene.paint.Color;
import static common.Constants.*;

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
    public void reinforce() {

    }

    @Override
    public void combat() {

    }

    @Override
    public void fortify() {

    }
}

package player;

import javafx.scene.paint.Color;

class PlayerAbstractTest extends Player{

    public PlayerAbstractTest(String name, Color color) throws IllegalArgumentException {
        super(name, color);
    }

    @Override
    public void startTurn() {

    }

    @Override
    public void startReinforcement() {

    }

    @Override
    public void initReinforce() {

    }

    @Override
    public void reinforce() {

    }

    @Override
    public boolean combat() {
        return false;
    }

    @Override
    public boolean cardUsage() {
        return false;
    }

    @Override
    public void fortify() {

    }

}

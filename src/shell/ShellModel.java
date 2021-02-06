package shell;

import common.Constants;
import map.MapComponent;

import java.util.Observable;

public class ShellModel extends Observable {
    private Player players[] = new Player[Constants.NUM_PLAYERS];

    public ShellModel() {
    }

}

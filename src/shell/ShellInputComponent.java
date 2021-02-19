package shell;

import common.Constants;
import javafx.scene.control.TextField;

import java.util.Observable;
import java.util.Observer;

public class ShellInputComponent extends TextField implements Observer {

    public ShellInputComponent() {
        setId(Constants.ComponentIds.SHELL_INPUT);

        ShellModel shellModel = ShellModel.getInstance();
        shellModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        if (updateArg.updateType.equals(ShellModelUpdateType.SET_INPUT_LINE)) {
            String inputText = (String) updateArg.arg;
            setText(inputText);

            // sets position of cursor at the end of line
            positionCaret(inputText.length());
        }
    }
}

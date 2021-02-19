package shell.component;

import common.Component;
import common.Constants;
import javafx.scene.control.TextField;
import shell.model.ShellModel;
import shell.model.ShellModelArg;
import shell.model.ShellModelUpdateType;

import java.util.Observable;
import java.util.Observer;

public class ShellInputComponent extends TextField implements Observer, Component {

    public ShellInputComponent() {
        setCssId();
        observe();
    }

    @Override
    public void build() {
    }

    @Override
    public void setCssId() {
        setId(Constants.ComponentIds.SHELL_INPUT);
    }

    @Override
    public void observe() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        if (updateArg.updateType.equals(ShellModelUpdateType.SET_INPUT_LINE)) {
            String inputText = (String) updateArg.arg;
            setText(inputText);

            // Sets position of cursor at the end of line
            positionCaret(inputText.length());
        }
    }
}

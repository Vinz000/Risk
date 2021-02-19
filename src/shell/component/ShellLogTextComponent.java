package shell.component;

import common.Component;
import common.Constants;
import javafx.scene.control.Label;
import shell.model.ShellModel;
import shell.model.ShellModelArg;
import shell.model.ShellModelUpdateType;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.SHELL_WIDTH;

public class ShellLogTextComponent extends Label implements Observer, Component {
    public ShellLogTextComponent() {
        setCssId();
        observe();
        build();
    }

    @Override
    public void build() {
        setWrapText(true);
        setMinWidth(SHELL_WIDTH - 20);
        setMaxWidth(SHELL_WIDTH - 20);
    }

    @Override
    public void setCssId() {
        setId(Constants.ComponentIds.LOG);
    }

    @Override
    public void observe() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        if (updateArg.updateType.equals(ShellModelUpdateType.NOTIFICATION)) {
            setText(getText() + "\n" + updateArg.arg);
        }
    }
}

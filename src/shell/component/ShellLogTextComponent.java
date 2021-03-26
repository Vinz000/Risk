package shell.component;

import common.BaseComponent;
import common.Constants;
import javafx.scene.control.Label;
import shell.model.ShellModel;
import shell.model.ShellModelArg;

import java.util.Observable;

import static common.Constants.SHELL_WIDTH;

public class ShellLogTextComponent extends Label {
    public ShellLogTextComponent() {
        BaseComponent.build(this::build, this::setCssId, this::modelsToObservable, this::update);
    }

    public void build() {
        setWrapText(true);
        setMinWidth(SHELL_WIDTH - 5);
        setMaxWidth(SHELL_WIDTH - 5);
    }

    public void setCssId() {
        setId(Constants.ComponentIds.LOG);
    }

    public Observable[] modelsToObservable() {
        return new Observable[]{
                ShellModel.getInstance()
        };
    }

    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        switch (updateArg.updateType) {
            case NOTIFICATION:
                setText(getText() + "\n" + updateArg.arg);
                break;
        }
    }
}

package shell;

import common.Constants;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class ShellLogComponent extends ScrollPane implements Observer {
    private final Label log = new Label();

    public ShellLogComponent() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.addObserver(this);

        // configurations for shellLogComponent
        setId(ComponentIds.SHELL_LOG);
        setMinSize(SHELL_WIDTH, MAP_HEIGHT - 60);
        setMaxSize(SHELL_WIDTH, MAP_HEIGHT - 60);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setContent(log);

        // configurations for log
        log.setId("log");
        log.setWrapText(true);
        log.setMinWidth(SHELL_WIDTH - 20);
        log.setMaxWidth(SHELL_WIDTH - 20);
        log.setId(Constants.ComponentIds.SHELL_LOG);

        // binds to the bottom of the log
        vvalueProperty().bind(log.heightProperty());

    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        if (updateArg.updateType.equals(ShellModelUpdateType.NOTIFICATION)) {
            log.setText(log.getText() + "\n" + updateArg.arg);
        }
    }
}

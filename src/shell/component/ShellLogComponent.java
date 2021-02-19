package shell.component;

import common.Component;
import javafx.scene.control.ScrollPane;

import static common.Constants.*;

public class ShellLogComponent extends ScrollPane implements Component {

    public ShellLogComponent() {
        setCssId();
        build();
    }

    @Override
    public void build() {
        ShellLogTextComponent logTextComponent = new ShellLogTextComponent();

        // Configurations for shellLogComponent
        setMinSize(SHELL_WIDTH, MAP_HEIGHT - 60);
        setMaxSize(SHELL_WIDTH, MAP_HEIGHT - 60);
        setHbarPolicy(ScrollBarPolicy.NEVER);

        setContent(logTextComponent);

        // Binds to the bottom of the log
        vvalueProperty().bind(logTextComponent.heightProperty());
    }

    @Override
    public void setCssId() {
        setId(ComponentIds.SHELL_LOG);
    }

    @Override
    public void observe() {
    }
}

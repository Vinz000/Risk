package shell.component;

import common.BaseComponent;
import javafx.scene.control.ScrollPane;

import static common.Constants.*;

public class ShellLogComponent extends ScrollPane {

    public ShellLogComponent() {
        BaseComponent.build(this::build, this::setCssId);
    }

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

    public void setCssId() {
        setId(ComponentIds.SHELL_LOG);
    }
}

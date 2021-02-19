package shell.component;

import common.Component;
import javafx.scene.layout.VBox;

import static common.Constants.*;

public class ShellComponent extends VBox implements Component {

    public ShellComponent() {
        setCssId();
        build();
    }

    @Override
    public void build() {
        ShellInputComponent inputComponent = new ShellInputComponent();
        ShellLogComponent logComponent = new ShellLogComponent();

        setSpacing(10);

        getChildren().addAll(logComponent, inputComponent);
    }

    @Override
    public void setCssId() {
        setId(ComponentIds.SHELL);
    }

    @Override
    public void observe() {
    }
}

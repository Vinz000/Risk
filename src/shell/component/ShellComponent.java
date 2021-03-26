package shell.component;

import common.BaseComponent;
import javafx.scene.layout.VBox;

import static common.Constants.ComponentIds;

public class ShellComponent extends VBox {

    public ShellComponent() {
        BaseComponent.build(this::build, this::setCssId);
    }

    public void build() {
        ShellInputComponent inputComponent = new ShellInputComponent();
        ShellLogComponent logComponent = new ShellLogComponent();
        setSpacing(10);
        getChildren().addAll(logComponent, inputComponent);
    }

    public void setCssId() {
        setId(ComponentIds.SHELL);
    }
}

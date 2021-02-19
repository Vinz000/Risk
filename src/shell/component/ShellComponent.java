package shell.component;

import common.Component;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import shell.model.ShellModel;
import shell.model.ShellModel.ShellPrompt;


import static common.Constants.*;

public class ShellComponent extends VBox implements Component {

    public ShellComponent() {
        build();
    }

    @Override
    public void build() {
        ShellModel shellModel = ShellModel.getInstance();
        ShellInputComponent inputComponent = new ShellInputComponent();
        ShellLogComponent logComponent = new ShellLogComponent();

        setSpacing(10);

        EventHandler<ActionEvent> onEnterPressed = event -> {

            String userInput = inputComponent.getText();
            ShellPrompt nextPrompt = shellModel.nextPrompt();

            // If there is a value in the queue...
            if (nextPrompt != null) {
                if (nextPrompt.validator.apply(userInput)) {
                    nextPrompt.handler.accept(userInput);
                } else {
                    shellModel.notify("Invalid Input.");
                    shellModel.retryPrompt(nextPrompt);
                }
            } else {
                shellModel.notify(Notifications.GG);
                inputComponent.setDisable(true);
            }

            inputComponent.clear();
        };

        inputComponent.setOnAction(onEnterPressed);

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

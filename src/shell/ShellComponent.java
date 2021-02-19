package shell;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;


import static common.Constants.*;

public class ShellComponent extends VBox {

    private final ShellLogComponent logComponent = new ShellLogComponent();
    private final ShellInputComponent inputComponent = new ShellInputComponent();

    public ShellComponent() {
        setId(ComponentIds.SHELL);

        build();
    }

    private void build() {
        ShellModel shellModel = ShellModel.getInstance();

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
}

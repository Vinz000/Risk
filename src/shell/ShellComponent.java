package shell;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;

import static common.Constants.*;

public class ShellComponent extends VBox implements Observer {

    private final Label log = new Label();
    private final TextField inputLine = new TextField();

    public ShellComponent(ShellModel shellModel) {
        super();
        build(shellModel);
    }

    public void appendLogText(String message) {
        log.setText(log.getText() + "\n" + message);
    }

    private void build(ShellModel shellModel) {

        setId(ComponentIds.SHELL);

        log.setId(ComponentIds.SHELL_LOG);

        log.setWrapText(true);
        log.setMinWidth(SHELL_WIDTH);
        log.setMaxWidth(SHELL_WIDTH);
        setVgrow(log, Priority.ALWAYS);

        EventHandler<ActionEvent> onEnterPressed = event -> {

            String userInput = inputLine.getText();
            ShellPrompt nextPrompt = shellModel.nextPrompt();

            // If there is a value in the queue...
            if (nextPrompt != null) {
                if (nextPrompt.validator.apply(userInput)) {
                    nextPrompt.handler.accept(userInput);
                } else {
                    appendLogText("Invalid input, please try again.");
                    shellModel.retryPrompt(nextPrompt);
                }
            } else {
                appendLogText(Notifications.GG);
                inputLine.setDisable(true);
            }

            inputLine.clear();
        };
        inputLine.setOnAction(onEnterPressed);
        inputLine.setId(ComponentIds.SHELL_INPUT);

        getChildren().addAll(log, inputLine);
        setAlignment(Pos.BOTTOM_RIGHT);
    }

    @Override
    public void update(Observable o, Object arg) {

        // [arg] is a notification.
        if (arg instanceof String) {
            appendLogText((String) arg);
        }
    }


}

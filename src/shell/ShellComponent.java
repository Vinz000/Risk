package shell;

import common.Constants;
import game.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;


public class ShellComponent extends VBox implements Observer {

    private final Label log = new Label(Constants.Prompts.WELCOME);
    private final TextField inputLine = new TextField();
    private GameState gameState = GameState.NEW;

    public ShellComponent(ShellModel shellModel) {
        super();
        build(shellModel);
    }

    private void appendLogText(String message) {
        log.setText(log.getText() + "\n" + message);
    }

    private void build(ShellModel shellModel) {

        log.setAlignment(Pos.BOTTOM_LEFT);
        log.setWrapText(true);
        log.setMinWidth(Constants.SHELL_WIDTH);
        log.setMaxWidth(Constants.SHELL_WIDTH);
        setVgrow(log, Priority.ALWAYS);

        EventHandler<ActionEvent> eventHandler = event -> {

            switch (gameState) {
                case NEW:
                    appendLogText(Constants.Prompts.NAME + "(Player one)");
                    break;
                case PLAYER_ONE_NAME:
                    appendLogText("Nice to meet you, " + inputLine.getText());
                    appendLogText(Constants.Prompts.NAME + "(Player two)");
                    break;
                case PLAYER_TWO_NAME:
                    appendLogText("And you, " + inputLine.getText());
                    appendLogText("Lets get started!");

                    // TODO Allocate territories

                    break;

                default:
                    System.out.println("Should never be reached");
            }

            // Move to next game state
            gameState = gameState.next();
            inputLine.clear();
        };

        inputLine.setOnAction(eventHandler);
        inputLine.setMaxWidth(Constants.SHELL_WIDTH);

        getChildren().add(log);
        getChildren().add(inputLine);
        setAlignment(Pos.BOTTOM_LEFT);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModel shellModel = ((ShellModel) o);

        appendLogText("ShellComponent update");
    }
}

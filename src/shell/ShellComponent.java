package shell;

import common.Constants;
import game.GameRoot;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;

public class ShellComponent extends BorderPane implements Observer {
    public ShellComponent(ShellModel shellModel) {
        super();
        build(shellModel);
    }

    private void build(ShellModel shellModel) {

        TextField input = new TextField();
        Label prompts = new Label("Player 1. " + Constants.Prompts.NAME);
        prompts.setPrefSize(200, 20);
        Label log = new Label();

        EventHandler<ActionEvent> event = event1 -> {

            if (shellModel.getPlayer1() == null) {
                shellModel.setPlayer1(input.getText());
                prompts.setText("Player 2. " + Constants.Prompts.NAME);
            } else if (shellModel.getPlayer2() == null) {
                shellModel.setPlayer2(input.getText());
                prompts.setText(Constants.Prompts.OPTION);
            } else {
                log.setText(log.getText() + "\n" + input.getText());
                prompts.setText(Constants.Prompts.OPTION);
            }
            input.clear();
            System.out.print(log.getText());

        };

        input.setOnAction(event);

        VBox shell = new VBox();
        shell.getChildren().add(log);
        shell.getChildren().add(prompts);
        shell.getChildren().add(input);
        VBox.setVgrow(log, Priority.ALWAYS);
        log.setAlignment(Pos.BOTTOM_LEFT);
        shell.setAlignment(Pos.BOTTOM_LEFT);
        setCenter(new GameRoot());
        setLeft(shell);
        setMargin(shell, new Insets(5));
        shell.prefWidth(200);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModel shellModel = ((ShellModel) o);

    }
}

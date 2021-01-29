package shell;

import common.Constants;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.ArrayList;

public class UserIO extends Application {

    public static final ArrayList<String> playerNames = new ArrayList<>(2);
    public static  final ArrayList<String> logger = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Log");

        TextField a = new TextField(Constants.Prompts.NAME);

        TilePane r = new TilePane();

        Label l = new Label();

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(playerNames.size() < 2){
                    playerNames.add(a.getText());
                }
                logger.add(l.getText());
                l.setText(a.getText());
                a.clear();
                System.out.print(playerNames);
            }
        };

    a.setOnAction(event);

    r.getChildren().add(a);
    r.getChildren().add(l);

    Scene sc = new Scene(r, 200, 200);

    primaryStage.setScene(sc);

    primaryStage.show();
    }
}

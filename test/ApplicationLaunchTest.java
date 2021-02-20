//import game.GameComponent;
//import game.GameCore;
//import javafx.application.Application;
//import javafx.beans.InvalidationListener;
//import javafx.scene.Scene;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.input.KeyCode;
//import javafx.stage.Stage;
//import org.junit.Test;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.testfx.api.FxRobot;
//import org.testfx.api.FxToolkit;
//import org.testfx.framework.junit5.ApplicationTest;
//import org.testfx.service.query.NodeQuery;
//import shell.component.ShellInputComponent;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static common.Constants.*;
//import static org.junit.Assert.fail;
//
//public class ApplicationLaunchTest extends FxRobot {
//
//    public static class DemoApplication extends Application {
//
//        @Override
//        public void start(Stage primaryStage) {
//
//            CountDownLatch setSceneLatch = new CountDownLatch(1);
//            InvalidationListener invalidationListener = observable -> setSceneLatch.countDown();
//            primaryStage.sceneProperty().addListener(observable -> {
//                setSceneLatch.countDown();
//                primaryStage.sceneProperty().removeListener(invalidationListener);
//            });
//
//            GameComponent gameComponent = new GameComponent();
//            Scene gameScene = new Scene(
//                    gameComponent,
//                    MAP_WIDTH + SHELL_WIDTH,
//                    MAP_HEIGHT
//            );
//
//            gameScene.getStylesheets().add(Paths.STYLE_SHEET);
//            primaryStage.getIcons().add(new Image(Paths.ICON));
//            primaryStage.setMinWidth(MAP_WIDTH + SHELL_WIDTH);
//            primaryStage.setMinHeight(MAP_HEIGHT);
//            primaryStage.setResizable(false);
//            primaryStage.setTitle("Risk");
//            primaryStage.setScene(gameScene);
//
//            try {
//                if (!setSceneLatch.await(10, TimeUnit.SECONDS)) {
//                    fail("Timeout");
//                }
//            } catch (Exception ex) {
//                fail("Unexpected Exception: " + ex);
//            }
//
//            primaryStage.show();
//            GameCore.start();
//        }
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//        ApplicationTest.launch(DemoApplication.class);
//    }
//
//    @AfterEach
//    void cleanUp() throws Exception {
//        FxToolkit.cleanupStages();
//    }
//
//    @Test
//    public void happyPathTest() {
//
//        NodeQuery shellComponentQuery = robotContext().getNodeFinder().lookup(shellComponentNode ->
//                shellComponentNode.toString().equals("ShellComponent[id=shell-component]")
//        );
//
//        shellComponentQuery.tryQueryAs(ShellInputComponent.class).ifPresent(node -> {
//
//            node.getChildrenUnmodifiable().forEach(node1 -> {
//                if (node1.toString().equals("TextField[id=input-line, styleClass=text-input text-field]")) {
//                    robotContext().getWriteRobot().write(TestingVariables.player1);
//                    Assertions.assertEquals(TestingVariables.player1, ((TextField) node1).getText());
//                    robotContext().getTypeRobot().push(KeyCode.ENTER);
//                    robotContext().getWriteRobot().write(TestingVariables.player2);
//                    Assertions.assertEquals(TestingVariables.player2, ((TextField) node1).getText());
//                    robotContext().getTypeRobot().push(KeyCode.ENTER);
//                    robotContext().getWriteRobot().write(TestingVariables.no);
//                    Assertions.assertEquals(TestingVariables.no, ((TextField) node1).getText());
//                    robotContext().getTypeRobot().push(KeyCode.ENTER);
//                    robotContext().getTypeRobot().push(KeyCode.ENTER);
//                }
//            });
//        });
//    }
//}
package Controller;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.Stack;

public class NavigationManager {
    private static NavigationManager instance;
    private final Stage primaryStage;
    private final BorderPane rootLayout;
    private final Stack<Node> viewStack;

    private NavigationManager(Stage stage) {
        this.primaryStage = stage;
        this.rootLayout = new BorderPane();
        this.viewStack = new Stack<>();
        Scene scene = new Scene(rootLayout, 1000, 800);
        primaryStage.setScene(scene);
    }

    public static NavigationManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new NavigationManager(stage);
        }
        return instance;
    }

    public void navigateTo(Node view) {
        viewStack.push(rootLayout.getCenter());
        rootLayout.setCenter(view);
    }

    public void goBack() {
        if (!viewStack.isEmpty()) {
            rootLayout.setCenter(viewStack.pop());
        }
    }

    public void setInitialView(Node view) {
        rootLayout.setCenter(view);
        viewStack.clear();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
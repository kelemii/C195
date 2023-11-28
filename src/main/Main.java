package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Help.JDBC;

/**
 * starts the application
 */
public class Main extends Application {

    /**
     * starts app
     * @param stage sets stage
     * @throws Exception if error
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
        stage.setTitle("Login");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    /**
     * starts app, opens connection to db
     * @param args starts app
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
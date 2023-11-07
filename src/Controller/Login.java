package Controller;


import DAO.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class Login implements Initializable {
    Stage stage;
    private static int currentUserID = 0;
    @FXML
    private TextField UserName, password;
    @FXML private Label timeZoneLabel;
    private UserDAO loginForm = new UserDAO();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneId systemTimeZone = ZoneId.systemDefault();
        String timeZoneString = systemTimeZone.getId();
        timeZoneLabel.setText(timeZoneString);
        System.out.println("initialized login");
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String user = UserName.getText();
        String pwd = password.getText();
        currentUserID = loginForm.checkLogin(user, pwd);
        if (currentUserID > 0) {
            //open main page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/Main.fxml"));
            Parent root = loader.load();
            stage = (Stage) UserName.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Main");
            stage.show();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The username or password you entered is incorrect.");

            alert.showAndWait();
        }

    }

    public void exitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}

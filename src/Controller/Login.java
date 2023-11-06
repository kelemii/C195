package Controller;

import DAO.userDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class Login implements Initializable {
    private static int currentUserID = 0;
    @FXML
    private TextField UserName, password;
    @FXML private Label timeZoneLabel;
    private userDAOImpl loginForm = new userDAOImpl();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneId systemTimeZone = ZoneId.systemDefault();
        String timeZoneString = systemTimeZone.getId();
        timeZoneLabel.setText(timeZoneString);
        System.out.println("initialized login");
    }

    public void handleLogin(ActionEvent actionEvent) {
        String user = UserName.getText();
        String pwd = password.getText();
        currentUserID = loginForm.checkLogin(user, pwd);
        if (currentUserID > 0) {
            //open main page
            System.out.println("success");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The username or password you entered is incorrect.");

            alert.showAndWait();
        }
//        System.out.println("button clicked");
//        System.out.println(UserName.getText());
//        System.out.println(password.getText());

    }

    public void exitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}

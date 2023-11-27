package Controller;


import DAO.UserDAO;
import Help.AppointmentAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import static Help.JDBC.connection;

public class Login implements Initializable {
    Stage stage;
    private static int currentUserID = 0;
    public static String currentUserName;
    @FXML
    private TextField UserName, password;
    @FXML
    private Label timeZoneLabel, AppName, location;
    @FXML
    private Button LoginBtn, ExitBtn;
    private UserDAO loginForm = new UserDAO();
    ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/login", Locale.getDefault());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneId systemTimeZone = ZoneId.systemDefault();
        String timeZoneString = systemTimeZone.getId();
        timeZoneLabel.setText(timeZoneString);
        resourceBundle = ResourceBundle.getBundle("lang/login", Locale.getDefault());
        AppName.setText(resourceBundle.getString("Scheduling_Application"));
        UserName.setPromptText(resourceBundle.getString("username"));
        password.setPromptText(resourceBundle.getString("password"));
        LoginBtn.setText(resourceBundle.getString("Login"));
        ExitBtn.setText(resourceBundle.getString("Exit"));
        location.setText(resourceBundle.getString("Location"));
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException, SQLException {
        String user = UserName.getText();
        String pwd = password.getText();
        currentUserID = loginForm.checkLogin(user, pwd);

        if (currentUserID > 0) {
            // Log successful login attempt
            logLoginActivity(user, true);

            // Open the main page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/Main.fxml"));
            Parent root = loader.load();
            stage = (Stage) UserName.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Main");
            stage.show();
            AppointmentAlert appointmentAlert = new AppointmentAlert();
            appointmentAlert.checkUpcomingAppointments();
        } else {
            // Log failed login attempt
            logLoginActivity(user, false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The username or password you entered is incorrect.");

            alert.showAndWait();
        }
    }

    private void logLoginActivity(String username, boolean success) {
        String logFileName = "login_activity.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String status = success ? "SUCCESS" : "FAILURE";
            String logEntry = String.format("%s - Username: %s, Status: %s%n", timestamp, username, status);
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void exitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}

package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import home.homeApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
        @FXML
        private Button loginButton;
        @FXML
        private JFXCheckBox rememberCheckBox;
        @FXML
        private Button closeButton;
        @FXML
        private Label messageField;
        @FXML
        private TextField usernameField;
        @FXML
        private TextField passwordField;
        @FXML
        private BorderPane loginPane;


        public void loginButtonOnAction(ActionEvent event) {
            if (usernameField.getText().isBlank()) {
                messageField.setText("username missing");
            } else if (passwordField.getText().isBlank()) {
                messageField.setText("password missing");
            } else {
                checkUser();
            }
        }

        public void checkUser() {
            try (Connection con = DatabaseConnect.getConnect()) {
                PreparedStatement admin_query = con.prepareStatement("select * from user_account" +
                        "    right join admin a on user_account.user_id = a.user_id" +
                        "    and user_account.user = ? and user_account.password = ?");
                admin_query.setString(1, usernameField.getText());
                admin_query.setString(2, passwordField.getText());
                ResultSet resultSet = admin_query.executeQuery();
                if (resultSet.next() && resultSet.getString("user") != null) {
                    if (rememberCheckBox.isSelected()) {
                        UserSession.createUserSession(usernameField.getText());
                    }
                    loadHome(); //admin rights
                    showAlert(usernameField.getText());
                    return;
                } else {
                    PreparedStatement emp_query = con.prepareStatement("select * from user_account" +
                            " right join employee a on user_account.user_id = a.user_id" +
                            " and user_account.user =  ? and user_account.password = ?");
                    emp_query.setString(1, usernameField.getText());
                    emp_query.setString(2, passwordField.getText());
                    resultSet = emp_query.executeQuery();
                }
                if (resultSet.next() && resultSet.getString("user") != null) {
                    if (rememberCheckBox.isSelected()) {
                        UserSession.createUserSession(usernameField.getText());
                    }
                    loadHome();
                    showAlert(usernameField.getText());
                } else {
                    messageField.setText("Invalid Credentials!");
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        }

        public void closeButtonOnAction(ActionEvent event) {
            Stage s = (Stage) closeButton.getScene().getWindow();
            s.close();
        }


        public void loadHome() throws IOException {
            homeApp homeApp = new homeApp();
            if (closeButton != null) {
                closeButton.fire();
            }
            homeApp.start(new Stage());
        }

        public void showAlert(String user) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Message");
            alert.setHeaderText("You logged in as "+ user.toUpperCase());
            alert.setContentText("Welcome " + user.toUpperCase());
            alert.showAndWait();
        }


}

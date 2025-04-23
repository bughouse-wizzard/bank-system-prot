package bank.system.prot.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import bank.system.prot.service.AuthService;

import java.sql.Connection;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            AuthService authService = new AuthService(connection);
            boolean success = authService.register(username, password);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful!", ButtonType.OK);
                alert.showAndWait();

                // Закрываем окно регистрации
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Registration failed. Username might already exist.", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        // Закрываем окно регистрации
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
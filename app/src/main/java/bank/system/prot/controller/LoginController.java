package bank.system.prot.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import bank.system.prot.db.Database;
import bank.system.prot.service.AuthService;

import java.sql.Connection;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            AuthService authService = new AuthService(connection); // Используем переданное соединение
            int userId = authService.loginAndGetUserId(username, password);

            if (userId > 0) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Scene scene = new Scene(loader.load());

                DashboardController dashboardController = loader.getController();
                dashboardController.initialize(connection, userId); // Передаем соединение и ID пользователя

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load());

            RegisterController registerController = loader.getController();
            registerController.setConnection(connection); // Передаем соединение в контроллер

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
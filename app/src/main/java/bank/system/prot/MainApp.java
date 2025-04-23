package bank.system.prot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import bank.system.prot.db.Database;
import bank.system.prot.controller.LoginController;

import java.sql.Connection;

public class MainApp extends Application {
    private static Connection connection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        connection = Database.getConnection(); // Создаем соединение один раз

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController loginController = loader.getController();
        loginController.setConnection(connection); // Передаем соединение в контроллер

        primaryStage.setTitle("Bank System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close(); // Закрываем соединение при завершении приложения
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package bank.system.prot.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;

import bank.system.prot.service.AccountService;
import bank.system.prot.service.AuthService;

import java.math.BigDecimal;
import java.sql.Connection;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import bank.system.prot.model.Account;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, Integer> accountIdColumn;
    @FXML private TableColumn<Account, BigDecimal> balanceColumn;
    @FXML private TextField toUsernameField;
    @FXML private TextField amountField;
    @FXML private TextField depositAmountField;

    private AccountService accountService;
    private AuthService authService;
    private Connection connection;
    private int userId;
    private String username;

    public void initialize(Connection connection, int userId) {
        this.connection = connection;
        this.accountService = new AccountService(connection);
        this.authService = new AuthService(connection);
        this.userId = userId;
        this.username = authService.getUsernameById(userId); // Получаем имя пользователя

        // Настройка колонок таблицы
        accountIdColumn.setCellValueFactory(cellData -> cellData.getValue().accountIdProperty().asObject());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());

        // Загрузка данных пользователя
        loadAccounts();
    }

    private void loadAccounts() {
        ObservableList<Account> accounts = FXCollections.observableArrayList(accountService.getAccountsByUserId(userId));
        accountsTable.setItems(accounts);
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    private void handleTransfer() {
        try {
            String toUsername = toUsernameField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());

            int toUserId = authService.getUserIdByUsername(toUsername);
            if (toUserId == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Recipient username not found!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            boolean success = accountService.transfer(userId, toUserId, amount);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transfer successful!", ButtonType.OK);
                alert.showAndWait();
                loadAccounts();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Transfer failed!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeposit() {
        try {
            BigDecimal amount = new BigDecimal(depositAmountField.getText());
            boolean success = accountService.deposit(userId, amount);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Deposit successful!", ButtonType.OK);
                alert.showAndWait();
                loadAccounts();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Deposit failed!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            LoginController loginController = loader.getController();
            loginController.setConnection(connection); // Передаем соединение

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
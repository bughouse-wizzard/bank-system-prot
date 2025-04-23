package bank.system.prot.service;

import bank.system.prot.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class AccountService {
    private final Connection connection;

    public AccountService(Connection connection) {
        this.connection = connection;
    }

    public boolean createAccount(int userId) {
        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transfer(int fromUserId, int toUserId, BigDecimal amount) {
        String findAccountSql = "SELECT id FROM accounts WHERE user_id = ? AND balance >= ? LIMIT 1";
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        String transactionSql = "INSERT INTO transactions (from_account_id, to_account_id, amount) VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            // Найти счет отправителя с достаточным балансом
            int fromAccountId;
            try (PreparedStatement findStmt = connection.prepareStatement(findAccountSql)) {
                findStmt.setInt(1, fromUserId);
                findStmt.setBigDecimal(2, amount);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    fromAccountId = rs.getInt("id");
                } else {
                    System.out.println("Недостаточно средств или счет отправителя не найден.");
                    connection.rollback();
                    return false;
                }
            }

            // Найти счет получателя
            int toAccountId;
            try (PreparedStatement findStmt = connection.prepareStatement(findAccountSql)) {
                findStmt.setInt(1, toUserId);
                findStmt.setBigDecimal(2, BigDecimal.ZERO); // Получателю не нужен минимальный баланс
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    toAccountId = rs.getInt("id");
                } else {
                    System.out.println("Счет получателя не найден.");
                    connection.rollback();
                    return false;
                }
            }

            // Снять средства со счета отправителя
            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawSql)) {
                withdrawStmt.setBigDecimal(1, amount);
                withdrawStmt.setInt(2, fromAccountId);
                int rowsUpdated = withdrawStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("Ошибка при снятии средств.");
                    connection.rollback();
                    return false;
                }
            }

            // Зачислить средства на счет получателя
            try (PreparedStatement depositStmt = connection.prepareStatement(depositSql)) {
                depositStmt.setBigDecimal(1, amount);
                depositStmt.setInt(2, toAccountId);
                int rowsUpdated = depositStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("Ошибка при зачислении средств.");
                    connection.rollback();
                    return false;
                }
            }

            // Записать транзакцию
            try (PreparedStatement transactionStmt = connection.prepareStatement(transactionSql)) {
                transactionStmt.setInt(1, fromAccountId);
                transactionStmt.setInt(2, toAccountId);
                transactionStmt.setBigDecimal(3, amount);
                transactionStmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean deposit(int userId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            System.out.println("Пополнение счета: userId=" + userId + ", amount=" + amount); // Логируем данные
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated); // Логируем результат
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Account> getAccountsByUserId(int userId) {
        String sql = "SELECT id, balance FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("id");
                BigDecimal balance = rs.getBigDecimal("balance");
                accounts.add(new Account(accountId, balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }
}
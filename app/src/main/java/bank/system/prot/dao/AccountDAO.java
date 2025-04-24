package bank.system.prot.dao;

import bank.system.prot.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createAccount(int userId) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
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
        }
        return accounts;
    }

    public boolean updateBalance(int accountId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, accountId);
            return stmt.executeUpdate() > 0;
        }
    }
}
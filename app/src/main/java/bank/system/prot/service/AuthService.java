package bank.system.prot.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthService {
    private final Connection connection;

    public AuthService(Connection connection) {
        this.connection = connection;
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword.startsWith("$2a$")) {
            // Оригинальный формат BCrypt
            return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
        } else {
            // Base64-формат
            String decodedHash = new String(java.util.Base64.getDecoder().decode(hashedPassword));
            return BCrypt.verifyer().verify(plainPassword.toCharArray(), decodedHash).verified;
        }
    }

    public boolean register(String username, String password) {
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertUserSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        String createAccountSql = "INSERT INTO accounts (user_id, balance) VALUES (?, 0)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Пользователь с именем " + username + " уже существует.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            String hashedPassword = hashPassword(password);
            insertStmt.setString(1, username);
            insertStmt.setString(2, hashedPassword);
            insertStmt.executeUpdate();

            // Получаем ID нового пользователя
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);

                // Создаем аккаунт для нового пользователя
                try (PreparedStatement accountStmt = connection.prepareStatement(createAccountSql)) {
                    accountStmt.setInt(1, userId);
                    accountStmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int loginAndGetUserId(String username, String password) {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String hash = rs.getString("password");
                if (verifyPassword(password, hash)) {
                    return rs.getInt("id"); // Возвращаем ID пользователя
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Возвращаем -1, если вход не удался
    }

    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
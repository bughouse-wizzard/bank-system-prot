package bank.system.prot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class Database {
    private Database() {
        // Private constructor to prevent instantiation
    }
    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) return connection;

        try (InputStream input = Database.class.getResourceAsStream("/db/db.properties")) {
            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
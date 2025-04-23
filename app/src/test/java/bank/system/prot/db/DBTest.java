package bank.system.prot.db;

import bank.system.prot.db.Database;
import java.sql.*;

public class DBTest {
    public static void main(String[] args) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString("username"));
        }
    }
}
package za.co.wethinkcode.ticketing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Database() {
    }

    public static Connection connect(String path) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:student_registration.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                admission_number TEXT NOT NULL UNIQUE,
                full_name TEXT NOT NULL,
                email TEXT NOT NULL,
                course TEXT NOT NULL,
                gender TEXT NOT NULL
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
}
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

interface CrudRepository<T> {
    void add(T item) throws SQLException;
    List<T> findAll() throws SQLException;
    List<T> search(String keyword) throws SQLException;
    void update(T item) throws SQLException;
    void deleteById(int id) throws SQLException;
}

public class StudentRepository implements CrudRepository<Student> {

    @Override
    public void add(Student student) throws SQLException {
        String sql = "INSERT INTO students (admission_number, full_name, email, course, gender) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getAdmissionNumber());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getCourse());
            pstmt.setString(5, student.getGender());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, admission_number, full_name, email, course, gender FROM students ORDER BY id DESC";
        List<Student> students = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        }
        return students;
    }

    @Override
    public List<Student> search(String keyword) throws SQLException {
        String sql = """
            SELECT id, admission_number, full_name, email, course, gender
            FROM students
            WHERE admission_number LIKE ? OR full_name LIKE ? OR course LIKE ?
            ORDER BY id DESC
            """;
        List<Student> students = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }
        }
        return students;
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = """
            UPDATE students
            SET admission_number = ?, full_name = ?, email = ?, course = ?, gender = ?
            WHERE id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getAdmissionNumber());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getCourse());
            pstmt.setString(5, student.getGender());
            pstmt.setInt(6, student.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("admission_number"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("course"),
            rs.getString("gender")
        );
    }
}
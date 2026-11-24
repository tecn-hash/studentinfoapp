import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        StudentRepository repository = new StudentRepository();

        SwingUtilities.invokeLater(() -> {
            StudentRegistrationForm form = new StudentRegistrationForm(repository);
            form.setVisible(true);
        });
    }
}
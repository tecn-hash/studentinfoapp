import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class StudentRegistrationForm extends JFrame {
    private final StudentRepository repository;

    private final JTextField admissionField;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JComboBox<String> courseCombo;
    private final JComboBox<String> genderCombo;
    private final JTextField searchField;

    private final DefaultTableModel tableModel;
    private final JTable studentTable;

    private Integer selectedStudentId = null;

    public StudentRegistrationForm(StudentRepository repository) {
        super("Integrated Java Student Registration System");
        this.repository = repository;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        admissionField = new JTextField(18);
        nameField = new JTextField(18);
        emailField = new JTextField(18);

        courseCombo = new JComboBox<>(new String[]{
            "BSc. Software Engineering",
            "BSc. Computer Science",
            "BSc. Information Technology",
            "Diploma in ICT"
        });

        genderCombo = new JComboBox<>(new String[]{
            "Male", "Female", "Other"
        });

        searchField = new JTextField(22);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Admission No.", "Full Name", "Email", "Course", "Gender"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterTablePanel(), BorderLayout.CENTER);
        add(buildBottomSearchPanel(), BorderLayout.SOUTH);

        attachEvents();
        refreshTable(null);
    }

    private JPanel buildTopPanel() {
        JPanel container = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Registration Form"));

        formPanel.add(new JLabel("Admission Number:"));
        formPanel.add(admissionField);

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseCombo);

        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderCombo);

        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        JButton registerBtn = new JButton("Register");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        registerBtn.addActionListener(e -> registerStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        clearBtn.addActionListener(e -> clearForm());

        actionPanel.add(registerBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(clearBtn);

        container.add(formPanel, BorderLayout.CENTER);
        container.add(actionPanel, BorderLayout.SOUTH);
        return container;
    }

    private JPanel buildCenterTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registered Students"));
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Search & View Records"));

        JButton searchBtn = new JButton("Search");
        JButton viewAllBtn = new JButton("View All");

        searchBtn.addActionListener(e -> refreshTable(searchField.getText().trim()));
        viewAllBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable(null);
        });

        panel.add(new JLabel("Keyword (Adm No / Name / Course):"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(viewAllBtn);
        return panel;
    }

    private void attachEvents() {
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentTable.getSelectedRow();
                if (row != -1) {
                    selectedStudentId = (Integer) tableModel.getValueAt(row, 0);
                    admissionField.setText(tableModel.getValueAt(row, 1).toString());
                    nameField.setText(tableModel.getValueAt(row, 2).toString());
                    emailField.setText(tableModel.getValueAt(row, 3).toString());
                    courseCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                    genderCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                }
            }
        });
    }

    private boolean validateInputs() {
        if (admissionField.getText().trim().isEmpty()
            || nameField.getText().trim().isEmpty()
            || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Admission Number, Full Name, and Email are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private Student buildStudentFromForm(Integer id) {
        String adm = admissionField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseCombo.getSelectedItem().toString();
        String gender = genderCombo.getSelectedItem().toString();

        return (id == null)
            ? new Student(adm, name, email, course, gender)
            : new Student(id, adm, name, email, course, gender);
    }

    private void registerStudent() {
        if (!validateInputs()) {
            return;
        }
        try {
            repository.add(buildStudentFromForm(null));
            JOptionPane.showMessageDialog(this, "Student registered successfully.");
            clearForm();
            refreshTable(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while registering student: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a student row from the table first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!validateInputs()) {
            return;
        }
        try {
            repository.update(buildStudentFromForm(selectedStudentId));
            JOptionPane.showMessageDialog(this, "Student record updated successfully.");
            clearForm();
            refreshTable(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while updating record: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a student row from the table to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this student record?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                repository.deleteById(selectedStudentId);
                JOptionPane.showMessageDialog(this, "Student record deleted.");
                clearForm();
                refreshTable(null);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database error while deleting record: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void clearForm() {
        selectedStudentId = null;
        admissionField.setText("");
        nameField.setText("");
        emailField.setText("");
        courseCombo.setSelectedIndex(0);
        genderCombo.setSelectedIndex(0);
        studentTable.clearSelection();
    }

    private void refreshTable(String searchKeyword) {
        try {
            List<Student> students = (searchKeyword == null || searchKeyword.isEmpty())
                ? repository.findAll()
                : repository.search(searchKeyword);

            tableModel.setRowCount(0);
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getAdmissionNumber(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getCourse(),
                    s.getGender()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading student records: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
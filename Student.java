public class Student extends Person {
    private int id;
    private String admissionNumber;
    private String course;

    public Student(String admissionNumber, String fullName, String email, String course, String gender) {
        super(fullName, email, gender);
        this.admissionNumber = admissionNumber;
        this.course = course;
    }

    public Student(int id, String admissionNumber, String fullName, String email, String course, String gender) {
        this(admissionNumber, fullName, email, course, gender);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public String getRoleSummary() {
        return "Student [" + admissionNumber + "] - " + getFullName() + " (" + course + ")";
    }

    @Override
    public String toString() {
        return getRoleSummary();
    }
}
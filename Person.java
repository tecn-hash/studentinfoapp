public abstract class Person {
    private String fullName;
    private String email;
    private String gender;

    public Person(String fullName, String email, String gender) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Abstract method enforced on subclasses
    public abstract String getRoleSummary();

    @Override
    public String toString() {
        return fullName + " (" + gender + ", " + email + ")";
    }
}
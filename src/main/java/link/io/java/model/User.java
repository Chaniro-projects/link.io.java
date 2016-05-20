package link.io.java.model;

public class User {
    private String name;
    private String firstName;
    private String role;
    private String mail;
    private String id;

    public User() {}

    public User(String name, String firstName, String role, String mail, String id) {
        this.name = name;
        this.firstName = firstName;
        this.role = role;
        this.mail = mail;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

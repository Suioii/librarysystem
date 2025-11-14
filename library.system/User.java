package librarysystem;

public class User {
    private int memberId;
    private String name;
    private String email;
    private String role; // "LIBRARIAN" or "MEMBER"
    private boolean isActive;
    
    public User(int memberId, String name, String email, String role, boolean isActive) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }
    
    // Getters
    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    
    public boolean isLibrarian() {
        return "LIBRARIAN".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "', role=" + role + "}";
    }
}
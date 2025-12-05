package librarysystem;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private long loginTime;
    private boolean isActive;
    
    private SessionManager() {
        this.isActive = false;
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    
    public void startSession(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
        this.isActive = true;
        System.out.println("Session started for: " + user.getName() + " (" + user.getRole() + ")");
    }
    
    
    public void endSession() {
        if (currentUser != null) {
            System.out.println("Session ended for: " + currentUser.getName());
        }
        this.currentUser = null;
        this.loginTime = 0;
        this.isActive = false;
    }
    
   
    public User getCurrentUser() {
        return currentUser;
    }
    
    
    public boolean isLoggedIn() {
        return isActive && currentUser != null;
    }
    
    
    public boolean isLibrarian() {
        return isLoggedIn() && currentUser.isLibrarian();
    }
    
    
    public boolean isMember() {
        return isLoggedIn() && !currentUser.isLibrarian();
    }
    
    
    public long getSessionDurationMinutes() {
        if (!isActive) return 0;
        return (System.currentTimeMillis() - loginTime) / (1000 * 60);
    }
    
  
    public void validateAccess(String requiredRole) {
        if (!isLoggedIn()) {
            throw new SecurityException("Access denied: Please log in first.");
        }
        
        if ("LIBRARIAN".equals(requiredRole) && !isLibrarian()) {
            throw new SecurityException("Access denied: Librarian privileges required.");
        }
    }
    
    
    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "No active session";
        }
        
        return String.format("User: %s | Role: %s | Duration: %d min", 
            currentUser.getName(), 
            currentUser.getRole(), 
            getSessionDurationMinutes());
    }
    
   
    public boolean isSessionExpired() {
        return getSessionDurationMinutes() > 480;
    }

}

package librarysystem;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private long loginTime;
    private boolean isActive;
    
    // Private constructor for singleton pattern
    private SessionManager() {
        this.isActive = false;
    }
    
    // Get the single instance of SessionManager
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Start a new session when user logs in
     */
    public void startSession(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
        this.isActive = true;
        System.out.println("Session started for: " + user.getName() + " (" + user.getRole() + ")");
    }
    
    /**
     * End the current session when user logs out
     */
    public void endSession() {
        if (currentUser != null) {
            System.out.println("Session ended for: " + currentUser.getName());
        }
        this.currentUser = null;
        this.loginTime = 0;
        this.isActive = false;
    }
    
    /**
     * Get the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return isActive && currentUser != null;
    }
    
    /**
     * Check if current user is a librarian
     */
    public boolean isLibrarian() {
        return isLoggedIn() && currentUser.isLibrarian();
    }
    
    /**
     * Check if current user is a member
     */
    public boolean isMember() {
        return isLoggedIn() && !currentUser.isLibrarian();
    }
    
    /**
     * Get session duration in minutes
     */
    public long getSessionDurationMinutes() {
        if (!isActive) return 0;
        return (System.currentTimeMillis() - loginTime) / (1000 * 60);
    }
    
    /**
     * Validate that user has required role for access
     */
    public void validateAccess(String requiredRole) {
        if (!isLoggedIn()) {
            throw new SecurityException("Access denied: Please log in first.");
        }
        
        if ("LIBRARIAN".equals(requiredRole) && !isLibrarian()) {
            throw new SecurityException("Access denied: Librarian privileges required.");
        }
    }
    
    /**
     * Get session information for display
     */
    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "No active session";
        }
        
        return String.format("User: %s | Role: %s | Duration: %d min", 
            currentUser.getName(), 
            currentUser.getRole(), 
            getSessionDurationMinutes());
    }
    
    /**
     * Force logout if session is too long (basic security)
     */
    public boolean isSessionExpired() {
        // 8 hour session limit
        return getSessionDurationMinutes() > 480;
    }
}
package service.auth;

/**
 * Simple in-memory auth context to hold access and refresh tokens for the running app.
 * Thread-safe setters/getters for basic usage by ApiClient and login code.
 */
public class AuthContext {
    private static final AuthContext INSTANCE = new AuthContext();

    private volatile String accessToken;
    private volatile String refreshToken;
    private volatile String accountType;

    private AuthContext() {}

    public static AuthContext getInstance() {
        return INSTANCE;
    }

    public synchronized void setTokens(String accessToken, String refreshToken, String accountType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accountType = accountType;
    }

    public synchronized void clear() {
        this.accessToken = null;
        this.refreshToken = null;
        this.accountType = null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccountType() {
        return accountType;
    }
}

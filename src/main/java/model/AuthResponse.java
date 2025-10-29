package model;

/**
 * POJO để deserialize response xác thực từ backend.
 * Backend trả về đối tượng có các trường: accountType, accessToken, refreshToken
 */
public class AuthResponse {
    private String accountType;
    private String accessToken;
    private String refreshToken;

    public AuthResponse() {}

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

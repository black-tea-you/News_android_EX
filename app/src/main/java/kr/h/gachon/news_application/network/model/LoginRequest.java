package kr.h.gachon.news_application.network.model;

/**
 * Retrofit @Body 로 전달할 로그인 요청 DTO
 */
public class LoginRequest {
    private String userName;
    private String password;

    public LoginRequest() { }

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

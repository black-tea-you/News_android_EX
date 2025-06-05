package kr.h.gachon.news_application.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * { "accessToken": "eyJhbGciOi..." } 형태로 내려오는 응답 매핑용 DTO
 */

//로그인 성공 시 토큰 정보를 받아오기 위한 Model
public class LoginResponse {
    @SerializedName("accessToken")
    private String accessToken;

    public LoginResponse() { }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

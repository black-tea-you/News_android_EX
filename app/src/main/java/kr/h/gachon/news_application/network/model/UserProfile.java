package kr.h.gachon.news_application.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserProfile {
    @SerializedName("id")
    private Long id;

    @SerializedName("userName")
    private String userName;

    @SerializedName("email")
    private String email;

    /** 백엔드가 profile JSON에 포함하도록 구현했다면 */
    @SerializedName("keywords")
    private List<String> keywords;

    public UserProfile() { }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}

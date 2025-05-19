package kr.h.gachon.news_application.repository;

import android.content.Context;
import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.LoginRequest;
import kr.h.gachon.news_application.network.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {
    private final RetrofitRepository api;

    public AuthRepository(Context ctx) {
        this.api = RetrofitClient.getApiService(ctx);
    }

    /** 로그인 호출 */
    public void login(String userName, String password, Callback<LoginResponse> cb) {
        api.login(new LoginRequest(userName, password))
                .enqueue(cb);
    }
}

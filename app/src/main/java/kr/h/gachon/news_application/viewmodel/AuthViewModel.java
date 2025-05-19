package kr.h.gachon.news_application.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import kr.h.gachon.news_application.network.model.LoginResponse;
import kr.h.gachon.news_application.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo;

    private final MutableLiveData<LoginResponse> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginError   = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application app) {
        super(app);
        repo = new AuthRepository(app);
    }

    /** 성공 시 토큰 DTO */
    public LiveData<LoginResponse> getLoginSuccess() {
        return loginSuccess;
    }
    /** 실패 시 에러 메시지 */
    public LiveData<String> getLoginError() {
        return loginError;
    }

    public void login(String userName, String password) {
        repo.login(userName, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    loginSuccess.postValue(res.body());
                } else {
                    loginError.postValue("로그인 실패: 서버 응답 코드 " + res.code());
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginError.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }
}

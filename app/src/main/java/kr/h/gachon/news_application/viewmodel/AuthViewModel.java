package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import kr.h.gachon.news_application.network.model.LoginResponse;
import kr.h.gachon.news_application.repository.AuthRepository;
import kr.h.gachon.news_application.network.model.RegisterRequest; // 필요시
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo;

    // 로그인 결과
    private final MutableLiveData<LoginResponse> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String>        loginError   = new MutableLiveData<>();

    // 회원가입 결과
    private final MutableLiveData<String> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> registerError   = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application app) {
        super(app);
        repo = new AuthRepository(app);
    }

    // 로그인 LiveData
    public LiveData<LoginResponse> getLoginSuccess() { return loginSuccess; }
    public LiveData<String>        getLoginError()   { return loginError;   }

    // 회원가입 LiveData
    public LiveData<String> getRegisterSuccess() { return registerSuccess; }
    public LiveData<String> getRegisterError()   { return registerError;   }

    /** 로그인 요청 */
    public void login(String userName, String password) {
        repo.login(userName, password, new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    loginSuccess.postValue(res.body());
                } else {
                    loginError.postValue("로그인 실패: 서버 응답 코드 " + res.code());
                }
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginError.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }

    /** 회원가입 요청 */
    public void register(String userName, String password, String email) {
        repo.register(userName, password, email, new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> res) {
                if (res.isSuccessful() && res.body() != null) {
                    registerSuccess.postValue(res.body());
                } else {
                    String errMsg = "회원가입 실패: 코드 " + res.code();
                    try {
                        if (res.errorBody() != null) {
                            errMsg = res.errorBody().string(); // 서버 메시지 읽기
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                    registerError.postValue(errMsg);
                }
            }
            @Override public void onFailure(Call<String> call, Throwable t) {
                registerError.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }

}

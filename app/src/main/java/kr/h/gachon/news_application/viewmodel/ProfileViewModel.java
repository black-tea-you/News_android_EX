package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kr.h.gachon.news_application.repository.UserProfileRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private final UserProfileRepository repo;

    private final MutableLiveData<List<String>> keywords = new MutableLiveData<>();
    private final MutableLiveData<String> resultMsg = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application app) {
        super(app);
        repo = new UserProfileRepository(app);
    }

    public LiveData<List<String>> getKeywords()   { return keywords; }
    /** null이면 성공, 메시지 있으면 토스트용 에러 */
    public LiveData<String>       getResultMsg()  { return resultMsg; }

    public void loadKeywords() {
        repo.fetchKeywords(new Callback<List<String>>() {
            @Override public void onResponse(Call<List<String>> c, Response<List<String>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    keywords.postValue(r.body());
                    resultMsg.postValue(null);
                } else {
                    resultMsg.postValue("키워드 조회 실패: " + r.code());
                }
            }
            @Override public void onFailure(Call<List<String>> c, Throwable t) {
                resultMsg.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void addKeyword(String kw) {
        repo.addKeyword(kw, new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful()) {
                    resultMsg.postValue(null);
                    loadKeywords();
                } else {
                    resultMsg.postValue("등록 실패: " + r.code());
                }
            }
            @Override public void onFailure(Call<Void> c, Throwable t) {
                resultMsg.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void deleteKeyword(String kw) {
        repo.deleteKeyword(kw, new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful()) {
                    resultMsg.postValue(null);
                    loadKeywords();
                } else {
                    resultMsg.postValue("삭제 실패: " + r.code());
                }
            }
            @Override public void onFailure(Call<Void> c, Throwable t) {
                resultMsg.postValue("네트워크 오류: " + t.getMessage());
            }
        });
    }
}

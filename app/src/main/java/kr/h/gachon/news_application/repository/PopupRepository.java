package kr.h.gachon.news_application.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.News;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupRepository {

    /** 팝업용 뉴스 리스트 **/
    private final MutableLiveData<List<News>> popupList = new MutableLiveData<>();

    /** 에러 메시지 **/
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final RetrofitRepository api;

    public PopupRepository(Context context) {
        this.api = RetrofitClient.getApiService(context);
    }

    public LiveData<List<News>> getPopupList() {
        return popupList;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 1) GET /popup 호출
     *    성공 시 popupList.postValue(response.body());
     *    실패 시 error.postValue(메시지);
     */
    public void fetchPopupNews() {
        api.getPopupNews()
                .enqueue(new Callback<List<News>>() {
                    @Override
                    public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            popupList.postValue(response.body());
                        } else {
                            error.postValue("팝업 뉴스 조회 실패: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<News>> call, Throwable t) {
                        error.postValue("네트워크 오류: " + t.getMessage());
                    }
                });
    }
}

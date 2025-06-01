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

public class NewsRepository {

    private final MutableLiveData<List<News>> headlines = new MutableLiveData<>();
    private final MutableLiveData<String> error     = new MutableLiveData<>();
    private final RetrofitRepository api;

    // ★ Context를 받아서 RetrofitService를 생성
    public NewsRepository(Context context) {
        this.api = RetrofitClient.getApiService(context);
    }

    public LiveData<List<News>> getHeadlines() {
        return headlines;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchHeadlines() {
        api.getLatestHeadlines()
                .enqueue(new Callback<List<News>>() {
                    @Override
                    public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            headlines.postValue(response.body());
                        } else {
                            error.postValue("Error: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<News>> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }


}

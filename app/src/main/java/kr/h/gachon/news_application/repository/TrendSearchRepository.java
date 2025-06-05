package kr.h.gachon.news_application.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.TrendSearchDTO;
import kr.h.gachon.news_application.network.model.TrendSearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendSearchRepository {

    /** 트렌드 검색 결과(TrendSearchDTO 리스트) **/
    private final MutableLiveData<List<TrendSearchDTO>> trendingNews = new MutableLiveData<>();

    /** 에러 메시지를 담을 LiveData **/
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final RetrofitRepository api;

    public TrendSearchRepository(Context context) {
        this.api = RetrofitClient.getApiService(context);
    }

    /** 외부에서 관찰할 LiveData getter **/
    public LiveData<List<TrendSearchDTO>> getTrendingNews() {
        return trendingNews;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 1) startDate, endDate: "YYYY-MM-DD" 형식 문자열
     *    topK: 상위 기사 개수
     *
     *    성공 시 trendingNews.postValue(TrendSearchResult.getNews());
     *    실패 시 error.postValue(메시지);
     */
    public void fetchTrendSearch(String startDate, String endDate, int topK) {
        api.getTrendSearch(startDate, endDate, topK)
                .enqueue(new Callback<TrendSearchResult>() {
                    @Override
                    public void onResponse(Call<TrendSearchResult> call, Response<TrendSearchResult> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TrendSearchResult result = response.body();
                            // 뉴스 객체 리스트만 꺼내서 LiveData에 게시
                            trendingNews.postValue(result.getNews());
                        } else {
                            error.postValue("트렌드 검색 실패: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TrendSearchResult> call, Throwable t) {
                        error.postValue("네트워크 오류: " + t.getMessage());
                    }
                });
    }
}

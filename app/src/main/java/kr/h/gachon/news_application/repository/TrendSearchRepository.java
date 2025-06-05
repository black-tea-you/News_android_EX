// package kr.h.gachon.news_application.repository;

package kr.h.gachon.news_application.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.TrendSearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendSearchRepository {

    // 서버에서 받은 전체 래퍼 JSON(TrendSearchResponse)을 담을 LiveData
    private final MutableLiveData<TrendSearchResult> fullResponse = new MutableLiveData<>();

    // 에러 메시지를 담는 LiveData
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final RetrofitRepository api;

    public TrendSearchRepository(Context context) {
        this.api = RetrofitClient.getApiService(context);
    }

    /** 외부에서 observe할 수 있도록 getter 제공 **/
    public LiveData<TrendSearchResult> getFullResponse() {
        return fullResponse;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 서버에 트렌드 검색 요청을 보내고, 결과를 LiveData에 게시
     * @param startDate  "YYYY-MM-DD"
     * @param endDate    "YYYY-MM-DD"
     * @param topK       상위 몇 개 뉴스까지 가져올지
     */
    public void fetchTrendSearch(String startDate, String endDate, int topK) {
        api.getTrendSearch(startDate, endDate, topK).enqueue(new Callback<TrendSearchResult>() {
            @Override
            public void onResponse(Call<TrendSearchResult> call, Response<TrendSearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullResponse.postValue(response.body());
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

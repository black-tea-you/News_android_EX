package kr.h.gachon.news_application.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.SearchResult;
import kr.h.gachon.news_application.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {

    private static final String TAG = "SearchRepository";

    private final Context context;

    // 1) 초기값을 빈 리스트로 설정
    private final MutableLiveData<List<SearchResult>> searchResults =
            new MutableLiveData<>(Collections.emptyList());

    // 2) 에러 메시지를 담을 LiveData
    private final MutableLiveData<String> error = new MutableLiveData<>();

    // 3) RetrofitRepository 인스턴스
    private final RetrofitRepository api;

    public SearchRepository(Context context) {
        this.context = context.getApplicationContext();
        this.api = RetrofitClient.getApiService(context);
    }

    /** 검색 결과를 외부에서 관찰할 LiveData */
    public LiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }

    /** 에러 메시지를 외부에서 관찰할 LiveData */
    public LiveData<String> getError() {
        return error;
    }

    /**
     * 카테고리 + 키워드 + topK로 검색 요청을 보냅니다.
     * 응답이 오면 searchResults에, 실패하면 error에 값을 담아줍니다.
     */
    public void fetchSearchResults(String category, String keyword, int topK) {

        String token = TokenManager.getInstance(context).getToken();
        Log.d("SearchRepository", "fetchSearchResults 호출, 토큰: " + token);

        api.searchByKeyword(category, keyword, topK)
                .enqueue(new Callback<List<SearchResult>>() {
                    @Override
                    public void onResponse(
                            Call<List<SearchResult>> call,
                            Response<List<SearchResult>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 서버에서 내려준 리스트를 그대로 postValue
                            searchResults.postValue(response.body());
                        } else {
                            String msg = "Error code: " + response.code();
                            Log.e(TAG, "fetchSearchResults() 실패 - " + msg);
                            error.postValue(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                        String msg = "Network fail: " + t.getMessage();
                        Log.e(TAG, "fetchSearchResults() onFailure - " + msg);
                        error.postValue(msg);
                    }
                });
    }
}

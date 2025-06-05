// package kr.h.gachon.news_application.viewmodel;

package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kr.h.gachon.news_application.network.model.TrendSearchResult;
import kr.h.gachon.news_application.repository.TrendSearchRepository;

public class TrendSearchViewModel extends AndroidViewModel {

    private final TrendSearchRepository repository;
    private final LiveData<TrendSearchResult> fullResponse;
    private final LiveData<String> errorMsg;

    public TrendSearchViewModel(@NonNull Application application) {
        super(application);
        repository = new TrendSearchRepository(application.getApplicationContext());
        fullResponse = repository.getFullResponse();
        errorMsg = repository.getError();
    }

    /** Activity/Fragment에서 호출할 수 있도록 expose **/
    public LiveData<TrendSearchResult> getFullResponse() {
        return fullResponse;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void fetchTrendSearch(String startDate, String endDate, int topK) {
        repository.fetchTrendSearch(startDate, endDate, topK);
    }
}

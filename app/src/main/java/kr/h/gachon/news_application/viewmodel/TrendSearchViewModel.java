package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import kr.h.gachon.news_application.network.model.TrendSearchDTO;
import kr.h.gachon.news_application.repository.TrendSearchRepository;

public class TrendSearchViewModel extends AndroidViewModel {

    private final TrendSearchRepository repository;
    private final LiveData<List<TrendSearchDTO>> trendingNews;
    private final LiveData<String> errorMsg;

    public TrendSearchViewModel(@NonNull Application application) {
        super(application);
        repository = new TrendSearchRepository(application.getApplicationContext());
        trendingNews = repository.getTrendingNews();
        errorMsg = repository.getError();
    }

    public LiveData<List<TrendSearchDTO>> getTrendingNews() {
        return trendingNews;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    /**
     * startDate, endDate 형식: "YYYY-MM-DD"
     */
    public void fetchTrendSearch(String startDate, String endDate, int topK) {
        repository.fetchTrendSearch(startDate, endDate, topK);
    }
}

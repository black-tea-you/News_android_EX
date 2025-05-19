package kr.h.gachon.news_application.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.repository.NewsRepository;
import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private final NewsRepository repo;
    private final LiveData<List<News>> headlines;
    private final LiveData<String>    error;

    public NewsViewModel(@NonNull Application app) {
        super(app);
        // Application을 Context로 넘겨 줌
        repo = new NewsRepository(app);
        headlines = repo.getHeadlines();
        error     = repo.getError();
    }

    public LiveData<List<News>> getHeadlines() {
        return headlines;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadHeadlines() {
        repo.fetchHeadlines();
    }
}

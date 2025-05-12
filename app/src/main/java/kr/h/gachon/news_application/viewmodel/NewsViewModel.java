package kr.h.gachon.news_application.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.repository.NewsRepository;

public class NewsViewModel extends ViewModel {
    private final NewsRepository repo = new NewsRepository();

    public LiveData<List<News>> getHeadlines() {
        return repo.getHeadlines();
    }

    public LiveData<String> getError() {
        return repo.getError();
    }

    public void loadHeadlines() {
        repo.fetchHeadlines(); // country/category 필요 없음
    }
}


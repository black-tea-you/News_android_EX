package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import kr.h.gachon.news_application.network.model.SearchResult;   // ViewModel → UI 모델
import kr.h.gachon.news_application.repository.SearchRepository;

public class SearchViewModel extends AndroidViewModel {

    private final SearchRepository searchRepository;
    // UI에 노출할 LiveData
    private final LiveData<List<SearchResult>> searchResults;
    private final LiveData<String> errorMessage;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        // Repository 생성 시 Application Context를 넘김
        searchRepository = new SearchRepository(application);

        // Repository의 LiveData를 그대로 가져옴
        searchResults = searchRepository.getSearchResults();
        errorMessage = searchRepository.getError();
    }

    /** 카테고리+키워드+topK로 검색 실행 */
    public void search(String category, String keyword, int topK) {
        searchRepository.fetchSearchResults(category, keyword, topK);
    }

    /** UI에서 관찰할 검색 결과 LiveData */
    public LiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }

    /** UI에서 관찰할 에러 메시지 LiveData */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}

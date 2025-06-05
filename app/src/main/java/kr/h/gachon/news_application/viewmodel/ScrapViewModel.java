package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kr.h.gachon.news_application.network.model.News; // network.model.News
import kr.h.gachon.news_application.repository.ScrapRepository;

public class ScrapViewModel extends AndroidViewModel {

    private final ScrapRepository repository;

    /** UI에서 구독할 LiveData **/
    private final LiveData<List<News>> scrapList;
    private final LiveData<String> errorMsg;

    // 내부에서 사용하는 MutableLiveData도 필요하다면 직접 참조 가능
    private final MutableLiveData<String> internalError;

    public ScrapViewModel(@NonNull Application application) {
        super(application);
        repository = new ScrapRepository(application.getApplicationContext());

        // Repository가 관리하는 LiveData를 그대로 전달
        scrapList = repository.getScrapList();
        errorMsg = repository.getError();

        internalError = new MutableLiveData<>();
    }

    /** 외부(액티비티/프래그먼트)에서 참조할 Getter **/
    public LiveData<List<News>> getScrapList() {
        return scrapList;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    /**
     * 1) 스크랩된 뉴스 전체 목록을 가져와서 LiveData에 게시
     */
    public void fetchScraps() {
        repository.fetchScraps();
    }

    /**
     * 2) 특정 뉴스 스크랩 추가
     *    성공/실패 여부는 listener로 알림 → 필요시 UI쪽에서 토스트나 버튼 상태 업데이트 처리
     */
    public void addScrap(Long newsId, final OnRequestCompleteListener listener) {
        repository.addScrap(newsId, new ScrapRepository.OnRequestCompleteListener() {
            @Override
            public void onComplete(boolean success) {
                listener.onComplete(success);
            }
        });
    }

    /**
     * 3) 특정 뉴스 스크랩 삭제
     */
    public void deleteScrap(Long newsId, final OnRequestCompleteListener listener) {
        repository.deleteScrap(newsId, new ScrapRepository.OnRequestCompleteListener() {
            @Override
            public void onComplete(boolean success) {
                listener.onComplete(success);
            }
        });
    }

    /** ViewModel을 호출하는 쪽에 성공/실패 결과를 알려줄 인터페이스 **/
    public interface OnRequestCompleteListener {
        void onComplete(boolean success);
    }
}

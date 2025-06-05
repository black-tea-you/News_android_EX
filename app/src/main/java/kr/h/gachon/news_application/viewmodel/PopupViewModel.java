// package kr.h.gachon.news_application.viewmodel;
package kr.h.gachon.news_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.repository.PopupRepository;


//추후 로그인 액티비티 끝나고 실행 가능하도록 넣어주면 됨
public class PopupViewModel extends AndroidViewModel {
    private final PopupRepository repository;
    private final LiveData<List<News>> popupList;
    private final LiveData<String> errorMsg;

    public PopupViewModel(@NonNull Application application) {
        super(application);
        repository = new PopupRepository(application.getApplicationContext());
        popupList = repository.getPopupList();
        errorMsg = repository.getError();
    }

    public LiveData<List<News>> getPopupList() {
        return popupList;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void fetchPopupNews() {
        repository.fetchPopupNews();
    }
}

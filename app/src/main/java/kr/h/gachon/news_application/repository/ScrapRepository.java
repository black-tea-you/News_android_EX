package kr.h.gachon.news_application.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.News;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrapRepository {

    /** 스크랩된 기사 리스트를 담을 LiveData **/
    private final MutableLiveData<List<News>> scrapList = new MutableLiveData<>();

    /** 에러 메시지를 담을 LiveData **/
    private final MutableLiveData<String> error = new MutableLiveData<>();

    /** Retrofit 인터페이스 (RetrofitRepository)에 선언된 scrap 관련 메서드 사용 **/
    private final RetrofitRepository api;

    // 생성자에서 Context를 받아, RetrofitClient를 통해 RetrofitRepository 구현체 생성
    public ScrapRepository(Context context) {
        this.api = RetrofitClient.getApiService(context);
    }

    /** 외부(뷰모델 등)에서 관찰할 수 있도록 getter 제공 **/
    public LiveData<List<News>> getScrapList() {
        return scrapList;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 1) 로그인된 사용자 기준으로 “내가 스크랩한 뉴스 전체”를 가져오는 메서드
     *    GET /api/scrap
     *    결과가 성공적일 경우 scrapList에 게시하고, 실패 시 error에 메시지 게시
     */
    public void fetchScraps() {
        api.getScraps()
                .enqueue(new Callback<List<News>>() {
                    @Override
                    public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            scrapList.postValue(response.body());
                        } else {
                            error.postValue("스크랩 목록 조회 실패: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<News>> call, Throwable t) {
                        error.postValue("네트워크 오류: " + t.getMessage());
                    }
                });
    }

    /**
     * 2) 특정 뉴스(newsId)를 “스크랩”하는 메서드
     *    POST /api/scrap/{newsId}
     *    성공 여부는 callback으로 알려주고, 내부 에러 메시지는 error LiveData에 게시
     */
    public void addScrap(Long newsId, final OnRequestCompleteListener listener) {
        api.addScrap(newsId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // 추가에 성공하면 listener.onComplete(true)
                            listener.onComplete(true);
                        } else {
                            // 서버가 4xx/5xx 등으로 실패했을 때
                            error.postValue("스크랩 추가 실패: HTTP " + response.code());
                            listener.onComplete(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        error.postValue("네트워크 오류: " + t.getMessage());
                        listener.onComplete(false);
                    }
                });
    }

    /**
     * 3) 특정 뉴스(newsId)를 “스크랩 취소”하는 메서드
     *    DELETE /api/scrap/{newsId}
     *    성공 여부는 callback으로 알려주고, 내부 에러 메시지는 error LiveData에 게시
     */
    public void deleteScrap(Long newsId, final OnRequestCompleteListener listener) {
        api.deleteScrap(newsId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            listener.onComplete(true);
                        } else {
                            error.postValue("스크랩 취소 실패: HTTP " + response.code());
                            listener.onComplete(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        error.postValue("네트워크 오류: " + t.getMessage());
                        listener.onComplete(false);
                    }
                });
    }

    /**
     * 네트워크 요청 성공/실패를 알려줄 콜백 인터페이스
     */
    public interface OnRequestCompleteListener {
        void onComplete(boolean success);
    }
}
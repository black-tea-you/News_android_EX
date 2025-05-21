package kr.h.gachon.news_application.repository;

import android.content.Context;

import java.util.List;

import kr.h.gachon.news_application.network.RetrofitClient;
import kr.h.gachon.news_application.network.RetrofitRepository;
import kr.h.gachon.news_application.network.model.KeywordRequest;
import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileRepository {
    private final RetrofitRepository api;

    public UserProfileRepository(Context ctx) {
        api = RetrofitClient.getApiService(ctx);
    }

    /** 서버의 GET /api/profile/keywords 호출 */
    public void fetchKeywords(Callback<List<String>> cb) {
        api.getKeywords().enqueue(cb);
    }

    /** POST /api/profile/keywordAdd */
    public void addKeyword(String keyword, Callback<Void> cb) {
        api.addKeyword(new KeywordRequest(keyword)).enqueue(cb);
    }

    /** DELETE /api/profile/keywordDelete?keyword=… */
    public void deleteKeyword(String keyword, Callback<Void> cb) {
        api.deleteKeyword(keyword).enqueue(cb);
    }
}

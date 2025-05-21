package kr.h.gachon.news_application.network;

import java.util.List;

import kr.h.gachon.news_application.network.model.KeywordRequest;
import kr.h.gachon.news_application.network.model.LoginRequest;
import kr.h.gachon.news_application.network.model.LoginResponse;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.network.model.UserProfile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitRepository{

    // 최신 기사 리스트 (예: /api/news/headline)
    @GET("/api/news/headline")
    Call<List<News>> getLatestHeadlines();

    // 검색 기반 크롤링 (예: /api/news/search?lstcode=0020&start=2025-05-10&end=2025-05-12)
    @GET("/api/news/search")
    Call<List<News>> searchNews(
            @Query("lstcode") String lstcode,
            @Query("start") String start,
            @Query("end") String end
    );

    // 전체 DB 기사 (테스트용)
    @GET("/api/news/all")
    Call<List<News>> getAllArticles();


    /** 로그인 */
    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest body);


    /**===============프로필 관련 코드=============*/

    /** 등록된 키워드 전체 가져오기 */
    @GET("/api/profile/keywords")
    Call<List<String>> getKeywords();

    /** 키워드 추가 */
    @POST("/api/profile/keywordAdd")
    Call<Void> addKeyword(@Body KeywordRequest body);

    /** 키워드 삭제 */
    @DELETE("/api/profile/keywordDelete")
    Call<Void> deleteKeyword(@Query("keyword") String keyword);

    /**===============프로필 관련 코드=============*/
}

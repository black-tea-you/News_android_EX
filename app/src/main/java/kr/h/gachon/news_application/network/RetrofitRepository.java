package kr.h.gachon.news_application.network;

import java.util.List;

import kr.h.gachon.news_application.network.model.News;
import retrofit2.Call;
import retrofit2.http.GET;
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
}

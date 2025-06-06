package kr.h.gachon.news_application.network;

import java.util.List;

import kr.h.gachon.news_application.network.model.KeywordRequest;
import kr.h.gachon.news_application.network.model.LoginRequest;
import kr.h.gachon.news_application.network.model.LoginResponse;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.network.model.RegisterRequest;
import kr.h.gachon.news_application.network.model.SearchResult;
import kr.h.gachon.news_application.network.model.TrendSearchResult;
import kr.h.gachon.news_application.network.model.UserProfile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    /** 회원가입 */
    @POST("/api/register")
    Call<String> register(@Body RegisterRequest body);


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

    // 1) 로그인된 유저가 스크랩한 뉴스 전체 리스트 조회
    //    GET /api/scrap
    @GET("/api/scrap")
    Call<List<News>> getScraps();

    // 2) 스크랩 추가 (로그인된 유저 기준으로 해당 newsId 스크랩)
    //    POST /api/scrap/{newsId}
    @POST("/api/scrap/{newsId}")
    Call<Void> addScrap(@Path("newsId") Long newsId);

    // 3) 스크랩 삭제 (로그인된 유저 기준으로 해당 newsId 스크랩 제거)
    //    DELETE /api/scrap/{newsId}
    @DELETE("/api/scrap/{newsId}")
    Call<Void> deleteScrap(@Path("newsId") Long newsId);

    /**===============프로필 관련 코드=============*/

    //키워드 검색 15개 반환해주고 여기서 Date 기준으로 정렬하면 과거순/최신순 정렬 가능
    @GET("/api/news/search-by-keyword")
    Call<List<SearchResult>> searchByKeyword(
            @Query("category") String category,
            @Query("keyword") String keyword,
            @Query("topK") int topK
    );

    
    /*트렌드 검색 날짜 지정*/

    /** 3) 트렌드 검색(키워드 + 기사 리스트) **/
    // GET /trend_search?start=YYYY-MM-DD&end=YYYY-MM-DD&topK=5
    @GET("api/keyword/trend_search")
    Call<TrendSearchResult> getTrendSearch(
            @Query("start") String startDate,   // 예: "2025-05-01"
            @Query("end")   String endDate,     // 예: "2025-05-05"
            @Query("topK")  int topK
    );

    /** 4) 팝업용 뉴스(유저별) **/
    // GET /popup
    @GET("api/keyword/popup")
    Call<List<News>> getPopupNews();
}

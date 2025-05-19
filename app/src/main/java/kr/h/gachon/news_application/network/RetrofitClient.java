package kr.h.gachon.news_application.network;

import android.content.Context;
import kr.h.gachon.news_application.util.TokenManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:10000/";
    private static Retrofit retrofit;

    private static Retrofit getRetrofit(Context ctx) {
        if (retrofit == null) {
            // (1) 로깅 인터셉터
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // (2) 토큰 자동 헤더 추가 인터셉터
            AuthInterceptor authInterceptor =
                    new AuthInterceptor(TokenManager.getInstance(ctx));

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /** 로그인 등 모든 API 호출을 위한 서비스 제공 */
    public static RetrofitRepository getApiService(Context ctx) {
        return getRetrofit(ctx).create(RetrofitRepository.class);
    }
}


package kr.h.gachon.news_application.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//retrofit2 json 파일 형식을 받아오기 위해서 network 연결을 retrofit2 library를 사용함
public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:10000/";

    private static Retrofit retrofit;

    public static RetrofitRepository getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(RetrofitRepository.class);
    }
}

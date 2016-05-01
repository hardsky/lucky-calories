package io.swagger.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.auth.ApiKeyAuth;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * In current Retrofit 2.0.2 'Interceptors' collection in OkHttp is unmodified (can set only during construction).
 * So generated client with athorization is not working.
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 01.05.16.
 */
public class LuckyCaloriesApiClient {
    public static LuckyCaloriesApi createClient(String access_token){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();

        //String baseUrl = "http://localhost:4003/api/v1";
        String baseUrl = "http://10.0.2.2:4003/api/v1"; //from emulator to host computer (localhost)
        if(!baseUrl.endsWith("/"))
            baseUrl = baseUrl + "/";


        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        if(access_token != null){
            ApiKeyAuth authInterceptor = new ApiKeyAuth("header", "access_token");
            authInterceptor.setApiKey(access_token);

            okHttpBuilder.addInterceptor(authInterceptor);
        }

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(okHttpBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonCustomConverterFactory.create(gson))
                .build();

        return retrofit.create(LuckyCaloriesApi.class);
    }
}

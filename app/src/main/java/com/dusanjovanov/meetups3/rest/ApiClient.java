package com.dusanjovanov.meetups3.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by duca on 29/12/2016.
 */

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.7/api/v1/";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("authorization", "801bb1e7ebfc127b17fb1330335701c2")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            })
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    private static final MeetupsApi api = retrofit.create(MeetupsApi.class);

    public static MeetupsApi getApi(){
        return api;
    }

}

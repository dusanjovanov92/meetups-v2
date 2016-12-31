package com.dusanjovanov.meetups3.rest;

import com.dusanjovanov.meetups3.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by duca on 29/12/2016.
 */

public interface MeetupsApi {
    @GET("users/search/{query}")
    Call<ArrayList<User>> searchUsers(@Path("query") String query);

    @FormUrlEncoded
    @POST("users")
    Call<Void> createUser(@Field("display_name") String displayName,
                              @Field("email") String email,
                              @Field("photo_url") String photoUrl,
                              @Field("token") String token);
}

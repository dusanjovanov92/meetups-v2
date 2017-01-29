package com.dusanjovanov.meetups3.rest;

import com.dusanjovanov.meetups3.models.Contact;
import com.dusanjovanov.meetups3.models.Group;
import com.dusanjovanov.meetups3.models.MeetingResponse;
import com.dusanjovanov.meetups3.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by duca on 29/12/2016.
 */

public interface MeetupsApi {
    @GET("users/getByEmail/{email}")
    Call<User> getUser(@Path("email")String email);

    @GET("users/search/{query}")
    Call<ArrayList<User>> searchUsers(@Path("query") String query);

    @FormUrlEncoded
    @POST("users")
    Call<User> createUser(@Field("display_name") String displayName,
                              @Field("email") String email,
                              @Field("photo_url") String photoUrl,
                              @Field("token") String token);

    @GET("users/{id_user}/groups")
    Call<ArrayList<Group>> getGroups(@Path("id_user") int idUser);

    @GET("users/{id_user}/contacts")
    Call<ArrayList<Contact>> getContacts(@Path("id_user") int idUser);

    @GET("users/{id_user}/requests")
    Call<String> getRequests(@Path("id_user") int idUser);

    @FormUrlEncoded
    @POST("users/{id_user1}/contacts/{id_user2}")
    Call<Void> addToContacts(@Path("id_user1")int receivingUser, @Path("id_user2") int sendingUser,
                             @Field("firebase_node") String firebaseNode);

    @DELETE("users/{id_user1}/contacts/{id_user2}")
    Call<Void> deleteContact(@Path("id_user1") int idUser1, @Path("id_user2") int idUser2);

    @GET("users/{id_user1}/relationship/{id_user2}")
    Call<String> getRelationship(@Path("id_user1")int idUser1,@Path("id_user2") int idUser2);

    @POST("users/{id_user1}/contactRequests/{id_user2}")
    Call<Void> sendContactRequest(@Path("id_user1") int idUser1, @Path("id_user2") int idUser2);

    @DELETE("users/{id_user1}/contactRequests/{id_user2}")
    Call<Void> deleteContactRequest(@Path("id_user1") int idUser1,@Path("id_user2")int idUser2);

    @FormUrlEncoded
    @POST("groups")
    Call<Void> createGroup(@Field("name") String name,
                           @Field("admin") int admin);

    @GET("groups/{id_group}/meetings")
    Call<String> getGroupMeetings(@Path("id_group") int idGroup);

    @GET("groups/{id_group}")
    Call<Group> getGroup(@Path("id_group") int idGroup);

    @POST("groups/{id_group}/memberRequests/{id_user}")
    Call<Integer> sendMemberRequest(@Path("id_group") int idGroup, @Path("id_user") int idUser);

    @POST("groups/{id_group}/addMember/{id_user}")
    Call<Void> addMember(@Path("id_group") int idGroup, @Path("id_user") int idUser);

    @DELETE("groups/{id_group}")
    Call<Void> deleteGroup(@Path("id_group") int idGroup);

    @DELETE("meetings/{id_meeting}")
    Call<Void> deleteMeeting(@Path("id_meeting") int idMeeting);

    @GET("meetings/{id_meeting}/responses")
    Call<List<MeetingResponse>> getMeetingResponses(@Path("id_meeting") int idMeeting);

    @FormUrlEncoded
    @PUT("meetings/{id_meeting}/meetingResponses/{id_user}")
    Call<Void> updateMeetingResponse(@Path("id_meeting") int idMeeting,@Path("id_user") int idUser,@Field("response") int response);

    @DELETE("memberRequests/{id_request}")
    Call<Void> deleteMemberRequest(@Path("id_request")int idRequest);
}

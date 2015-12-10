package com.obenproto.oben.api;

import com.obenproto.oben.response.LoginResponse;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by Petro Rington on 12.11.2015.
 */
public interface ObenAPIService {

    ////  Recall of user login
    @FormUrlEncoded
    @POST("morphing/ws/MorphingService/loginUser")
    Call<LoginResponse> userLogin(@Field("userEmail") String userEmail,
                                  @Field("userPassword") String userPassword,
                                  @Field("userDisplayName") String userDisplayName);

    ////  Recall of save user avatar ( ​updated since 1.0 release ​
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar")
    Call<LoginResponse> saveUserAvatar(@Part("userId") int userId,
                                       @Part("recordId") int recordId,
                                       @Part("audioFile") RequestBody audioFile,
                                       @Part("avatarId") int avatarId);

    ////    Recall of user avatar
    @GET("morphing/ws/MorphingService/getUserAvatar/9")
    Call<LoginResponse> getUserAvatar(@Query("userId") int userId);

    ////    Recall of user logout
    @POST("morphing/ws/MorphingService/logoutUser")
    Call<LoginResponse> userLogout();
}
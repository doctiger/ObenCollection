package com.obenproto.oben.api;

import com.obenproto.oben.response.ObenApiResponse;
import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by Petro Rington on 12.11.2015.
 */
public interface ObenAPIService {

    ////  Recall of user login
    @FormUrlEncoded
    @POST("morphing/ws/MorphingService/loginUser")
    Call<ObenApiResponse> userLogin(@Field("userEmail") String userEmail,
                                  @Field("userPassword") String userPassword,
                                  @Field("userDisplayName") String userDisplayName);

    ////  Recall of save user avatar ( ​updated since 1.0 release ​
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar")
    Call<ObenApiResponse> saveUserAvatar(@Part("userId") int userId,
                                       @Part("recordId") int recordId,
                                       @Part("audioFile") RequestBody audioFile);

    //// Send the request for Regular User Avatar.
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar")
    Call<ObenApiResponse> saveRegularAvatar(@Part("userId") int userId,
                                          @Part("recordId") int recordId,
                                          @Part("audioFile") RequestBody audioFile,
                                          @Part("avatarId") int avatarId);

    ////    Recall of user avatar
    @GET("morphing/ws/MorphingService/getUserAvatar/{userId}")
    Call<ObenApiResponse> getUserAvatar(@Path("userId") int userId);

    ////    Recall of user logout
    @POST("morphing/ws/MorphingService/logoutUser")
    Call<ObenApiResponse> userLogout();

    ////    Recall of avatar data
    @GET("morphing/ws/MorphingService/getAvatar/{avatarId}")
    Call<ObenApiResponse> getAvatarData(@Path("avatarId") int avatarId);

    ////    Recall of phrase data
    @GET("morphing/ws/MorphingService/getPhrases")
    Call<List<ObenApiResponse>> getPhraseData();
}
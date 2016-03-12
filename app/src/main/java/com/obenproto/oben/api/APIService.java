package com.obenproto.oben.api;

import com.obenproto.oben.api.domain.ObenPhrase;
import com.obenproto.oben.api.response.GetAllUserAvatarsResponse;
import com.obenproto.oben.api.response.GetAvatarResponse;
import com.obenproto.oben.api.response.GetPhrasesResponse;
import com.obenproto.oben.api.response.GetUserAvatarResponse;
import com.obenproto.oben.api.response.LoginResponse;
import com.obenproto.oben.api.response.ObenApiResponse;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

public interface APIService {

    ////  Recall of user login
    @FormUrlEncoded
    @POST("morphing/ws/MorphingService/loginUser")
    Call<LoginResponse> userLogin(@Field("userEmail") String userEmail,
                                  @Field("userPassword") String userPassword,
                                  @Field("userDisplayName") String userDisplayName);

    ////  Recall of save user avatar ( ​updated since 1.0 release ​)
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar")
    Call<ObenApiResponse> saveUserAvatar(@Part("userId") int userId,
                                         @Part("recordId") int recordId,
                                         @Part("audioFile") RequestBody audioFile,
                                         @Part("avatarId") Integer avatarId);

    ////  Recall of save user avatar ( ​updated since 1.0 release ​)
    ////  (don't exit the avatarID)Create the avatarID for regular, commercial and freestyle
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar")
    Call<ObenApiResponse> saveOriginalUserAvatar(@Part("userId") int userId,
                                                 @Part("recordId") int recordId,
                                                 @Part("audioFile") RequestBody audioFile);

    ////  Recall of save regular avatar ( ​updated since 1.0 release ​)
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/1")
    Call<ObenApiResponse> saveRegularUserAvatar(@Part("userId") int userId,
                                                @Part("recordId") int recordId,
                                                @Part("audioFile") RequestBody audioFile,
                                                @Part("avatarId") int avatarId);

    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/1")
    Call<ObenApiResponse> saveOriginalRegularUserAvatar(@Part("userId") int userId,
                                                        @Part("recordId") int recordId,
                                                        @Part("audioFile") RequestBody audioFile);

    ////  Recall of save commercial avatar ( ​updated since 1.0 release ​)
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/2")
    Call<ObenApiResponse> saveCommercialUserAvatar(@Part("userId") int userId,
                                                   @Part("recordId") int recordId,
                                                   @Part("audioFile") RequestBody audioFile,
                                                   @Part("avatarId") int avatarId);

    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/2")
    Call<ObenApiResponse> saveOriginalCommercialUserAvatar(@Part("userId") int userId,
                                                           @Part("recordId") int recordId,
                                                           @Part("audioFile") RequestBody audioFile);

    ////  Recall of save freestyle avatar ( ​updated since 1.0 release ) ​
    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/3")
    Call<ObenApiResponse> saveFreestyleUserAvatar(@Part("userId") int userId,
                                                  @Part("recordId") int recordId,
                                                  @Part("audioFile") RequestBody audioFile,
                                                  @Part("avatarId") int avatarId);

    @Multipart
    @POST("morphing/ws/MorphingService/saveUserAvatar/mode/3")
    Call<ObenApiResponse> saveOriginalFreestyleUserAvatar(@Part("userId") int userId,
                                                          @Part("recordId") int recordId,
                                                          @Part("audioFile") RequestBody audioFile);

    ////    Recall of user avatar
    @GET("morphing/ws/MorphingService/getUserAvatar/{userId}")
    Call<GetUserAvatarResponse> getUserAvatar(@Path("userId") int userId);

    ////    Recall of user logout
    @POST("morphing/ws/MorphingService/logoutUser")
    Call<ObenApiResponse> userLogout();

    ////    Recall of avatar data
    @GET("morphing/ws/MorphingService/getAvatar/{avatarId}")
    Call<ObenApiResponse> getAvatarData(@Path("avatarId") int avatarId);

    /**
     * Get all user avatars.
     *
     * @param userId User ID.
     * @return All user avatars as ArrayList object.
     */
    @GET("morphing/ws/MorphingService/getAllUserAvatars/{userId}")
    Call<GetAllUserAvatarsResponse> getAllUserAvatars(@Path("userId") Integer userId);

    /**
     * Get avatar data from avatar ID.
     *
     * @param avatarId Avatar ID.
     * @return Recorded sentences of avatar ID.
     */
    @GET("morphing/ws/MorphingService/getAvatar/{avatarId}")
    Call<GetAvatarResponse> getAvatar(@Path("avatarId") int avatarId);

    /**
     * Get the avatar ID for Commercial
     */
    @GET("morphing/ws/MorphingService/getAvatars/{userId}/mode/2")
    Call<List<ObenApiResponse>> getCommercialAvatars(@Path("userId") int userId);

    /**
     * Get the avatar ID for Freestyle
     */
    @GET("morphing/ws/MorphingService/getAvatars/{userId}/mode/3")
    Call<List<ObenApiResponse>> getFreestyleAvatars(@Path("userId") int userId);

    /**
     * Get all phrases.
     *
     * @param mode User mode.
     * @return All phrases as list object.
     */
    @GET("morphing/ws/MorphingService/getPhrases/mode/{mode}")
    Call<GetPhrasesResponse> getPhrases(@Path("mode") int mode);
}
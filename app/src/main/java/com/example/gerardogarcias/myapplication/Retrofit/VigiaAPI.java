package com.example.gerardogarcias.myapplication.Retrofit;

import com.example.gerardogarcias.myapplication.Model.CheckUserResponse;
import com.example.gerardogarcias.myapplication.Model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VigiaAPI {
    @FormUrlEncoded
    @POST("checkuser")
    Call<CheckUserResponse> checkuser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register")
    Call<User> register(@Field("phone") String phone,
                        @Field("name") String name,
                        @Field("lastname") String lastname,
                        @Field("email") String email,
                        @Field("address") String address);
}

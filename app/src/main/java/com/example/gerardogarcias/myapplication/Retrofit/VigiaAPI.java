package com.example.gerardogarcias.myapplication.Retrofit;

import com.example.gerardogarcias.myapplication.Model.CheckUserResponse;
import com.example.gerardogarcias.myapplication.Model.Reporte;
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
                        @Field("email") String email);

    @FormUrlEncoded
    @POST("getuser")
    Call<User> getuser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("reportes")
    Call<Reporte> reportes(@Field("date") String date,
                           @Field("hour") String hour,
                           @Field("description") String description,
                           @Field("user_id") String user_id,
                           @Field("situation_id") String situation_id,
                           @Field("street") String street,
                           @Field("colony") String colony,
                           @Field("zip_code") String zip_code,
                           @Field("house_number") String house_number,
                           @Field("requester_name") String requester_name,
                           @Field("requester_lastname") String requester_lastname,
                           @Field("people_involved") String people_involved);
}

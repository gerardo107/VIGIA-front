package com.example.gerardogarcias.myapplication.Util;

import com.example.gerardogarcias.myapplication.Retrofit.RetrofitClient;
import com.example.gerardogarcias.myapplication.Retrofit.VigiaAPI;

public class Common {
    // En emulador localhost es 10.0.2.2
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    public static VigiaAPI getApi(){
        return RetrofitClient.getClient(BASE_URL).create(VigiaAPI.class);
    }
}

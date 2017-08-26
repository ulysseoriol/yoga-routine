package com.example.ulysse.myoga.Network;

/**
 * Created by ulysse on 8/11/17.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl)
    {
        //TODO: Build caching mechanism

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
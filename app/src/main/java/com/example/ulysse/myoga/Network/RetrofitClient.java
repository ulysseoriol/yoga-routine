package com.example.ulysse.myoga.Network;

/**
 * Created by ulysse on 8/11/17.
 */

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//TODO: Singleton
public class RetrofitClient
{
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    /**
     * @return
     */
    public static NetworkApiInterface createNetworkService()
    {
        //TODO: Build caching mechanism

        if (retrofit == null)
        {
            //Client build from Interceptor to simulate slow connection network
            OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(
                    new NetworkSlowdownInterceptor()
            ).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NetworkApiInterface.class);
    }
}
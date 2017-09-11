/*
 * Copyright (c) 2015 Ink Applications, LLC.
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 */

package com.example.ulysse.myoga.Network;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Slows down a network request.
 *
 * This is intended for faking slow connections when debugging, it simply
 * sleeps the thread in a network interceptor to simulate longer connection
 * times.
 *
 * To use this, add it to your OkHttpClient's network interceptors:
 *
 *     OkHttpClient client = new OkHttpClient();
 *     client.networkInterceptors().add(new NetworkSlowdown());
 *
 * @author Maxwell Vandervelde (Max@MaxVandervelde.com)
 */
public class NetworkSlowdownInterceptor implements Interceptor
{
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        this.sleep();
        Log.d("NetworkSlowdown", "Network slowdown done. Proceeding chain");

        return chain.proceed(chain.request());
    }

    /**
     * Sleep the thread 5 seconds to slow the request.
     */
    private void sleep()
    {
        try {
            Log.d("NetworkSlowdown", "Sleeping for 5 seconds");
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            Log.e("NetworkSlowdown", "Interrupted", e);
        }
    }
}


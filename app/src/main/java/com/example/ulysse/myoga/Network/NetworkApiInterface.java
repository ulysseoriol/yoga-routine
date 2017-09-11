package com.example.ulysse.myoga.Network;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ulysse on 8/11/17.
 */

public interface NetworkApiInterface
{
    @GET("poses.json")
    Call<ApiNetworkResponse> getYogaPoseListRequest();
}

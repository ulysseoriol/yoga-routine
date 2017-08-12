package com.example.ulysse.myoga.Network;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ulysse on 8/11/17.
 */

public interface IApiNetworkService
{
    @GET("poses.json")
    Call<ApiNetworkResponse> getYogaPoseList();
}

package com.example.ulysse.myoga.Utils;

import com.example.ulysse.myoga.Network.NetworkAPIInterface;
import com.example.ulysse.myoga.Network.RetrofitClient;

/**
 * Created by ulysse on 8/11/17.
 */

public class ApiUtils
{

    public static final String BASE_URL = "http://10.0.2.2:8080/";

    public static NetworkAPIInterface createNetworkService()
    {
        return RetrofitClient.getClient(BASE_URL).create(NetworkAPIInterface.class);
    }
}
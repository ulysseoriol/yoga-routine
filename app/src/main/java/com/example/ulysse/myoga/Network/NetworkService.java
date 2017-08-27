package com.example.ulysse.myoga.Network;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ulysse on 8/15/17.
 */

public class NetworkService
{
    private final NetworkAPIInterface networkAPIInterface;

    public NetworkService(NetworkAPIInterface networkAPIInterface)
    {
        this.networkAPIInterface = networkAPIInterface;
    }

    public void getYogaPoseList(final GetYogaPoseListCallback callback)
    {
        networkAPIInterface.getYogaPoseListRequest().enqueue(new Callback<List<Pose>>()
        {
            @Override
            public void onResponse(Call<List<Pose>> call, Response<List<Pose>> response)
            {
                if (response.isSuccessful())
                {
//                    try
//                    {
//                        Thread.sleep(2000);
//                    }
//                    catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
                    callback.onSuccess(response.body());
                }
                else
                {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<List<Pose>> call, Throwable networkError)
            {
                callback.onError(networkError);

            }
        });
    }

    public interface GetYogaPoseListCallback
    {
        void onSuccess(List<Pose> yogaPoseListResponse);

        void onError(Throwable t);
    }
}

package com.example.ulysse.myoga.Network;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;

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
        networkAPIInterface.getYogaPoseListRequest().enqueue(new Callback<ApiNetworkResponse>()
        {
            @Override
            public void onResponse(Call<ApiNetworkResponse> call, Response<ApiNetworkResponse> response)
            {
                if (response.isSuccessful())
                {
                    callback.onSuccess(response.body());
                }
                else
                {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<ApiNetworkResponse> call, Throwable networkError)
            {
                callback.onError(networkError);

            }
        });
    }

    public interface GetYogaPoseListCallback
    {
        void onSuccess(ApiNetworkResponse yogaPoseListResponse);

        void onError(Throwable t);
    }
}

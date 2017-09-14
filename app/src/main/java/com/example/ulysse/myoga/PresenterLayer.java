package com.example.ulysse.myoga;

import android.util.Log;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.Network.NetworkService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulysse on 8/28/17.
 */

public class PresenterLayer implements PresenterInteractor
{
    private MainActivity activityView;
    private NetworkService networkService;
    private List<Pose> yogaPoseListDB;

    public PresenterLayer(MainActivity activityView, NetworkService networkService, List<Pose> yogaPoseList)
    {
        this.activityView = activityView;
        this.networkService = networkService;
        this.yogaPoseListDB = yogaPoseList;
    }


    @Override
    public List<Pose> getYogaPoseListDB()
    {
        return yogaPoseListDB;
    }

    /**
     * Search yoga pose list for string
     *
     * @param searchQuery
     * @return
     */
    @Override
    public List<Pose> searchYogaPoseList(String searchQuery)
    {
        searchQuery = searchQuery.toLowerCase();

        List<Pose> result = new ArrayList<>();

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < yogaPoseListDB.size(); i++)
        {
            if (yogaPoseListDB.get(i).getEnglishName().toLowerCase().contains(searchQuery))
            {
                result.add(yogaPoseListDB.get(i));
            }
        }

        return result;
    }

    /**
     * Send request
     */
    @Override
    public void loadYogaPoseList()
    {
        networkService.getYogaPoseList(new NetworkService.GetYogaPoseListCallback()
        {
            @Override
            public void onSuccess(ApiNetworkResponse yogaPoseDataResponse)
            {
                yogaPoseListDB = yogaPoseDataResponse.getYogaPoseList(); //save reference for search
                activityView.updateViewForRequestSuccess(yogaPoseListDB);
                Log.d("PresenterLayer", "posts loaded from API");
            }

            @Override
            public void onError(Throwable networkError)
            {
                activityView.updateViewForRequestError();
                Log.d("PresenterLayer", "error loading posts from API");
            }
        });
    }
}


package com.example.ulysse.myoga.Model;

import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiNetworkResponse {

    @SerializedName("yogaPoseList")
    @Expose
    public List<Pose> yogaPoseList;

    public List<Pose> getYogaPoseList()
    {
        return yogaPoseList;
    }

    public List<Pose> searchPose(String query)
    {
        query = query.toLowerCase();

        List<Pose> result = new LinkedList<>();
        List<Pose> baseList = yogaPoseList;

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < baseList.size(); i++)
        {
            if (baseList.get(i).englishName.toLowerCase().contains(query))
            {
                result.add(baseList.get(i));
            }
        }

        return result;
    }
}

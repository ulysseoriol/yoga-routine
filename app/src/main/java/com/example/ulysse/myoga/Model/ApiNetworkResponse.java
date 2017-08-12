
package com.example.ulysse.myoga.Model;

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

    public void setYogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }
}

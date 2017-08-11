package com.example.ulysse.myoga.Network;

import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

/**
 * Created by ulysse on 8/7/17.
 */

public class YogaPoseList
{
    List<Pose> yogaPoseList;

    public List<Pose> getYogaPoseList()
    {
        return yogaPoseList;
    }

    public void setYogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }

    public YogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }
}

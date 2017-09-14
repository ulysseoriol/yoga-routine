package com.example.ulysse.myoga;

import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

/**
 * Created by ulysse on 8/28/17.
 */

public interface PresenterInteractor
{
    void loadYogaPoseList();
    List<Pose> searchYogaPoseList(String searchQuery);
    List<Pose> getYogaPoseListDB();
}

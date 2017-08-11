package com.example.ulysse.myoga.Model;

/**
 * Created by ulysse on 8/7/17.
 */

public class Pose
{
    private String englishName;
    private String sanskritName;

    public void setEnglishName(String englishName)
    {
        this.englishName = englishName;
    }

    public void setSanskritName(String sanskritName)
    {
        this.sanskritName = sanskritName;
    }

    public String getEnglishName() { return englishName; }
    public String getSanskritName()
    {
        return sanskritName;
    }
}

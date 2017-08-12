
package com.example.ulysse.myoga.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pose
{

    @SerializedName("englishName")
    @Expose
    public String englishName;
    @SerializedName("sanskritName")
    @Expose
    public String sanskritName;

}

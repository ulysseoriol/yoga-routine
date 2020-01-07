
package com.example.ulysse.myoga.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Pose implements Parcelable
{

    @SerializedName("englishName")
    private String englishName;
    @SerializedName("sanskritName")
    private String sanskritName;
    @SerializedName("poseImageUrl")
    private String poseImageUrl;

    public String getPoseImageUrl()
    {
        return poseImageUrl;
    }

    public void setPoseImageUrl(String poseImageUrl)
    {
        this.poseImageUrl = poseImageUrl;
    }


    public String getEnglishName()
    {
        return englishName;
    }

    public void setEnglishName(String englishName)
    {
        this.englishName = englishName;
    }

    public String getSanskritName()
    {
        return sanskritName;
    }

    public void setSanskritName(String sanskritName)
    {
        this.sanskritName = sanskritName;
    }

    Pose (Parcel in)
    {
        englishName = in.readString();
        sanskritName = in.readString();
        poseImageUrl = in.readString();
    }

    static final Parcelable.Creator<Pose> CREATOR = new Parcelable.Creator<Pose>()
    {
        @Override
        public Pose createFromParcel(Parcel parcel)
        {
            return new Pose(parcel);
        }

        @Override
        public Pose[] newArray(int i)
        {
            return new Pose[i];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i)
    {
        dest.writeString(englishName);
        dest.writeString(sanskritName);
        dest.writeString(poseImageUrl);
    }
}

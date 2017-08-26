
package com.example.ulysse.myoga.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pose implements Parcelable
{

    @SerializedName("englishName")
    @Expose
    public String englishName;
    @SerializedName("sanskritName")
    @Expose
    public String sanskritName;

    Pose (Parcel in)
    {
        englishName = in.readString();
        sanskritName = in.readString();
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
    }
}

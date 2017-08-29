package com.example.ulysse.myoga.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiNetworkResponse implements Parcelable
{
    @SerializedName("yogaPoseList")
    private List<Pose> yogaPoseList;

    public List<Pose> getYogaPoseList()
    {
        return yogaPoseList;
    }

    public ApiNetworkResponse (List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }

    ApiNetworkResponse (Parcel parcel)
    {
        yogaPoseList = new ArrayList<Pose>();
        parcel.readTypedList(yogaPoseList, Pose.CREATOR);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeTypedList(yogaPoseList);
    }

    static final Parcelable.Creator<ApiNetworkResponse> CREATOR = new Parcelable.Creator<ApiNetworkResponse>()
    {
        @Override
        public ApiNetworkResponse createFromParcel(Parcel parcel)
        {
            return new ApiNetworkResponse(parcel);
        }

        @Override
        public ApiNetworkResponse[] newArray(int i)
        {
            return new ApiNetworkResponse[i];
        }
    };

}

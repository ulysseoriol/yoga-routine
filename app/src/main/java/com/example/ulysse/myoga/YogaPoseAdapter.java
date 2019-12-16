package com.example.ulysse.myoga;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

/**
 * Created by ulysse on 8/7/17.
 */

public class YogaPoseAdapter extends RecyclerView.Adapter<YogaPoseAdapter.ViewHolder>
{
    private List<Pose> yogaPoseList;

    public List<Pose> getYogaPoseList()
    {
        return yogaPoseList;
    }

    public void setYogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
        notifyDataSetChanged();
    }

    public YogaPoseAdapter(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }

    @Override
    public int getItemCount()
    {
        return yogaPoseList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Pose pose = yogaPoseList.get(position);
        holder.bind(pose);
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView simpleTextView;

        public ViewHolder(View view)
        {
            super(view);
            this.simpleTextView = itemView.findViewById(R.id.poseName);
        }

        public void bind(Pose pose)
        {
            simpleTextView.setText(pose.getEnglishName());
        }
    }
}

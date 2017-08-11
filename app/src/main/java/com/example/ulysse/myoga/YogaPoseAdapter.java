package com.example.ulysse.myoga;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.databinding.GridviewItemBinding;


import java.util.List;

/**
 * Created by ulysse on 8/7/17.
 */

public class YogaPoseAdapter extends RecyclerView.Adapter<YogaPoseAdapter.BindingHolder>
{
    private List<Pose> yogaPoseList;


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
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        GridviewItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.gridview_item, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position)
    {
        Pose pose = yogaPoseList.get(position);
        holder.bind(pose);
    }

    public void setYogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
        notifyDataSetChanged();
    }

    public class BindingHolder extends RecyclerView.ViewHolder
    {
        private final GridviewItemBinding binding;

        public BindingHolder(GridviewItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pose pose)
        {
            binding.setVariable(BR.pose, pose);
            binding.executePendingBindings();
        }
    }
}

package com.example.ulysse.myoga;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ulysse.myoga.Model.Pose;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by ulysse on 8/7/17.
 */

public class YogaPoseAdapter extends RecyclerView.Adapter<YogaPoseAdapter.ViewHolder>
{
    private final String WEBSITE_BASE_URL = "http://www.yogajournal.com/pose/";

    private List<Pose> yogaPoseList;

    public YogaPoseAdapter(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
    }

    public List<Pose> getYogaPoseList()
    {
        return yogaPoseList;
    }

    public void setYogaPoseList(List<Pose> yogaPoseList)
    {
        this.yogaPoseList = yogaPoseList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return yogaPoseList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent, false);
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
        TextView poseTextView;
        ImageView poseImageView;
        CardView cardView;

        public ViewHolder(View view)
        {
            super(view);
            this.poseTextView = itemView.findViewById(R.id.poseName);
            this.poseImageView = itemView.findViewById(R.id.poseImage);
            this.cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(Pose pose)
        {
            poseTextView.setText(pose.getEnglishName());
            Glide.with(poseImageView.getContext()).load(pose.getPoseImageUrl()).centerCrop().into(poseImageView);
            cardView.setOnClickListener((View view) ->
            {
                String poseName = pose.getEnglishName().toLowerCase();
                poseName = poseName.replaceAll("[^a-z]", "-");//Format url
                poseName = WEBSITE_BASE_URL + poseName;
                Intent loadUrlIntent = new Intent(Intent.ACTION_VIEW);
                loadUrlIntent.setData(Uri.parse(poseName));
                view.getContext().startActivity(loadUrlIntent);

            });

        }
    }
}

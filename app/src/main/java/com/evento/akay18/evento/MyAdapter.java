package com.evento.akay18.evento;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d02d33pak on 16/3/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.mViewHolder> {

    List<EventDetails> eventList;
    Context context;

    public MyAdapter(List<EventDetails> list, Context context) {
        this.eventList = list;
        this.context = context;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        mViewHolder mVH = new mViewHolder(view);

        return mVH;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        EventDetails mylist = eventList.get(position);
        holder.eventName.setText(mylist.getTitle());
        holder.eventOrganiser.setText("Organiser: "+mylist.getOrganiser());
        holder.eventDate.setText("Date: "+mylist.getDate());
        holder.eventTime.setText("Time: "+mylist.getTime());
        holder.eventLocation.setText(mylist.getLocation());
        holder.eventDetails.setText("Description: "+mylist.getDescription());
    }

    @Override
    public int getItemCount() {
        int arr = 0;

        try {
            if (eventList.size() == 0) {
                arr = 0;
            } else {
                arr = eventList.size();
            }
        } catch (Exception e) {
        }
        return arr;
    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventOrganiser, eventDetails, eventLocation, eventDate, eventTime;


        public mViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.info_event);
            eventOrganiser = itemView.findViewById(R.id.info_org);
            eventDate = itemView.findViewById(R.id.info_date);
            eventTime = itemView.findViewById(R.id.info_time);
            eventDetails = itemView.findViewById(R.id.info_detail);
            eventLocation = itemView.findViewById(R.id.info_location);

        }
    }
}



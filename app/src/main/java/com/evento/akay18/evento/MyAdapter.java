package com.evento.akay18.evento;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        final EventDetails mylist = eventList.get(position);
        holder.eventName.setText(mylist.getTitle());
        holder.eventOrganiser.setText("Organiser: "+mylist.getOrganiser());
        holder.eventDate.setText("Date: "+mylist.getDate());
        holder.eventTime.setText("Time: "+mylist.getTime());
        holder.eventLocation.setText("Venue: "+mylist.getLocation());
        holder.eventDetails.setText("Description: "+mylist.getDescription());

        holder.eventDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MapURL = "http://maps.google.co.in/maps?q="+ mylist.getLocation().replace(" ","+").trim();
                Log.i("URL ---- ", MapURL);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MapURL));
                context.startActivity(intent);
            }
        });
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
        TextView eventName, eventOrganiser, eventDetails, eventLocation, eventDate, eventTime, eventDirection;


        public mViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            eventName = itemView.findViewById(R.id.info_event);
            eventOrganiser = itemView.findViewById(R.id.info_org);
            eventDate = itemView.findViewById(R.id.info_date);
            eventTime = itemView.findViewById(R.id.info_time);
            eventDetails = itemView.findViewById(R.id.info_detail);
            eventLocation = itemView.findViewById(R.id.info_location);
            eventDirection = itemView.findViewById(R.id.info_direction);

        }
    }
}



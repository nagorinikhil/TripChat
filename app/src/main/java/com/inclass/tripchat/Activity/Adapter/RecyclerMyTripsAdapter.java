package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inclass.tripchat.Activity.Activity.TripDetailActivity;
import com.inclass.tripchat.Activity.Interface.MyTripsInterface;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMyTripsAdapter extends RecyclerView.Adapter<RecyclerMyTripsAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<Trip> myTripsArrayList;
    MyTripsInterface myTripsInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_titleMyTrips,textView_locationMyTrips;
        ImageView imageView_MyTrips;
        Button button_chatRoomMyTrips,button_removeMyTrips;

        public ViewHolder(View view) {
            super(view);
            textView_titleMyTrips = (TextView)view.findViewById(R.id.textView_titleAllTrips);
            textView_locationMyTrips = (TextView)view.findViewById(R.id.textView_locationMyTrips);
            imageView_MyTrips = (ImageView) view.findViewById(R.id.imageView_AllTrips);
            button_chatRoomMyTrips = (Button) view.findViewById(R.id.button_chatRoomMyTrips);
            button_removeMyTrips = (Button) view.findViewById(R.id.button_joinAllTrips);
        }
    }

    public RecyclerMyTripsAdapter(Context context, int resource, List<Trip> myTripsArrayList, MyTripsInterface myTripsInterface) {
        this.context = context;
        this.resource = resource;
        this.myTripsArrayList = (ArrayList<Trip>) myTripsArrayList;
        this.myTripsInterface = myTripsInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Trip trip = myTripsArrayList.get(position);

        holder.textView_titleMyTrips.setText(trip.getTitle());
        holder.textView_locationMyTrips.setText(trip.getLocation());
        if(trip.getImageUrl()!=null && !trip.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(trip.getImageUrl())
                    .resize(100,100)
                    .into(holder.imageView_MyTrips);
        } else {

        }

        holder.button_chatRoomMyTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Trip Details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra("TripId",trip.getTripId());
                context.startActivity(intent);
            }
        });

        holder.button_removeMyTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTripsInterface.removeTrip(trip.getTripId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return myTripsArrayList.size();
    }
}


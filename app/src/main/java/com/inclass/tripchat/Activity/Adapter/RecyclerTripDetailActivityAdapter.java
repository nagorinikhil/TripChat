package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inclass.tripchat.Activity.Interface.TripDetailInterface;
import com.inclass.tripchat.Activity.POJO.Places;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * Created by Nikhil on 27/04/2017.
 */

public class RecyclerTripDetailActivityAdapter extends RecyclerView.Adapter<RecyclerTripDetailActivityAdapter.ViewHolder> {

    Context context;
    int resource;
    ArrayList<Places> placesArrayList;
    TripDetailInterface tripDetailInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_PlaceName;
        ImageView imageView_deletePlace;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_PlaceName = (TextView)itemView.findViewById(R.id.textView_PlaceNameTripDetailActivity);
            imageView_deletePlace = (ImageView)itemView.findViewById(R.id.imageView_deletePlaceTripDetailActivity);
        }
    }

    public RecyclerTripDetailActivityAdapter(Context context, int resource, ArrayList<Places> placesArrayList, TripDetailInterface tripDetailInterface) {
        this.context = context;
        this.resource = resource;
        this.placesArrayList = placesArrayList;
        this.tripDetailInterface = tripDetailInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Places place = placesArrayList.get(position);

        holder.textView_PlaceName.setText(place.getPlaceName());
        holder.imageView_deletePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripDetailInterface.removePlace(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placesArrayList.size();
    }



}

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inclass.tripchat.Activity.Activity.TripDetailActivity;
import com.inclass.tripchat.Activity.Interface.AllTripInterface;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAllTripsAdapter extends RecyclerView.Adapter<RecyclerAllTripsAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<Trip> tripsArrayList;
    AllTripInterface allTripInterface;
    String currentUserId;
    FirebaseUser firebaseUser;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_titleAllTrips,textView_locationAllTrips;
        TextView textView_creatorAllTrips;
        ImageView imageView_AllTrips;
        Button button_chatRoomAllTrips,button_joinAllTrips;

        public ViewHolder(View view) {
            super(view);
            textView_creatorAllTrips = (TextView)view.findViewById(R.id.textView_creatorAllTrips);
            textView_titleAllTrips = (TextView)view.findViewById(R.id.textView_titleAllTrips);
            textView_locationAllTrips = (TextView)view.findViewById(R.id.textView_locationAllTrips);
            imageView_AllTrips = (ImageView) view.findViewById(R.id.imageView_AllTrips);
            button_chatRoomAllTrips = (Button) view.findViewById(R.id.button_chatRoomAllTrips);
            button_joinAllTrips = (Button) view.findViewById(R.id.button_joinAllTrips);
        }
    }

    public RecyclerAllTripsAdapter(Context context, int resource, List<Trip> tripsArrayList, String id, AllTripInterface allTripInterface) {
        this.context = context;
        this.resource = resource;
        this.tripsArrayList = (ArrayList<Trip>) tripsArrayList;
        this.currentUserId = id;
        this.allTripInterface = allTripInterface;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Trip trip = tripsArrayList.get(position);

        holder.textView_titleAllTrips.setText(trip.getTitle());
        holder.textView_locationAllTrips.setText("Location: "+trip.getLocation());
        holder.textView_creatorAllTrips.setText("Creator: "+trip.getCreator()
        );
        if(trip.getImageUrl()!=null && !trip.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(trip.getImageUrl())
                    .resize(100,100)
                    .into(holder.imageView_AllTrips);
        } else {

        }

        if(!trip.isActive()){
            holder.button_chatRoomAllTrips.setEnabled(false);
            holder.button_chatRoomAllTrips.setText("Deactivated");
        } else {
            holder.button_chatRoomAllTrips.setEnabled(true);
            holder.button_chatRoomAllTrips.setText("Details");
        }

        if(trip.getFriendArrayList() == null){
            if(trip.getCreatorID().equals(currentUserId)){
                holder.button_joinAllTrips.setEnabled(false);
                holder.button_chatRoomAllTrips.setEnabled(true);
            }else {
                holder.button_chatRoomAllTrips.setEnabled(false);
                holder.button_joinAllTrips.setEnabled(true);
            }
        }else {
            if(trip.getFriendArrayList().contains(currentUserId)){
                holder.button_joinAllTrips.setEnabled(true);
                holder.button_joinAllTrips.setText("Leave");
                holder.button_joinAllTrips.setTag("Leave");
                holder.button_chatRoomAllTrips.setEnabled(true);
            } else if(trip.getCreatorID().equals(currentUserId)){
                holder.button_joinAllTrips.setEnabled(false);
                holder.button_chatRoomAllTrips.setEnabled(true);
            } else {
                holder.button_chatRoomAllTrips.setEnabled(false);
                holder.button_joinAllTrips.setEnabled(true);
            }
        }


        holder.button_chatRoomAllTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Trip Details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra("TripId",trip.getTripId());
                context.startActivity(intent);
            }
        });

        holder.button_joinAllTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(((Button)view).getTag().equals("Join")){
                    Toast.makeText(context, "Trip Joined", Toast.LENGTH_SHORT).show();
                    ((Button)view).setText("Leave");
                    ((Button)view).setTag("Leave");

                    allTripInterface.joinTrip(position);
                } else if(((Button)view).getTag().equals("Leave")){
                    Toast.makeText(context, "Trip Left", Toast.LENGTH_SHORT).show();
                    ((Button)view).setText("Join");
                    ((Button)view).setTag("Join");
                    allTripInterface.leaveTrip(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripsArrayList.size();
    }
}


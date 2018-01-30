package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inclass.tripchat.Activity.Activity.ChatActivity;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerChatRoomListAdapter extends RecyclerView.Adapter<RecyclerChatRoomListAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<Trip> tripsArrayList;
    String currentUserId;
    FirebaseUser firebaseUser;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_titleChatRoomList,textView_locationChatRoomList;
        TextView textView_creatorNameChatRoomList;
        ImageView imageView_AllTrips;

        public ViewHolder(View view) {
            super(view);
            textView_titleChatRoomList = (TextView)view.findViewById(R.id.textView_titleChatRoomList);
            textView_locationChatRoomList = (TextView)view.findViewById(R.id.textView_locationChatRoomList);
            textView_creatorNameChatRoomList = (TextView)view.findViewById(R.id.textView_creatorNameChatRoomList);
            imageView_AllTrips = (ImageView) view.findViewById(R.id.imageView_ChatRoomList);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Welcome to Chat Room", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("TripId",tripsArrayList.get(getAdapterPosition()).getTripId());
                    context.startActivity(intent);
                }
            });
        }
    }

    public RecyclerChatRoomListAdapter(Context context, int resource, List<Trip> tripsArrayList, String id) {
        this.context = context;
        this.resource = resource;
        this.tripsArrayList = (ArrayList<Trip>) tripsArrayList;
        this.currentUserId = id;
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
        holder.textView_creatorNameChatRoomList.setText("Creator: "+trip.getCreator());
        holder.textView_titleChatRoomList.setText(trip.getTitle());
        holder.textView_locationChatRoomList.setText("Location: "+trip.getLocation());
        if(trip.getImageUrl()!=null && !trip.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(trip.getImageUrl())
                    .resize(100,100)
                    .into(holder.imageView_AllTrips);
        } else {

        }

    }

    @Override
    public int getItemCount() {
        return tripsArrayList.size();
    }
}


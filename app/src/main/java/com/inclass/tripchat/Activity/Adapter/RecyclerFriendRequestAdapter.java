package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.inclass.tripchat.Activity.Interface.FriendRequestInterface;
import com.inclass.tripchat.Activity.POJO.FriendRequest;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerFriendRequestAdapter extends RecyclerView.Adapter<RecyclerFriendRequestAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<FriendRequest> friendRequestArrayList;
    DataSnapshot dataSnapshot;
    FriendRequestInterface friendRequestInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_NameItemAcceptFriend;
        ImageView imageView_ItemAcceptRequest;
        Button button_ConfirmAccetRequest,button_DeleteItemAcceptRequest;

        public ViewHolder(View view) {
            super(view);
            textView_NameItemAcceptFriend = (TextView)view.findViewById(R.id.textView_NameItemAcceptFriend);
            imageView_ItemAcceptRequest = (ImageView) view.findViewById(R.id.imageView_ItemAcceptRequest);
            button_ConfirmAccetRequest = (Button) view.findViewById(R.id.button_ConfirmAccetRequest);
            button_DeleteItemAcceptRequest = (Button) view.findViewById(R.id.button_DeleteItemAcceptRequest);
        }
    }

    public RecyclerFriendRequestAdapter(Context context, int resource, List<FriendRequest> friendRequestArrayList, DataSnapshot dataSnapshot, FriendRequestInterface friendRequestInterface) {
        this.context = context;
        this.resource = resource;
        this.friendRequestArrayList = (ArrayList<FriendRequest>) friendRequestArrayList;
        this.dataSnapshot = dataSnapshot;
        this.friendRequestInterface = friendRequestInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d("UID SIZE", friendRequestArrayList.size()+"");
        if(dataSnapshot!=null){
            Log.d("InSIDE ONBIND Accept", "INSIDE DATASNAPSHOT");
            final User user = dataSnapshot.child(friendRequestArrayList.get(position).getSender()).getValue(User.class);
            holder.textView_NameItemAcceptFriend.setText(user.getfName()+" "+user.getlName());
            if(user.getImageUrl()!=null && !user.getImageUrl().equals("")){
                Picasso.with(context)
                        .load(user.getImageUrl())
                        .resize(50,50)
                        .into(holder.imageView_ItemAcceptRequest);
            }else {

            }

            holder.button_ConfirmAccetRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendRequestInterface.removeRequest(friendRequestArrayList.get(position).getReqId());
                    friendRequestInterface.addfriend(user.getUserId());
                }
            });

            holder.button_DeleteItemAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendRequestInterface.removeRequest(friendRequestArrayList.get(position).getReqId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        //Log.d("getItemCount", friendRequestArrayList.size()+"");
        return friendRequestArrayList.size();
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot){
        this.dataSnapshot = dataSnapshot;
    }
}


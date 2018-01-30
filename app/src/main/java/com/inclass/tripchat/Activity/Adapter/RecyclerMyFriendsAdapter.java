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
import com.inclass.tripchat.Activity.Interface.MyFriendsInterface;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMyFriendsAdapter extends RecyclerView.Adapter<RecyclerMyFriendsAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<String> uidArrayList;
    DataSnapshot dataSnapshot;
    MyFriendsInterface myFriendsInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_NameItemMyFriends;
        ImageView imageView_ItemMyFriends;
        Button button_ItemMyFriends;

        public ViewHolder(View view) {
            super(view);
            textView_NameItemMyFriends = (TextView)view.findViewById(R.id.textView_NameItemMyFriends);
            imageView_ItemMyFriends = (ImageView) view.findViewById(R.id.imageView_ItemMyFriends);
            button_ItemMyFriends = (Button) view.findViewById(R.id.button_ItemMyFriends);
        }
    }

    public RecyclerMyFriendsAdapter(Context context, int resource,List<String> uidArrayList, DataSnapshot dataSnapshot, MyFriendsInterface myFriendsInterface) {
        this.context = context;
        this.resource = resource;
        this.uidArrayList = (ArrayList<String>) uidArrayList;
        this.dataSnapshot = dataSnapshot;
        this.myFriendsInterface = myFriendsInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("UID SIZE MyFriend", uidArrayList.size()+"");
        if(dataSnapshot!=null){
            Log.d("InSIDE ONBIND", "INSIDE DATASNAPSHOT");
            final User user = dataSnapshot.child(uidArrayList.get(position)).getValue(User.class);
            holder.textView_NameItemMyFriends.setText(user.getfName()+" "+user.getlName());
            if(user.getImageUrl()!=null && !user.getImageUrl().equals("")){
                Picasso.with(context)
                        .load(user.getImageUrl())
                        .resize(50,50)
                        .into(holder.imageView_ItemMyFriends);
            }else {

            }

            holder.button_ItemMyFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myFriendsInterface.removeFriend(user.getUserId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return uidArrayList.size();
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot){
        this.dataSnapshot = dataSnapshot;
    }
}


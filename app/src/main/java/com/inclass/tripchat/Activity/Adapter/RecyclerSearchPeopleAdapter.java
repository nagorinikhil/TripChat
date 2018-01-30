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

import com.inclass.tripchat.Activity.Interface.SearchPeopleInterface;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerSearchPeopleAdapter extends RecyclerView.Adapter<RecyclerSearchPeopleAdapter.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<User> userArrayList;
    ArrayList<String> friendArrayList, friendReqSentArrayListString, friendReqReceivedArrayListString;
    SearchPeopleInterface searchPeopleInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_NameItemSearchPeople;
        ImageView imageView_ItemSearchPeople;
        Button button_ItemSearchPeople;

        public ViewHolder(View view) {
            super(view);
            textView_NameItemSearchPeople = (TextView) view.findViewById(R.id.textView_NameItemSearchPeople);
            imageView_ItemSearchPeople = (ImageView) view.findViewById(R.id.imageView_ItemSearchPeople);
            button_ItemSearchPeople = (Button) view.findViewById(R.id.button_ItemSearchPeople);
        }
    }

    public RecyclerSearchPeopleAdapter(Context context, int resource, List<User> userArrayList, List<String> uidArrayList, List<String> friendReqSentArrayListString, List<String> friendReqReceivedArrayListString, SearchPeopleInterface searchPeopleInterface) {
        this.context = context;
        this.resource = resource;
        this.userArrayList = (ArrayList<User>) userArrayList;
        this.friendArrayList = (ArrayList<String>) uidArrayList;
        this.friendReqSentArrayListString = (ArrayList<String>) friendReqSentArrayListString;
        this.friendReqReceivedArrayListString = (ArrayList<String>) friendReqReceivedArrayListString;
        this.searchPeopleInterface = searchPeopleInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("UID SIZE Search", friendArrayList.size() + "");

        final User user = userArrayList.get(position);
        holder.textView_NameItemSearchPeople.setText(user.getfName() + " " + user.getlName());
        if(user.getImageUrl()!=null && !user.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(user.getImageUrl())
                    .resize(50,50)
                    .into(holder.imageView_ItemSearchPeople);
        }else {

        }

        if(friendArrayList!=null && friendArrayList.contains(user.getUserId())){
            holder.button_ItemSearchPeople.setEnabled(false);
            holder.button_ItemSearchPeople.setText("Add Friend");
            holder.button_ItemSearchPeople.setTag("Friend");
        } else if(friendReqSentArrayListString != null && friendReqSentArrayListString.contains(user.getUserId())){
            holder.button_ItemSearchPeople.setText("Sent");
            holder.button_ItemSearchPeople.setTag("Sent");
            holder.button_ItemSearchPeople.setEnabled(false);
        } else if(friendReqReceivedArrayListString != null && friendReqReceivedArrayListString.contains(user.getUserId())){
            holder.button_ItemSearchPeople.setEnabled(false);
            holder.button_ItemSearchPeople.setText("Accept");
            holder.button_ItemSearchPeople.setTag("Accept");
        } else {

        }

        holder.button_ItemSearchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPeopleInterface.addFriendRequest(user.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();

    }

}


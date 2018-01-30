package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * Created by Nikhil on 22/04/2017.
 */

public class ListViewChatAdapter extends ArrayAdapter {

    Context context;
    int resource;
    String myUid;
    ArrayList<Chat> chatArrayList;

    public ListViewChatAdapter(Context context, int resource, ArrayList<Chat> chatArrayList, String myUid) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.chatArrayList = chatArrayList;
        this.myUid = myUid;
    }

    @Override
    public int getCount() {
        return chatArrayList.size();
    }

    @Override
    public Chat getItem(int position) {
        return chatArrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resource,parent,false);

        TextView textViewSender = (TextView)convertView.findViewById(R.id.textView_senderRightItemListView);
        TextView textViewTime = (TextView)convertView.findViewById(R.id.textView_timeRightItemListView);
        ImageView imageViewMessage = (ImageView)convertView.findViewById(R.id.imageView_rightItemListView);
        TextView textViewMessage = (TextView)convertView.findViewById(R.id.textView_messageRightItemListView);

        /*if(chat.getImageUrl().length()>0){
            textViewMessage.setVisibility(View.INVISIBLE);
            imageViewMessage.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(chat.getImageUrl())
                    .into(imageViewMessage);
        }else {
            imageViewMessage.setVisibility(View.INVISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewMessage.setText(chat.getMessage());
        }*/
        textViewSender.setText("adsfghj");

        /*if(chat.getSender().equals(myUid)){
            convertView = inflater.inflate(R.layout.item_chat_right,parent,false);
            TextView textViewSender = (TextView)convertView.findViewById(R.id.textView_senderRightItemListView);
        TextView textViewTime = (TextView)convertView.findViewById(R.id.textView_timeRightItemListView);
        ImageView imageViewMessage = (ImageView)convertView.findViewById(R.id.imageView_rightItemListView);
        TextView textViewMessage = (TextView)convertView.findViewById(R.id.textView_messageRightItemListView);

        if(chat.getImageUrl().length()>0){
            textViewMessage.setVisibility(View.INVISIBLE);
            imageViewMessage.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(chat.getImageUrl())
                    .into(imageViewMessage);
        }else {
            imageViewMessage.setVisibility(View.INVISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            textViewMessage.setText(chat.getMessage());
        }
        textViewSender.setText(chat.getSenderName());
        } else {
            convertView = inflater.inflate(R.layout.item_chat_left,parent,false);
            TextView textViewSender = (TextView)convertView.findViewById(R.id.textView_senderLeftItemListView);
            TextView textViewTime = (TextView)convertView.findViewById(R.id.textView_timeLeftItemListView);
            ImageView imageViewMessage = (ImageView)convertView.findViewById(R.id.imageView_LeftItemListView);
            TextView textViewMessage = (TextView)convertView.findViewById(R.id.textView_messageLeftItemListView);

            if(chat.getImageUrl().length()>0){
                textViewMessage.setVisibility(View.INVISIBLE);
                imageViewMessage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat.getImageUrl())
                        .into(imageViewMessage);
            }else {
                imageViewMessage.setVisibility(View.INVISIBLE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(chat.getMessage());
            }
            textViewSender.setText(chat.getSenderName());

        }
*/
            return convertView;
    }
}

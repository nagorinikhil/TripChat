package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;



public class CustomChatAdapter extends ArrayAdapter {
    Context context;
    int resource;
    String myUid;
    ArrayList<Chat> chatArrayList;

    PrettyTime p;
    public CustomChatAdapter(Context context, int resource, ArrayList<Chat> itemArrayList, String myUid) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.myUid = myUid;
        this.chatArrayList = itemArrayList;
        Chat chat = new Chat();
        chat.setImageUrl("");
        chat.setSender("asd");
        chat.setSenderName("asdaD");
        //chat.setTime(new Date());
        chat.setMessage("asdfhnfgd");
        chatArrayList.add(chat);
        Log.d("adaidlmvsdmv",this.chatArrayList.size()+"");
        p= new PrettyTime();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Chat chat = getItem(position);

        Log.d("In Adapter", "HELLO");

        if(chat.getSender().equals(myUid)){
            convertView = inflater.inflate(R.layout.item_chat_right, parent, false);
            TextView textViewSender = (TextView)convertView.findViewById(R.id.textView_senderRightItemListView);
            TextView textViewTime = (TextView)convertView.findViewById(R.id.textView_timeRightItemListView);
            ImageView imageViewMessage = (ImageView)convertView.findViewById(R.id.imageView_rightItemListView);
            TextView textViewMessage = (TextView)convertView.findViewById(R.id.textView_messageRightItemListView);

            Log.d("ME Sender","MINE");

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
            //textViewTime.setText(p.format(chat.getTime()));

        }else {
            convertView = inflater.inflate(R.layout.item_chat_left, parent, false);
            TextView textViewSender = (TextView)convertView.findViewById(R.id.textView_senderLeftItemListView);
            TextView textViewTime = (TextView)convertView.findViewById(R.id.textView_timeLeftItemListView);
            ImageView imageViewMessage = (ImageView)convertView.findViewById(R.id.imageView_LeftItemListView);
            TextView textViewMessage = (TextView)convertView.findViewById(R.id.textView_messageLeftItemListView);

            Log.d("Not Mine","Not Me");

            if(chat.getImageUrl().length()>0){
                textViewMessage.setVisibility(View.VISIBLE);
                imageViewMessage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat.getImageUrl())
                        .into(imageViewMessage);

            }else {
                imageViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(chat.getMessage());
            }
            textViewSender.setText(chat.getSenderName());
            //textViewTime.setText(p.format(chat.getTime()));
        }

        return convertView;
    }

}

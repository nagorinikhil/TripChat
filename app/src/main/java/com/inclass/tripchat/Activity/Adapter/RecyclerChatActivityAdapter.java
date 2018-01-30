package com.inclass.tripchat.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inclass.tripchat.Activity.Interface.ChatActivityInterface;
import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

public class RecyclerChatActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static Context context;
    int resource;
    ArrayList<Chat> myChatArrayList;
    String myUid;
    PrettyTime prettyTime;
    ChatActivityInterface chatActivityInterface;

    public class leftViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSender,textViewTime, textViewMessage;
        ImageView imageViewMessage;

        public leftViewHolder(View itemView) {
            super(itemView);
            textViewSender = (TextView)itemView.findViewById(R.id.textView_senderLeftItemListView);
            textViewTime = (TextView)itemView.findViewById(R.id.textView_timeLeftItemListView);
            imageViewMessage = (ImageView)itemView.findViewById(R.id.imageView_LeftItemListView);
            textViewMessage = (TextView)itemView.findViewById(R.id.textView_messageLeftItemListView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeChat(getAdapterPosition());
                    return  true;
                }
            });
        }
    }

    public class rightViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSender,textViewTime, textViewMessage;
        ImageView imageViewMessage;

        public rightViewHolder(View itemView) {
            super(itemView);
            textViewSender = (TextView)itemView.findViewById(R.id.textView_senderRightItemListView);
            textViewTime = (TextView)itemView.findViewById(R.id.textView_timeRightItemListView);
            imageViewMessage = (ImageView)itemView.findViewById(R.id.imageView_rightItemListView);
            textViewMessage = (TextView)itemView.findViewById(R.id.textView_messageRightItemListView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeChat(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public RecyclerChatActivityAdapter(Context context, int resource, List<Chat> myChatList, String myUid, ChatActivityInterface anInterface) {
        this.context = context;
        this.resource = resource;
        this.myChatArrayList = (ArrayList<Chat>) myChatList;
        this.myUid = myUid;
        prettyTime = new PrettyTime();
        this.chatActivityInterface = anInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_right, parent, false);
            return new rightViewHolder(itemView);
        } else if(viewType == 2){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_left, parent, false);
            return new leftViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = myChatArrayList.get(position);
        if(holder instanceof leftViewHolder){
            ((leftViewHolder)holder).textViewSender.setText(chat.getSenderName());
            ((leftViewHolder)holder).textViewTime.setText(prettyTime.format(chat.getTime()));
            if(chat.getImageUrl().length()>0){
                ((leftViewHolder)holder).textViewMessage.setVisibility(View.INVISIBLE);
                ((leftViewHolder)holder).imageViewMessage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat.getImageUrl())
                        .into(((leftViewHolder)holder).imageViewMessage);
            }else {
                ((leftViewHolder)holder).textViewMessage.setVisibility(View.VISIBLE);
                ((leftViewHolder)holder).imageViewMessage.setVisibility(View.INVISIBLE);
                ((leftViewHolder)holder).textViewMessage.setText(chat.getMessage());
            }

        } else if(holder instanceof rightViewHolder){
            ((rightViewHolder)holder).textViewSender.setText(chat.getSenderName());
            ((rightViewHolder)holder).textViewTime.setText(prettyTime.format(chat.getTime()));
            if(chat.getImageUrl().length()>0){
                ((rightViewHolder)holder).textViewMessage.setVisibility(View.INVISIBLE);
                ((rightViewHolder)holder).imageViewMessage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat.getImageUrl())
                        .into(((rightViewHolder)holder).imageViewMessage);
            }else {
                ((rightViewHolder)holder).textViewMessage.setVisibility(View.VISIBLE);
                ((rightViewHolder)holder).imageViewMessage.setVisibility(View.INVISIBLE);
                ((rightViewHolder)holder).textViewMessage.setText(chat.getMessage());
            }
        }
    }

    /* @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_right, parent, false);
            return new rightViewHolder(itemView);
        } else if(viewType == 2){

        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }*/

   /* @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Chat chat = myChatArrayList.get(position);
        Log.d("Chat Recycle", chat.getSenderName()+ " " +position);

        holder.textViewSender.setText(chat.getSenderName());
        holder.textViewMessage.setText(chat.getMessage());

    }*/

    public void removeChat(int position){
        chatActivityInterface.removeChat(position);
    }

    @Override
    public int getItemCount() {
        Log.d("Recycle myChatArrayList",  myChatArrayList.size()+"");
        return myChatArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Chat chat = myChatArrayList.get(position);
        if(chat.getSender().equals(myUid)){
            return 1;
        } else {
            return 2;
        }
//        return super.getItemViewType(position);
    }
}


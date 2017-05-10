package tech.doujiang.launcher.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.model.MessageBean;

/**
 * Created by grinch on 11/04/2017.
 */

public class ContactSMSAdapter extends RecyclerView.Adapter<ContactSMSAdapter.ViewHolder> {

    private ArrayList<MessageBean> myMessageList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;

        TextView leftMsg;
        TextView rightMsg;
        TextView contactSMSItemTime;

        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.contact_sms_item_left);
            rightLayout = (LinearLayout)view.findViewById(R.id.contact_sms_item_right);
            leftMsg = (TextView)view.findViewById(R.id.left_msg);
            rightMsg = (TextView)view.findViewById(R.id.right_msg);
            contactSMSItemTime = (TextView)view.findViewById(R.id.contact_sms_item_time);
        }
    }

    public ContactSMSAdapter(ArrayList<MessageBean> messageList){
        myMessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_sms_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean msg = myMessageList.get(position);
        Date timeDate = new Date(msg.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE HH:mm");
        String time = simpleDateFormat.format(timeDate);
        holder.contactSMSItemTime.setText(time);
        Log.d("ContactSMSAdapter", msg.getContent());
        if(msg.getType() == 1){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else if(msg.getType() == 2){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return myMessageList.size();
    }

    public void swap(ArrayList<MessageBean> MessageList){
        myMessageList.clear();
        myMessageList.addAll(MessageList);
        notifyDataSetChanged();
    }

}

package tech.doujiang.launcher.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.ContactSMSActivity;
import tech.doujiang.launcher.activity.ContactSMSActivityBeta;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.model.SMSBean;

/**
 * Created by grinch on 10/04/2017.
 */

public class SMSListAdapter extends RecyclerView.Adapter<SMSListAdapter.ViewHolder> {

    private List<SMSBean> mysmslist;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView smsImage;
        TextView smsText;
        TextView smsContact;
        TextView smsTime;
        LinearLayout linearLayout;

        public ViewHolder(View view){
            super(view);
            smsImage = (ImageView)view.findViewById(R.id.sms_item_img);
            smsText = (TextView)view.findViewById(R.id.sms_item_text);
            smsContact = (TextView)view.findViewById(R.id.sms_item_name);
            smsTime = (TextView)view.findViewById(R.id.sms_item_time);
            linearLayout = (LinearLayout)view.findViewById(R.id.sms_item_linear);
        }
    }

    public SMSListAdapter(List<SMSBean> smslist){
        mysmslist = smslist;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.sms_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Intent intent = new Intent(parent.getContext(), ContactSMSActivityBeta.class);
                SMSBean sms = mysmslist.get(pos);
                intent.putExtra("contactId", sms.getThread_id());
                intent.putExtra("name", sms.getName());
                intent.putExtra("date", sms.getDate());
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SMSBean sms = mysmslist.get(position);
        holder.smsImage.setImageResource(R.drawable.contacts);
        Log.d("SMSListAdapter", sms.getMsg_snippet());

        holder.smsContact.setText(sms.getName());
        holder.smsText.setText(sms.getMsg_snippet());
        Date timeDate = new Date(sms.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE HH:mm");
        String time = simpleDateFormat.format(timeDate);
        holder.smsTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return mysmslist.size();
    }
}

package tech.doujiang.launcher.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
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

        public ViewHolder(View view){
            super(view);
            smsImage = (ImageView)view.findViewById(R.id.sms_item_img);
            smsText = (TextView)view.findViewById(R.id.sms_item_text);
            smsContact = (TextView)view.findViewById(R.id.sms_item_name);
            smsTime = (TextView)view.findViewById(R.id.sms_item_time);
        }
    }

    public SMSListAdapter(List<SMSBean> smslist){
        mysmslist = smslist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.sms_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SMSBean sms = mysmslist.get(position);
        holder.smsImage.setImageResource(R.drawable.sms_img);
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

package tech.doujiang.launcher.adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.model.CallLogBean;

/**
 * Created by grinch on 08/04/2017.
 */

public class CallLogListAdapter extends RecyclerView.Adapter<CallLogListAdapter.ViewHolder> {

    private List<CallLogBean> callLogs;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView call_type;
        TextView name;
        TextView number;
        TextView time;
        ImageView callbak;

        public ViewHolder(View view){
            super(view);
            call_type = (ImageView)view.findViewById(R.id.call_type);
            name = (TextView)view.findViewById(R.id.call_name);
            number = (TextView)view.findViewById(R.id.call_number);
            time = (TextView)view.findViewById(R.id.call_time);
            //callbak = (ImageView)view.findViewById(R.id.call_back);
        }
    }
    public CallLogListAdapter(List<CallLogBean> callLogList){
        callLogs = callLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.call_log_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallLogBean callLog = callLogs.get(position);

        switch (callLog.getType()) {
            case 1:
                holder.call_type.setBackgroundResource(R.drawable.call_in);
                break;
            case 2:
                holder.call_type.setBackgroundResource(R.drawable.call_out);
                break;
            case 3:
                holder.call_type.setBackgroundResource(R.drawable.call_off);
                break;
            default:
                break;
        }
        holder.name.setText(callLog.getName());
        holder.number.setText(callLog.getNumber());
        Date timeDate = new Date(callLog.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(timeDate);
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }
}

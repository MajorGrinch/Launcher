package tech.doujiang.launcher.adapter;

import java.text.SimpleDateFormat;
import java.util.*;

//import org.apache.commons.beanutils.Converter;

import android.content.*;
import android.net.Uri;
//import android.support.annotation.CallSuper;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.model.CallLogBean;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class DialAdapter extends BaseAdapter{
    private Context ctx;
    private List<CallLogBean> callLogs;
    private LayoutInflater inflater;

    public DialAdapter(Context context, List<CallLogBean> callLogs) {
        this.ctx = context;
        this.callLogs = callLogs;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return callLogs.size();
    }

    @Override
    public Object getItem(int position) {
        return callLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.call_log_list_item
                    , null);
            holder = new ViewHolder();
            holder.call_type = (ImageView) convertView.findViewById(R.id.call_type);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CallLogBean callLog = callLogs.get(position);
        switch (callLog.getType()) {
            case 1:
                holder.call_type
                        .setBackgroundResource(R.drawable.call_in);
                break;
            case 2:
                holder.call_type
                        .setBackgroundResource(R.drawable.call_out);
                break;
            case 3:
                holder.call_type
                        .setBackgroundResource(R.drawable.call_off);
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

        addViewListener(holder.name, callLog, position);
        addViewListener(holder.number, callLog, position);
        return convertView;
    }

    private static class ViewHolder {
        ImageView call_type;
        TextView name;
        TextView number;
        TextView time;
        TextView call_btn;
    }

    private void addViewListener(View view, final CallLogBean callLog
            , final int position) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + callLog.getNumber());
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                ctx.startActivity(intent);
            }
        });
    }
}

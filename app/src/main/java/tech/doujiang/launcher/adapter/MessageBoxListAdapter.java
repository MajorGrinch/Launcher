package tech.doujiang.launcher.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.util.Constant;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class MessageBoxListAdapter extends BaseAdapter{

    private List<MessageBean> mbList;
    private Context ctx;
    private LinearLayout layout_father;
    private Constant constant;
    private LayoutInflater vi;
    private LinearLayout layout_child;
    private TextView tvDate;
    private TextView tvText;

    public MessageBoxListAdapter(Context context, List<MessageBean> coll) {
        ctx = context;
        vi = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mbList = coll;
        this.constant = new Constant();
    }

    public int getCount() {
        return mbList.size();
    }

    public Object getItem(int position) {
        return mbList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        MessageBean mb = mbList.get(position);
        int itemLayout;
        if (mb.getType() == constant.LAYOUT_INCOMING) {
            itemLayout = R.layout.list_say_he_item;
        } else {
            itemLayout = R.layout.list_say_me_item;
        }
        layout_father = new LinearLayout(ctx);
        vi.inflate(itemLayout, layout_father, true);

        layout_father.setBackgroundColor(Color.TRANSPARENT);
        layout_child = (LinearLayout) layout_father
                .findViewById(R.id.layout_bj);

        tvText = (TextView) layout_father
                .findViewById(R.id.messagedetail_row_text);
        tvText.setText(mb.getText());

        tvDate = (TextView) layout_father
                .findViewById(R.id.messagedetail_row_date);
        Date timeDate = new Date(mb.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(timeDate);
        tvDate.setText(time);

        addListener(tvText, tvDate, layout_child, mb);

        return layout_father;
    }

    public void addListener(final TextView tvText, final TextView tvDate,
                            LinearLayout layout_bj, final MessageBean mb) {

        layout_bj.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            }
        });

        layout_bj.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                tvText.setTextColor(Color.BLACK);
                showListDialog(newtan, mb);
                return true;
            }
        });

        layout_bj.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        tvText.setTextColor(Color.BLACK);
                        break;

                    default:
                        tvText.setTextColor(Color.BLACK);
                        break;
                }
                return false;
            }
        });
    }

    private String[] newtan = new String[] { "cut", "copy", "paste", "select all" };

    private void showListDialog(final String[] arg, final MessageBean mb) {
        new AlertDialog.Builder(ctx).setTitle("")
                .setItems(arg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                break;

                            case 1:
                                // ClipboardManager cmb = (ClipboardManager) ctx
                                //       .getSystemService(ctx.CLIPBOARD_SERVICE);
                                //cmb.setText(mb.getText());
                                break;
                            case 2:

                                break;
                            case 3:
                                break;
                        }
                        ;
                    }
                }).show();
    }
}

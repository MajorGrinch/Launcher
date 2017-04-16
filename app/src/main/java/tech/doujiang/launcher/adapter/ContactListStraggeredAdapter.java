package tech.doujiang.launcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MyApplication;

/**
 * Created by grinch on 07/04/2017.
 */

public class ContactListStraggeredAdapter extends
        RecyclerView.Adapter<ContactListStraggeredAdapter.ViewHolder> {

    //private LinearLayout single_contact;

    private List<ContactBean> contactList;

    Random myrand = new Random();

    Context context = MyApplication.getContext();
    private static final String TAG = "ContactListStraggeredAd";
    ArrayList<Integer> mycolor = new ArrayList();


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.contact_username);
        }
    }

    public ContactListStraggeredAdapter(List<ContactBean> list) {
        contactList = list;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void remove(int position) {
        contactList.remove(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_straggered_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        mycolor.add(context.getResources().getColor(R.color.seagreen));
        mycolor.add(context.getResources().getColor(R.color.orange));
        mycolor.add(context.getResources().getColor(R.color.limegreen));
        mycolor.add(context.getResources().getColor(R.color.mediumturquoise));
        mycolor.add(context.getResources().getColor(R.color.orchid));
        mycolor.add(context.getResources().getColor(R.color.purple));
        LinearLayout linearLayout = (LinearLayout)holder.itemView.findViewById(R.id.contact_straggered_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                ContactBean contact = contactList.get(pos);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+contact.getPhoneNum()));
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ContactBean contact = contactList.get(position);
        holder.name.setText(contact.getDisplayName());

        int index = position % mycolor.size();
        Log.d("Choose color", Integer.toString(index));
        holder.itemView.setBackgroundColor((mycolor.get(index)));
    }
}

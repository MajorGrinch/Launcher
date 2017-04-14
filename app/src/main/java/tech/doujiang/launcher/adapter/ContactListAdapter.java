package tech.doujiang.launcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.ContactDetailActivityBeta;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MyApplication;

/**
 * Created by grinch on 08/04/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private List<ContactBean> mcontactList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView name;
        public ViewHolder(View view){
            super(view);
            name = (TextView)view.findViewById(R.id.contact_list_name);
            linearLayout = (LinearLayout)view.findViewById(R.id.contact_list_layout);
        }
    }

    public ContactListAdapter(List<ContactBean> contactList){
        mcontactList = contactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                ContactBean contact = mcontactList.get(pos);
                Intent intent = new Intent(v.getContext(), ContactDetailActivityBeta.class);
                intent.putExtra("contactId", contact.getContactId());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactBean contact = mcontactList.get(position);
        holder.name.setText(contact.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mcontactList.size();
    }
}

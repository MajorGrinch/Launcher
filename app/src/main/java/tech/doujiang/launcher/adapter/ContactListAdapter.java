package tech.doujiang.launcher.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.AddContactActivity;
import tech.doujiang.launcher.activity.ContactAppActivity;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MyApplication;

/**
 * Created by grinch on 08/04/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private List<ContactBean> mcontactList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        TextView name;
        public ViewHolder(View view){
            super(view);
            photo = (ImageView)view.findViewById(R.id.contact_list_image);
            name = (TextView)view.findViewById(R.id.contact_list_name);
        }
    }

    public ContactListAdapter(List<ContactBean> contactList){
        mcontactList = contactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactBean contact = mcontactList.get(position);
        holder.name.setText(contact.getDisplayName());
        holder.photo.setImageResource(R.drawable.contacts);
    }

    @Override
    public int getItemCount() {
        return mcontactList.size();
    }
}

package tech.doujiang.launcher.adapter;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import android.R.integer;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.provider.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import tech.doujiang.launcher.model.*;
import tech.doujiang.launcher.view.*;
import tech.doujiang.launcher.R;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class ContactListAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<ContactBean> list;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private Context ctx;

    public ContactListAdapter(Context context, List<ContactBean> list,
                              QuickAlphabeticBar alpha) {
        this.ctx = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.alphaIndexer = new HashMap<String, Integer>();
        this.sections = new String[list.size()];

        for(int i = 0; i < list.size(); i ++) {
            String name = getAlpha(list.get(i).getPinYin());
            if (!alphaIndexer.containsKey(name)) {
                alphaIndexer.put(name, i);
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sectionList.toArray();

        alpha.setAlphaIndexer(alphaIndexer);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        list.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder = new ViewHolder();
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactBean contact = list.get(position);
        String name = contact.getDisplayName();
        String number = contact.getPhoneNum();
        holder.name.setText(name);
        holder.number.setText(number);

        if (contact.getPhotoPath() != null && !contact.getPhotoPath().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(contact.getPhotoPath());
            holder.photo.setImageBitmap(bitmap);
        } else {
            holder.photo.setImageResource(R.drawable.contact);
        }

        String currentStr = getAlpha(contact.getPinYin());

        String previewStr = (position - 1) >= 0 ?
                getAlpha(list.get(position - 1).getPinYin()) : " ";

        if (!previewStr.equals(currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView photo;
        TextView alpha;
        TextView name;
        TextView number;
    }

    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }

    }
}
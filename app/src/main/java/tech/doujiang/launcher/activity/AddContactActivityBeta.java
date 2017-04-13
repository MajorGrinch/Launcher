package tech.doujiang.launcher.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;

public class AddContactActivityBeta extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private ImageButton contactPhoto;
    private EditText contactName, contactNum, contactEmail;
    private ContactBean contact;
    public Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_beta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_contact_toolbar);
        toolbar.setTitle("Create new contact");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black);
        }
        contact = new ContactBean();
        dbHelper = MyDatabaseHelper.getDBHelper(this.getApplicationContext());

        contactPhoto = (ImageButton) findViewById(R.id.contact_photo_beta);
        contactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
                Log.d("AddContactActivityBeta", "Click the contact photo");
            }
        });

        contactName = (EditText) findViewById(R.id.contact_name_beta);
        contactNum = (EditText) findViewById(R.id.contact_number_beta);
        contactEmail = (EditText) findViewById(R.id.contact_email_beta);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                AddContactActivityBeta.this.finish();
                break;
            case R.id.save_contact:
                addContact();
                break;
            default:
                break;
        }
        return true;
    }

    private void choosePhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        // From Android 4.4, we can only get image number.
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的ID
                String id = docId.split(":")[1];
                //获取相册路径
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //Long.valueOf(docId):将 string 参数解析为有符号十进制 long
                //withAppendedId（Uri contentUri, long id)这个方法负责把id和contentUri连接成一个新的Uri
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            contactPhoto.setImageBitmap(bitmap);
        } else {
            Toast.makeText(AddContactActivityBeta.this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }
        contact.setPhotoPath(imagePath);
    }

    private void addContact() {
        if (contactName.getText().toString().isEmpty()) {
            Toast.makeText(AddContactActivityBeta.this, R.string.warn_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (contactNum.getText().toString().isEmpty()) {
            Toast.makeText(AddContactActivityBeta.this, R.string.warn_empty_number, Toast.LENGTH_SHORT).show();
            return;
        }
        if (contactEmail.getText().toString().isEmpty()) {
            Toast.makeText(AddContactActivityBeta.this, R.string.warn_empty_email, Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher match = pattern.matcher(contactEmail.getText().toString());
        if (!match.matches()) {
            Toast.makeText(AddContactActivityBeta.this, R.string.warn_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        contact.setDisplayName(contactName.getText().toString());
        contact.setPhoneNum(contactNum.getText().toString());
        String name = contactName.getText().toString();
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            String[] vals = null;
            try {
                vals = PinyinHelper.toHanyuPinyinStringArray(c, format);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            if (vals == null) {
                sb.append(name.charAt(i));
            } else {
                sb.append(vals[0]);
            }
        }
        contact.setEmail(contactEmail.getText().toString());
        String pinYin = sb.toString().toUpperCase();
        contact.setPinYin(pinYin);
        dbHelper.addContact(contact);
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones == null) {
            Log.i("Phones: ", "NULL");
        }
        Log.i("PhoneCount: ", Integer.toString(phones.getCount()));
        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phoneNumber.equals(contact.getPhoneNum())) {
                String contactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                Uri deleteContactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                int id = getContentResolver().delete(deleteContactUri, null, null);
                Log.i("DeleteContact: ", Integer.toString(id));
            }
        }
        phones.close();
        AddContactActivityBeta.this.finish();
    }

    public boolean verify() {
        return true;
    }
}

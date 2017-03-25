package tech.doujiang.launcher.model;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class ContactBean {
    private int contactId;
    private String displayName;
    private String phoneNum;
    private String photoPath;
    private int selectId = 0;
    private String pinYin;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getContactId() {
        return contactId;
    }
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public int getSelectId() {
        return selectId;
    }
    public void setSelectId(int selectId) {
        this.selectId = selectId;
    }
    public String getPinYin() {
        return pinYin;
    }
    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }
}

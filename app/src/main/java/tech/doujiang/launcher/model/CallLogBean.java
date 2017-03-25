package tech.doujiang.launcher.model;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class CallLogBean {
    private int id;
    private String name;
    private String number;
    private Long date;
    private int duration; // 通话时长
    private int type; // 1. 接入(INCOMING_TYPE) 2. 打出(OUTGOING_TYPE) 3. 未接(MISSED_TYPE)
    private int count;
    private int isRead; // 0 false, 1 true

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

   public int getDuration() {
       return duration;
   }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public CallLogBean() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int isRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}

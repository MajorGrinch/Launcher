package tech.doujiang.launcher.model;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class SMSBean {

    private int thread_id;
    private int msg_count;
    private String msg_snippet;
    private String name;
    private String number;
    private Long date;
    private String read;

    public SMSBean() {
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getMsg_snippet() {
        return msg_snippet;
    }

    public void setMsg_snippet(String msgSnippet) {
        msg_snippet = msgSnippet;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }

    public int getMsg_count() {
        return msg_count;
    }

    public void setMsg_count(int msg_count) {
        this.msg_count = msg_count;
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
}

package tech.doujiang.launcher.model;

/**
 * Created by kirk on 4/24/17.
 */

public class SystemSMSBean {
    private int thread_id;
    private String address;
    private String person;
    private long date;
    private long date_sent;
    private int protocol;
    private int read;
    private int status;
    private int type;
    private int reply_path_present;
    private String subject;
    private String body;
    private String service_center;
    private int locked;
    private int error_code;
    private int seen;

    public int getRead(){
        return this.read;
    }

    public void setRead(int read){
        this.read = read;
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getType(){
        return this.type;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getReply_path_present(){
        return this.reply_path_present;
    }

    public void setReply_path_present(int reply_path_present){
        this.reply_path_present = reply_path_present;
    }

    public String getSubject(){
        return this.subject;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public String getService_center(){
        return this.service_center;
    }

    public void setService_center(String service_center){
        this.service_center = service_center;
    }

    public int getLocked(){
        return this.locked;
    }

    public void setLocked(int locked){
        this.locked = locked;
    }

    public int getError_code(){
        return this.error_code;
    }

    public void setError_code(int error_code){
        this.error_code = error_code;
    }

    public int getSeen(){
        return this.seen;
    }

    public void setSeen(int seen){
        this.seen = seen;
    }

    public int getThread_id(){
        return this.thread_id;
    }

    public void setThread_id(int thread_id){
        this.thread_id = thread_id;
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getPerson(){
        return  this.person;
    }

    public void setPerson(String person){
        this.person = person;
    }

    public long getDate(){
        return this.date;
    }

    public void setDate(long date){
        this.date = date;
    }

    public long getDate_sent(){
        return this.date_sent;
    }

    public void setDate_sent(long date_sent){
        this.date_sent = date_sent;
    }

    public int getProtocol(){
        return this.protocol;
    }

    public void setProtocol(int protocol){
        this.protocol = protocol;
    }

}

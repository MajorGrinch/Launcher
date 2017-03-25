package tech.doujiang.launcher.util;

public class Loginfo {

    private String username;
    private String psw;

    public Loginfo(String username, String psw){
        this.username = username;
        this.psw = psw;
    }

    public String getUsername(){
        return this.username;
    }
    public String getPsw(){
        return this.psw;
    }
}

package tech.doujiang.launcher.util;

public class Loginfo {

    private String username;
    private String psw;
    private String macaddress;

    public Loginfo(String username, String psw, String mac){
        this.username = username;
        this.psw = psw;
        this.macaddress = mac;
    }

    public String getUsername(){
        return this.username;
    }
    public String getPsw(){
        return this.psw;
    }
    public String getMacaddress(){
        return this.macaddress;
    }
}

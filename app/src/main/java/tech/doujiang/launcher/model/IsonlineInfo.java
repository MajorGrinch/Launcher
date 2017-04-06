package tech.doujiang.launcher.model;

import java.io.Serializable;

/**
 * Created by grinch on 05/04/2017.
 */

public class IsonlineInfo implements Serializable {
    private String username;
    private String online;
    private String infoerase;
    private String islost;
    private double longitude;
    private double latitude;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = Boolean.toString(online);
    }

    public String getInfoerase() {
        return infoerase;
    }

    public void setInfoerase(boolean infoerase) {
        this.infoerase = Boolean.toString(infoerase);
    }

    public String getIslost() {
        return islost;
    }

    public void setIslost(boolean islost) {
        this.islost = Boolean.toString(islost);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

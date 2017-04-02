package tech.doujiang.launcher.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by grinch on 01/04/2017.
 */

public class RSAKey implements Serializable {
    private Map<String, byte[]> keyMap;

    public Map<String, byte[]> getKeyMap(){
        return keyMap;
    }

    public void setKeyMap(Map<String, byte[]> keyMap){
        this.keyMap = keyMap;
    }
}

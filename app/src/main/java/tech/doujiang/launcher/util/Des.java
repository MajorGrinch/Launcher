package tech.doujiang.launcher.util;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.io.FileWriter;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Des {
    private SecretKeySpec key;
    public Des(String str) {
        setKey(str);
    }
    public Des(){}

    public void setKey(String strkey){
        try{
            this.key = new SecretKeySpec( strkey.getBytes("UTF-8"), "DES" );
        }catch(UnsupportedEncodingException ex){
            System.out.println("Use default encoding");
            this.key = new SecretKeySpec( strkey.getBytes(), "DES");
        }
    }


    public void encrypt(int start, String file, String destFile) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        InputStream preinput = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int num = preinput.available();
        int remain = (num-start) %8;
        if( remain != 0 ){
            preinput.read(buffer);
            for( int i = 0; i < 8-remain; i++ )
                buffer[num+i] = ' ';
            FileWriter w = new FileWriter(file);
            w.write(new String(buffer).substring(0,num+8-remain));
            w.close();
            preinput.close();
        }
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(destFile);

        is.read( buffer, 0, start );
        CipherInputStream cis = new CipherInputStream(is, cipher);
        int r;
        while ((r = cis.read(buffer, start, buffer.length-start)) > 0) {
            out.write(buffer, 0, r+start);

        }
        cis.close();
        is.close();
        out.close();
    }

    public void decrypt(int start, String file, String destFile) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        InputStream preinput = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int num = preinput.available();
        int remain = (num-start) % 8;
        Log.v("key", this.key.toString());
        Log.v("start, num and remain", "start: " + start + "num: " + num + " remain: " + remain);
        if( remain != 0 ){
            preinput.read(buffer);
            for( int i = 0; i < 8-remain; i++ )
                buffer[num+i] = ' ';
            FileWriter w = new FileWriter(file);
            Log.v("Write back", new String(buffer).substring(0, num+8-remain));
            w.write( new String(buffer).substring(0,num+8-remain) );
            w.close();
            preinput.close();
        }
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(destFile);

        is.read(buffer, 0, start);
        out.write(buffer,0, start);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        int r;
        while ((r = is.read(buffer, 0, buffer.length-start)) >= 0) {
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
    }
}


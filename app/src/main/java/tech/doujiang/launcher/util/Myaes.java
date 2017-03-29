package tech.doujiang.launcher.util;
/**
 * Created by grinch on 19/12/2016.
 */

import javax.crypto.Cipher;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 算法 java
 *
 * @author Grinch
 */
public class Myaes {
    private static Cipher cipher;
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM_ECB = "AES/ECB/PKCS5Padding";
    private static SecretKeySpec secretKey;

    private static void setKey(String strkey) {
        try {
            secretKey = new SecretKeySpec(strkey.getBytes("UTF-8"), KEY_ALGORITHM);
        } catch (Exception ex) {
            secretKey = new SecretKeySpec(strkey.getBytes(), KEY_ALGORITHM);
        }
    }

    public static void init(String str) throws Exception {
        setKey(str);
        cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
    }

    public static byte[] encrypt(byte[] content) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] result = cipher.doFinal(content);
        return result;
    }

    public static byte[] decrypt(byte[] content) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] result = cipher.doFinal(content);
        return result;
    }

    public static void encryptFile(int start, String file, String destFile) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        fis.read( buffer, 0, start );
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int l;
        while ( (l = cis.read(buffer, start, buffer.length - start)) != -1 ) {
            if ( l == 16 ) {
                fos.write(buffer, start, l);
            } else {
                System.out.println("l: " + l);
                fos.write(buffer, 0, l + start);
            }
        }
        cis.close();
        fis.close();
        fos.close();
    }

    public static void decryptFile(int start, String file, String destFile) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(destFile);
        byte[] buffer = new byte[fis.available()];
        System.out.println("file size: " + fis.available());
        fis.read(buffer, 0, start);
        fos.write(buffer, 0, start);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int l;
        while ((l = fis.read(buffer, 0, buffer.length - start)) != -1) {
            System.out.println("decrypt: " + l );
            cos.write(buffer, 0, l);
        }
        cos.close();
        fos.close();
        fis.close();
    }
}

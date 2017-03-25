package tech.doujiang.launcher.util;

/**
 * Created by grinch on 19/12/2016.
 */


import javax.crypto.Cipher;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.spec.SecretKeySpec;

/**
 * AES 算法 java
 *
 * @author Grinch
 */
public class Myaes {
    static Cipher cipher;
    static final String KEY_ALGORITHM = "AES";
    static final String CIPHER_ALGORITHM_ECB = "AES/ECB/PKCS5Padding";
    static SecretKeySpec secretKey;

    private void setKey(String strkey) {
        try {
            secretKey = new SecretKeySpec(strkey.getBytes("UTF-8"), KEY_ALGORITHM);
        } catch (Exception ex) {
            System.out.println("Use default encoding");
            secretKey = new SecretKeySpec(strkey.getBytes(), KEY_ALGORITHM);
        }
    }

    public void init(String str) throws Exception {
        setKey(str);
        cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
    }


    public static void main(String[] args) throws Exception { //使用样例
        Myaes a = new Myaes();
        a.init("123456789abcdefg");
        FileInputStream in = new FileInputStream("a.txt");
        byte[] plaintext = new byte[in.available()];
        in.read(plaintext);
        in.close();
        byte[] ciphertext = a.encrypt(plaintext);

        System.out.println(ciphertext.length);
        FileOutputStream out = new FileOutputStream("temp.txt");
        out.write(ciphertext);
        out.close();

        FileInputStream inf = new FileInputStream("temp.txt");
        byte[] encryptedtext = new byte[inf.available()];
        inf.read(encryptedtext);
        inf.close();
        System.out.println(encryptedtext.length);
        byte[] decryptedtext = a.decrypt(encryptedtext);
        FileOutputStream outf = new FileOutputStream("result.txt");
        outf.write(decryptedtext);
        outf.close();
    }

    public byte[] encrypt(byte[] content) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] result = cipher.doFinal(content);
        return result;
    }

    public byte[] decrypt(byte[] content) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] result = cipher.doFinal(content);
        return result;
    }

}

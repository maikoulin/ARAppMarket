package com.winhearts.arappmarket.utils;

//import org.apache.commons.codec.binary.Base64;

import android.util.Base64;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密，主要用于服务器返回的加密信息解密
 * Created with IntelliJ IDEA. User: hongxingshi Date: 14-9-23 Time: 上午10:59 To
 * change this template use File | Settings | File Templates.
 */
public class AesUtils {

    private static final int KEY_LENGTH = 128;
    private static final String AES = "AES";
    private static final String ENCODE = "UTF-8";

    private static final String IV = "12345678abcdefgh";

    private static final int KEY_SIZE = 16;

    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static final String PASSWORD = "56212235226886758978225025933009171679";

    public static String encryptBase64(String content, String password) throws Exception {
        byte keyPtr[] = new byte[KEY_SIZE];
        byte passPtr[] = password.getBytes(ENCODE);
        for (int i = 0; i < KEY_SIZE; i++) {
            if (i < passPtr.length) {
                keyPtr[i] = passPtr[i];
            } else {
                keyPtr[i] = 0;
            }
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyPtr, AES);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes(ENCODE)));
        byte[] result = cipher.doFinal(content.getBytes(ENCODE));
        return Base64.encodeToString(result, Base64.NO_WRAP);
    }

    public static String decryptBase64(String content, String password) throws Exception {
        byte keyPtr[] = new byte[KEY_SIZE];
        byte passPtr[] = password.getBytes(ENCODE);
        for (int i = 0; i < KEY_SIZE; i++) {
            if (i < passPtr.length) {
                keyPtr[i] = passPtr[i];
            } else {
                keyPtr[i] = 0;
            }
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyPtr, AES);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes(ENCODE)));
        byte[] base64 = Base64.decode(content, Base64.NO_WRAP);
        byte[] result = cipher.doFinal(base64);
        return new String(result, Charset.forName(ENCODE)); // 加密
    }

    public static void main(String[] args) throws Exception {
        String password = "1212";
        System.out.println(AesUtils.encryptBase64(password, "56212235226886758978225025933009171679"));

        System.out.println(AesUtils.decryptBase64(AesUtils.encryptBase64(password, "56212235226886758978225025933009171679"), "56212235226886758978225025933009171679"));

    }
}

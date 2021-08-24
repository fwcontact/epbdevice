package com.epb.epbdevice.woo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignUtil {
	private SignUtil() {
    }

    public static byte[] encryptHmacByte(String data, String secret) throws Exception {
        SecretKeySpec gse = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacMD5");
        Mac msg1 = Mac.getInstance(gse.getAlgorithm());
        msg1.init(gse);
        byte[] bytes1 = msg1.doFinal(data.getBytes("UTF-8"));
        return bytes1;
    }

    public static String encryptHmacString(String data, String secret) throws Exception {
        byte[] hmacByte = encryptHmacByte(data, secret);
        return byte2hex(hmacByte);
    }

    public static byte[] encryptMD5(String data) throws Exception {
        return encryptMD5(data.getBytes("UTF-8"));
    }

    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest gse = MessageDigest.getInstance("MD5");
        byte[] bytes1 = gse.digest(data);
        return bytes1;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 255);
            if (hex.length() == 1) {
                sign.append("0");
            }

            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    //md5加密
    public synchronized static String getMD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.memberson;

import sun.misc.BASE64Encoder;

/**
 *
 * @author s a m s u n g
 */
public class AES {

    // 加密
    public static String Encrypt(String sSrc) throws Exception {
        byte[] bSrc = sSrc.getBytes("utf-8");
        String enSrc = new BASE64Encoder().encode(bSrc);
        return enSrc;
    }
}


package com.github.tvbox.osc.bbox.util.urlhttp;

import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class AESUtil
{
    public static byte[] decryptData(byte[] array, final String s, final String s2) throws Exception {
        try {
            final Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(2, new SecretKeySpec(s.getBytes(), "AES"), new IvParameterSpec(s2.getBytes()));
            array = instance.doFinal(array);
            array = Base64.getDecoder().decode(array);
            return array;
        }
        catch (Exception ex) {
            throw ex;
        }
    }

    public static String desEncry(final String s, String substring) throws Exception {
        String substring2;
        if (substring.length() > 16) {
            substring2 = substring.substring(substring.length() - 16);
            substring = substring.substring(0, 16);
        }
        else {
            substring2 = substring;
        }
        return new String(decryptData(parseHexStr2Byte(s), substring, substring2), "UTF-8");
    }

    public static String encry(final String s, String substring) throws Exception {
        String substring2;
        if (substring.length() > 16) {
            substring2 = substring.substring(substring.length() - 16);
            substring = substring.substring(0, 16);
            final StringBuilder sb = new StringBuilder();
            sb.append(substring2);
            sb.append(substring);
            sb.toString();
        }
        else {
            substring2 = substring;
        }
        return parseByte2HexStr(encryptData(s, substring, substring2));
    }

    public static byte[] encryptData(final String s, final String s2, final String s3) throws Exception {
        try {
            final Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final byte[] encode = Base64.getEncoder().encode(s.getBytes("UTF-8"));
            final byte[] array = new byte[encode.length];
            System.arraycopy(encode, 0, array, 0, encode.length);
            instance.init(1, new SecretKeySpec(s2.getBytes(), "AES"), new IvParameterSpec(s3.getBytes()));
            return instance.doFinal(array);
        }
        catch (Exception ex) {
            throw ex;
        }
    }

    public static String parseByte2HexStr(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            String s2;
            final String s = s2 = Integer.toHexString(array[i] & 0xFF);
            if (s.length() == 1) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append('0');
                sb2.append(s);
                s2 = sb2.toString();
            }
            sb.append(s2.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(final String s) {
        if (s.length() < 1) {
            return null;
        }
        final byte[] array = new byte[s.length() / 2];
        for (int i = 0; i < s.length() / 2; ++i) {
            final int n = i * 2;
            final int n2 = n + 1;
            array[i] = (byte)(Integer.parseInt(s.substring(n, n2), 16) * 16 + Integer.parseInt(s.substring(n2, n + 2), 16));
        }
        return array;
    }
}



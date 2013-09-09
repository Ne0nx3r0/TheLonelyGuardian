package com.gmail.ne0nx3r0.lonelyguardian.crypto;

import java.security.MessageDigest;

public class MD5Digest
{
    //Note: This should ONLY be used for verifying connections, and not for passwords
    public static String getHash(String str) throws Exception 
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        md.update(str.getBytes());
        
        byte[] digest = md.digest();
        
        StringBuilder sb = new StringBuilder();
        
        for (byte b : digest) {
                sb.append(Integer.toHexString((int) (b & 0xff)));
        }

        return sb.toString();
    }
}
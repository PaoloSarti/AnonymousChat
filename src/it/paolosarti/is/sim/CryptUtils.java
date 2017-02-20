package it.paolosarti.is.sim;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptUtils {

    public static IvParameterSpec generateIv(int size) {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[size];
        sr.nextBytes(bytes);
        return new IvParameterSpec(bytes);
    }

    public static String ivToString(IvParameterSpec iv){
        return Base64.getEncoder().encodeToString(iv.getIV());
    }

    public static IvParameterSpec ivFromString(String ivString){
        return new IvParameterSpec(Base64.getDecoder().decode(ivString));
    }

    public static String padWithSpaces(String s, int desiredLength){
        int off = desiredLength-s.length();
        int toAdd = off>0?off:0;
        StringBuilder sb = new StringBuilder(s);
        for(int i=0; i<off; i++){
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String padWithSpacesMultiple(String s, int mult){
        int desiredlength = (s.length()/mult+2)*mult;
        return padWithSpaces(s, desiredlength);
    }
}
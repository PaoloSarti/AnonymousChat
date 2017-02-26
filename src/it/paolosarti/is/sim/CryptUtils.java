package it.paolosarti.is.sim;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static SecretKey calculateMasterSecret(SecretKey pre_master_secret, BigInteger ra, BigInteger rb ){
        byte[] raBytes = ra.toByteArray();
        byte[] rbBytes = rb.toByteArray();
        byte[] preBytes = pre_master_secret.getEncoded();
        byte[] concatBytes = new byte[raBytes.length+rbBytes.length+preBytes.length];
        System.arraycopy(raBytes, 0, concatBytes, 0, raBytes.length);
        System.arraycopy(rbBytes, 0, concatBytes, raBytes.length, rbBytes.length);
        System.arraycopy(preBytes, 0, concatBytes, raBytes.length+rbBytes.length, preBytes.length);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digested = md.digest(concatBytes);
            SecretKeySpec key = new SecretKeySpec(digested, 0, 16, "AES");
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get num init vectors of nBytes from a SecureRandom seeded with a master key
     * @param key
     * @param num
     * @param nBytes
     * @return
     */
    public static IvParameterSpec[] createIvsFromKey(SecretKey key, int num, int nBytes){
        SecureRandom rand = new SecureRandom(key.getEncoded());
        IvParameterSpec[] ivs = new IvParameterSpec[num];

        for(int i=0; i<num; i++){
            byte[] bytes = new byte[nBytes];
            rand.nextBytes(bytes);
            ivs[i] = new IvParameterSpec(bytes);
        }
        return ivs;
    }

    public static String padWithSpaces(String s, int desiredLength){
        int off = desiredLength-s.getBytes().length;
        int toAdd = off>0?off:0;
        StringBuilder sb = new StringBuilder(s);
        for(int i=0; i<off; i++){
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String padWithSpacesMultiple(String s, int mult){
        int desiredlength = (s.getBytes().length/mult+2)*mult;
        return padWithSpaces(s, desiredlength);
    }

}
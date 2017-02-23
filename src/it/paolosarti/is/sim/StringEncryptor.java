package it.paolosarti.is.sim;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;

/**
 * Created by paolo on 20/02/2017.
 */
public class StringEncryptor extends  StringCrypto{

    public StringEncryptor(SecretKey key, IvParameterSpec iv, String transformation){
        super(key, iv, transformation);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String message){
        try {
            String paddedWithSpaces = CryptUtils.padWithSpacesMultiple(message, 16);
            byte[] strBytes = paddedWithSpaces.getBytes("UTF-8");
            byte[] encrypted = cipher.update(strBytes);
            //System.out.println("encrypted string:" + Base64.getEncoder().encodeToString(encrypted));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}

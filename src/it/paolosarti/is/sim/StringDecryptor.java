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
public class StringDecryptor extends StringCrypto{
    public StringDecryptor(SecretKey key, IvParameterSpec iv, String transformation) {
        super(key, iv, transformation);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public String decrypt(String encrypted){
        try {
            byte[] original = cipher.update(Base64.getDecoder()
                    .decode(encrypted.getBytes("UTF-8")));
            return new String(original).trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}

package it.paolosarti.is.sim;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;

/**
 * Created by paolo on 20/02/2017.
 */
public class StringCrypto {
    protected Cipher cipher;
    protected final SecretKey key;
    protected final IvParameterSpec iv;
    protected final String transformation;

    public StringCrypto(SecretKey key, IvParameterSpec iv, String transformation){
        this.key = key;
        this.iv = iv;
        this.transformation = transformation;
        try {
            this.cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
}

package it.paolosarti.is.dh.chat;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class Encryptor {

    public static String encryptString(String algorithm, SecretKey key, String value) {
        byte[] encryptedBytes=null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            cipherOutputStream.write(value.getBytes());
            cipherOutputStream.flush();
            cipherOutputStream.close();
            encryptedBytes = outputStream.toByteArray();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptString(String algorithm, SecretKey key, String cryptoString) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] data = Base64.getDecoder().decode(cryptoString);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(data), cipher);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buf)) >= 0)
                outputStream.write(buf, 0, bytesRead);

            cipherInputStream.close();

            result = new String(outputStream.toByteArray());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] concatByteArrays(byte[] a, byte[] b){
        byte[] c = new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, a.length, c, a.length, b.length);
        return c;
    }

    /**
     public static String encrypt(SecretKey key, String value) {
     try {

     Cipher cipher = Cipher.getInstance("AES");
     cipher.init(Cipher.ENCRYPT_MODE, key);

     byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
     System.out.println("encrypted string: "
     + Base64.getEncoder().encodeToString(encrypted));

     return Base64.getEncoder().encodeToString(encrypted);
     } catch (Exception ex) {
     ex.printStackTrace();
     }

     return null;
     }

     public static String decrypt(SecretKey key, String encrypted) {
     try {
     Cipher cipher = Cipher.getInstance("AES");
     cipher.init(Cipher.DECRYPT_MODE, key);

     byte[] original = cipher.doFinal(encrypted.getBytes("UTF-8"));

     return new String(original);
     } catch (Exception ex) {
     ex.printStackTrace();
     }

     return null;
     }
     **/

}
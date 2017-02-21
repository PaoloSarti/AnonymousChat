package it.paolosarti.is.dh;

import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

public class DiffieHellman {

    private BigInteger P;
    private BigInteger G;
    private BigInteger X;
    private BigInteger Y;
    private KeyPair kp;

    public DiffieHellman(int keysize){
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DiffieHellman");
            kpg.initialize(keysize);
            init(kpg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public DiffieHellman(BigInteger P, BigInteger G){
        try {
            DHParameterSpec param = new DHParameterSpec(P, G);
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DiffieHellman");
            kpg.initialize(param);

            init(kpg);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public DiffieHellman(BigInteger P, BigInteger G, BigInteger X, BigInteger Y){
        this.P = P;
        this.G = G;
        this.X = X;
        this.Y = Y;

        DHPublicKeySpec pubSpec = new DHPublicKeySpec(Y,P,G);
        DHPrivateKeySpec privSpec = new DHPrivateKeySpec(X,P,G);

        try {

            KeyFactory kf = KeyFactory.getInstance("DiffieHellman");
            this.kp = new KeyPair(kf.generatePublic(pubSpec), kf.generatePrivate(privSpec));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private void init(KeyPairGenerator kpg){
        try {
            kp = kpg.generateKeyPair();
            KeyFactory kfactory = KeyFactory.getInstance("DiffieHellman");

            DHPublicKeySpec kspec = kfactory.getKeySpec(kp.getPublic(), DHPublicKeySpec.class);
            DHPrivateKeySpec pkspec = kfactory.getKeySpec(kp.getPrivate(), DHPrivateKeySpec.class);

            this.P = kspec.getP();
            this.G = kspec.getG();
            this.Y = kspec.getY();
            this.X = pkspec.getX();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static DiffieHellman load(String fileName, boolean refresh){
        Properties p = new Properties();

        DiffieHellman dh;

        try {
            p.load(new FileInputStream(fileName));
            BigInteger P = new BigInteger((String) p.get("P"));
            BigInteger G = new BigInteger((String) p.get("G"));

            if(!refresh) {
                try {
                    BigInteger X = new BigInteger((String) p.get("X"));
                    BigInteger Y = new BigInteger((String) p.get("Y"));
                    dh = new DiffieHellman(P, G, X, Y);
                }
                catch(Exception e) {
                    dh = new DiffieHellman(P, G);
                }
            }else{
                dh = new DiffieHellman(P, G);
            }

        } catch (IOException e) {
            dh = new DiffieHellman(1024);
            dh.store(fileName, !refresh);
        }

        return dh;
    }

    public static DiffieHellman load(String fileName) {
        return load(fileName,true);
    }

    public void store(String fileName, boolean memoXY){
        Properties p = new Properties();
        p.setProperty("P",P.toString());
        p.setProperty("G",G.toString());
        if(memoXY) {
            p.setProperty("X",X.toString());
            p.setProperty("Y",Y.toString());
        }

        File f = new File(fileName);

        try {
            p.store(new FileOutputStream(new File(fileName)),"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store(String fileName){
        store(fileName,false);
    }

    public BigInteger getK(BigInteger otherY){
        return otherY.modPow(X,P);
    }

    /**
     *
     * Get a key from the most significant bits of the shared key K, derived from the public key otherY
     *
     * @param otherY
     * @param bits
     * @param algorithm
     * @return
     */
    public SecretKey getK(BigInteger otherY, int bits, String algorithm){
        int bytes = bits/8;
        byte[] biKey = getK(otherY).toByteArray();

        return  new SecretKeySpec(biKey,0, bytes, algorithm.split("/")[0]);
    }

    public void print(){
        System.out.println("DiffieHellman parameters:");
        System.out.println("P: " + P);
        System.out.println("G: " + G);
        System.out.println("X: " + X);
        System.out.println("Y: " + Y);
        System.out.println();
    }

    public KeyPair getKp() {
        return kp;
    }

    public BigInteger getP() {
        return P;
    }

    public BigInteger getG() {
        return G;
    }

    public BigInteger getX() {
        return X;
    }

    public BigInteger getY() {
        return Y;
    }
}

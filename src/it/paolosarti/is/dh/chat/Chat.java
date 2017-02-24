package it.paolosarti.is.dh.chat;

import it.paolosarti.is.dh.DiffieHellman;
import it.paolosarti.is.sim.CryptUtils;
import it.paolosarti.is.sim.StringDecryptor;
import it.paolosarti.is.sim.StringEncryptor;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class Chat {
    private final ObjectOutputStream outSocket;
    private final ObjectInputStream inSocket;
    private final DiffieHellman dh;
    private final String algorithm;
    private final boolean server;
    private final boolean debug;

    public Chat(Socket socket, DiffieHellman dh, String algorithm, boolean server, boolean debug) throws Exception{
        this.outSocket = new ObjectOutputStream(socket.getOutputStream());
        this.inSocket = new ObjectInputStream(socket.getInputStream());
        this.dh = dh;
        this.algorithm = algorithm;
        this.server = server;
        this.debug = debug;
    }

    public void chatOnSocket(){

        try {
            //Diffie Hellman

            //Assume known the public parameters P and G

            //Send public parameter Y
            outSocket.writeUTF(dh.getY().toString());
            outSocket.flush();

            //Read the other's Y
            String sY = inSocket.readUTF();

            //System.out.println("Public parameter received: "+sY);
            System.out.println("Public parameter received");

            //Calculate the pre_master from the other's public parameter
            SecretKey pre_master_key = dh.getK(new BigInteger(sY), 128, algorithm);
            System.out.println("Calculated pre master secret");

            //Send and receive a parameter to randomize the master secret
            BigInteger[] rands = shareRandoms();

            //Calculate the master secret
            SecretKey master_secret = CryptUtils.calculateMasterSecret(pre_master_key, server?rands[0]:rands[1], server?rands[1]:rands[0]);

            System.out.println("Shared master secret calculated");
            if(debug)
                System.out.println("Key length: "+master_secret.getEncoded().length*8+"\n");

            //Create the ivs
            IvParameterSpec[] ivs = CryptUtils.createIvsFromKey(master_secret, 2, 16);

            System.out.println("Start Chat");
            listenInput(master_secret, server?ivs[1]:ivs[0]);
            System.out.print(">");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            StringEncryptor se = new StringEncryptor(master_secret, server?ivs[0]:ivs[1], algorithm);

            while((line = br.readLine())!= null){
                String encrypted = se.encrypt(line);
                if(debug)
                    System.out.println("Encrypted string to send: "+encrypted);
                outSocket.writeUTF(encrypted);
                outSocket.flush();
                System.out.print(">");
            }
            //t.interrupt();
            outSocket.close();
            inSocket.close();
            System.out.println("Chat ended");
        }
        catch (Exception e) {
            System.out.println("Unable to get socket streams...");
            if(debug);
                e.printStackTrace();
        }
    }

    private BigInteger[] shareRandoms() throws Exception {
        BigInteger[] bigs = new BigInteger[2];
        SecureRandom sr = new SecureRandom();

        //create the first one
        bigs[0] = new BigInteger(1024, sr);
        //and send it
        if(debug)
            System.out.println("Sending generated random: "+bigs[0]);

        outSocket.writeUTF(bigs[0].toString());
        outSocket.flush();
        //then receive the other

        bigs[1] = new BigInteger(inSocket.readUTF());

        if(debug)
            System.out.println("Received random: "+bigs[1]);

        return bigs;
    }

    private void listenInput(SecretKey key, IvParameterSpec iv) {
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                StringDecryptor sd = new StringDecryptor(key, iv, algorithm);
                try {
                    while (!this.isInterrupted()) {
                        String encrypted = inSocket.readUTF();
                        if(debug)
                            System.out.println("Encrypted string received: "+encrypted);
                        String s = sd.decrypt(encrypted);
                        System.out.print("\b");
                        System.out.println("<"+s);
                        System.out.print(">");
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Stopped listening");
                }
            }
        };
        t.start();
    }

    private IvParameterSpec[] oldShareIvs() throws Exception{
        //Share initialization vectors
        IvParameterSpec [] ivs = new IvParameterSpec[2];
        if(server){
            ivs[0] = CryptUtils.generateIv(16);
            ivs[1] = CryptUtils.generateIv(16);
            String ivString1 = CryptUtils.ivToString(ivs[0]);
            String ivString2 = CryptUtils.ivToString(ivs[1]);
            outSocket.writeUTF(ivString1);
            outSocket.writeUTF(ivString2);
            outSocket.flush();
            System.out.println("Sent IV1: "+ivString1);
            System.out.println("Sent IV2: "+ivString1);
        }
        else {
            String ivString1 = inSocket.readUTF();
            String ivString2 = inSocket.readUTF();
            ivs[0] = CryptUtils.ivFromString(ivString1);
            ivs[1] = CryptUtils.ivFromString(ivString2);
            System.out.println("Received IV1: "+ivString1);
            System.out.println("Received IV2: "+ivString2);
        }
        return ivs;
    }

}

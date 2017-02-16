package it.paolosarti.is.dh.chat;

import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

public class ChatUtils {
    public static void listenInput(ObjectInputStream inSocket, SecretKey key, String algorithm) {
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    while (!this.isInterrupted()) {
                        String encrypted = inSocket.readUTF();
                        //System.out.println("Encrypted string received: "+encrypted);
                        String s = Encryptor.decryptString(algorithm, key, encrypted);
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

    public static void chatOnSocket(Socket socket, DiffieHellman dh, String algorithm){
        ObjectOutputStream outSocket;
        ObjectInputStream inSocket;

        try {
            outSocket = new ObjectOutputStream(socket.getOutputStream());
            inSocket = new ObjectInputStream(socket.getInputStream());

            //Diffie Hellman

            //Assume known the public parameters P and G

            //Send public parameter Y
            outSocket.writeUTF(dh.getY().toString());
            outSocket.flush();

            //Read the other's Y
            String sY = inSocket.readUTF();

            //Anonimous DH Variant
            //Generate some bytes to ensure randomization even if the DH parameters are the same
            //SecureRandom sr = new SecureRandom();
            //sr.nextBytes();

            //System.out.println("Public parameter received: "+sY);
            System.out.println("Public parameter received");
            SecretKey key = dh.getK(new BigInteger(sY), 128, algorithm);

            //System.out.println("Shared secret key: "+dh.getK(new BigInteger(sY)));
            System.out.println("Shared secret key calculated");
            System.out.println("Key length: "+key.getEncoded().length*8+"\n");
            System.out.println("Start Chat");
            ChatUtils.listenInput(inSocket, key, algorithm);
            System.out.print(">");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while((line = br.readLine())!= null){
                String encrypted = Encryptor.encryptString(algorithm, key, line);
                //System.out.println("Encrypted string to send: "+encrypted);
                outSocket.writeUTF(encrypted);
                outSocket.flush();
                System.out.print(">");
            }
            //t.interrupt();
            socket.close();
            System.out.println("Chat ended");
        }
        catch (IOException e) {
            System.out.println("Unable to get socket streams...");
            e.printStackTrace();
        }
    }

}

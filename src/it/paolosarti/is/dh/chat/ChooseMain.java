package it.paolosarti.is.dh.chat;

import it.paolosarti.is.DiffieHellman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChooseMain {
    private static String address = "localhost";
    private static int port = 3000;
    private static final String fileName = "dh.properties";
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    public static void main(String[] args){
        if(args.length!=1){
            System.out.println("USAGE: java -jar AnonymousChat (client|c)|(server|s)");
            System.exit(1);
        }

        DiffieHellman dh = DiffieHellman.load(fileName);

        if(args[0].equals("client")||args[0].equals("c")){
            try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                String line;

                System.out.println("Connect to: ("+address+":"+port+")");
                while((line=br.readLine())!=null) {
                    if(!line.equals("")){
                        String[] splitted = line.split(":");
                        address = splitted[0];
                        port = Integer.parseInt(splitted[1]);
                    }

                    System.out.println("Requesting connection");
                    Socket server = new Socket(address, port);
                    System.out.println("Connected");

                    ChatUtils.chatOnSocket(server, dh, algorithm, false);

                    System.out.println("Connect to: ("+address+":"+port+")");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(args[0].equals("server")||args[0].equals("s")){

            try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                String line;
                System.out.println("Listen on port: ("+port+")");

                line = br.readLine();

                if(!line.equals("")){
                    port = Integer.parseInt(line);
                }

                ServerSocket server = new ServerSocket(port);
                System.out.println("Listening on port " + port);
                Socket client = server.accept();

                System.out.println("Accept chat? (ctrl+D or ctrl+Z) to refuse and exit");
                while((line=br.readLine())!=null) {

                    System.out.println("New connection on port: " + client.getLocalPort() + "\n");
                    ChatUtils.chatOnSocket(client, dh, algorithm, true);

                    System.out.println("Continue listening? (ctrl+D or ctrl+Z) to refuse and exit");
                    line=br.readLine();
                    if(line == null)
                        break;

                    client = server.accept();
                    System.out.println("Accept chat? (ctrl+D or ctrl+Z) to refuse and exit");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            System.out.println("USAGE: java -jar AnonymousChat (client|c)|(server|s)");
            System.exit(1);
        }
    }
}

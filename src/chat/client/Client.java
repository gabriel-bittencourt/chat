package chat.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;

import chat.ClientApp;

public class Client {

    public String ipAddress;
    public int port;

    public Socket server;

    public ClientApp clientApp;

    public BufferedReader inputStream;
    public BufferedWriter outputStream;


    public Client(chat.ClientApp clientApp) {
        this.ipAddress = "";
        this.port = -1;
        this.clientApp = clientApp;
    }

    public Client(String ipAdress, int port) {
        this.ipAddress = ipAdress;
        this.port = port;
    }

    public void run (){

        new Thread( () -> {

            try {
                this.server = new Socket(this.ipAddress, this.port);

                this.inputStream = new BufferedReader(new InputStreamReader(
                        server.getInputStream(), StandardCharsets.UTF_8));
                this.outputStream = new BufferedWriter(new OutputStreamWriter(
                        server.getOutputStream(), StandardCharsets.UTF_8));
                this.outputStream.flush();


                while (true) {
                    this.recMsg();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }

    public void recMsg () throws IOException {
        String msg = this.inputStream.readLine();
        System.out.println("Server >> " + msg);
        Platform.runLater(() -> this.clientApp.addMsg("Server >> " + msg));
    }

    public void sendMsg (String msg) throws IOException {

        System.out.println("You >> " + msg);
        this.outputStream.write(msg + '\n');
        this.outputStream.flush();
        Platform.runLater(() -> this.clientApp.addMsg("You >> " + msg));
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public int getPort(){
        return this.port;
    }

    public void setPort(int port){
        this.port = port;
    }

}

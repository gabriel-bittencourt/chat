package chat.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        String line = "";
        String message = "";
        do{
            message += line;
            line = this.inputStream.readLine();
        }while(!(line.equals("Text") || line.equals("Audio")));

        final String finalMessage = message;
        if(line.equals("Text")){
            System.out.println("Server >> " + finalMessage);
            Platform.runLater(() -> this.clientApp.addMsg("Server >> " + finalMessage));
        }
        else{
            System.out.println("Server >> Mensagem de voz");
            byte[] audio = finalMessage.getBytes(StandardCharsets.UTF_8);
            Platform.runLater(() -> this.clientApp.addMsg(audio));
        }
    }

    public void sendMsg (String msg) throws IOException {
        System.out.println("You >> " + msg);
        this.outputStream.write(msg + "\nText\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.clientApp.addMsg("You >> " + msg));
    }

    public void sendMsg (byte[] audio) throws IOException {
        System.out.println("You >> Mensagem de voz");
        String msg = Base64.getEncoder().encodeToString(audio);
        this.outputStream.write(msg + "\nAudio\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.clientApp.addMsg(audio));
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

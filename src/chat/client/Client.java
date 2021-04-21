package chat.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javafx.application.Platform;

import chat.ClientApp;

public class Client {

    private String ipAddress;
    private int port;

    private Socket server;

    public ClientApp clientApp;

    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    private boolean connected = false;

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
                this.server.setSoTimeout(500);
                this.connected = true;

                this.inputStream = new BufferedReader(new InputStreamReader(
                        server.getInputStream(), StandardCharsets.UTF_8));
                this.outputStream = new BufferedWriter(new OutputStreamWriter(
                        server.getOutputStream(), StandardCharsets.UTF_8));
                this.outputStream.flush();

                this.outputStream.write("Cliente conectado" + "\nText\n");
                this.outputStream.flush();

                while (connected) {

                    this.recMsg();
                }

                this.server.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }

    public void disconnect() throws IOException {
        this.connected = false;
        this.outputStream.write("#exit");
        this.outputStream.flush();
        Platform.runLater(() -> this.clientApp.disableChat());
    }

    public void recMsg () throws IOException {
        try {

            String line = "";
            String message = "";
            do {
                message = message + line;
                line = this.inputStream.readLine();

                if (line.equals("#exit")){
                    this.disconnect();
                    return;
                }

            } while (!(line.equals("Text") || line.equals("Audio")));

            final String finalMessage = message;
            if (line.equals("Text")) {
                System.out.println("Server >> " + finalMessage);
                Platform.runLater(() -> this.clientApp.addMsg("Server >> " + finalMessage));
            } else {
                System.out.println("Server >> Mensagem de voz");
                byte[] audio = Base64.getDecoder().decode(finalMessage);
                Platform.runLater(() -> this.clientApp.addMsg(audio, "Server"));
            }

        }  catch (SocketTimeoutException e){
            // Não faz nada
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
        Platform.runLater(() -> this.clientApp.addMsg(audio, "You"));
    }

    // Getters e Setters

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

    public boolean getConnected() { return this.connected; }

    public void setConnected(boolean connected) { this.connected = connected; }

}
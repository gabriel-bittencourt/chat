package chat.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javafx.application.Platform;


import chat.ServerApp;


public class Server {

    public int port;

    public ServerSocket server;
    public Socket client;

    public ServerApp mainApp;

    public BufferedReader inputStream;
    public BufferedWriter outputStream;


    public Server(ServerApp serverApp) {
        this.port = -1;
        this.mainApp = serverApp;
    }

    public void run () {

        new Thread( () -> {
            try {
                this.server = new ServerSocket(this.port);

                this.client = this.server.accept();

                System.out.println(
                    '\n' + "Conexão estabelecida" + '\n' +
                    '\t' + "IP: " + this.client.getInetAddress() + '\n' +
                    '\t' + "Porta: " + this.port + '\n'
                );

                this.inputStream = new BufferedReader(new InputStreamReader(
                        client.getInputStream(), StandardCharsets.UTF_8));
                this.outputStream = new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream(), StandardCharsets.UTF_8));
                this.outputStream.flush();


                this.outputStream.write("Conexão iniciada" + "\nText\n");
                this.outputStream.flush();

                while (true) {
                    this.recMsg();
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
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
            Platform.runLater(() -> this.mainApp.addMsg("Server >> " + finalMessage));
        }
        else{
            System.out.println("Server >> Mensagem de voz");
            byte[] audio = Base64.getDecoder().decode(finalMessage);
            Platform.runLater(() -> this.mainApp.addMsg(audio));
        }
    }

    public void sendMsg (String msg) throws IOException {

        System.out.println("You >> " + msg);
        this.outputStream.write(msg + "\nText\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.addMsg("You >> " + msg));
    }

    public void sendMsg (byte[] audio) throws IOException {
        System.out.println("You >> Mensagem de voz");
        String msg = new String(audio, StandardCharsets.UTF_8);
        this.outputStream.write(msg + "\nAudio\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.addMsg(audio));
    }

    public int getPort(){
        return this.port;
    }

    public void setPort(int port){
        this.port = port;
    }

}

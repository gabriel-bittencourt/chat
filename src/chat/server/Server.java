package chat.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.nio.charset.StandardCharsets;
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


                this.outputStream.write("Conexão iniciada" + "\n");
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
        String msg = this.inputStream.readLine();
        System.out.println("Client >> " + msg);
        Platform.runLater(() -> this.mainApp.addMsg("Client >> " + msg));
    }

    public void sendMsg (String msg) throws IOException {

        System.out.println("You >> " + msg);
        this.outputStream.write(msg + '\n');
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.addMsg("You >> " + msg));
    }

    public int getPort(){
        return this.port;
    }

    public void setPort(int port){
        this.port = port;
    }

}

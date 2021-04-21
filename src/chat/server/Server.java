package chat.server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javafx.application.Platform;


import chat.ServerApp;


public class Server {

    private int port;

    private ServerSocket server;
    private Socket client;

    public ServerApp mainApp;

    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    private boolean connected = false;

    public Server(ServerApp serverApp) {
        this.port = -1;
        this.mainApp = serverApp;
    }

    public void run () {

        new Thread( () -> {
            try {
                this.server = new ServerSocket(this.port);

                this.client = this.server.accept();
                this.client.setSoTimeout(500);
                this.connected = true;
                Platform.runLater(() -> this.mainApp.enableChat());

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

                while (connected) {

                    this.recMsg();
                }

                System.out.println("Disconectando...");
                this.client.close();
                this.server.close();

            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }

    public void disconnect() throws IOException {
        this.connected = false;
        this.outputStream.write("#exit");
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.disableChat());
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
                System.out.println("Client >> " + finalMessage);
                Platform.runLater(() -> this.mainApp.addMsg("Client >> " + finalMessage));
            } else {
                System.out.println("Client >> Mensagem de voz");
                byte[] audio = Base64.getDecoder().decode(finalMessage);
                Platform.runLater(() -> this.mainApp.addMsg(audio, "Client"));
            }

        } catch (SocketTimeoutException e){
            // Não faz nada
        }
    }

    // Getters e Setters

    public void sendMsg (String msg) throws IOException {

        System.out.println("You >> " + msg);
        this.outputStream.write(msg + "\nText\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.addMsg("You >> " + msg));
    }

    public void sendMsg (byte[] audio) throws IOException {
        System.out.println("You >> Mensagem de voz");
        String msg = Base64.getEncoder().encodeToString(audio);
        this.outputStream.write(msg + "\nAudio\n");
        this.outputStream.flush();
        Platform.runLater(() -> this.mainApp.addMsg(audio, "You"));
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
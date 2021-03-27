package chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Server {

    private int port;
//    private ObjectInputStream socketInput;
//    private OutputStream socketOutput;

    Server (int port) {
        this.port = port;
    }

    public void run () {

        Scanner read = new Scanner(System.in);

        try {

            ServerSocket server = new ServerSocket(this.port);

            System.out.println("Ouvindo a porta: " + this.port);
            Socket client = server.accept();

            System.out.println(
                    '\n' + "Conexão estabelecida" + '\n' +
                    '\t' + "IP: " + client.getInetAddress() +
                    '\t' + "Porta: " + this.port + '\n'
            );

            Scanner socketInput = new Scanner(client.getInputStream());
            PrintStream socketOutput = new PrintStream(client.getOutputStream());
//            socketInput = client.getInputStream();
//            socketOutput = client.getOutputStream();

            // Envia mensagem para o cliente avisando que iniciou conexão
            socketOutput.println("Conexão estabelecida");


            String msg = "";

            while ( true ){

                // Lê mensagem do cliente
                msg = socketInput.nextLine();
                System.out.println("Cliente >> " + msg);

                if( msg.equals("exit") ) {
                    break;
                }

                // Envia mensagem
                System.out.print(">> ");
                msg = read.nextLine();

                if( msg.equals("exit") ) {
                    msg = "Conexão encerrada pelo servidor.";
                    System.out.println(msg);
                    socketOutput.println(msg);
                    break;
                }

                socketOutput.println(msg);
                System.out.println();

            }


            server.close();
            client.close();


        } catch (Exception e){
            //
        }


    }

    public static void main (String[] args) {

        Scanner read = new Scanner(System.in);

        int port = 0;

        try {

            System.out.print("Insira a porta: ");
            port = read.nextInt();
            System.out.println("\nPorta: " + port);

        } catch (Exception e){
            System.err.println("Erro: " + e.toString());
        }

        Server server = new Server(port);
        server.run();

    }

}

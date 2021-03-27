package chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Struct;
import java.util.Scanner;
import java.io.*;

public class Client {

    String ipAdress;
    int port;

    Client (String ipAdress, int port) {
        this.ipAdress = ipAdress;
        this.port = port;
    }

    public void run () {

        Scanner read = new Scanner(System.in);

        try {

            Socket server = new Socket(this.ipAdress, this.port);


            Scanner socketInput = new Scanner(server.getInputStream());
            PrintStream socketOutput = new PrintStream(server.getOutputStream());

            String msg = "";

            // Recebendo mensagem do servidor
            msg = socketInput.nextLine();;
            System.out.println("Servidor >> " + msg);

            while ( true ) {

                // Envia mensagem
                System.out.print(">> ");
                msg = read.nextLine();

                if( msg.equals("exit") ) {
                    msg = "Conexão encerrada pelo cliente.";
                    System.out.println(msg);
                    socketOutput.println(msg);
                    break;
                }

                socketOutput.println(msg);

                // Lê mensagem do servidor
                msg = socketInput.nextLine();
                System.out.println("Servidor >> " + msg);

                if( msg.equals("exit") ) {
                    System.out.println("Conexão encerrada pelo servidor.");
                    break;
                }

            }

            server.close();

        } catch (Exception e){
            //
        }

    }


    public static void main(String[] args) {

        Scanner read = new Scanner(System.in);

        String ipAdress = "";
        int port = 0;

        try {

            System.out.print("Insira a endereço IP: ");
            ipAdress = read.nextLine();
            System.out.println("\nInsira a porta: ");
            port = read.nextInt();

        } catch (Exception e){
            System.err.println("Erro: " + e.toString());
        }

        Client client = new Client(ipAdress, port);
        client.run();


    }

}

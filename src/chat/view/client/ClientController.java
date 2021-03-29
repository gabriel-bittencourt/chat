package chat.view.client;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.Pane;
import chat.client.Client;

import java.io.IOException;


public class ClientController {

    // Server
    public Button connectServerBtn;
    public Button disconnectServerBtn;
    public TextField inputPort;
    public TextField inputIp;
    //

    // Mensagens
    public Pane msgPane;
    public TextField msgField;
    //

    public Client client;

    @FXML
    private void initialize() {
        System.out.println("Iniciou controller");

    }

    public void connect() {

        String ipAddress = inputIp.getText();
        String portTxt = inputPort.getText();
        int port;

        if ( !portTxt.trim().equals("") ){

            try {

                System.out.println("Conectando ao servidor!");

                port = Integer.parseInt(portTxt);

                this.client.setIpAddress(ipAddress);
                this.client.setPort(port);

                this.client.run();

            } catch (Exception e) {
                System.err.println("Erro: " + e.toString());
            }

        }

    }

    public void addMsg(String msg) {



    }

    public void sendMsg() throws IOException {

        String msg = this.msgField.getText();
        this.msgField.setText("");

        this.client.sendMsg(msg);

    }

}

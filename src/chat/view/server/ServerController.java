package chat.view.server;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import chat.server.Server;

import java.io.IOException;


public class ServerController {

    // Server
    public Button startServerBtn;
    public Button stopServerBtn;
    public TextField inputPort;
    //

    // Mensagens
    public Pane msgPane;
    public TextField msgField;
    public Button sendMsgBtn;
    //

    public Server server;

    @FXML
    private void initialize() {
        System.out.println("Iniciou controller");
    }


    public void startServer() {

        if ( this.server.getPort() != -1 ) {
            System.out.println("Servidor já iniciado!");
            return;
        }

        String portTxt = inputPort.getText();
        int port;

        if ( !portTxt.trim().equals("") ){
            try {

                System.out.println("Iniciando servidor");

                port = Integer.parseInt(portTxt);

                this.server.setPort(port);

                this.server.run();

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

        this.server.sendMsg(msg);

    }

}

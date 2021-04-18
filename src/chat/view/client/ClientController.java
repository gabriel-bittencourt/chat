package chat.view.client;


import audio.Microphone;
import audio.Speaker;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.layout.Pane;
import chat.client.Client;
import javafx.scene.text.Text;

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

    private Microphone microphone;
    private Speaker speaker;

    @FXML
    private void initialize() {
        microphone = new Microphone();
        speaker = new Speaker();
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

    public void playAudio(byte[] audio){
        speaker.play(audio);
    }

    public void captureAudio(){
        microphone.startCapturing();
    }

    public void stopAndSendAudio() throws IOException{
        microphone.stopCapturing();
        byte[] audio = microphone.audioData;
        client.sendMsg(audio);
    }

    public void addMsg(String msg) {
        int offset = scrollMsgPane() + 15;
        Text text = new Text(5, offset, msg);
        msgPane.getChildren().add(text);
    }

    public void addMsg(byte[] msg){
        int offset = scrollMsgPane();
        Button button = new Button("Mensagem de voz");
        button.setLayoutY(offset);
        button.setLayoutX(5);
        button.setOnAction((event) -> playAudio(msg));
        msgPane.getChildren().add(button);
    }

    public void sendMsg() throws IOException {

        String msg = this.msgField.getText();
        this.msgField.setText("");

        this.client.sendMsg(msg);

    }

    // Retorna o y a ser usado na prox msg
    private int scrollMsgPane(){
        ObservableList<Node> paneChildren = msgPane.getChildren();
        int offset = 25;
        int maxChildren = (int)(msgPane.getHeight() / offset);
        int nChildren = paneChildren.size();

        if(nChildren >= maxChildren){

            paneChildren.remove(0);
            nChildren--;

            for (Node child: paneChildren) {
                double prevY = child.getLayoutY();
                child.setLayoutY(prevY - offset);
            }
        }

        return (nChildren) * offset;
    }

}

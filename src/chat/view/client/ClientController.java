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
    public Button msgBtn;
    public Button audioBtn;
    //

    public Client client;

    private Microphone microphone;
    private Speaker speaker;


    @FXML
    private void initialize() {
        microphone = new Microphone();
        speaker = new Speaker();

        this.disableChat();
    }

    public void connect() {

        String ipAddress = inputIp.getText();
        String portTxt = inputPort.getText();
        int port;

        if ( !portTxt.trim().equals("") ){

            try {

                port = Integer.parseInt(portTxt);

                this.client.setIpAddress(ipAddress);
                this.client.setPort(port);

                this.client.run();
                this.enableChat();

            } catch (Exception e) {
                System.err.println("Erro: " + e);
            }

        }

    }

    public void disconnect() throws IOException {
        this.client.sendMsg("Cliente encerrou conexão!");
        this.client.disconnect();
    }

    public void enableChat() {
        this.msgField.setDisable(false);
        this.msgBtn.setDisable(false);
        this.audioBtn.setDisable(false);
        this.disconnectServerBtn.setDisable(false);
    }

    public void disableChat() {
        this.msgField.setDisable(true);
        this.msgBtn.setDisable(true);
        this.audioBtn.setDisable(true);
        this.disconnectServerBtn.setDisable(true);
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

    public void addMsg(byte[] msg, String author){
        int offset = scrollMsgPane();
        Pane newPane = new Pane();
        newPane.setLayoutY(offset);

        Text text = new Text(5, 17, author + " >>");

        Button button = new Button("Mensagem de voz");
        button.setLayoutX(60);
        button.setOnAction((event) -> playAudio(msg));

        newPane.getChildren().addAll(text, button);

        msgPane.getChildren().add(newPane);
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
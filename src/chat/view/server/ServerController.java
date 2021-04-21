package chat.view.server;

import audio.Microphone;
import audio.Speaker;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import chat.server.Server;
import javafx.scene.text.Text;

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
    public Button msgBtn;
    public Button audioBtn;
    //

    public Server server;

    private Microphone microphone;
    private Speaker speaker;

    @FXML
    private void initialize() {
        microphone = new Microphone();
        speaker = new Speaker();

        this.disableChat();
    }

    public void startServer() {

        if ( this.server.getConnected() ) {
            return;
        }

        String portTxt = inputPort.getText();
        int port;

        if ( !portTxt.trim().equals("") ){
            try {
                port = Integer.parseInt(portTxt);

                this.server.setPort(port);

                this.server.run();

            } catch (Exception e) {
                System.err.println("Erro: " + e);
            }

        }

    }

    public void disconnect() throws IOException {
        this.server.sendMsg("Servidor encerrou conexão!");
        this.server.disconnect();
    }

    public void enableChat() {
        this.msgField.setDisable(false);
        this.msgBtn.setDisable(false);
        this.audioBtn.setDisable(false);
        this.stopServerBtn.setDisable(false);
    }

    public void disableChat() {
        this.msgField.setDisable(true);
        this.msgBtn.setDisable(true);
        this.audioBtn.setDisable(true);
        this.stopServerBtn.setDisable(true);
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
        this.server.sendMsg(audio);
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

        this.server.sendMsg(msg);

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
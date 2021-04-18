package chat.view.server;

import audio.Microphone;
import audio.Speaker;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    public Button sendMsgBtn;
    //

    public Server server;

    private Microphone microphone;
    private Speaker speaker;

    @FXML
    private void initialize() {
        microphone = new Microphone();
        speaker = new Speaker();
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

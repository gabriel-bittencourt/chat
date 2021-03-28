package view.server;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class Server {

    public TextField msgField;

    public Pane msgPane;

    public void sendMsg () {

        // Lê valor no campo de texto
        String msgText = msgField.getText();

        // Verifica se campo tá vazio
        if (msgText.trim().equals("")){
            msgField.setText("");
            return;
        }

        // "Levanta" as mensagens
        for (Node n : msgPane.getChildren()) {
            n.setLayoutY(n.getLayoutY() - 30);
        }

        // Adiciona a nova mensagem
        newMsg(msgText, msgPane.getWidth()/2, msgPane.getHeight() - 30);

        System.out.println("Mensagem enviada: " + msgText);

        // Limpa campo de texto
        msgField.setText("");

    }

    public void newMsg(String text, double x, double y) {

        Label msg = new Label(text);
        msg.setLayoutX(x);
        msg.setLayoutY(y);
        msgPane.getChildren().add(msg);

    }

}

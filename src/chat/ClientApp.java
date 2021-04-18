package chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import chat.client.Client;
import chat.view.client.ClientController;

public class ClientApp extends Application {


    public Stage primaryStage;
    public Parent root;

    public Client client;
    public ClientController controller;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/client/client.fxml"));
        this.root = loader.load();

        this.primaryStage.setTitle("Cliente");
        this.primaryStage.setResizable(false);
        this.primaryStage.setScene(new Scene(root,500,400));

        this.client = new Client(this);

        this.controller = loader.getController();
        this.controller.client = this.client;

        this.primaryStage.show();

    }

    public static void main(String[] args) {

        launch(args);
    }


    public void addMsg(String msg) {
        this.controller.addMsg(msg);
    }

    public void addMsg(byte[] audio){
        this.controller.addMsg(audio);
    }

}

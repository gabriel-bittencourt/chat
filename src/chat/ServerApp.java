package chat;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import chat.server.Server;
import chat.view.server.ServerController;

public class ServerApp extends Application {

    public Stage primaryStage;
    public Parent root;

    public Server server;
    public ServerController controller;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/server/server.fxml"));
        this.root = loader.load();

        this.primaryStage.setTitle("Server");
        this.primaryStage.setResizable(false);
        this.primaryStage.setScene(new Scene(root,500,400));

        this.server = new Server(this);

        this.controller = loader.getController();
        this.controller.server = this.server;

        this.primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void addMsg(String msg) {
        this.controller.addMsg(msg);
    }

    public void addMsg(byte[] audio, String author) {
        this.controller.addMsg(audio, author);
    }

    public void disableChat() {
        this.controller.disableChat();
    }

    public void enableChat() {
        this.controller.enableChat();
    }

}
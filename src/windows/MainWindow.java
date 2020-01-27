package windows;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import webClient.WebClient;

import java.io.IOException;
import java.net.Socket;

public class MainWindow extends Application{
    private Stage stage;
    private static WebClient webClient;

    public static void main(String[] args) throws IllegalAccessException {
        try {
            Socket clientSocket = new Socket("localhost", 8080);
            try {
                webClient = new WebClient(clientSocket);
                Application.launch(args);
            }catch (Exception ex){
                ex.printStackTrace();
            } finally {
                clientSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void start(Stage st) throws Exception{
        this.stage = st;
        Button incoming = createIncoming();;
        Button friends = createFriends();;
        Button chatGroup = createGroup();
        Button basket = createBasket();
        Button rename = createRename();

        Group group = new Group(incoming, friends, basket, chatGroup, rename);
        Scene scene = new Scene(group);
        scene.setFill(Color.LIGHTBLUE);
        stage.setResizable(false);
        stage.setOnCloseRequest(actionEvent ->{
            try{
               webClient.getCloseEvent();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        stage.getIcons().add(new Image("icons\\chat.jpg"));
        stage.setScene(scene);
        stage.setTitle("Chat");
        stage.setWidth(600);
        stage.setHeight(380);
        stage.show();
    }

    private Button createRename(){
        Button rename = new Button("Rename", new ImageView("icons\\profile.jpg"));
        rename.setPrefHeight(60);
        rename.setPrefWidth(160);
        rename.setLayoutX(440);
        rename.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                TextArea areaOut = new TextArea();
                areaOut.setLayoutX(30);
                areaOut.setLayoutY(20);
                areaOut.setPrefWidth(360);
                areaOut.setPrefHeight(20);
                Button bnt = new Button("Rename");
                bnt.setLayoutY(60);
                bnt.setLayoutX(185);
                bnt.setOnAction(actionEvent ->{
                    try {
                        webClient.setNewName(areaOut.getText());
                        Stage stage = (Stage) bnt.getScene().getWindow();
                        stage.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
                Group group = new Group(areaOut, bnt);
                Scene scene = new Scene(group);
                scene.setFill(Color.LIGHTBLUE);
                Stage newWindow = new Stage();
                newWindow.setTitle("Create new NameOfClient");
                newWindow.setScene(scene);
                newWindow.setWidth(430);
                newWindow.setHeight(140);
                newWindow.setX(stage.getX() + 50);
                newWindow.setY(stage.getY() + 50);
                newWindow.show();
            }
        });
        return rename;
    }

    private Button createIncoming(){
        Button incoming = new Button("Incoming", new ImageView("icons\\message.jpg"));
        incoming.setPrefHeight(60);
        incoming.setPrefWidth(200);
        incoming.setLayoutX(190);
        incoming.setLayoutY(40);
        incoming.setOnAction(actionEvent -> {
            try {
                IncomingWindow incomingWindow = new IncomingWindow(webClient, this);
                incomingWindow.start(stage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return incoming;
    }

    private Button createFriends(){
        Button friends = new Button("Friends", new ImageView("icons\\friends.jpg"));
        friends.setPrefHeight(60);
        friends.setPrefWidth(200);
        friends.setLayoutX(190);
        friends.setLayoutY(100);
        friends.setOnAction(actionEvent -> {
            try {
                FriendsWindow friendsWindow = new FriendsWindow(webClient, this);
                friendsWindow.start(stage);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return friends;
    }

    private Button createGroup(){
        Button chatGroup = new Button("Group", new ImageView("icons\\group.jpg"));
        chatGroup.setPrefHeight(60);
        chatGroup.setPrefWidth(200);
        chatGroup.setLayoutX(190);
        chatGroup.setLayoutY(160);
        chatGroup.setOnAction(actionEvent -> {
            try{
                GroupWindow groupWindow = new GroupWindow(webClient, this);
                groupWindow.start(stage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return chatGroup;
    }

    private Button createBasket(){
        Button basket = new Button("Basket", new ImageView("icons\\basket.jpg"));
        basket.setPrefHeight(60);
        basket.setPrefWidth(200);
        basket.setLayoutX(190);
        basket.setLayoutY(220);
        basket.setOnAction(actionEvent -> {
            try {
                CancelWindow cancelWindow = new CancelWindow(webClient, this);
                cancelWindow.start(stage);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return basket;
    }

}

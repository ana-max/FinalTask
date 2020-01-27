package windows;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import webClient.WebClient;

import java.io.IOException;

public class CancelWindow extends Application {
    private Button back = new Button("Back", new ImageView("icons\\cursor.jpg"));
    private MainWindow mainWindow;
    private WebClient webClient;
    private CancelDialogsWindow dialogWindow;
    private Stage stage;

    CancelWindow(WebClient client, MainWindow window){
        webClient = client;
        mainWindow = window;
        dialogWindow = new CancelDialogsWindow(webClient, mainWindow);
    }

    @Override
    public void start(Stage st) throws Exception {
        stage = st;
        ObservableList<Button> friends = getCancelMessages();
        createBack();
        BorderPane layout = new BorderPane();
        ListView<Button> listView = new ListView<>();
        listView.setItems(friends);
        layout.setCenter(listView);
        layout.setLayoutX(100);
        layout.setLayoutY(20);
        layout.setPrefHeight(280);
        layout.setPrefWidth(410);
        Group group = new Group(layout, back);
        Scene scene = new Scene(group);
        scene.setFill(Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<Button> getCancelMessages() throws IllegalAccessException, IOException {
        String answer = webClient.getCancelMessages();
        if (answer == null) return null;

        String[] messages = answer.split("\n");

        Button[] buttonArray = new Button[messages.length];
        int index = 0;
        for (int i = 0; i < buttonArray.length; i++){
            String name = messages[i].split(":")[0];
            Button bnt = createFriendButton(messages[i], index, name);
            buttonArray[i] = bnt;
            index += 50;
        }
        return FXCollections.observableArrayList(buttonArray);
    }

    private Button createFriendButton(String message, int index, String name){
        Button bnt = new Button(message, new ImageView("icons\\friend.jpg"));
        bnt.setLayoutX(140);
        bnt.setLayoutY(20 + index);
        bnt.setPrefHeight(50);
        bnt.setPrefWidth(400);
        bnt.setStyle(" -fx-background-color: linear-gradient(#87CEFA, #87CEFA);\n" +
                "    -fx-text-fill: white;\n" +
                "    text-align: left");
        bnt.setOnAction(actionEvent -> {
            try{
                webClient.dialogName = webClient.getNameOfClient();
                webClient.activeClient = name;
                dialogWindow.start(stage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return bnt;
    }

    private void createBack(){
        back.setPrefHeight(40);
        back.setPrefWidth(90);
        back.setLayoutY(20);
        back.setOnAction(actionEvent -> {
            try{
                mainWindow.start(stage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

}

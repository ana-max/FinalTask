package windows;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import webClient.WebClient;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class CancelDialogsWindow extends Application {
    private Button back = new Button("Back", new ImageView("icons\\cursor.jpg"));
    private MainWindow mainWindow;
    private Button cancel = new Button("Cancel");
    private ListView<Object> listView = new ListView<>();
    private Stage stage;
    private WebClient webClient;

    CancelDialogsWindow(WebClient client, MainWindow window){
        webClient = client;
        mainWindow = window;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            getHistory();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void start(Stage st) throws Exception {
        stage = st;
        createCancel();
        createBack();
        ObservableList<Object> messages = getMessages();
        listView.setItems(messages);
        BorderPane layout = new BorderPane();
        layout.setCenter(listView);
        layout.setLayoutX(100);
        layout.setLayoutY(20);
        layout.setPrefHeight(280);
        layout.setPrefWidth(440);
        Group group = new Group(layout, back, cancel);
        Scene scene = new Scene(group);
        scene.setFill(Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();
    }

    private void createCancel(){
        cancel.setPrefHeight(60);
        cancel.setPrefWidth(60);
        cancel.setLayoutY(260);
        cancel.setOnAction(actionEvent -> {
            try{
                webClient.cancelDeleteDialog();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
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


    private ObservableList<Object> getMessages(){
        ArrayList<Object> arrayList = new ArrayList<>();
        try{
            String his = webClient.getCancelHistory(webClient.activeClient, webClient.dialogName);
            if (his == null) return null;
            String[] h = his.split("\n");
            for (String s:h){
                if (s.indexOf("LINKLINK") > 0){
                    BorderPane borderPaneSelect = new BorderPane();
                    TextField textFieldFile = new TextField();
                    String name = s.split("LINKLINK")[0];
                    Button buttonSelectFile = new Button(s.split("LINKLINK")[1]);
                    buttonSelectFile.setOnAction(event -> {
                        FileChooser fileChooser = new FileChooser();
                        File file = fileChooser.showSaveDialog(stage);
                        if (file != null) {
                            textFieldFile.setText(file.getAbsolutePath());
                            try{
                                File fromServer = webClient.getFileFromServer(buttonSelectFile.getText());
                                File onSaveFile = new File(textFieldFile.getText());
                                Files.copy(fromServer.toPath(), onSaveFile.toPath());
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                    });
                    borderPaneSelect.setRight(buttonSelectFile);
                    arrayList.add(new HBoxCell(name, buttonSelectFile));
                }
                else arrayList.add(s);
            }

        }catch (IllegalAccessException|IOException ex){
            ex.printStackTrace();
        }
        return FXCollections.observableArrayList(arrayList);
    }

    private void getHistory() {
        ObservableList<Object> messages = getMessages();
        listView.setItems(messages);
    }
}

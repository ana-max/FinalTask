package windows;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class DialogWindow extends Application {
    private Button back = new Button("Back", new ImageView("icons\\cursor.jpg"));
    private Button attach = new Button("Attach");
    private Button send = new Button("Send");
    private Button delete = new Button("Delete");
    private TextArea areaOut = new TextArea();
    private TextArea areaIn = new TextArea();
    private MainWindow mainWindow;
    private ListView<Object> listView = new ListView<>();
    private Stage stage;
    private WebClient webClient;

    DialogWindow(WebClient client, MainWindow window){
        webClient = client;
        mainWindow = window;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            areaIn.setText("");
            getHistory();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void start(Stage st) throws Exception {
        stage = st;
        createAreaIn();
        createAreaOut();
        createSend();
        createAttach();
        createBack();
        createDelete();
        ObservableList<Object> messages = getMessages();
        listView.setItems(messages);
        BorderPane layout = new BorderPane();
        layout.setCenter(listView);
        layout.setLayoutX(100);
        layout.setLayoutY(20);
        layout.setPrefHeight(280);
        layout.setPrefWidth(440);
        Group group = new Group(layout, back, areaOut, send, attach, delete);
        Scene scene = new Scene(group);
        scene.setFill(Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();
    }

    private void createSend(){
        send.setPrefHeight(60);
        send.setPrefWidth(60);
        send.setLayoutX(420);
        send.setLayoutY(260);
        send.setOnAction(actionEvent -> {
            try{
                sendMessage();
            }catch (IllegalAccessException | IOException ex){
                ex.printStackTrace();
            }
        });
    }

    private void createDelete(){
        delete.setPrefHeight(60);
        delete.setPrefWidth(60);
        delete.setLayoutY(260);
        delete.setOnAction(actionEvent -> {
            try{
                webClient.deleteDialog();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    private void createAttach(){
        attach.setPrefHeight(60);
        attach.setPrefWidth(60);
        attach.setLayoutX(480);
        attach.setLayoutY(260);
        attach.setOnAction(actionEvent ->  {

            TextField textFieldFile = new TextField();
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                textFieldFile.setText(file.getAbsolutePath());
            }
            try{
                webClient.getAttach(textFieldFile.getText());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }


    private void createAreaIn(){
        areaIn.setLayoutX(100);
        areaIn.setLayoutY(20);
        areaIn.setPrefHeight(280);
        areaIn.setPrefWidth(440);
        areaIn.setEditable(false);
    }

    private void createAreaOut(){
        areaOut.setLayoutX(100);
        areaOut.setLayoutY(260);
        areaOut.setPrefHeight(60);
        areaOut.setPrefWidth(440);
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

    private void sendMessage() throws java.lang.IllegalAccessException, java.io.IOException{
        webClient.sendMessage(areaOut.getText());
        areaOut.setText("");
        getHistory();
    }

    private ObservableList<Object> getMessages(){
        ArrayList<Object> arrayList = new ArrayList<>();
        try{
            String his = webClient.getHistory(webClient.activeClient, webClient.dialogName);
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

    private void getHistory(){
        ObservableList<Object> messages = getMessages();
        listView.setItems(messages);
    }
}

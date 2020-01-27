package webClient;

import packets.*;
import serializator.Serializator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WebClient {
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Serializator serializator = new Serializator();
    public String activeClient = "";
    public String dialogName = "";

    public WebClient(Socket socket){
        try{
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void sendRequest(Packet packet) throws IllegalAccessException, IOException{
        byte[] request = serializator.Serialize(packet);
        dos.writeInt(request.length);
        dos.write(request);
        dos.flush();
    }

    private String getAnswerFromServer() throws IOException{
        int len = dis.readInt();
        byte[] data = new byte[len];
        dis.readFully(data);
        AnswerPacket answerPacket = (AnswerPacket) serializator.Deserialize(data);
        return answerPacket.answer;
    }
    public void cancelDeleteDialog()throws IllegalAccessException, IOException{
        CancelDeleteDialog cancelDeleteDialog = new CancelDeleteDialog();
        NameOfClientPacket nameOfClientPacket = new NameOfClientPacket();
        sendRequest(nameOfClientPacket);
        cancelDeleteDialog.from = getAnswerFromServer();
        cancelDeleteDialog.clientsInDialog = activeClient;
        cancelDeleteDialog.dialogName = dialogName;
        sendRequest(cancelDeleteDialog);
        getAnswerFromServer();
    }
    public String getIncomingMessages() throws IllegalAccessException, IOException{
        IncomingPacket incomingPacket = new IncomingPacket();
        sendRequest(incomingPacket);
        return getAnswerFromServer();
    }

    public String getCancelMessages() throws IllegalAccessException, IOException{
        CancelPacket cancelPacket = new CancelPacket();
        sendRequest(cancelPacket);
        return getAnswerFromServer();
    }

    public void sendMessage(String message) throws IllegalAccessException, IOException{
        NameOfClientPacket nameOfClientPacket = new NameOfClientPacket();
        MessagePacket messagePacket = new MessagePacket();
        sendRequest(nameOfClientPacket);
        messagePacket.from = getAnswerFromServer();
        messagePacket.clientsInDialog = activeClient;
        messagePacket.dialogName = dialogName;
        messagePacket.message = message;
        sendRequest(messagePacket);
        getAnswerFromServer();
    }

    public void deleteDialog() throws IllegalAccessException, IOException{
        NameOfClientPacket nameOfClientPacket = new NameOfClientPacket();
        DeletePacket deletePacket = new DeletePacket();
        sendRequest(nameOfClientPacket);
        deletePacket.from = getAnswerFromServer();
        deletePacket.clientsInDialog = activeClient;
        deletePacket.dialogName = dialogName;
        sendRequest(deletePacket);
        getAnswerFromServer();
    }

    public String getHistory(String name, String from) throws IllegalAccessException, IOException{
        HistoryPacket historyPacket = new HistoryPacket();
        historyPacket.user = name;
        historyPacket.from = from;
        sendRequest(historyPacket);
        return getAnswerFromServer();
    }

    public String getCancelHistory(String name, String from) throws IllegalAccessException, IOException{
        CancelHistoryPacket cancelHistoryPacket = new CancelHistoryPacket();
        cancelHistoryPacket.user = name;
        cancelHistoryPacket.from = from;
        sendRequest(cancelHistoryPacket);
        return getAnswerFromServer();
    }

    public void getCloseEvent() throws IllegalAccessException, IOException{
        ClosePacket closePacket = new ClosePacket();
        sendRequest(closePacket);
        getAnswerFromServer();
    }

    public String getFriends() throws IllegalAccessException, IOException{
        FriendsPacket friendsPacket = new FriendsPacket();
        sendRequest(friendsPacket);
        return getAnswerFromServer();
    }



    public File getFileFromServer(String fileName)throws IllegalAccessException, IOException{
        FilePacket filePacket = new FilePacket();
        filePacket.filename = fileName;
        sendRequest(filePacket);
        return new File(getAnswerFromServer());
    }

    public void setNewName(String name)throws IllegalAccessException, IOException{
        NewNamePacket newNamePacket = new NewNamePacket();
        newNamePacket.newName = name;
        sendRequest(newNamePacket);
        getAnswerFromServer();
    }

    public void getAttach(String fileName) throws IllegalAccessException, IOException{
        NameOfClientPacket nameOfClientPacket = new NameOfClientPacket();
        sendRequest(nameOfClientPacket);
        AttachPacket attachPacket = new AttachPacket();
        attachPacket.fileName = fileName;
        attachPacket.file = new File(fileName);
        attachPacket.from = getAnswerFromServer();
        attachPacket.clientsInDialog = activeClient;
        attachPacket.dialogName = dialogName;
        sendRequest(attachPacket);
        getAnswerFromServer();
    }

    public String getNameOfClient() throws IllegalAccessException, IOException{
        NameOfClientPacket nameOfClientPacket = new NameOfClientPacket();
        sendRequest(nameOfClientPacket);
        return getAnswerFromServer();
    }
}

package clientSocket;

import dispatcher.ThreadDispatcher;
import packets.*;
import serializator.Serializator;
import treaded.Threaded;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientSocket extends Threaded {
    private static Serializator serializator = new Serializator();
    private ThreadDispatcher td;
    private String name;
    private Socket clientSocket;
    private static HashMap<String, HashMap<String, String>> history = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> basket = new HashMap<>();
    private static HashMap<String, String> dialogs = new HashMap<>();
    private static HashMap<String, File> files = new HashMap<>();

    public ClientSocket(Socket socket, ThreadDispatcher td, int num) {
        clientSocket = socket;
        name = "Client " + num;
        this.td = td;
    }

    @Override
    public void run() {
        try {
            System.out.println(getName());
            InputStream in = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            try {
                DataInputStream dis = new DataInputStream(in);
                DataOutputStream dos = new DataOutputStream(os);

                while (true) {
                    dos.flush();
                    int len = dis.readInt();
                    byte[] data = new byte[len];
                    dis.readFully(data);

                    Object o = serializator.Deserialize(data);
                    Class clazz = o.getClass();
                    String typeOfClass = clazz.getTypeName();

                    String answer = getAnswer(typeOfClass, o);

                    AnswerPacket packet = new AnswerPacket();
                    packet.answer = answer;

                    byte[] answerInBytes = serializator.Serialize(packet);
                    dos.writeInt(answerInBytes.length);
                    dos.write(answerInBytes);
                }
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } finally {
                in.close();
                os.close();
            }
        }catch (EOFException ex){
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getAnswer(String type, Object o){
        switch (type){
            case "packets.FriendsPacket":
                return processFriendPacket((FriendsPacket)o);
            case "packets.MessagePacket":
                return processMessageRequest((MessagePacket)o);
            case "packets.IncomingPacket":
                return processIncomingPacket((IncomingPacket)o);
            case "packets.HistoryPacket":
                return processHistoryRequest((HistoryPacket)o);
            case "packets.NameOfClientPacket":
                return getName();
            case "packets.AttachPacket":
                return processAttachPacket((AttachPacket)o);
            case "packets.FilePacket":
                return getFile((FilePacket)o);
            case "packets.DeletePacket":
                return processDeletePacket((DeletePacket)o);
            case "packets.CancelPacket":
                return processCancelPacket((CancelPacket)o);
            case "packets.CancelHistoryPacket":
                return processCancelHistoryPacket((CancelHistoryPacket)o);
            case "packets.ClosePacket":
                return processClosePacket((ClosePacket)o);
            case "packets.NewNamePacket":
                return processNewNamePacket((NewNamePacket)o);
            case "packets.CancelDeleteDialog":
                return processCancelDeleteDialogPacket((CancelDeleteDialog)o);
        }
        return null;
    }
    private String processCancelDeleteDialogPacket(CancelDeleteDialog cancelDeleteDialog){
        String dialogName = cancelDeleteDialog.dialogName;
        String clientInChat = cancelDeleteDialog.clientsInDialog;
        String from = cancelDeleteDialog.from;
        if (dialogs.containsKey(dialogName)){
            historyToBasket(dialogName, clientInChat);
            historyToBasket(clientInChat, dialogName);
            for (String client: clientInChat.split("\n")){
                BasketToHistory(client, dialogName);
                BasketToHistory(dialogName, client);
            }
        }else if (dialogs.containsKey(clientInChat)){
            String inDialog = dialogs.get(clientInChat);
            BasketToHistory(clientInChat, inDialog);
            BasketToHistory(inDialog, clientInChat);
            for (String client: inDialog.split("\n")){
                BasketToHistory(client, clientInChat);
                BasketToHistory(clientInChat, client);
            }
        }
        else {
            BasketToHistory(from, clientInChat);
            BasketToHistory(clientInChat, from);
        }
        return null;
    }

    private void historyToBasket(String client1, String client2){
        if (!basket.containsKey(client1))
            basket.put(client1, new HashMap<>());
        if (!basket.get(client1).containsKey(client2))
            basket.get(client1).put(client2, "");
        String hist = basket.get(client1).get(client2);
        hist += history.get(client1).get(client2);
        if (hist.length() > 0)
            basket.get(client1).put(client2, hist);
        else
            basket.remove(client1);
        history.get(client1).remove(client2);
    }

    private void BasketToHistory(String client1, String client2){
        if (!history.containsKey(client1))
            history.put(client1, new HashMap<>());
        if (!history.get(client1).containsKey(client2))
            history.get(client1).put(client2, "");
        String hist = history.get(client1).get(client2);
        String h = basket.get(client1).get(client2);
        if (h != null) hist += h;
        if (hist.length() > 0)
            history.get(client1).put(client2, hist);
        else
            history.remove(client1);
        basket.get(client1).remove(client2);
    }

    private String processNewNamePacket(NewNamePacket newNamePacket){
        String newName = newNamePacket.newName;
        updateHistory(getName(), newName);
        history.remove(getName());
        ArrayList<Thread> threads = td.getMonitor();
        for (Thread thread: threads) {
            if (thread.getName().equals(getName())) {
                thread.setName(newName);
                break;
            }
        }
        setName(newName);
        return null;
    }

    private void updateHistory(String oldName, String newName){
        HashMap<String, String> historyOfOldName = history.get(oldName);
        if (historyOfOldName == null) return;
        history.put(newName, new HashMap<>());
        for (String client: historyOfOldName.keySet()){
            if (dialogs.containsKey(client)) {
                String inDialog = dialogs.get(client);
                inDialog = inDialog.replace(oldName, newName);
                String hist = history.get(client).get(dialogs.get(client));
                hist = hist.replace(oldName, newName);
                history.get(client).put(newName, hist);
                history.get(client).remove(oldName);
                for (String cl: inDialog.split("\n")){
                    history.get(client).put(cl, hist);
                    history.put(cl, new HashMap<>());
                    history.get(cl).put(client, hist);
                }
            }else{
                String oldHis = historyOfOldName.get(client);
                String newHis = oldHis.replace(oldName, newName);
                history.get(newName).put(client, newHis);
                history.get(client).put(newName, newHis);
                history.get(client).remove(oldName);
            }
        }
    }

    private String processClosePacket(ClosePacket closePacket){
        HashMap<String, String> historyOfClient = history.get(getName());
        if (historyOfClient == null)return null;
        for (String client: historyOfClient.keySet()){
            if (dialogs.containsKey(client)){
                String inChat = dialogs.get(client);
                String his = history.get(client).get(inChat) + getName() + " вышел из сети...\n";
                history.get(client).put(inChat, his);
                history.get(inChat).put(client, his);
                for (String cl: inChat.split("\n")){
                    history.get(cl).put(client, his);
                    history.get(client).put(cl, his);
                }
            }
            else {
                String his = history.get(getName()).get(client) + getName() + " вышел из сети...";
                history.get(getName()).put(client, his);
            }
        }
        return null;
    }
    private String processCancelPacket(CancelPacket cancelPacket){
        if (!basket.containsKey(getName())) return null;
        HashMap<String, String> deleted = basket.get(getName());
        if (deleted == null) return null;
        StringBuilder answer = new StringBuilder();
        for (String client: deleted.keySet()){
            if (deleted.get(client).length() > 0) {
                answer.append(client);
                answer.append("\n");
            }
        }
        return answer.toString();
    }

    private String processDeletePacket(DeletePacket deletePacket){
        String dialogName = deletePacket.dialogName;
        String clientInChat = deletePacket.clientsInDialog;
        String from = deletePacket.from;
        if (dialogs.containsKey(dialogName)){
            historyToBasket(dialogName, clientInChat);
            historyToBasket(clientInChat, dialogName);
            for (String client: clientInChat.split("\n")){
                historyToBasket(client, dialogName);
                historyToBasket(dialogName, client);
            }
        }else if (dialogs.containsKey(clientInChat)){
            String inDialog = dialogs.get(clientInChat);
            historyToBasket(clientInChat, inDialog);
            historyToBasket(inDialog, clientInChat);
            for (String client: inDialog.split("\n")){
                historyToBasket(client, clientInChat);
                historyToBasket(clientInChat, client);
            }
        }
        else {
            System.out.println("no");
            historyToBasket(from, clientInChat);
            historyToBasket(clientInChat, from);
        }
        return null;
    }

    private String processAttachPacket(AttachPacket attachPacket){
        String fileName = attachPacket.fileName;
        files.put(fileName, attachPacket.file);
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.clientsInDialog = attachPacket.clientsInDialog;
        messagePacket.from = attachPacket.from;
        messagePacket.dialogName = attachPacket.dialogName;
        messagePacket.message = "LINKLINK" + fileName;
        return processMessageRequest(messagePacket);
    }

    private String processIncomingPacket(IncomingPacket incomingPacket){
        if (!history.containsKey(getName())) return null;
        HashMap<String, String> incoming = history.get(getName());
        StringBuilder answer = new StringBuilder();
        if (incoming == null) return null;
        for (String client: incoming.keySet()){
            if (incoming.get(client).length() > 0) {
                answer.append(client);
                answer.append("\n");
            }
        }
        return answer.toString();
    }

    private String processHistoryRequest(HistoryPacket historyPacket){
        String user = historyPacket.user;
        String from = historyPacket.from;
        initializedHistory(from, user);
        initializedHistory(user, from);
        return history.get(user).get(from);
    }

    private String processCancelHistoryPacket(CancelHistoryPacket cancelHistoryPacket){
        String user = cancelHistoryPacket.user;
        String from = getName();
        initializedCancelHistory(from, user);
        initializedCancelHistory(user, from);
        return basket.get(user).get(from);
    }

    private String processMessageRequest(MessagePacket messagePacket){
        String dialogName = messagePacket.dialogName;
        if (!dialogName.equals(messagePacket.from) || dialogs.containsKey(messagePacket.clientsInDialog))
            return processGroupOfChat(messagePacket);
        String client = messagePacket.clientsInDialog;
        initializedHistory(client, dialogName);
        initializedHistory(dialogName, client);
        String his1 = history.get(dialogName).get(client);
        his1 += messagePacket.from + ": " + messagePacket.message;
        his1 += "\n";
        history.get(dialogName).put(client, his1);
        String his = history.get(client).get(dialogName);
        his += messagePacket.from + ": " + messagePacket.message;
        his += "\n";
        history.get(client).put(dialogName, his);
        if (!history.get(dialogName).containsKey(client))
            history.get(dialogName).put(client, "");
        return messagePacket.from + ": " + messagePacket.message;
    }

    private String processGroupOfChat(MessagePacket messagePacket){
        String dialogName = "";
        String clsInDialog = "";
        if (dialogs.containsKey(messagePacket.clientsInDialog)) {
            clsInDialog = dialogs.get(messagePacket.clientsInDialog);
            dialogName = messagePacket.clientsInDialog;
        }
        else if (!dialogs.containsKey(messagePacket.dialogName)) {
            dialogs.put(messagePacket.dialogName, messagePacket.clientsInDialog);
            clsInDialog = dialogs.get(messagePacket.dialogName);
            dialogName = messagePacket.dialogName;
        }
        else {
            clsInDialog = dialogs.get(messagePacket.dialogName);
            dialogName = messagePacket.dialogName;
        }
        String from = messagePacket.from;
        initializedHistory(dialogName, clsInDialog);
        initializedHistory(clsInDialog, dialogName);
        String his = history.get(dialogName).get(clsInDialog);
        his += from + ": " + messagePacket.message + "\n";
        history.get(dialogName).put(clsInDialog, his);
        history.get(clsInDialog).put(dialogName, his);
        String[] clientsInDialog = clsInDialog.split("\n");
        for (String client: clientsInDialog){
            initializedHistory(client, dialogName);
            initializedHistory(dialogName, client);
            String h = history.get(dialogName).get(client);
            h += from + ": " + messagePacket.message + "\n";
            history.get(client).put(dialogName, h);
            history.get(dialogName).put(client, h);
        }
        return from + ": " + messagePacket.message;
    }
    private String processFriendPacket(FriendsPacket friendsPacket){
        try {
            return monitorClients() ;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getFile(FilePacket filePacket){
        String fileName = filePacket.filename;
        return files.get(fileName).getPath();
    }

    private void initializedHistory(String client1, String client2){
        if (!history.containsKey(client1))
            history.put(client1, new HashMap<>());
        if (!history.get(client1).containsKey(client2))
            history.get(client1).put(client2, "");
    }

    private void initializedCancelHistory(String client1, String client2){
        if (!basket.containsKey(client1))
            basket.put(client1, new HashMap<>());
        if (!basket.get(client1).containsKey(client2))
            basket.get(client1).put(client2, "");
    }

    private String monitorClients() {
        ArrayList<Thread> threads = td.getMonitor();
        StringBuilder answer = new StringBuilder();
        for (Thread thread: threads){
            String name = thread.getName();
            if (!name.equals("Monitor") && !name.equals(getName())) {
                answer.append(thread.getName());
                answer.append("\n");
            }
        }
        return answer.toString();
    }

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) { this.name = name; }

}

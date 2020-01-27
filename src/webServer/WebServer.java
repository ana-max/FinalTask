package webServer;

import clientSocket.ClientSocket;
import dispatcher.ThreadDispatcher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WebServer{
    private static ThreadDispatcher td = ThreadDispatcher.getInstance();
    private int countOfClients;

    public void start(){
        try {
            ServerSocket server = new ServerSocket(8080);
            try{
                while (true) {
                    Socket socket = server.accept();
                    ClientSocket client = new ClientSocket(socket, td, countOfClients++);
                    td.Add(client);
                }
            }
            finally {
                server.close();
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }
}

package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket s;
        Socket socket;
        BufferedReader in;
        s = new ServerSocket(8090);
        socket = s.accept();
        System.out.println(socket.getInetAddress());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.print(in.readLine());
    }
}
package OS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Server  {
    private ServerSocket serverSocket;
    public  ArrayList<Socket> allSockets = new ArrayList<>(); 
    public   HashSet<ClientHandler> allClient = new HashSet<>();
    public  HashSet<ClientHandler> room1 = new HashSet<>();
    public   HashSet<ClientHandler> room2 = new HashSet<>();
    public   HashSet<ClientHandler> room3 = new HashSet<>();
    public BufferedReader bufferedReader;
    private ArrayList<String> commandArrayList = new ArrayList<>();
    
    private  BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public  void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client connect");
                allSockets.add(socket);
                ClientHandler clientHandler = new ClientHandler(socket,this);
                Thread thread = new Thread(clientHandler);
                thread.startVirtualThread(thread);
            }
        } catch (IOException e) {

        }
    }



    public void closeServerSocket(){
        try {
            if (serverSocket != null){
                serverSocket.close();
            }    
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}

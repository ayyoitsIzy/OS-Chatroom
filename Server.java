package OS;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    public   ArrayList<ClientHandler> allClient = new ArrayList<>();
    public   ArrayList<ClientHandler> room1 = new ArrayList<>();
    public   ArrayList<ClientHandler> room2 = new ArrayList<>();
    public   ArrayList<ClientHandler> room3 = new ArrayList<>();

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public  void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client connect");
                ClientHandler clientHandler = new ClientHandler(socket,this);
                Thread thread = new Thread(clientHandler);
                thread.start();
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

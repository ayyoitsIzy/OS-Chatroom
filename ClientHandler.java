package OS;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;
import java.net.*;


public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String clientRoomId;
    private static ArrayList<ClientHandler> CurrentRoom;
    private Server server;


    public ClientHandler(Socket socket,Server server){
        try {
            this.server = server;
            this.socket = socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.clientRoomId = bufferedReader.readLine();
            switch (clientRoomId) {
                case "1"  : 
                    server.room1.add(this);
                    CurrentRoom = server.room1;
                    break;
                case "2" :
                    server.room2.add(this);
                    CurrentRoom = server.room2;
                    break;
                case "3":
                    server.room3.add(this);
                    CurrentRoom = server.room3;
                    break;
                default:
                    throw new AssertionError();
            }
            //this.bufferedReader.flush();
            broadcastMessage("SERVER:"+clientUsername+" enter the chat ID:"+clientRoomId);
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    
    @Override
    public void run(){
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
             
                
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
        
    }
    public void broadcastMessage(String messageToSend){
            for (ClientHandler clientHandler : CurrentRoom){
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername) && clientHandler.clientRoomId.equals(clientRoomId)){
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }
            }
        }
        public void removeClientHandler(){
            CurrentRoom.remove(this);
            broadcastMessage("Server:"+clientUsername+"Left");
        }
        public void closeEverything(Socket socket,BufferedReader bufferedReader , BufferedWriter bufferedWriter){
            removeClientHandler();
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if (bufferedWriter!= null){
                    bufferedWriter.close();
                }
                if(socket!=null){
                    socket.close();
                }
            } catch (IOException e) {

            }
        }
    }

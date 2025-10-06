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
    private HashSet<ClientHandler> CurrentRoom;
    private Server server;
    private String Text;

    public ClientHandler(Socket socket,Server server){
        try {
            this.server = server;
            this.socket = socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.clientRoomId = bufferedReader.readLine();
            server.allClient.add(this);
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
            broadcastMessage("SERVER:"+clientUsername+" enter the chat ID: "+clientRoomId);
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    
    @Override
    public void run(){
        String messageFromClient;
        String command;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                String[] myArray = messageFromClient.split("\\s+");
                command = myArray[0];
                if (myArray.length == 2) {
                    Text = myArray[1];
                }
                Echo(String.format("Command : %s,%s ",command,Text));
                String Temp;                
                switch (command) {
                    case "say":
                        if(CurrentRoom == null){
                            Echo("You are not in any room rightnow! please enter room using command : join");
                            break;
                        }
                        Thread t2 = new Thread(() -> {
                        broadcastMessage(String.format("%s : %s",clientUsername,Text));
                        });
                        t2.start();
                        
                        break;
                    case "where":
                        Temp = "You are currently in room : %s";
                        Echo(String.format(Temp,clientRoomId));
                        break;
                    case "who":
                        Temp = String.format("Current member of Room #%s",clientRoomId);
                        Echo(Temp);
                        for (ClientHandler clientHandler : this.CurrentRoom) {
                         Temp = clientHandler.clientUsername;
                         Echo(Temp);
                        }
                        break;
                    case "join":
                        if (CurrentRoom != null) {
                         Echo("Please Leave Before joing any other room");
                              break;
                        } else {
                            Echo("Enter Room you want to join:");
                            clientRoomId = Text;
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
                                        Echo("Join Error");
                                        break;
                                }
                            break;
                        }
                    case "leave":
                        if (CurrentRoom != null) {
                         removeClientHandler();
                              break;
                        } else {
                            Echo("You are Currently Not in Any Room!");                        
                            break;
                        }
                    case "quit":
                        if (CurrentRoom != null)removeClientHandler();   
                        server.allClient.remove(this);
                        break;
                    default:
                        Echo("Unknow Command");
                }

            } catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
        
    }
    public void Echo(String message){
        try {
            bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
                
    }
    public void broadcastMessage(String messageToSend){
            for (ClientHandler clientHandler : this.CurrentRoom){
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)){
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
            broadcastMessage("Server:"+clientUsername+"Left");
            CurrentRoom.remove(this);
            CurrentRoom = null;
            clientRoomId = null;

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

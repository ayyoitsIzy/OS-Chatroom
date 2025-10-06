package OS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private  BufferedWriter bufferedWriter;
    private String username;
    private  String RoomID;

    public Client(Socket socket,String username,String RoomID){
        try {
          this.socket = socket;
          this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
          this.username = username;
          this.RoomID = RoomID;
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        } 

    }
    public void sendMessage(){
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(RoomID);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void listenForMessage(){
        new  Thread(){
            @Override
            public void run(){
                String msgFromGroupChat;
                while(socket.isConnected()){ 
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                    } catch (IOException e) 
                    {
                    closeEverything(socket,bufferedReader,bufferedWriter);
                    }   
                        

                }
            }
        }.start();
    }
   


    public void closeEverything(Socket socket,BufferedReader bufferedReader , BufferedWriter bufferedWriter){
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
        public static void main(String[] args) throws IOException {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username : ");
            String username = scanner.nextLine();
            System.out.println("Enter your RoomID : ");
            String RoomID = scanner.nextLine();
            Socket socket = new Socket("localhost",1234);
            Client client = new Client(socket, username,RoomID);
            client.listenForMessage();
            client.sendMessage();
        }
}

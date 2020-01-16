package main.server;

import main.Account;
import main.exceptions.InvalidAccountException;
import main.Message;
import main.exceptions.QuantityException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

public class ServerTask implements Runnable {

    protected final Socket CLIENT_SOCKET;

    public ServerTask(Socket CLIENT_SOCKET) {
        this.CLIENT_SOCKET = CLIENT_SOCKET;
    }

    public void run() {
        while(CLIENT_SOCKET.isConnected()) {
            try {
                ObjectOutputStream serverOS = new ObjectOutputStream(CLIENT_SOCKET.getOutputStream());
                ObjectInputStream serverIS = new ObjectInputStream(CLIENT_SOCKET.getInputStream());

                Message request = (Message) serverIS.readObject();
                Account recipient = Server.getActiveEmployees().get(CLIENT_SOCKET.getInetAddress());
                recipient = (recipient == null) ? Server.getActiveUsers().get(CLIENT_SOCKET.getInetAddress()) : null;

                if (isEmployee(CLIENT_SOCKET.getInetAddress()) == null && request.getCode().ordinal() > Message.MessageIndex.PURCHASE.ordinal()) {
                    serverOS.writeObject(new Message(Message.MessageIndex.ERROR, "You aren't authorized to perform such actions!"));
                    return;
                }
                try {
                    System.out.print("Server request: " + request.getCode().toString() + " ");
                    Message answer = ServerProtocol.parseMessage(request, recipient, CLIENT_SOCKET.getInetAddress());
                    System.out.print(" Server response: " + answer.getCode().toString());
                    serverOS.writeObject(answer);
                } catch (QuantityException e) {
                    serverOS.writeObject(new Message(Message.MessageIndex.ERROR, "The following item has insufficient quantity:" + e.getMessage()));
                } catch (SQLException e) {
                    serverOS.writeObject(new Message(Message.MessageIndex.ERROR, "The database is unresponsive, please wait..."));
                    e.printStackTrace();
                } catch (InvalidAccountException e) {
                    serverOS.writeObject(new Message(Message.MessageIndex.ERROR, "Invalid username or password, please try again..."));
                }
                System.out.println("- Request processed");
            } catch (IOException e) {
                e.printStackTrace();//TODO:
                break;
            } catch (ClassNotFoundException e) {

            }
        }
    }

    public Account isEmployee(InetAddress ip) {
        return Server.getActiveEmployees().get(ip);
    }
}

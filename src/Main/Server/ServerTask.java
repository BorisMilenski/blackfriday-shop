package Main.Server;

import Main.Account;
import Main.Exceptions.InvalidAccountException;
import Main.Message;
import Main.MessageIndex;
import Main.Exceptions.QuantityException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

public class ServerTask implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;

    public ServerTask(Socket clientSocket, String serverText, Server server) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        while(clientSocket.isConnected()) {
            try {
                ObjectOutputStream serverOS = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream serverIS = new ObjectInputStream(clientSocket.getInputStream());

                Message request = (Message) serverIS.readObject();
                Account recipient = Server.getActiveEmployees().get(clientSocket.getInetAddress());
                recipient = (recipient == null) ? Server.getActiveUsers().get(clientSocket.getInetAddress()) : null;

                if (isEmployee(clientSocket.getInetAddress()) == null && request.getCode().ordinal() > MessageIndex.PURCHASE.ordinal()) {
                    serverOS.writeObject(new Message(MessageIndex.ERROR, "You aren't authorized to perform such actions!"));
                    return;
                }
                try {
                    System.out.print("Server request: " + request.getCode().toString() + " ");
                    Message answer = ServerProtocol.parseMessage(request, recipient, clientSocket.getInetAddress());
                    System.out.print(" Server response: " + answer.getCode().toString());
                    serverOS.writeObject(answer);
                } catch (QuantityException e) {
                    serverOS.writeObject(new Message(MessageIndex.ERROR, "The following item has insufficient quantity:" + e.getMessage()));
                } catch (SQLException e) {
                    serverOS.writeObject(new Message(MessageIndex.ERROR, "The database is unresponsive, please wait..."));
                    e.printStackTrace();
                } catch (InvalidAccountException e) {
                    serverOS.writeObject(new Message(MessageIndex.ERROR, "Invalid username or password, please try again..."));
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

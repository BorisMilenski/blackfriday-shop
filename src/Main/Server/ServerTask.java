package Main.Server;

import Main.Account;
import Main.MessageIndex;
import Main.Exceptions.QuantityException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

public class ServerTask implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;
    protected MultiThreadedServer server = null;

    public ServerTask(Socket clientSocket, String serverText, MultiThreadedServer server) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.server = server;
    }

    public void run() {
        try {
            ObjectOutputStream serverOS = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream serverIS = new ObjectInputStream(clientSocket.getInputStream());

            Message request = (Message) serverIS.readObject();
            Account recipient = server.activeAdmins.get(clientSocket.getInetAddress());
            recipient = (recipient == null) ? server.activeUsers.get(clientSocket.getInetAddress()) : null; //TODO: Make this readable

            if (isAdmin(clientSocket.getInetAddress()) != null && request.getCode().ordinal() > MessageIndex.PURCHASE.ordinal()) {
                serverOS.writeObject(new Message(MessageIndex.ERRORPRIVILIGE, null));
                return;
            }
            try {
                serverOS.writeObject(new Message(MessageIndex.PRODUCTS, ServerProtocol.parseMessage(request, recipient, server)));
            } catch (QuantityException e) {
                serverOS.writeObject(new Message(MessageIndex.ERRORQUANTITY, "The following item has insufficient quantity:" + e.getMessage()));
            } catch (SQLException e) {
                serverOS.writeObject(new Message(MessageIndex.ERRORSQL, "The database is unresponsive, please wait..."));
            }

            serverOS.close();
            serverIS.close();
            System.out.println("Request processed:");
        } catch (IOException e) {
            e.printStackTrace();//TODO:
        } catch (ClassNotFoundException e) {

        }
    }

    public Account isAdmin(InetAddress ip) {
        return server.activeAdmins.get(ip);
    }
}

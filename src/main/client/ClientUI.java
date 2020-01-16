package main.client;

import main.IO.Terminal;
import main.Message;
import main.Product;
import main.ProductList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;

public class ClientUI {
    public static Terminal terminal = null;
    public static Socket serverSocket = null;
    public static ProductList products = null;
    public static ProductList cart = null;


    public static void main(String[] args) {
        cart = new ProductList();
        while (true) {
            try {
                serverSocket = new Socket(InetAddress.getLocalHost(), 50121);
                break;
            } catch (IOException e) {
                System.out.println("Server is offline");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

            terminal = new Terminal();
            terminal.build();
            terminal.update();

    }

    public static Object requestFromServer(Message request) {
        Message answer = null;
        while (true) {
            try {
                ObjectOutputStream clientOS;
                ObjectInputStream clientIS;
                clientOS = new ObjectOutputStream(serverSocket.getOutputStream());
                clientIS = new ObjectInputStream(serverSocket.getInputStream());
                clientOS.writeObject(request);
                answer = (Message) clientIS.readObject();
                if (answer == null) {
                    Thread.sleep(500);
                    continue;
                }
                System.out.println("Request processed:" + request.getCode().toString());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                return ClientProtocol.parseMessage(answer, terminal);
            }
        }

    }

    public static void updateProducts(String sortBy, boolean reverse) {
        products = (ProductList) requestFromServer(new Message(Message.MessageIndex.PRODUCTS, null));
        Collections.sort(products.getProducts(), newProductComparator(sortBy, reverse));
    }

    public static Comparator<Product> newProductComparator(String sortBy, boolean reverse) {
        Comparator<Product> productComparator = Comparator.comparing((Product) -> {
            try {
                return (Comparable) Product.getClass().getMethod("get" + sortBy).invoke(Product);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (reverse) {
            productComparator = productComparator.reversed();
        }
        return productComparator;
    }


}

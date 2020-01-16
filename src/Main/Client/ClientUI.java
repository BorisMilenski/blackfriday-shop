package Main.Client;

import Main.IO.Terminal;
import Main.MessageIndex;
import Main.Product;
import Main.ProductList;
import Main.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

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
                ObjectInputStream clinetIS;
                clientOS = new ObjectOutputStream(serverSocket.getOutputStream());
                clinetIS = new ObjectInputStream(serverSocket.getInputStream());
                clientOS.writeObject(request);
                answer = (Message) clinetIS.readObject();
                if (answer == null) {
                    Thread.sleep(500);
                    continue;
                }
                System.out.println("Request processed:" + request.getCode().toString());

            } catch (IOException e) {

            } catch (ClassNotFoundException e) {

            } finally {
                return ClientProtocol.parseMessage(answer, terminal);
            }
        }

    }

    public static void updateProducts(String sortBy, boolean reverse) {
        products = (ProductList) requestFromServer(new Message(MessageIndex.PRODUCTS, null));
        Collections.sort(products.getProducts(), newProductComparator(sortBy, reverse));
    }

    public static Comparator<Product> newProductComparator(String sortBy, boolean reverse){
        Comparator<Product> productComparator = Comparator.comparing((Product) -> {
            try {
                return (Comparable) Product.getClass().getMethod("get" + sortBy).invoke(Product);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
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

package Main.IO;

import Main.*;
import Main.Client.ClientUI;
import org.beryx.textio.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.beryx.textio.ReadInterruptionStrategy.Action.ABORT;

public class Terminal {
    protected TextIO textIO = null;
    protected TextTerminal<?> terminal = null;
    private State state = State.START;
    private State lastState = State.START;
    private String sortBy = "ID";
    private boolean isDescending = false;
    private Account userAcc = null;

    public enum State {
        START,
        CREATE,
        LOGIN,
        MAIN,
        LIST,
        PURCHASE,
        PURCHASEALL,
        CUSTOMER,
        EMPLOYEE,
        ADDCART,
        CART,
        LISTADV,
        ADD,
        EDIT,
        REMOVE,
        STARTBLACK,
        ENDBLACK,
        SORTBY,
        EXIT

    }

    public Terminal() {

    }

    public void setState(State state) {
        this.lastState = this.state;
        this.state = state;
    }

    public void build() {
        textIO = TextIoFactory.getTextIO();
        terminal = textIO.getTextTerminal();

        //Setup terminal

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        TerminalProperties<?> props = terminal.getProperties();
        props.setPromptColor(new Color(255, 255, 255));
        props.setPaneBackgroundColor(new Color(10, 25, 90));
        props.setPaneDimension((int) screenSize.getWidth(), (int) screenSize.getHeight()/2);

        //Register keystroke handlers

        String backKeyStroke = "ctrl Z";

        boolean registeredBack = terminal.registerHandler(backKeyStroke, t -> new ReadHandlerData(ABORT));

        boolean hasHandlers = registeredBack;
        if (!hasHandlers) {
            terminal.println("No handlers can be registered.");
        } else {
            terminal.println("--------------------------------------------------------------------------------");
            if (registeredBack) {
                terminal.println("During data entry you can press '" + backKeyStroke + "' to go back to the previous field.\n");
            }
            terminal.println("You can use these key combinations at any moment during your data entry session.");
            terminal.println("--------------------------------------------------------------------------------");
        }
        textIO.getTextTerminal().setBookmark("START");
    }

    public void printList(ProductList products, boolean[] fieldsToDisplay) {
        new ListPrinter(products, fieldsToDisplay).accept(this.textIO);
    }

    public void showError(String errorMessage) {
        JOptionPane optionPane = new JOptionPane(errorMessage, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog("ERROR");
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.dispose();
    }

    public void update() {//TODO: Read up on recursion
        switch (state) {
            case START:
                terminal.resetToBookmark("START");
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. Sign in",
                        "2. Sign up\n"
                )))){
                    case 1:
                        setState(State.LOGIN);
                        break;
                    case 2:
                        setState(State.CREATE);
                        break;
                }
                this.update();
                break;
            case CREATE:
                terminal.resetToBookmark("START");
                createAccount();
                setState(State.LOGIN);
                this.update();
                break;
            case LOGIN:
                terminal.resetToBookmark("START");
                userAcc = login();
                setState(State.MAIN);
                this.update();
                break;
            case MAIN:
                sortBy = "ID";
                isDescending = false;
                if (userAcc.isEmployee()) {
                    setState(Terminal.State.EMPLOYEE);
                } else {
                    setState(Terminal.State.CUSTOMER);
                }
                this.update();
                break;
            case CUSTOMER:
                terminal.resetToBookmark("START");
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. View all products",
                        "2. View your current shopping cart",
                        "3. Log out.\n")
                ))) {
                    case 1:
                        setState(State.LIST);
                        break;
                    case 2:
                        setState(State.CART);
                        break;
                    case 3:
                        setState(State.LOGIN);
                        break;
                }
                this.update();
                break;
            case SORTBY:
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. Name",
                        "2. Category",
                        "3. Price",
                        "4. Quantity",
                        "5. Discount Percent",
                        "6. Popularity"
                )))) {
                    case 1:
                        sortBy = "Name";
                        break;
                    case 2:
                        sortBy = "Category";
                        break;
                    case 3:
                        sortBy = "Price";
                        break;
                    case 4:
                        sortBy = "Quantity";
                        break;
                    case 5:
                        sortBy = "BlackfridayDiscount";
                        break;
                    case 6:
                        sortBy = "Popularity";
                        break;
                }
                isDescending = textIO.newBooleanInputReader().withDefaultValue(true).read("Descending [Y] or ascending order [N]?\n");
                setState(lastState);
                this.update();
            case LIST:
                terminal.resetToBookmark("START");
                ClientUI.updateProducts(sortBy, isDescending);
                boolean isBlackFriday = isBlackFriday();
                printList(ClientUI.products, new boolean[]{false, true, true, true, true, false, isBlackFriday, isBlackFriday, false});
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. Buy a product",
                        "2. Add a product to your shopping cart",
                        "3. Sort the products by",
                        "4. Return to main menu.\n")
                ))) {
                    case 1:
                        setState(State.PURCHASE);
                        break;
                    case 2:
                        setState(State.ADDCART);
                        break;
                    case 3:
                        setState(State.SORTBY);
                        break;
                    case 4:
                        setState(State.MAIN);
                        break;
                }
                terminal.setBookmark("LIST");
                this.update();
                break;
            case PURCHASE:
                buyProduct();
                terminal.resetToBookmark("LIST");
                setState(State.LIST);
                this.update();
                break;
            case PURCHASEALL:
                buyProducts(ClientUI.cart);
                ClientUI.cart = new ProductList();
                terminal.resetToBookmark("CART");
                setState(State.CART);
                this.update();
                break;
            case ADDCART:
                ClientUI.cart.add(pickProduct());
                terminal.resetToBookmark("LIST");
                setState(State.LIST);
                this.update();
                break;
            case CART:
                terminal.resetToBookmark("START");
                isBlackFriday = isBlackFriday();
                Collections.sort(ClientUI.cart.getProducts(), ClientUI.newProductComparator(sortBy, isDescending));
                printList(ClientUI.cart, new boolean[]{false, true, true, true, true, false, isBlackFriday, isBlackFriday, false});
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. Buy all products",
                        "2. Remove items from your cart",
                        "3. Sort the items in your cart by",
                        "4. Return to main menu.\n")
                ))) {
                    case 1:
                        setState(State.PURCHASEALL);
                        break;
                    case 2:
                        ClientUI.cart = new ProductList();
                        setState(State.CART);
                        break;
                    case 3:
                        setState(State.SORTBY);
                        break;
                    case 4:
                        setState(State.MAIN);
                        break;
                }
                terminal.setBookmark("CART");
                this.update();
                break;

            case EMPLOYEE:
                terminal.resetToBookmark("START");
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. View all products",
                        "2.  Log out.\n")
                ))) {
                    case 1:
                        setState(State.LISTADV);
                        break;
                    case 2:
                        setState(State.LOGIN);
                        break;
                }
                this.update();
                break;
            case LISTADV:
                terminal.resetToBookmark("START");
                ClientUI.updateProducts(sortBy, isDescending);
                isBlackFriday = isBlackFriday();
                printList(ClientUI.products, new boolean[]{false, true, true, true, true, true, isBlackFriday, isBlackFriday, false});
                switch (presentOptions(new ArrayList<>(Arrays.asList(
                        "1. Add new products",
                        "2. Edit existing products",
                        "3. Remove products",
                        "4. Sort products by",
                        "5. Start BlackFriday campaign",
                        "6. End BlackFriday campaign",
                        "7. Return to main menu.\n")
                ))) {
                    case 1:
                        setState(State.ADD);
                        break;
                    case 2:
                        setState(State.EDIT);
                        break;
                    case 3:
                        setState(State.REMOVE);
                        break;
                    case 4:
                        setState(State.SORTBY);
                        break;
                    case 5:
                        setState(State.STARTBLACK);
                        break;
                    case 6:
                        setState(State.ENDBLACK);
                        break;
                    case 7:
                        setState(State.MAIN);
                        break;
                }
                terminal.setBookmark("LISTADV");
                this.update();
                break;
            case ADD:
                terminal.resetToBookmark("LISTADV");
                addProduct();
                setState(State.LISTADV);
                this.update();
                break;
            case EDIT:
                terminal.resetToBookmark("LISTADV");
                editProduct();
                setState(State.LISTADV);
                this.update();
                break;
            case REMOVE:
                terminal.resetToBookmark("LISTADV");
                removeProduct();
                setState(State.LISTADV);
                this.update();
                break;
            case STARTBLACK:
                startPromotion();
                setState(State.LISTADV);
                this.update();
                terminal.resetToBookmark("LISTADV");
                break;
            case ENDBLACK:
                endPromotion();
                setState(State.LISTADV);
                this.update();
                terminal.resetToBookmark("LISTADV");
                break;
            case EXIT:
                break;
        }
    }


    private Account getUserData() {
        return new AccountDetailsCollector().apply(textIO);
    }

    private Account login() {
        Account account;
        while (true) {
            account = this.getUserData();
            Object temp = ClientUI.requestFromServer(new Message(MessageIndex.LOGIN, account));
            if (temp != null) {
                account = (Account) temp;
                break;
            }
        }
        return account;
    }

    private Account createAccount(){
        Account account;
        while (true) {
            account = this.getUserData();
            Object temp = ClientUI.requestFromServer(new Message(MessageIndex.CREATE, account));
            if (temp != null) {
                account = (Account) temp;
                break;
            }
        }
        return account;
    }

    private void buyProduct() {
        Product toBuy = pickProduct();
        ClientUI.requestFromServer(new Message(MessageIndex.PURCHASE, new ProductList(new ArrayList<>(Collections.singletonList(toBuy)))));//TODO: See if the returned boolean is useful, else remove
    }

    private void buyProducts(ProductList toBuy) {
        ClientUI.requestFromServer(new Message(MessageIndex.PURCHASE, toBuy));//TODO: See if the returned boolean is useful, else remove
    }

    private Product pickProduct() {
        return new PurchaseProduct().apply(textIO);
    }

    private int presentOptions(List<String> options) {
        int out;
        out = textIO.newIntInputReader()
                .withMinVal(1)
                .withMaxVal(options.size())
                .read(options);
        return out;
    }

    private void addProduct() {
        ClientUI.requestFromServer(new Message(MessageIndex.INSERT, new ProductList(new ArrayList<>(Collections.singletonList(
                new AddProduct().apply(textIO)
        )))));
    }

    private void editProduct() {
        ClientUI.requestFromServer(new Message(MessageIndex.EDIT, new ProductList(new ArrayList<>(Collections.singletonList(
                new EditProduct().apply(textIO)
        )))));
    }

    private void removeProduct() {
        ClientUI.requestFromServer(new Message(MessageIndex.DELETE, new ProductList(new ArrayList<>(Collections.singletonList(
                new RemoveProduct().apply(textIO)
        )))));
    }

    private void startPromotion() {
        new StartPromotion().accept(textIO);
        ClientUI.requestFromServer(new Message(MessageIndex.STARTBLACKFRIDAY, ClientUI.products));
    }


    private void endPromotion() {
        ClientUI.requestFromServer(new Message(MessageIndex.ENDBLACKFRIDAY, ClientUI.products));
    }

    private boolean isBlackFriday(){
        boolean result = false;
        for(Product p: ClientUI.products.getProducts()){
            if (p.getBlackfridayDiscount() > 0){
                result = true;
                break;
            }
        }
        return result;
    }


}

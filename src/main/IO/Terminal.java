package main.IO;

import main.Account;
import main.Message;
import main.Product;
import main.ProductList;
import main.client.ClientUI;
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
    private String topMessage = "";

    public enum State {
        // Common options
        START,
        CREATE,
        LOGIN,
        MAIN,
        SORT_BY,
        EXIT,
        // Customer options
        CUSTOMER,
        LIST,
        PURCHASE,
        PURCHASE_ALL,
        ADD_CART,
        CART,
        // Employee options
        EMPLOYEE,
        CREATE_EMP,
        LIST_EMP,
        ADD,
        EDIT,
        REMOVE,
        START_BLACK,
        END_BLACK,
        REVENUE

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
        props.setPaneBackgroundColor(new Color(10, 10, 90));
        props.setPaneDimension((int) screenSize.getWidth(), (int) screenSize.getHeight() / 2);

        //Register keystroke handlers

        String backKeyStroke = "ctrl Z";

        boolean registeredBack = terminal.registerHandler(backKeyStroke, t -> new ReadHandlerData(ABORT));

        if (!registeredBack) {
            terminal.println("No handlers can be registered.");
        } else {
            terminal.println("--------------------------------------------------------------------------------");
            if (registeredBack) {
                terminal.println("You can press '" + backKeyStroke + "' to go back to the previous field.\n");
            }
            terminal.println("You can use this key combinations at any moment during your data entry session.");
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
        try {
            switch (state) {
                case START:
                    terminal.resetToBookmark("START");
                    switch (presentOptions(new ArrayList<>(Arrays.asList(
                            "1. Sign in",
                            "2. Sign up",
                            "3. Close the shop\n"
                    )))) {
                        case 1:
                            setState(State.LOGIN);
                            break;
                        case 2:
                            setState(State.CREATE);
                            break;
                        case 3:
                            setState(State.EXIT);
                            break;
                    }
                    this.update();
                    break;
                case CREATE:
                    terminal.resetToBookmark("START");
                    createAccount(false);
                    setState(State.START);
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
                case SORT_BY:
                    ArrayList<String> options = new ArrayList<>(Arrays.asList(
                            "1. Name",
                            "2. Category",
                            "3. Price",
                            "4. Quantity",
                            "5. Discount Percent"));
                    if (isBlackFriday()) {
                        options.add("6. Promotional Price\n");
                    } else {
                        options.get(options.size() - 2).concat("\n");
                    }
                    switch (presentOptions(options)) {
                        case 1:
                            sortBy = "Name";
                            break;
                        case 2:
                            sortBy = "Category";
                            break;
                        case 3:
                            sortBy = "NormalPrice";
                            break;
                        case 4:
                            sortBy = "Quantity";
                            break;
                        case 5:
                            sortBy = "BlackfridayDiscount";
                            break;
                        case 6:
                            sortBy = "ActualPrice";
                            break;
                    }
                    isDescending = textIO.newBooleanInputReader().withDefaultValue(true).read("Descending [Y] or ascending order [N]?\n");
                    setState(lastState);
                    this.update();
                case LIST:
                    terminal.resetToBookmark("START");
                    topMessage();
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
                            setState(State.ADD_CART);
                            break;
                        case 3:
                            setState(State.SORT_BY);
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
                case PURCHASE_ALL:
                    buyProducts(ClientUI.cart);
                    ClientUI.cart = new ProductList();
                    terminal.resetToBookmark("CART");
                    setState(State.CART);
                    this.update();
                    break;
                case ADD_CART:
                    ClientUI.cart.add(pickProduct());
                    terminal.resetToBookmark("LIST");
                    setState(State.LIST);
                    this.update();
                    break;
                case CART:
                    terminal.resetToBookmark("START");
                    topMessage = String.format("Current total for your cart: %.2f", ProductList.calculateTotal(ClientUI.cart));
                    topMessage();
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
                            setState(State.PURCHASE_ALL);
                            break;
                        case 2:
                            ClientUI.cart = new ProductList();
                            setState(State.CART);
                            break;
                        case 3:
                            setState(State.SORT_BY);
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
                            "2. Create a new employee account",
                            "3.  Log out.\n")
                    ))) {
                        case 1:
                            setState(State.LIST_EMP);
                            break;
                        case 2:
                            setState(State.CREATE_EMP);
                            break;
                        case 3:
                            setState(State.START);
                            break;
                    }
                    this.update();
                    break;
                case CREATE_EMP:
                    terminal.resetToBookmark("START");
                    createAccount(true);
                    setState(State.MAIN);
                    this.update();
                    break;
                case LIST_EMP:
                    terminal.resetToBookmark("START");
                    topMessage();
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
                            "7. Calculate Revenue in period",
                            "8. Return to main menu.\n")
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
                            setState(State.SORT_BY);
                            break;
                        case 5:
                            setState(State.START_BLACK);
                            break;
                        case 6:
                            setState(State.END_BLACK);
                            break;
                        case 7:
                            setState(State.REVENUE);
                            break;
                        case 8:
                            setState(State.MAIN);
                            break;
                    }
                    terminal.setBookmark("LIST_EMP");
                    this.update();
                    break;
                case ADD:
                    terminal.resetToBookmark("LIST_EMP");
                    addProduct();
                    setState(State.LIST_EMP);
                    this.update();
                    break;
                case EDIT:
                    terminal.resetToBookmark("LIST_EMP");
                    editProduct();
                    setState(State.LIST_EMP);
                    this.update();
                    break;
                case REMOVE:
                    terminal.resetToBookmark("LIST_EMP");
                    setState(State.LIST_EMP);
                    removeProduct();
                    this.update();
                    break;
                case START_BLACK:
                    terminal.resetToBookmark("LIST_EMP");
                    startPromotion();
                    setState(State.LIST_EMP);
                    this.update();
                    break;
                case END_BLACK:
                    terminal.resetToBookmark("LIST_EMP");
                    endPromotion();
                    setState(State.LIST_EMP);
                    this.update();
                    break;
                case REVENUE:
                    terminal.resetToBookmark("LIST_EMP");
                    topMessage = String.format("The revenue in the selected period: %.2f", revenue());
                    setState(State.LIST_EMP);
                    this.update();
                    break;
                case EXIT:
                    textIO.dispose();
                    break;
            }
        } catch (ReadAbortedException e) {
            setState(lastState);
            this.update();
        }
    }


    private Account getUserData() {
        return new AccountDetailsCollector().apply(textIO);
    }

    private Account login() {
        Account account;
        while (true) {
            account = this.getUserData();
            Object temp = ClientUI.requestFromServer(new Message(Message.MessageIndex.LOGIN, account));
            if (temp != null) {
                account = (Account) temp;
                break;
            }
        }
        return account;
    }

    private void createAccount(boolean isEmployee) {
        Account account = this.getUserData();
        account.setEmployee(isEmployee);
        ClientUI.requestFromServer(new Message(Message.MessageIndex.CREATE, account));
    }

    private void buyProduct() {
        Product toBuy = pickProduct();
        ClientUI.requestFromServer(new Message(Message.MessageIndex.PURCHASE, new ProductList(new ArrayList<>(Collections.singletonList(toBuy)))));
    }

    private void buyProducts(ProductList toBuy) {
        ClientUI.requestFromServer(new Message(Message.MessageIndex.PURCHASE, toBuy));
    }

    private Product pickProduct() {
        return new PurchaseProduct().apply(textIO);
    }

    private int presentOptions(List<String> options) {
        int out = 0;
        try {
            out = textIO.newIntInputReader()
                    .withMinVal(1)
                    .withMaxVal(options.size())
                    .read(options);

        } catch (ReadAbortedException e) {
            setState(lastState);
        }
        return out;
    }

    private void addProduct() {
        ClientUI.requestFromServer(new Message(Message.MessageIndex.INSERT, new ProductList(new ArrayList<>(Collections.singletonList(
                new AddProduct().apply(textIO)
        )))));
    }

    private void editProduct() {
        ClientUI.requestFromServer(new Message(Message.MessageIndex.EDIT, new ProductList(new ArrayList<>(Collections.singletonList(
                new EditProduct().apply(textIO)
        )))));
    }

    private void removeProduct() {
        ClientUI.requestFromServer(new Message(Message.MessageIndex.DELETE, new ProductList(new ArrayList<>(Collections.singletonList(
                new RemoveProduct().apply(textIO)
        )))));
    }

    private void startPromotion() {
        new StartPromotion().accept(textIO);
        ClientUI.requestFromServer(new Message(Message.MessageIndex.START_BLACK_FRIDAY, ClientUI.products));
    }


    private void endPromotion() {
        ClientUI.requestFromServer(new Message(Message.MessageIndex.END_BLACK_FRIDAY, ClientUI.products));
    }

    private boolean isBlackFriday() {
        boolean result = false;
        for (Product p : ClientUI.products.getProducts()) {
            if (p.getBlackfridayDiscount() > 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    private double revenue() {
        return (double) ClientUI.requestFromServer(new Message(Message.MessageIndex.REVENUE, new Revenue().apply(textIO)));
    }

    private void topMessage() {
        terminal.println(topMessage);
        topMessage = "";
    }


}

package main.IO;

import main.client.ClientUI;
import main.Product;
import org.beryx.textio.ReadAbortedException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PurchaseProduct implements Function<TextIO, Product> {
    private final Product PRODUCT = new Product();
    private int productIndex = 1;
    private int quantity = 1;
    private final List<Runnable> OPERATIONS = new ArrayList<>();

    private int getProductIndex() {
        return productIndex;
    }

    private void setProductIndex(int productIndex) {
        this.productIndex = productIndex;
    }

    @Override
    public Product apply(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        addTask(textIO, "Which item would you like to purchase?", this::getProductIndex, this::setProductIndex, ClientUI.products.getProducts()::size);
        int step = 0;
        terminal.setBookmark("bookmark_" + step);
        try {
            OPERATIONS.get(step).run();
        }catch (ReadAbortedException e){
            throw e;
        }
        step++;
        addTask(textIO, "How many would you like to purchase?", () -> (this.quantity), (t) -> this.quantity = t, ClientUI.products.getProducts().get(getProductIndex() - 1)::getQuantity);
        while (step < OPERATIONS.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                OPERATIONS.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            } catch (IllegalArgumentException e) {
                terminal.println("Item out of stock");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                terminal.resetToBookmark("LIST");
                step = OPERATIONS.size();
            }
            step++;
        }
        PRODUCT.modify(ClientUI.products.getProducts().get(getProductIndex() - 1));
        PRODUCT.setQuantity(quantity);
        return PRODUCT;
    }

    private void addTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> maxValueSupplier) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(1)
                .withMaxVal(maxValueSupplier.get())
                .read(prompt)));
    }
}

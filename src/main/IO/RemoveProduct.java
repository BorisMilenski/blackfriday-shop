package main.IO;

import main.Product;
import main.client.ClientUI;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemoveProduct implements Function<TextIO, Product> {
    private final Product PRODUCT = new Product();
    private int productIndex = 1;
    private boolean confirm = false;
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
        addTask(textIO, "Which item would you like to remove?", this::getProductIndex, this::setProductIndex, ClientUI.products.getProducts()::size);
        OPERATIONS.get(0).run();
        PRODUCT.modify(ClientUI.products.getProducts().get(getProductIndex() - 1));
        confirmRemove(textIO, "Do you really want to remove: " + PRODUCT.getName() + "?\n", () -> confirm, (b) -> this.confirm = b);
        OPERATIONS.get(1).run();
        if (confirm){
        return PRODUCT;
        }
        else{
            return null;
        }
    }

    private void addTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> maxValueSupplier) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(1)
                .withMaxVal(maxValueSupplier.get())
                .read(prompt)));
    }

    private void confirmRemove(TextIO textIO, String prompt, Supplier<Boolean> defaultValueSupplier, Consumer<Boolean> valueSetter) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newBooleanInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }

}

package Main.IO;

import Main.Client.ClientUI;
import Main.Product;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemoveProduct implements Function<TextIO, Product> {
    private Product product = new Product();
    private int productIndex = 1;
    private final List<Runnable> operations = new ArrayList<>();

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
        operations.get(0).run();
        product.modify(ClientUI.products.getProducts().get(getProductIndex() - 1));
        return product;
    }

    private void addTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> maxValueSupplier) {
        operations.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(1)
                .withMaxVal(maxValueSupplier.get())
                .read(prompt)));
    }
}

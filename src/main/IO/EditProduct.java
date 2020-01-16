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

public class EditProduct implements Function<TextIO, Product> {
    private final Product PRODUCT = new Product();
    private int productIndex = 1;
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
        addIntTask(textIO, "Which item would you like to edit?", this::getProductIndex, this::setProductIndex, () -> 1, ClientUI.products.getProducts()::size);
        int step = 0;
        while (step < OPERATIONS.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                OPERATIONS.get(step).run();
            } catch (ReadAbortedException e) {
                if(step == 0) throw e;
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        PRODUCT.modify(ClientUI.products.get(productIndex - 1));
        addDoubleTask(textIO, "Product minimum price: ", PRODUCT::getMinPrice, PRODUCT::setMinPrice, () -> 0.0);
        addStringTask(textIO, "Product name: ", PRODUCT::getName, PRODUCT::setName);
        addEnumTask(textIO, "Product category: ", PRODUCT::getCategory, PRODUCT::setCategory);
        addIntTask(textIO, "Product quantity: ", PRODUCT::getQuantity, PRODUCT::setQuantity, PRODUCT::getQuantity, () -> 10000);
        while (step < OPERATIONS.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                OPERATIONS.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        addDoubleTask(textIO, "Product price: ", PRODUCT::getMinPrice, PRODUCT::setNormalPrice, PRODUCT::getMinPrice);
        while (step < OPERATIONS.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                OPERATIONS.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        return PRODUCT;
    }

    private void addStringTask(TextIO textIO, String prompt, Supplier<String> defaultValueSupplier, Consumer<String> valueSetter) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newStringInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }

    @SafeVarargs
    private final void addIntTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> minValueSupplier, Supplier<Integer>... maxValueSupplier) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
                .withMaxVal(maxValueSupplier[0].get())
                .read(prompt)));
    }

    private void addDoubleTask(TextIO textIO, String prompt, Supplier<Double> defaultValueSupplier, Consumer<Double> valueSetter, Supplier<Double> minValueSupplier) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newDoubleInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
                .read(prompt)));
    }

    private void addEnumTask(TextIO textIO, String prompt, Supplier<Product.Category> defaultValueSupplier, Consumer<Product.Category> valueSetter) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newEnumInputReader(Product.Category.class)
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }
}

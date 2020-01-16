package main.IO;

import main.Product;
import org.beryx.textio.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AddProduct implements Function<TextIO, Product> {
    private final Product PRODUCT = new Product();
    private final List<Runnable> OPERATIONS = new ArrayList<>();

    @Override
    public Product apply(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        addStringTask(textIO, "Product name: ", PRODUCT::getName, PRODUCT::setName, "^[a-zA-Z]*[a-zA-Z-_]$");
        addEnumTask(textIO, "Product category: ", PRODUCT::getCategory, PRODUCT::setCategory);
        addIntTask(textIO, "Product quantity: ", PRODUCT::getQuantity, PRODUCT::setQuantity, PRODUCT::getQuantity);
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
        addDoubleTask(textIO, "Product minimum price: ", PRODUCT::getMinPrice, PRODUCT::setMinPrice, () -> 0.0);
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
        PRODUCT.setName(PRODUCT.getName().substring(0,1).toUpperCase() + PRODUCT.getName().substring(1));
        return PRODUCT;
    }

    private void addStringTask(TextIO textIO, String prompt, Supplier<String> defaultValueSupplier, Consumer<String> valueSetter, String regex) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newStringInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withPattern(regex)
                .read(prompt)));
    }

    private void addIntTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> minValueSupplier) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
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

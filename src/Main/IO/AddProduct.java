package Main.IO;

import Main.Product;
import org.beryx.textio.ReadAbortedException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AddProduct implements Function<TextIO, Product> {
    private Product product = new Product();
    private final List<Runnable> operations = new ArrayList<>();

    @Override
    public Product apply(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        addStringTask(textIO, "Product name: ", product::getName, product::setName);
        addEnumTask(textIO, "Product category: ", product::getCategory, product::setCategory);
        addIntTask(textIO, "Product quantity: ", product::getQuantity, product::setQuantity, product::getQuantity);
        int step = 0;
        while (step < operations.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                operations.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        addDoubleTask(textIO, "Product minimum price: ", product::getMinPrice, product::setMinPrice, product::getMinPrice);
        while (step < operations.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                operations.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        addDoubleTask(textIO, "Product price: ", product::getMinPrice, product::setNormalPrice, product::getMinPrice);
        while (step < operations.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                operations.get(step).run();
            } catch (ReadAbortedException e) {
                if (step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        return product;
    }

    private void addStringTask(TextIO textIO, String prompt, Supplier<String> defaultValueSupplier, Consumer<String> valueSetter) {
        operations.add(() -> valueSetter.accept(textIO.newStringInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }

    private void addIntTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> minValueSupplier) {
        operations.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
                .read(prompt)));
    }

    private void addDoubleTask(TextIO textIO, String prompt, Supplier<Double> defaultValueSupplier, Consumer<Double> valueSetter, Supplier<Double> minValueSupplier) {
        operations.add(() -> valueSetter.accept(textIO.newDoubleInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
                .read(prompt)));
    }

    private void addEnumTask(TextIO textIO, String prompt, Supplier<Product.Category> defaultValueSupplier, Consumer<Product.Category> valueSetter) {
        operations.add(() -> valueSetter.accept(textIO.newEnumInputReader(Product.Category.class)
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }
}

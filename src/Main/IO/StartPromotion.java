package Main.IO;

import Main.Client.ClientUI;
import Main.Product;
import org.beryx.textio.ReadAbortedException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StartPromotion implements Consumer<TextIO> {
    private final List<Runnable> operations = new ArrayList<>();

    public void accept(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        terminal.println("Enter a discount percent (leave blank for 0%):");
        ArrayList<Product> products = ClientUI.products.getProducts();
        for (int i = 0; i < products.size(); i++) {
            int finalI = i;
            addDoubleTask(textIO, products.get(i).getName(), products.get(i)::getBlackfridayDiscount, (t)->{products.get(finalI).setBlackfridayDiscount(t/100);} , () -> 0.0, () -> 100*(1 - (products.get(finalI).getMinPrice() / (products.get(finalI).getNormalPrice()))));
        }
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
    }

    private void addDoubleTask(TextIO textIO, String prompt, Supplier<Double> defaultValueSupplier, Consumer<Double> valueSetter, Supplier<Double> minValueSupplier, Supplier<Double>... maxValueSupplier) {
        operations.add(() -> valueSetter.accept(textIO.newDoubleInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValueSupplier.get())
                .withMaxVal(maxValueSupplier[0].get())
                .read(prompt)));
    }

}

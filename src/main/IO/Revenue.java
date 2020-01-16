package main.IO;

import org.beryx.textio.ReadAbortedException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Revenue implements Function<TextIO, GregorianCalendar[]> {
    private final GregorianCalendar[] DATES = new GregorianCalendar[2];
    private final List<Runnable> OPERATIONS = new ArrayList<>();


    @Override
    public GregorianCalendar[] apply(TextIO textIO) {
        Calendar start = DATES[0];
        Calendar end = DATES[1];
        start.set(Calendar.MONTH, 0);
        TextTerminal<?> terminal = textIO.getTextTerminal();
        terminal.println("Enter a starting date:");
        addEnumTask(textIO, "Year", () -> start.get(Calendar.YEAR), (i) -> start.set(Calendar.YEAR, i), () -> 0, () -> start.getActualMaximum(Calendar.YEAR));
        addEnumTask(textIO, "Month", () -> start.get(Calendar.MONTH), (i) -> start.set(Calendar.MONTH, i-1), () -> 0, () -> start.getMaximum(Calendar.MONTH));
        addEnumTask(textIO, "Day", () -> start.get(Calendar.DAY_OF_MONTH), (i) -> start.set(Calendar.DAY_OF_MONTH, i), () -> 1, () -> start.getActualMaximum(Calendar.DAY_OF_MONTH));
        terminal.println("Enter a ending date:");
        addEnumTask(textIO, "Year", () -> end.get(Calendar.YEAR), (i) -> end.set(Calendar.YEAR, i), () -> 0, () -> end.getActualMaximum(Calendar.YEAR));
        addEnumTask(textIO, "Month", () -> end.get(Calendar.MONTH), (i) -> end.set(Calendar.MONTH, i-1), () -> 0, () -> end.getMaximum(Calendar.MONTH));
        addEnumTask(textIO, "Day", () -> end.get(Calendar.DAY_OF_MONTH), (i) -> end.set(Calendar.DAY_OF_MONTH, i), () -> 1, () -> end.getActualMaximum(Calendar.DAY_OF_MONTH));
        int step = 0;
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
        return DATES;
    }

    private void addEnumTask(TextIO textIO, String prompt, Supplier<Integer> defaultValueSupplier, Consumer<Integer> valueSetter, Supplier<Integer> minValue, Supplier<Integer> maxValue) {
        OPERATIONS.add(() -> valueSetter.accept(textIO.newIntInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinVal(minValue.get())
                .withMaxVal(maxValue.get())
                .read(prompt)));
    }

}

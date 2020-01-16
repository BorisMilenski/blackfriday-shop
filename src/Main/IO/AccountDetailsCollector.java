package Main.IO;

import Main.Account;
import org.beryx.textio.ReadAbortedException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AccountDetailsCollector implements Function<TextIO, Account> {

    protected final Account account = new Account();
    private final List<Runnable> operations = new ArrayList<>();

    @Override
    public Account apply(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        addTask(textIO, "Username", account::getUsername, account::setUsername, 1, false, "^[a-zA-Z0-9_-]+$");//TODO: Review Lambda expressions (functional interfaces) and method references!
        addTask(textIO, "Password", account::getPassword, account::setPassword, 8, true, "(([a-z])+([A-Z])+([0-9])+)");
        int step = 0;
        while(step < operations.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                operations.get(step).run();
            } catch (ReadAbortedException e) {
                if(step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }
        return account;
    }

    private void addTask(TextIO textIO, String prompt, Supplier<String> defaultValueSupplier, Consumer<String> valueSetter, int minLength, boolean inputMasking, String pattern) {
        operations.add(() -> valueSetter.accept(textIO.newStringInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .withMinLength(minLength)
                .withInputMasking(inputMasking)
                .withPattern(pattern)
                .read(prompt)));
    }
}
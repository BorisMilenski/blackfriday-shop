package Main.IO;

import Main.Product;
import Main.ProductList;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;


public class ListPrinter implements Consumer<TextIO> {
    private ProductList toPrint = null;
    private boolean[] fieldsToDisplay = null;

    public ListPrinter(ProductList toPrint, boolean[] fieldsToDisplay) {
        this.toPrint = toPrint;
        this.fieldsToDisplay = fieldsToDisplay;
    }

    @Override
    public void accept(TextIO textIO) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        terminal.executeWithPropertiesConfigurator((t) -> {
                    t.setPromptUnderline(true);
                    t.setPromptBold(true);
                }, (t) ->
                        terminal.print(getColumnLabels(fieldsToDisplay))
        );
        for (int i = 0; i < toPrint.getProducts().size(); i++) {
            terminal.print(getDataFromProduct(i + 1, toPrint.get(i), fieldsToDisplay));
        }
        terminal.println();
    }

    public String getColumnLabels(boolean[] fieldsToInclude){
        Field[] fields = Product.class.getDeclaredFields();
        ArrayList<String> args = new ArrayList<>();
        String format = "| %5s ";
        args.add("#");
        for(int i =0; i< fields.length; i++){
            if (fieldsToInclude[i]){
                format = format.concat("| %25s ");
                args.add(fields[i].getName().substring(0,1).toUpperCase() + fields[i].getName().substring(1));
            }
        }
        format = format.concat("|\n");
        return String.format(Locale.UK, format, args.toArray());
    }

    public String getDataFromProduct(int index, Product product, boolean[] fieldsToInclude) {
        Field[] fields = Product.class.getDeclaredFields();
        ArrayList<Method> methods= new ArrayList<>();
        for(int i = 0; i < fields.length; i++){
            String methodName = "get"+fields[i].getName().substring(0,1).toUpperCase()+fields[i].getName().substring(1);
            try {
                methods.add(Product.class.getMethod(methodName));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Object> args = new ArrayList<>();
        String format = "| %5d ";
        args.add(index);
        for(int i =0; i< fields.length; i++){

            if(fieldsToInclude[i]){
                if (fields[i].getType() == int.class){
                    format = format.concat("| %25d ");
                }
                if (fields[i].getType() == double.class){
                    if(fields[i].getName().contains("Discount")){
                        format = format.concat("| %24.2f%s ");
                        try {
                            args.add((Double)(methods.get(i).invoke(product))*100);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        args.add("%");
                        continue;
                    }
                    format = format.concat("| %25.2f ");
                }
                if (fields[i].getType() == String.class||fields[i].getType() == Product.Category.class){
                    format = format.concat("| %25s ");

                }
                if (fields[i].getType() == Product.Category.class){
                    try {
                        args.add(methods.get(i).invoke(product).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                try {
                    args.add(methods.get(i).invoke(product));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        format = format.concat("|\n");
        return String.format(Locale.UK, format, args.toArray());
    }

}

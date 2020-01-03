package Main.Exceptions;

public class BelowMinimumPriceException extends Exception {
    @Override
    public String getMessage() {
        return "Price set below minimum";
    }
}

package Main.Exceptions;

public class QuantityException extends Exception{
    @Override
    public String getMessage() {
        return "Quantity not available";
    }
}

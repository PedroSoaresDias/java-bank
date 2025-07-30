package java.bank.exception;

public class NotFundsEnoughException extends RuntimeException {
    public NotFundsEnoughException(String message) {
        super(message);
    }
}

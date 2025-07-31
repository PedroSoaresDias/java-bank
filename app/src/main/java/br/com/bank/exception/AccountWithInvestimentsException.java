package br.com.bank.exception;

public class AccountWithInvestimentsException extends RuntimeException {
    public AccountWithInvestimentsException(String message) {
        super(message);
    }
}

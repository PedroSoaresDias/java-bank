package java.bank.reposiroty;

import java.bank.exception.AccountNotFoundException;
import java.bank.exception.PixInUseException;
import java.bank.model.AccountWallet;
import java.util.List;

// import lombok.Getter;

import static java.bank.reposiroty.CommonsRepository.checkFundsForTransaction;

// @Getter
public class AccountRepository {
    private List<AccountWallet> accounts;

    public AccountWallet create(final List<String> pix, final long initialFunds) {
        var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();

        for (var p : pix) {
            if (pixInUse.contains(p)) {
                throw new PixInUseException("O pix '" + p + "' já está em uso.");
            }
        }

        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount) {
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "depósito");
    }

    public long withdraw(final String pix, final long amount) {
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "Pix enviado de '" + sourcePix + "' para '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByPix(final String pix) {
        return accounts.stream()
            .filter(a -> a.getPix().contains(pix))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix '" + pix + "' não existe ou foi encerrada."));
    }

    public List<AccountWallet> list() {
        return this.accounts;
    }
}

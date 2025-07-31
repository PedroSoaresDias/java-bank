package br.com.bank.reposiroty;

import br.com.bank.exception.AccountNotFoundException;
import br.com.bank.exception.PixInUseException;
import br.com.bank.model.AccountWallet;
import br.com.bank.model.MoneyAudit;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.bank.reposiroty.CommonsRepository.checkFundsForTransaction;
import static java.time.temporal.ChronoUnit.SECONDS;

public class AccountRepository {
    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(final List<String> pix, final long initialFunds) {
        if (!accounts.isEmpty()) {
            List<String> pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
    
            for (String p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("O pix '" + p + "' já está em uso.");
                }
            }
        }

        AccountWallet newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount) {
        AccountWallet target = findByPix(pix);
        target.addMoney(fundsAmount, "depósito");
    }

    public long withdraw(final String pix, final long amount) {
        AccountWallet source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        AccountWallet source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        AccountWallet target = findByPix(targetPix);
        String message = "Pix enviado de '" + sourcePix + "' para '" + targetPix + "'";
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

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix) {
        AccountWallet wallet = findByPix(pix);
        List<MoneyAudit> audit = wallet.getFinancialTransaction();
        return audit.stream().collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(SECONDS)));
    }
}

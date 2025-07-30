package java.bank.reposiroty;

import static java.bank.reposiroty.CommonsRepository.checkFundsForTransaction;

import java.bank.exception.AccountWithInvestimentsException;
import java.bank.exception.InvestimentNotFoundException;
import java.bank.exception.WalletNotFoundException;
import java.bank.model.AccountWallet;
import java.bank.model.Investiment;
import java.bank.model.InvestimentWallet;
import java.util.ArrayList;
import java.util.List;

public class InvestimentRepository {
    private long nextId;
    private final List<Investiment> investiments = new ArrayList<>();
    private final List<InvestimentWallet> wallets = new ArrayList<>();

    public Investiment create(final long tax, final long initialFunds) {
        this.nextId++;
        var investiment = new Investiment(this.nextId, tax, initialFunds);
        investiments.add(investiment);
        return investiment;
    }

    public InvestimentWallet initInvestiment(final AccountWallet account, final long id) {
        var accountsInUse = wallets.stream().map(InvestimentWallet::getAccount).toList();

        if (accountsInUse.contains(account)) {
            throw new AccountWithInvestimentsException("A conta '" + account + "' já possui um investimento.");
        }

        var investiment = findById(id);
        checkFundsForTransaction(account, investiment.initialFunds());
        var wallet = new InvestimentWallet(investiment, account, investiment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    public InvestimentWallet deposit(final String pix, final long funds) {
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimento");
        return wallet;
    }

    public InvestimentWallet withdraw(final String pix, final long funds) {
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Saque de investimentos");

        if (wallet.getFunds() == 0) {
            wallets.remove(wallet);
        }

        return wallet;
    }

    public void updateAmount() {
        wallets.forEach(w -> w.updateAmount(w.getInvestiment().tax()));
    }

    public Investiment findById(final long id) {
        return investiments.stream()
        .filter(i -> i.id() == id)
        .findFirst()
        .orElseThrow(() -> new InvestimentNotFoundException("O investimento '" + id + "' não foi encontrado."));
    }

    public InvestimentWallet findWalletByAccountPix(final String pix) {
        return wallets.stream()
        .filter(w -> w.getAccount().getPix().contains(pix))
        .findFirst()
        .orElseThrow(() -> new WalletNotFoundException("A carteira não foi encontrada"));
    }

    public List<InvestimentWallet> listWallets() {
        return this.wallets;
    }

    public List<Investiment> list() {
        return this.investiments;
    }
}

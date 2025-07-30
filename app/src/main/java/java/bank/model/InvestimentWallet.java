package java.bank.model;

import lombok.Getter;
import lombok.ToString;

import static java.bank.model.BankService.INVESTMENT;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@ToString
@Getter
public class InvestimentWallet extends Wallet {
    private final Investiment investiment;
    private final AccountWallet account;

    public InvestimentWallet(final Investiment investiment, final AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investiment = investiment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "Investimento");
    }

    public void updateAmount(final long percent) {
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getService(), "Rendimentos", OffsetDateTime.now());

        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();

        this.money.addAll(money);
    }
}

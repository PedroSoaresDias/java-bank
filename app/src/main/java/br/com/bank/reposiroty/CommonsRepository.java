package br.com.bank.reposiroty;

import lombok.NoArgsConstructor;

import static br.com.bank.model.BankService.ACCOUNT;
import static lombok.AccessLevel.PRIVATE;

import br.com.bank.exception.NoFundsEnoughException;
import br.com.bank.model.Money;
import br.com.bank.model.MoneyAudit;
import br.com.bank.model.Wallet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor(access =  PRIVATE)
public final class CommonsRepository {
    public static void checkFundsForTransaction(final Wallet source, final long amount) {
        if (source.getFunds() < amount) {
            throw new NoFundsEnoughException("Sua conta não tem dinheiro o suficiente para realizar essa transação.");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description) {
        MoneyAudit history = new MoneyAudit(transactionId, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }
}

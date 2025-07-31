package br.com.bank;

import br.com.bank.exception.AccountNotFoundException;
import br.com.bank.exception.NoFundsEnoughException;
import br.com.bank.exception.WalletNotFoundException;
import br.com.bank.model.AccountWallet;
import br.com.bank.model.Investment;
import br.com.bank.model.InvestmentWallet;
import br.com.bank.model.MoneyAudit;
import br.com.bank.reposiroty.AccountRepository;
import br.com.bank.reposiroty.InvestmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.time.OffsetDateTime;

public class App {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investimentRepository = new InvestmentRepository();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá, seja bem-vindo ao DIO Bank");

        while (true) {
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Criar uma carteira de investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar na conta");
            System.out.println("6 - Transferência entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");

            int option = scanner.nextInt();

            switch (option) {
                case 1 -> createAccount();
                case 2 -> createInvestiment();
                case 3 -> createWalletInvestiment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestiment();
                case 8 -> rescueInvestiment();
                case 9 -> accountRepository.list().forEach(System.out::println);
                case 10 -> investimentRepository.list().forEach(System.out::println);
                case 11 -> investimentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investimentRepository.updateAmount();
                    System.out.println("Investimentos reajustados.");
                }
                case 13 -> checkHistory();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }
    
    private static void createAccount() {
        System.out.println("Informe as chaves Pix (separadas por ';')");
        List<String> pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito");
        long amount = scanner.nextLong();
        AccountWallet wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada: " + wallet);
    }
    
    private static void createInvestiment() {
        System.out.println("Informe a taxa do investimento");
        int tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito");
        long initialFunds = scanner.nextLong();
        Investment investiment = investimentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado: " + investiment);
    }
    
    private static void withdraw() {
        System.out.println("Informe a chave Pix da conta para saque:");
        String pix = scanner.next();
        System.out.println("Informe o valor a ser sacado: ");
        long amount = scanner.nextLong();
        try {
            accountRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("Informe a chave Pix da conta para depósito:");
        String pix = scanner.next();
        System.out.println("Informe o valor a ser depositado: ");
        long amount = scanner.nextLong();

        try {
            accountRepository.deposit(pix, amount);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void transferToAccount() {
        System.out.println("Informe a chave Pix da conta de origem:");
        String source = scanner.next();
        System.out.println("Informe a chave Pix da conta de destino: ");
        String target = scanner.next();
        System.out.println("Informe o valor a ser transferido:");
        long amount = scanner.nextLong();

        try {
            accountRepository.transferMoney(source, target, amount);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createWalletInvestiment() {
        System.out.println("Informe a chave Pix da conta:");
        String pix = scanner.next();
        AccountWallet account = accountRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento");
        int investimentId = scanner.nextInt();
        InvestmentWallet investimentWallet = investimentRepository.initInvestiment(account, investimentId);
        System.out.println("Conta de investimento criada: " + investimentWallet);
    }

    private static void incInvestiment() {
        System.out.println("Informe a chave Pix da conta para investimento:");
        String pix = scanner.next();
        System.out.println("Informe o valor a ser investido: ");
        long amount = scanner.nextLong();

        try {
            investimentRepository.deposit(pix, amount);
        } catch (WalletNotFoundException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void rescueInvestiment() {
        System.out.println("Informe a chave Pix da conta para resgate do investimento:");
        String pix = scanner.next();
        System.out.println("Informe o valor a ser resgatado: ");
        long amount = scanner.nextLong();
        try {
            investimentRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkHistory() {
        System.out.println("Informe a chave Pix da conta para verificar extrato:");
        String pix = scanner.next();

        try {
            Map<OffsetDateTime, List<MoneyAudit>> sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k, v) -> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.getFirst().transactionId());
                System.out.println(v.getFirst().description());
                System.out.println("R$" + (v.size() / 100) + "," + (v.size() % 100));
            });
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

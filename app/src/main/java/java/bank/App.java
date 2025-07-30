package java.bank;

import java.bank.exception.AccountNotFoundException;
import java.bank.exception.NoFundsEnoughException;
import java.bank.reposiroty.AccountRepository;
import java.bank.reposiroty.InvestimentRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestimentRepository investimentRepository = new InvestimentRepository();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá, seja bem-vindo ao DIO Bank");

        while (true) {
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
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
                case 1: createAccount();
                case 2: createInvestiment();
                case 3:
                case 4: deposit();
                case 5: withdraw();
                case 6:
                case 7:
                case 8:
                case 9:
                    accountRepository.list().forEach(System.out::println);
                case 10:
                    investimentRepository.list().forEach(System.out::println);
                case 11:
                    investimentRepository.listWallets().forEach(System.out::println);
                case 12: {
                    investimentRepository.updateAmount();
                    System.out.println("Investimentos reajustados.");
                }
                case 13:
                case 14:
                    System.exit(0);
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }
    
    private static void createAccount() {
        System.out.println("Informe as chaves Pix (separadas por ';')");
        List<String> pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito");
        long amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada: " + wallet);
    }
    
    private static void createInvestiment() {
        System.out.println("Informe a taxa do investimento");
        int tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito");
        long initialFunds = scanner.nextLong();
        var investiment = investimentRepository.create(tax, initialFunds);
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
}

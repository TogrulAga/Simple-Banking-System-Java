package banking;

import java.util.Scanner;

public class BankingSystem {
    static Scanner scanner = new Scanner(System.in);
    static AccountList accountList;

    public static void run(String fileName) {
        accountList = new AccountList(fileName);
        
        while (true) {
            var answer = showMenu();

            switch (answer) {
                case 1:
                    generateAccount();
                    break;
                case 2:
                    var account = logIn();
                    if (account != null) {
                        accountOperations(account);
                    }
                    break;
                case 0:
                    exit();
                    return;
            }
        }
    }

    private static void accountOperations(Account account) {
        System.out.println();
        if (account == null) {
            System.out.println("Wrong card number or PIN!\n");
            return;
        }

        System.out.println("You have successfully logged in!\n");

        while (true) {
            var answer = showAccountMenu();

            switch (answer) {
                case 1:
                    System.out.printf("%nBalance: %d%n", account.getBalance());
                    break;
                case 2:
                    addIncome(account);
                    break;
                case 3:
                    doTransfer(account);
                    break;
                case 4:
                    closeAccount(account);
                    return;
                case 5:
                    System.out.println("\nYou have successfully logged out!\n");
                    return;
                case 0:
                    exit();
            }
        }
    }

    private static void closeAccount(Account account) {
        accountList.closeAccount(account);

        System.out.println("\nThe account has been closed!");
    }

    private static void doTransfer(Account account) {
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        String cardNumber = scanner.next();

        if (cardNumber.equals(account.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        if (!Account.checkLuhnAlgorithm(cardNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }

        Account transferAccount = accountList.validateTransferAccount(cardNumber);

        if (transferAccount == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int transferAmount = scanner.nextInt();

        if (account.getBalance() < transferAmount) {
            System.out.println("Not enough money!");
            return;
        }

        accountList.addIncome(transferAccount, transferAmount);
        accountList.addIncome(account, -transferAmount);

        System.out.println("Success!");
    }

    private static void addIncome(Account account) {
        System.out.println("\nEnter income:");
        int income = scanner.nextInt();
        accountList.addIncome(account, income);
        System.out.println("Income was added!");
    }

    private static Account logIn() {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.next();

        System.out.println("Enter your PIN:");
        String pinCode = scanner.next();

        return accountList.validate(cardNumber, pinCode);
    }

    private static void generateAccount() {
        var account = accountList.createAccount();

        System.out.println();
        System.out.println("Your card has been created");

        System.out.println("Your card number:");
        System.out.println(account.getCardNumber());

        System.out.println("Your card PIN:");
        System.out.println(account.getPinCode());

        System.out.println();
    }

    private static void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }

    private static int showMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");

        return scanner.nextInt();
    }

    private static int showAccountMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");

        return scanner.nextInt();
    }
}

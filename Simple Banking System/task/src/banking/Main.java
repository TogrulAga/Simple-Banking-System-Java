package banking;

public class Main {
    public static void main(String[] args) {
        String fileName = "";
        for (int i = 0; i < args.length - 1; i++) {
            if ("-fileName".equals(args[i])) {
                fileName = args[i + 1];
            }
        }
        BankingSystem.run(fileName);
    }
}
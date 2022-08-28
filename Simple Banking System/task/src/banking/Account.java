package banking;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private static final String MAJOR_INDUSTRY_IDENTIFIER = "400000";
    private final String checksum;
    private final String cardNumber;
    private final String pinCode;
    private int balance;

    public Account(String accountNumber, String pinCode) {
        this.checksum = calculateChecksum(MAJOR_INDUSTRY_IDENTIFIER, accountNumber);
        this.cardNumber = MAJOR_INDUSTRY_IDENTIFIER + accountNumber + this.checksum;
        this.pinCode = pinCode;
        this.balance = 0;
    }

    public Account(String cardNumber, String pinCode, int balance) {
        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.balance = balance;
        this.checksum = String.valueOf(this.cardNumber.charAt(15));
    }

    private static String calculateChecksum(String mii, String accountNumber) {
        var string = mii + accountNumber;
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            digits.add(Integer.parseInt(String.valueOf(string.charAt(i))));
        }

        for (int i = 0; i < digits.size(); i += 2) {
            var digit = digits.get(i) * 2;
            digit -= digit > 9 ? 9 : 0;
            digits.set(i, digit);
        }

        var sum = 0;
        for (Integer digit : digits) {
            sum += digit;
        }

        return String.valueOf(sum % 10 != 0 ? 10 - sum % 10 : 0);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinCode() {
        return pinCode;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int income) {
        balance += income;
    }

    public static boolean checkLuhnAlgorithm(String cardNumber) {
        String checksum = String.valueOf(cardNumber.charAt(cardNumber.length() - 1));
        String mii = cardNumber.substring(0, 6);
        String calculatedChecksum = calculateChecksum(mii, cardNumber.substring(6, cardNumber.length() - 1));
        return checksum.equals(calculatedChecksum);
    }
}

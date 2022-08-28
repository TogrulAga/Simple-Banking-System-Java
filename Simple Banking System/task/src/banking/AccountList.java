package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;

public class AccountList {
    private final Random random = new Random();
    static SQLiteDataSource dataSource = new SQLiteDataSource();

    public AccountList(String fileName) {
        String url = String.format("jdbc:sqlite:%s", fileName);

        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                try (Statement statement = con.createStatement()) {
                    statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                            "id INTEGER PRIMARY KEY," +
                            "number TEXT," +
                            "pin TEXT," +
                            "balance INTEGER DEFAULT 0)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account createAccount() {
        String accountNumber;
        String pinCode;
        Account account;

        do {
            accountNumber = getRandomAccountNumber();
            pinCode = getRandomPinCode();
            account = new Account(accountNumber, pinCode);
        } while (isAccountCreated(account));

        saveToDB(account);

        return account;
    }

    private void saveToDB(Account account) {
        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                try (Statement statement = con.createStatement()) {
                    statement.executeUpdate("INSERT INTO card (number, pin, balance) VALUES " +
                            String.format("('%s', '%s', %d)", account.getCardNumber(), account.getPinCode(), account.getBalance()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isAccountCreated(Account account) {
        String selectAccountSQL = "SELECT * FROM card WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                try (PreparedStatement selectAccount = con.prepareStatement(selectAccountSQL)) {
                    selectAccount.setString(1, account.getCardNumber());
                    ResultSet resultSet = selectAccount.executeQuery();

                    if (resultSet.isClosed()) {
                        return false;
                    }

                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getRandomAccountNumber() {
        return getRandomNumberString(9);
    }

    public String getRandomPinCode() {
        return getRandomNumberString(4);
    }

    public String getRandomNumberString(int size) {
        var digits = random.ints(size, 0, 10).toArray();
        StringBuilder number = new StringBuilder();

        for (int digit: digits) {
            number.append(digit);
        }

        return number.toString();
    }

    public Account validate(String cardNumber, String pinCode) {
        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                try (Statement statement = con.createStatement()) {
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM card " +
                            String.format("WHERE number='%s' AND pin='%s'", cardNumber, pinCode));
                    resultSet.next();
                    if (resultSet.isClosed()) {
                        return null;
                    }

                    int balance = resultSet.getInt("balance");
                    resultSet.close();

                    return new Account(cardNumber, pinCode, balance);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Account validateTransferAccount(String cardNumber) {
        String selectAccountSQL = "SELECT * FROM card WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                try (PreparedStatement selectAccount = con.prepareStatement(selectAccountSQL)) {
                    selectAccount.setString(1, cardNumber);
                    ResultSet resultSet = selectAccount.executeQuery();
                    if (resultSet.isClosed()) {
                        return null;
                    }

                    int balance = resultSet.getInt("balance");
                    resultSet.close();

                    return new Account(cardNumber, null, balance);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addIncome(Account account, int income) {
        String updateAccountSQL = "UPDATE card SET balance = ? WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement updateAccount = con.prepareStatement(updateAccountSQL)) {
                updateAccount.setInt(1, account.getBalance() + income);
                updateAccount.setString(2, account.getCardNumber());
                account.setBalance(income);
                updateAccount.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAccount(Account account) {
        String deleteAccountSQL = "DELETE FROM card WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement deleteAccount = con.prepareStatement(deleteAccountSQL)) {
                deleteAccount.setString(1, account.getCardNumber());
                deleteAccount.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

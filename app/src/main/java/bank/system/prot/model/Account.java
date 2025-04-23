package bank.system.prot.model;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Account {
    private final IntegerProperty accountId;
    private final ObjectProperty<BigDecimal> balance;

    public Account(int accountId, BigDecimal balance) {
        this.accountId = new SimpleIntegerProperty(accountId);
        this.balance = new SimpleObjectProperty<>(balance);
    }

    public int getAccountId() {
        return accountId.get();
    }

    public IntegerProperty accountIdProperty() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance.get();
    }

    public ObjectProperty<BigDecimal> balanceProperty() {
        return balance;
    }
}
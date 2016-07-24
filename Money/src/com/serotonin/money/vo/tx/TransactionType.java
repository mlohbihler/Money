package com.serotonin.money.vo.tx;

public enum TransactionType {
    BUY("Buy", Buy.class, false), //
    CASHADJ("Cash adjustment", CashAdjustment.class, false), //
    CASHDIV("Cash dividend", CashDividend.class, true), //
    CONTRIBUTION("Contribution", Contribution.class, false), //
    DEPOSIT("Deposit", Deposit.class, false), //
    EXCHADJ("Exchange adjustment", ExchangeAdjustment.class, false), //
    FEE_REBATE("Fee rebate", FeeRebate.class, false), //
    GRANT("Grant", Grant.class, false), //
    INTEREST("Interest", Interest.class, false), //
    MANAGEMENT_FEE("Management fee", ManagementFee.class, false), //
    MERGER("Merger", Merger.class, false), //
    REINVDIV("Reinvested dividend", ReinvestedDividend.class, true), //
    SELL("Sell", Sell.class, false), //
    SPLIT("Split", Split.class, false), //
    SPLIT_CASH("Split cash", SplitCash.class, false), //
    STOCKDIV("Stock dividend", StockDividend.class, true), //
    TAX("Tax", Tax.class, false), //
    TRANSFER_IN("Transfer in", TransferIn.class, false), //
    WITHDRAWAL("Withdrawal", Withdrawal.class, false), //
    ;

    public final String prettyName;
    private final Class<? extends Transaction> clazz;
    public final boolean dividend;

    private TransactionType(String prettyName, Class<? extends Transaction> clazz, boolean dividend) {
        this.prettyName = prettyName;
        this.clazz = clazz;
        this.dividend = dividend;
    }

    public Transaction createTransaction() {
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TransactionType forPrettyName(String s) {
        for (TransactionType type : values()) {
            if (type.prettyName.equals(s))
                return type;
        }
        throw new RuntimeException("No transaction type for description '" + s + "'");
    }

    public static TransactionType forString(String s) {
        if (s == null)
            return null;
        try {
            return valueOf(s);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}

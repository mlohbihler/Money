package com.serotonin.money.util;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lohbihler.atomicjson.JList;
import lohbihler.atomicjson.JMap;
import lohbihler.atomicjson.JsonReader;

public class RateOfReturnTest {
    static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");

    public static void main(final String[] args) throws Exception {
        try (final FileReader file = new FileReader("private/values.json")) {
            final JList values = new JsonReader(file).read();
            for (final Object value : values)
                calculateReturns(new Account((JMap) value));
        }
    }

    private static void calculateReturns(final Account account) {
        for (final Valuation valuation : account.valuations) {
            calculateReturn(account, valuation);
        }
    }

    private static void calculateReturn(final Account account, final Valuation valuation) {
        // Get the list of investments made on or before the valuation.
        final List<Investment> investmentList = new ArrayList<>();
        for (final AccountInvestment ai : account.investments) {
            if (ai.date.after(valuation.date))
                break;
            investmentList.add(new Investment(ai.amount, RateOfReturn.differenceInYears(ai.date, valuation.date)));
        }

        // Convert to an array
        final Investment[] investments = investmentList.toArray(new Investment[investmentList.size()]);

        // Calculate ROR
        final double ror = RateOfReturn.calculate(calculateInterest(valuation.value, investments), investments);
        System.out.println(account.name + " @ " + SDF.format(valuation.date.getTime()) + ": " + ror);
    }

    private static double calculateInterest(final double totalValue, final Investment... investments) {
        double interest = totalValue;
        for (final Investment investment : investments)
            interest -= investment.amount.doubleValue();
        return interest;
    }

    static class Account {
        final String name;
        final AccountInvestment[] investments;
        final Valuation[] valuations;

        public Account(final JMap json) {
            name = json.getString("name");

            final JList invJsons = json.getList("investments");
            investments = new AccountInvestment[invJsons.size()];
            for (int i = 0; i < investments.length; i++)
                investments[i] = new AccountInvestment(invJsons.getMap(i));

            final JList valJsons = json.getList("valuations");
            valuations = new Valuation[valJsons.size()];
            for (int i = 0; i < valuations.length; i++)
                valuations[i] = new Valuation(valJsons.getMap(i));
        }
    }

    static class AccountInvestment {
        final GregorianCalendar date;
        final double amount;

        public AccountInvestment(final JMap json) {
            date = toGc(json.getString("date"));
            amount = json.getDouble("amount");
        }
    }

    static class Valuation {
        final GregorianCalendar date;
        final double value;

        public Valuation(final JMap json) {
            date = toGc(json.getString("date"));
            value = json.getDouble("value");
        }
    }

    private static GregorianCalendar toGc(final String date) {
        final GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTimeInMillis(SDF.parse(date).getTime());
        } catch (final ParseException e) {
            throw new RuntimeException(e);
        }
        return gc;
    }
}

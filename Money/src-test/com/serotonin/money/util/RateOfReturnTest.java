package com.serotonin.money.util;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonTypeReader;
import com.serotonin.json.type.JsonValue;

public class RateOfReturnTest {
    static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");

    public static void main(String[] args) throws Exception {
        try (final FileReader file = new FileReader("private/values.json")) {
            final JsonTypeReader reader = new JsonTypeReader(file);
            final JsonArray values = reader.read().toJsonArray();
            for (final JsonValue value : values)
                calculateReturns(new Account(value.toJsonObject()));
        }
    }

    private static void calculateReturns(Account account) {
        for (Valuation valuation : account.valuations) {
            calculateReturn(account, valuation);
        }
    }
    
    private static void calculateReturn(Account account, Valuation valuation) {
        // Get the list of investments made on or before the valuation.
        List<Investment> investmentList = new ArrayList<>();
        for (AccountInvestment ai : account.investments) {
            if (ai.date.after(valuation.date))
                break;
            investmentList.add(new Investment(ai.amount, RateOfReturn.differenceInYears(ai.date, valuation.date)));
        }
        
        // Convert to an array
        Investment[] investments = investmentList.toArray(new Investment[investmentList.size()]);
        
        // Calculate ROR
        final double ror = RateOfReturn.calculate(calculateInterest(valuation.value, investments), investments);
        System.out.println(account.name + " @ " + SDF.format(valuation.date.getTime()) + ": " + ror);
    }

    private static double calculateInterest(double totalValue, Investment... investments) {
        double interest = totalValue;
        for (final Investment investment : investments)
            interest -= investment.amount.doubleValue();
        return interest;
    }

    static class Account {
        final String name;
        final AccountInvestment[] investments;
        final Valuation[] valuations;

        public Account(JsonObject json) {
            name = json.getString("name");
            
            final JsonArray invJsons = json.getJsonArray("investments");
            investments = new AccountInvestment[invJsons.size()];
            for (int i = 0; i < investments.length; i++)
                investments[i] = new AccountInvestment(invJsons.getJsonObject(i));
            
            final JsonArray valJsons = json.getJsonArray("valuations");
            valuations = new Valuation[valJsons.size()];
            for (int i = 0; i < valuations.length; i++)
                valuations[i] = new Valuation(valJsons.getJsonObject(i));
        }
    }
    
    static class AccountInvestment {
        final GregorianCalendar date;
        final double amount;
        
        public AccountInvestment(JsonObject json) {
            date = toGc(json.getString("date"));
            amount = json.getDouble("amount");
        }
    }
    
    static class Valuation {
        final GregorianCalendar date;
        final double value;
        
        public Valuation(JsonObject json) {
            date = toGc(json.getString("date"));
            value = json.getDouble("value");
        }
    }
    
    private static GregorianCalendar toGc(String date) {
        final GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTimeInMillis(SDF.parse(date).getTime());
        }
        catch (final ParseException e) {
            throw new RuntimeException(e);
        }
        return gc;
    }
}

package com.serotonin.money.util;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonTypeReader;
import com.serotonin.json.type.JsonValue;

public class RateOfReturnTest {
    static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");

    public static void main(String[] args) throws Exception {
        final JsonTypeReader reader = new JsonTypeReader(new FileReader("private/values.json"));
        final JsonArray values = reader.read().toJsonArray();
        for (final JsonValue value : values)
            calculateReturn(new AccountValue(value.toJsonObject()));
    }

    private static void calculateReturn(AccountValue accountValue) {
        final double ror = RateOfReturn.calculate(calculateInterest(accountValue.value, accountValue.investments),
                accountValue.investments);
        System.out.println(accountValue.name + " @ " + SDF.format(accountValue.date.getTime()) + ": " + ror);
    }

    private static double calculateInterest(double totalValue, Investment... investments) {
        double interest = totalValue;
        for (final Investment investment : investments)
            interest -= investment.amount.doubleValue();
        return interest;
    }

    static class AccountValue {
        final String name;
        final GregorianCalendar date;
        final double value;
        final Investment[] investments;

        public AccountValue(JsonObject json) {
            name = json.getString("name");
            date = toGc(json.getString("date"));
            value = json.getDouble("value");

            final JsonArray invJsons = json.getJsonArray("investments");
            investments = new Investment[invJsons.size()];
            for (int i = 0; i < investments.length; i++) {
                final JsonObject invJson = invJsons.getJsonObject(i);
                investments[i] = new Investment(invJson.getBigDecimal("amount"),
                        RateOfReturn.differenceInYears(toGc(invJson.getString("date")), date));
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
}

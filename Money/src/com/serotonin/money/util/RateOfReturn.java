package com.serotonin.money.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class RateOfReturn {
    public static double calculate(double totalInterest, Investment... investments) {
        List<Investment> list = new ArrayList<Investment>(investments.length);
        for (Investment i : investments)
            list.add(i);
        return calculate(new BigDecimal(totalInterest), list);
    }

    public static double calculate(BigDecimal totalInterest, List<Investment> investments) {
        totalInterest = totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP);

        double min = Double.NaN;
        double max = Double.NaN;

        double test = 1;
        while (true) {
            BigDecimal testInterest = getInterest(test, investments).setScale(2, BigDecimal.ROUND_HALF_UP);

            int comp = totalInterest.compareTo(testInterest);

            if (comp == 0)
                return test;

            if (comp < 0) {
                max = test;
                if (Double.isNaN(min))
                    test /= 2;
                else
                    test = (max + min) / 2;
            }
            else {
                min = test;
                if (Double.isNaN(max))
                    test *= 2;
                else
                    test = (max + min) / 2;
            }

            // Check to ensure we don't end up in an infinite loop.
            if (test == min || test == max)
                return test;
        }
    }

    private static BigDecimal getInterest(double rate, List<Investment> investments) {
        BigDecimal total = new BigDecimal(0);
        BigDecimal invested = new BigDecimal(0);
        for (Investment investment : investments) {
            invested = invested.add(investment.amount);
            total = total.add(investment.amount.multiply(new BigDecimal(Math.pow(rate, investment.years))));
        }
        return total.subtract(invested);
    }

    public static void main(String[] args) {
        //        // AIM1523
        //        System.out.println(calculate(-665, new Investment(21333.78, 1.3972453027921252), new Investment(-20668.77732,
        //                1.3917658507373307)));
        //
        //        // ARMH
        //        System.out.println(calculate(1228.57, new Investment(2650.71, 1.649300097312673), new Investment(-3868.09,
        //                1.14245078224418)));
        //        // SY
        //        System.out.println(calculate(1994.25, new Investment(3088.72, 2.1588891384085636), new Investment(-9.99,
        //                2.1506699603263715), new Investment(2134.60, 1.649300097312673), new Investment(-4012.0671,
        //                1.6465603712852759), new Investment(-3195.51, 1.6465603712852759)));
        //        // GOOG
        //        System.out.println(calculate(697.26, new Investment(2700.09, 1.649300097312673), new Investment(3101.60,
        //                1.3808069466277415), new Investment(-6498.95, 1.2191631110113033)));
        //
        // AMZN
        System.out.println( //
                calculate(697.26, //
                        new Investment(2700.09, 1.649300097312673), //
                        new Investment(3101.60, 1.3808069466277415), //
                        new Investment(-6498.95, 1.2191631110113033)));

        //        System.out.println(calculate(228.5, new Investment(500, 3), new Investment(300, 2)));
        //        System.out.println(calculate(234.8, new Investment(800, 3), new Investment(-300, 1)));
        //        System.out.println(calculate(231.8, new Investment(800, 3), new Investment(-300, 2), new Investment(300, 1)));

        //        System.out.println(calculate(250, new Investment(1000, 0.5), new Investment(-1500, 0)));
        //        System.out.println(calculate(250, new Investment(1000, 1), new Investment(-1500, 0.5)));
        //        System.out.println(calculate(250, new Investment(1000, 1.5), new Investment(-1500, 1)));
        //        System.out.println(calculate(250, new Investment(1000, 2), new Investment(-1500, 1.5)));
        //        System.out.println(calculate(250, new Investment(1000, 2.5), new Investment(-1500, 2)));
        //        System.out.println(calculate(250, new Investment(1000, 3), new Investment(-1500, 2.5)));

        //        System.out.println(calculate(500, new Investment(1000, 0.5), new Investment(-1500, 0)));
        //        System.out.println(calculate(500, new Investment(1000, 1), new Investment(-1500, 0.5)));
        //        System.out.println(calculate(500, new Investment(1000, 1.5), new Investment(-1500, 1)));
        //        System.out.println(calculate(500, new Investment(1000, 2), new Investment(-1500, 1.5)));
        //        System.out.println(calculate(500, new Investment(1000, 2.5), new Investment(-1500, 2)));
        //        System.out.println(calculate(500, new Investment(1000, 3), new Investment(-1500, 2.5)));

        //        // SPB
        //        double days = 20;
        //        List<Investment> investments = new ArrayList<Investment>();
        //        investments.add(new Investment(1908.99, days / 365));
        //        investments.add(new Investment(-2153.01, (days - 2) / 365));
        //
        //        double rate = 1;
        //        while (rate <= 2) {
        //            System.out.println(getInterest(rate, investments));
        //            rate += 0.001;
        //        }

        //        List<Investment> investments = new ArrayList<Investment>();
        //        investments.add(new Investment(1000, 1D));
        //        investments.add(new Investment(-1100, 350D / 365));
        //        System.out.println(calculate(new BigDecimal(-1), investments));

        //        List<Investment> investments = new ArrayList<Investment>();
        //        investments.add(new Investment(1000, 1D));
        //        investments.add(new Investment(-1100, 0));
        //        //        System.out.println(calculate(new BigDecimal(100), investments));
        //
        //        System.out.println(getInterest(1.1, investments));

        //        double rate = -0.1;
        //        while (rate <= 2) {
        //            System.out.println(getInterest(rate, investments));
        //            rate += 0.01;
        //        }

        //        System.out.println(calculate(244.02, new Investment(642.99, 2.1181974698704993), new Investment(633.00,
        //                2.1181974698704993), new Investment(633.00, 2.1181974698704993), new Investment(-2153.00,
        //                2.1127329889961826)));

        //        double days = 450;
        //        System.out.println(calculate(244.02, new Investment(642.99, days / 365), new Investment(633.00, days / 365),
        //                new Investment(633.00, days / 365), new Investment(-2153.01, (days - 2) / 365)));

        //        System.out.println(calculate(1500, new Investment(500, 3), new Investment(300, 2)));
        //        System.out.println(calculate(1500, new Investment(800, 3), new Investment(-300, 1)));
        //        System.out.println(calculate(1500, new Investment(800, 3), new Investment(-300, 2), new Investment(300, 1)));
        //
        //        System.out.println(calculate(1100, new Investment(500, 2), new Investment(500, 1)));
        //        System.out.println(calculate(1100, new Investment(1000, 2), new Investment(-500, 1)));
        //System.out.println(calculate(1100, new Investment(1000, 2), new Investment(-32.62855, 1)));

        //        System.out.println(calculate(1000, new Investment(500, 2), new Investment(300, 1)));
        //        System.out.println(calculate(1000, new Investment(800, 2), new Investment(-300, 1)));
        //System.out.println(calculate(1000, new Investment(800, 2), new Investment(-32.62855, 1)));

        //        System.out.println(calculate(2900, new Investment(1000, 2), new Investment(1000, 1)));
        //        System.out.println(calculate(2900, new Investment(1000, 1), new Investment(1000, 0.5)));
        //        System.out.println(calculate(2900, new Investment(1000, 1)));
        //        System.out.println(calculate(900, new Investment(1000, 1)));
        //        System.out.println(calculate(-900, new Investment(1000, 1)));
        //
        //        System.out.println(calculate(1500, new Investment(500, 2), new Investment(500, 1)));
        //
        //        System.out.println(calculate(1500, new Investment(1000, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-500, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-100, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-10, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-1, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-900, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-990, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-999, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-1000, 1)));
        //        System.out.println(calculate(1500, new Investment(1000, 2), new Investment(-1001, 1)));

        //        List<Investment> investments = new ArrayList<Investment>();
        //        investments.add(new Investment(1000, 1D));
        //        investments.add(new Investment(-1100, 0.98));
        //        System.out.println(calculate2(new BigDecimal(50), investments));
    }

    public static int differenceInDays(GregorianCalendar from, GregorianCalendar to) {
        if (from.compareTo(to) > 0) {
            GregorianCalendar swap = from;
            from = to;
            to = swap;
        }

        from = (GregorianCalendar) from.clone();

        int days = 0;

        int year = from.get(Calendar.YEAR);
        while (year < to.get(Calendar.YEAR)) {
            days += daysInYear(from, year);
            days -= from.get(Calendar.DAY_OF_YEAR) - 1;

            year++;
            from.set(year, 0, 1);
        }

        days += to.get(Calendar.DAY_OF_YEAR) - from.get(Calendar.DAY_OF_YEAR);

        return days;
    }

    public static double differenceInYears(Date from, Date to) {
        return differenceInYears(toGC(from), toGC(to));
    }

    private static GregorianCalendar toGC(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        return gc;
    }

    public static double differenceInYears(GregorianCalendar from, GregorianCalendar to) {
        if (from.compareTo(to) > 0) {
            GregorianCalendar swap = from;
            from = to;
            to = swap;
        }

        from = (GregorianCalendar) from.clone();

        double years = 0;

        int year = from.get(Calendar.YEAR);
        while (year < to.get(Calendar.YEAR)) {
            int daysInYear = daysInYear(from, year);
            int days = daysInYear - from.get(Calendar.DAY_OF_YEAR) + 1;
            years += ((double) days) / daysInYear;

            year++;
            from.set(year, 0, 1);
        }

        years += ((double) (to.get(Calendar.DAY_OF_YEAR) - from.get(Calendar.DAY_OF_YEAR))) / daysInYear(from, year);

        return years;
    }

    private static int daysInYear(GregorianCalendar gc, int year) {
        if (gc.isLeapYear(year))
            return 366;
        return 365;
    }
}

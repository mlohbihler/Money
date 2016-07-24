package test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) {
        GregorianCalendar gc = new GregorianCalendar(2014, Calendar.APRIL, 1);
        System.out.println(gc.getActualMaximum(Calendar.DATE));
        System.out.println(gc.getMaximum(Calendar.DATE));
    }
}

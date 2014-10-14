package ca.uwaterloo.iss4e.common.holiday;

/*
 * [Christmas.java]
 *
 * Summary: calculate when Christmas day occurs in Canada and the UK.
 *
 * Copyright: (c) 1999-2014 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.7+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  4.2 2008-12-03 add World AIDS day
 */

import static java.lang.System.out;

/**
 * calculate when Christmas day occurs in Canada and the UK.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class Christmas extends HolInfo {
    /**
     * convert Saturdays to next Monday, Sundays to following Monday
     *
     * @param ordinal days since 1970-01-01.
     * @param shift   ACTUAL = false if you want the actual date of the holiday. SHIFTED = true if you want the date
     *                taken off work,
     * @return adjusted ordinal
     * @noinspection WeakerAccess
     */
    private static int shiftSatToMonSunToMon(int ordinal, boolean shift) {
        if (shift) {
            switch (BigDate.dayOfWeek(ordinal)) {
                case 0:
                    /* shift to Monday */
                    return ordinal + 1;
                case 1:/* monday */
                case 2:/* tuesday */
                case 3:/* wednesday */
                case 4:/* thursday */
                case 5:/* friday */
                default:
                    /* leave as is */
                    return ordinal;
                case 6:/* saturday */
                    /* shift to Monday */
                    return ordinal + 2;
            } // end switch
        } // end if
        else {
            return ordinal;
        }
    } // end shiftSatToMonSunToMon

    /**
     * Test driver
     *
     * @param args not used
     */
    public static void main(String[] args) {
        HolInfo h = new Christmas();
        out.println(h.getName());
        out.println(h.getFirstYear(OBSERVED));
        out.println(h.getFirstYear(PROCLAIMED));
        out.println(h.getRule());
        out.println(h.getAuthority());
        BigDate d = new BigDate(h.when(2007, ACTUAL, OBSERVED));
        out.println(d.getYYYY() + "/" + d.getMM() + "/" + d.getDD());
        d.setOrdinal(h.when(2007, SHIFTED, OBSERVED));
        out.println(d.getYYYY() + "/" + d.getMM() + "/" + d.getDD());
    } // end main

    /**
     * @inheritDoc
     */
    public String getAuthority() {
        return "";
    }

    /**
     * @inheritDoc
     */
    public int getFirstYear(int base) {
        return 1;
    }

    /**
     * @inheritDoc
     */
    public String getName() {
        return "Christmas in UK and Canada";
    }

    /**
     * @inheritDoc
     */
    public String getRule() {
        return "Always on Dec 25.";
    }

    /**
     * Nominally, Christmas and boxing day are on the 25th and 26th in Canada. They are always celebrated on those days
     * no matter what day of the week they fall on. However, if either of those days falls on a Saturday or Sunday, you
     * get a day off work on a following weekday to compensate. If they both fall on a Saturday/Sunday, you get two days
     * off work. The shift feature calculates which compensating days you get off. When was this holiday in a given
     * year?
     *
     * @param year  (-ve means BC, +ve means AD, 0 not permitted.)
     * @param shift true if want date of holiday shifted to nearest weekday.
     * @param base  PROCLAIMED=based on date holiday was officially proclaimed CELEBRATED=based on date holiday was
     *              first celebrated
     * @return ordinal days since 1970-01-01. return NULL_ORDINAL if the holiday was not celebrated in that year.
     */
    public int when(int year, boolean shift, int base) {
        if (!isYearValid(year, base)) {
            return BigDate.NULL_ORDINAL;
        }
        return shiftSatToMonSunToMon(BigDate.toOrdinal(year, 12, 25),
                shift);
    } // end when.
}

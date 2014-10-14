package ca.uwaterloo.iss4e.common.holiday;

/*
 * [BoxingDay.java]
 *
 * Summary: calculate when Boxing day occurs.
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

/**
 * calculate when Boxing day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class BoxingDay extends HolInfo {
    /**
     * Nominally, Christmas and boxing day are on the 25th and 26th in Canada. They are always celebrated on those days
     * no matter what day of the week they fall on. However, if either of those days falls on a Saturday or Sunday, you
     * get a day off work on a following weekday to compensate. If they both fall on a Saturday/Sunday, you get two days
     * off work. The shift feature calculates which compensating days you get off. convert Saturdays to following
     * Monday, Sundays to following Tuesday, Mondays to Tuesday.
     *
     * @param ordinal days since 1970-01-01.
     * @param shift   false if you want the actual date of the holiday. true if you want the date taken off work.
     * @return adjusted ordinal
     */
    private static int boxingDayShift(int ordinal, boolean shift) {
        // This is complicated because preceding Christmas day may be shifted
        // too.
        if (shift) {
            switch (BigDate.dayOfWeek(ordinal)) {
                case 0:
                    /* shift to Tuesday */
                    return ordinal + 2;
                case 1:
                    /* monday */
                    /* shift to Tuesday */
                    return ordinal + 1;
                case 2:
                    /* tuesday */
                case 3:
                    /* wednesday */
                case 4:
                    /* thursday */
                case 5:
                    /* friday */
                default:
                    return ordinal;
                case 6:
                    /* saturday */
                    /* shift to Monday */
                    return ordinal + 2;
            } // end switch
        } // end if
        else {
            return ordinal;
        }
    } // end boxingDayShift

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
        return "Boxing Day in UK and Canada";
    }

    /**
     * @inheritDoc
     */
    public String getRule() {
        return "Day after Christmas.";
    }

    /**
     * @inheritDoc
     */
    public int when(int year, boolean shift, int base) {
        if (!isYearValid(year, base)) {
            return BigDate.NULL_ORDINAL;
        }
        return boxingDayShift(BigDate.toOrdinal(year, 12, 26), shift);
    } // end when.
}

package ca.uwaterloo.iss4e.common.holiday;

/**
 * Created by xiliu on 26/05/14.
 */
/*
 * [HolInfo.java]
 *
 * Summary: Information about a single holiday. base class for various holiday calculators.
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
 *  1.0 1999-09-09 initial version
 */

/**
 * Information about a single holiday. base class for various holiday calculators.
 * <p/>
 * See class com.mindprod.holidays.Christmas for sample implementation of HolInfo.
 * <p/>
 * For rules about how various holidays are
 * computed see: http://www.mnsinc.com/utopia/Calendar/Holiday_Dates/Holiday_Determinations.html To get a list of US
 * Federal Statutory holidays see: http://www.opm.gov/fedhol/1999.htm or http://aa.usno.navy.mil/AA/faq/docs/holidays
 * .html.
 * To get a list of US Federal Observances (not holidays) see: http://www4.law.cornell.edu/uscode/unframed/36/ch9.html
 * http://www.askjeeves.com/ was very helpful in tracking down information about the various holidays. For a list of
 * Canadian Bank holidays see: http://infoservice.gc.ca/canadiana/bochol-99_e.html For a list of Canadian provincial
 * holidays see: http://www.pch.gc.ca/ceremonial-symb/english/day_prv.html For a list of Canadian Federal Holidays see:
 * http://www.pch.gc.ca/ceremonial-symb/english/day.html Calendrical Calculations by Dershowitz and Reingold handles
 * Chinese New Year. http://emr.cs.uiuc.edu/home/reingold/calendar-book/index.shtml For astronomical calculations see:
 * http://www.ccs.neu.edu/home/ramsdell/jdk1.1/lunisolar/lunisolar.html For C Calendar code see:
 * http://www.magnet.ch/serendipity/hermetic/index.html For a large collection of calendar code see:
 * http://www.hiline.net/users/rms/ section 4.2 Cool Stuff in SCDTL For various calendar links see:
 * http://www.calendarzone.com/ For various calendar links see: http://dir.yahoo.com/Reference/Calendars/ For a global
 * list of holidays see: http://holidayfestival.com/
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 1999-09-09 initial version
 * @since 1999
 */
@SuppressWarnings({"ALL"})
public abstract class HolInfo {
    /**
     * base calculations on the actual date the holiday is observed.
     *
     * @noinspection WeakerAccess
     */
    public static final boolean ACTUAL = false;
    /**
     * base calculations on the nearest weekday to the holiday.
     */
    public static final boolean SHIFTED = true;
    /**
     * base calculations on date holiday was first celebrated.
     */
    public static final int OBSERVED = 1;
    /**
     * base calculations on date holiday was first officially proclaimed.
     */
    public static final int PROCLAIMED = 0;
    /**
     * true if debugging. May turn on extra logging information.
     *
     * @noinspection WeakerAccess
     */
    static final boolean DEBUGGING = false;

    /**
     * convert Saturdays to preceding Friday, Sundays to following Monday.
     *
     * @param ordinal days since 1970-01-01.
     * @param shift   ACTUAL = false if you want the actual date of the holiday. SHIFTED = true if you want the date
     *                taken off work, usually the nearest weekday.
     * @return adjusted ordinal
     * @noinspection WeakerAccess
     */
    static int shiftSatToFriSunToMon(int ordinal, boolean shift) {
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
                    /* shift to Friday */
                    return ordinal - 1;
            } // end switch
        } // end if
        else {
            return ordinal;
        }
    } // end shiftSatToFriSunToMon

    /**
     * authority who provided the information about the holiday.
     *
     * @return name of person, email address, website etc. that describes the rules about the holiday. "" if no one in
     * particular.
     */
    public abstract String getAuthority();

    /**
     * Get year holiday first proclaimed or first celebrated.
     *
     * @param base PROCLAIMED=based on date holiday was officially proclaimed. CELEBRATED=based on date holiday was
     *             first celebrated.
     * @return year first proclaimed, or first celebrated.
     */
    public abstract int getFirstYear(int base);

    /**
     * Get name of holiday e.g. "Christmas"
     *
     * @return English language name of the holiday.
     */
    public abstract String getName();

    /**
     * Get rule in English for how the holiday is calculated. e.g. "Always on Dec 25." or "Third Monday in March." may
     * contain embedded \n characters.
     *
     * @return rule for how holiday is computed.
     */
    public abstract String getRule();

    /**
     * When was this holiday in a given year?, based on PROCLAIMED date.
     *
     * @param year must be 1583 or later.
     * @return ordinal days since 1970-01-01.
     */
    final public int when(int year) {
        return when(year, false, PROCLAIMED);
    }

    /**
     * When was this holiday in a given year?, based on PROCLAIMED date.
     *
     * @param year  must be 1583 or later.
     * @param shift ACTUAL = false if you want the actual date of the holiday. SHIFTED = true if you want the date taken
     *              off work, usually the nearest weekday.
     * @return ordinal days since 1970-01-01, suitabl for feeding to BigDate.
     */
    final public int when(int year, boolean shift) {
        return when(year, shift, PROCLAIMED);
    }

    /**
     * When was this holiday in a given year?
     *
     * @param year  (-ve means BC, +ve means AD, 0 not permitted.)
     * @param shift ACTUAL = false if you want the actual date of the holiday. SHIFTED = true if you want the date taken
     *              off work, usually the nearest weekday.
     * @param base  PROCLAIMED=based on date holiday was officially proclaimed CELEBRATED=based on date holiday was
     *              first celebrated
     * @return ordinal days since 1970-01-01. return NULL_ORDINAL if the holiday was not celebrated in that year.
     * @noinspection WeakerAccess
     */
    public abstract int when(int year, boolean shift, int base);

    /**
     * Is year valid for this holiday?
     *
     * @param year The year you want to test.
     * @param base PROCLAIMED=based on date holiday was officially proclaimed . CELEBRATED=based on date holiday was
     *             first celebrated.
     * @return true if the holiday was celebrated/proclained by that year.
     * @noinspection WeakerAccess
     */
    final protected boolean isYearValid(int year, int base) {
        return (year != 0) && (year >= getFirstYear(base));
    } // isYearValid
}

package ca.uwaterloo.iss4e.common.holiday;

/**
 * Created by xiliu on 26/05/14.
 */
/*
 * [IsHoliday.java]
 *
 * Summary: Find out if there is any holiday/celebration on a given date.
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

import ca.uwaterloo.iss4e.common.Utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.out;
/**
 * Find out if there is any holiday/celebration on a given date.
 * <p/>
 * Lets you easily define a set of holidays, then rapidly determine if any given date is a holiday. It is quite easy to
 * specify which days you want considered holidays using methods like addHoliday where you specify the date or the name
 * of the Holiday, addAmericanFederalHolidays and addWeekDaysAsHolidays.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class IsHoliday
{
    /**
     * true if debugging
     *
     * @noinspection WeakerAccess
     */
    static final boolean DEBUGGING = true;
    /**
     * ordinal of Jan 1 of first year to compute holidays.
     *
     * @noinspection WeakerAccess
     */
    protected int firstOrd;
    /**
     * First year in range to compute holidays.
     *
     * @noinspection WeakerAccess
     */
    protected int firstYear;
    /**
     * ordinal of Dec 31 of last year to compute holidays.
     *
     * @noinspection WeakerAccess
     */
    protected int lastOrd;
    /**
     * Last year in range to compute holidays.
     *
     * @noinspection WeakerAccess
     */
    protected int lastYear;
    /**
     * corresponding bit is true means day is a holiday.
     *
     * @noinspection WeakerAccess
     */
    protected java.util.BitSet holidayBits;
    /**
     * Constructor to define range of years over which we wish to compute holidays.
     *
     * @param firstYear first year to cover, must be > 0
     * @param lastYear  last year to cover
     *
     * @noinspection SameParameterValue, WeakerAccess, SameParameterValue, WeakerAccess
     */
    public IsHoliday( int firstYear, int lastYear )
    {
        // avoid problem of non-existent year 0, only support AD
        if ( firstYear < 1 )
        {
            throw new IllegalArgumentException( "firstYear="
                    + firstYear
                    + " must be > 0." );
        }
        if ( lastYear > BigDate.MAX_YEAR )
        {
            throw new IllegalArgumentException( "lastYear="
                    + lastYear
                    + " must be <= "
                    + BigDate
                    .MAX_YEAR
                    + "." );
        }
        if ( lastYear < firstYear )
        {
            throw new IllegalArgumentException( "firstYear="
                    + firstYear
                    + " must be <= lastYear="
                    + lastYear
                    + "." );
        }
        this.firstYear = firstYear;
        this.lastYear = lastYear;
        firstOrd = BigDate.toOrdinal( firstYear, 1, 1 );
        lastOrd = BigDate.toOrdinal( lastYear, 12, 31 );
        // starts off all zeros, nothing is declared a holiday yet
        holidayBits = new java.util.BitSet( lastOrd - firstOrd + 1 );
    } // end constructor




    public void updateWeekDate() {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;


        String url = "jdbc:postgresql://localhost/irish";
        String user = "xiliu";
        String password = "Abcd1234";

        try {
            IsHoliday h = new IsHoliday( 2008, 2012 );
            //h.addWeekendsAsHolidays();
            h.addCanadianFederalHolidays(HolInfo.SHIFTED);


            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
           // PreparedStatement pstmt = con.prepareStatement("UPDATE smas_water_dailyreadingbytype set holidayflag=? WHERE readdate=?");
            //rs = st.executeQuery("select distinct readdate from smas_water_dailyreadingbytype order by readdate");
            rs = st.executeQuery("select date from smas_date order by 1 asc");
            PreparedStatement pstmt = con.prepareStatement("UPDATE smas_date set canadianholiday=? WHERE date=?");

            while (rs.next()) {
               String dateStr = rs.getString(1);
               boolean isHoliday =  h.isHoliday( BigDate.toOrdinal( dateStr));
                System.out.println(dateStr + "; holiday=" + isHoliday);
                pstmt.setInt(1, isHoliday?1:0);
                pstmt.setDate(2, Utils.toSqlDate(dateStr, "yyyy-MM-dd"));
                pstmt.execute();
            }
            pstmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();

                }
                if (con != null) {

                    con.close();
               }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
















    public static void main( String[] args )
    {
        if ( DEBUGGING )
        {
            // prepare list of holidays
            IsHoliday h = new IsHoliday( 1990, 2014 );
            //h.addWeekendsAsHolidays();
            h.addCanadianFederalHolidays(HolInfo.SHIFTED);
            // add nearest weekday to the actual holiday, shift=true
            /*h.addAmericanFederalHolidays( HolInfo.SHIFTED );
            for ( int year = 1990; year <= 2010; year++ )
            {
                h.addHoliday( new GroundhogDay().when( year,
                        HolInfo.SHIFTED,
                        HolInfo.PROCLAIMED ) );
                h.addHoliday( new GeneralElectionDay().when( year,
                        HolInfo.SHIFTED,
                        HolInfo.PROCLAIMED ) );
            }*/
            // declare New Year's eve for the millennium a holiday
          //  h.addHoliday( BigDate.toOrdinal( 1999, 12, 31 ) );
            // test if any given day is a holiday


            h.updateWeekDate();



            out.println( "Today "
                    + ( h.isHoliday( BigDate.localToday() )
                    ? "is"
                    : "is not" )
                    + " a holiday." );
            out.println( "2005-12-25 "
                    + ( h.isHoliday( BigDate.toOrdinal( 2005,
                    12,
                    25 ) )
                    ? "is"
                    : "is not" )
                    + " a holiday." );
            // count business days
            int startOrd = BigDate.toOrdinal( 1999, 1, 1 );
            int endOrd = BigDate.toOrdinal( 1999, 12, 31 );
            int count = 0;
            for ( int i = startOrd; i <= endOrd; i++ )
            {
                if ( !h.isHoliday( i ) )
                {
                    count++;
                }
            }
            out.println( "There were "
                    + count
                    + " business days in 1999" );
        } // end if
    } // end main

    /**
     * add all Canadian Federal holidays To get a list of US Federal Statutory holidays see:
     * http://www.opm.gov/fedhol/1999.htm or http://aa.usno.navy.mil/AA/faq/docs/holidays.html.
     *
     * @param shift shift ACTUAL = false if you want the actual date of the holiday. SHIFTED = true if you want the date
     *              taken off work, usually the nearest weekday.
     */
    public void addCanadianFederalHolidays( boolean shift )
    {
        for ( int year = firstYear; year <= lastYear; year++ )
        {
            /* want shifted days that you take the day off if falls on a weekend */
            addHoliday( new NewYearsDay()
                    .when( year, shift, HolInfo.PROCLAIMED ) );
            addHoliday( new GoodFriday().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new EasterMonday().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new VictoriaDay().when( year, shift ) );
            addHoliday( new CanadaDay().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new LabourDay().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new CanadianThanksgiving().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new RemembranceDay().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new Christmas().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
            addHoliday( new BoxingDay().when( year,
                    shift,
                    HolInfo.PROCLAIMED ) );
        } // end for
    }/* end addCanadianFederalHolidays */
    /**
     * add another holiday to the holiday table.
     *
     * @param ordinal day to add as a holiday measured in days since 1970-01-01. Note you must add it once for each
     *                year in the range. We do not presume it falls the same day each year.
     *
     * @noinspection WeakerAccess
     */
    public void addHoliday( int ordinal )
    {
        if ( ordinal < firstOrd || ordinal > lastOrd )
        {
            // just ignore out of range or NULL_ORDINAL.
            // It is possible for shifted dates to wander a tad out of range.
            return;
        }
        holidayBits.set( ordinal - firstOrd );
    } // end addHoliday
    /**
     * add all Saturdays and Sundays in the year range as holidays
     *
     * @noinspection WeakerAccess
     */
    public void addWeekendsAsHolidays()
    {
        int ordFirstSunday = BigDate.ordinalOfnthXXXDay( 1/* first */, BigDate.SUNDAY, firstYear, BigDate.JAN );
        for ( int i = ordFirstSunday; i <= lastOrd; i += 7 )
        {
            addHoliday( i );
        } // end for
        int ordFirstSaturday = BigDate.ordinalOfnthXXXDay( 1/* first */, BigDate.SATURDAY, firstYear, BigDate.JAN );
        for ( int i = ordFirstSaturday; i <= lastOrd; i += 7 )
        {
            addHoliday( i );
        } // end for
    }/* end addWeekendsAsHolidays */
    /**
     * get first year in range to compute the holidays.
     *
     * @return year yyyy.
     */
    public int getFirstYear()
    {
        return firstYear;
    }
    /**
     * Get last year in range to compute holidays.
     *
     * @return year YYYY.
     */
    public int getLastYear()
    {
        return lastYear;
    }
    /**
     * Is the given day a holiday? What constitutes a holiday is determined by the setHoliday calls
     *
     * @param bigDate date to test
     *
     * @return true if that day is a holiday
     * @noinspection WeakerAccess
     */
    public boolean isHoliday( BigDate bigDate )
    {
        return isHoliday( bigDate.getOrdinal() );
    } // end isHoliday
    /**
     * Is the given day a holiday? What constitutes a holiday is determined by the setHoliday calls
     *
     * @param ordinal days since 1970-01-01
     *
     * @return true if that day is a holiday.
     * @noinspection WeakerAccess
     */
    public boolean isHoliday( int ordinal )
    {
        if ( ordinal < firstOrd || ordinal > lastOrd )
        {
            throw new IllegalArgumentException( "out of range ordinal date: "
                    + ordinal );
        }
        return holidayBits.get( ordinal - firstOrd );
    } // end isHoliday
}

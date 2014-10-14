package ca.uwaterloo.iss4e.common.holiday;

/*
 * [EasterSunday.java]
 *
 * Summary: calculate when Easter Sunday occurs.
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
 * calculate when Easter Sunday occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class EasterSunday extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "Felix Gursky\u2019s interpretation of:\n"
                + " 1. Christophorus Clavius: Calendarium Gregorianum Perpetuum.\n"
                + "    Cum Summi Pontificis Et Aliorum Principum. Romae, Ex Officina\n"
                + "    Dominicae Basae, MDLXXXII, Cum Licentia Superiorum.\n"
                + " 2. Christophorus Clavius: Romani Calendarii A Gregorio XIII.\n"
                + "    Pontifice Maximo Restituti Explicatio. Romae, MDCIII.\n"
                + "We are using the Roman Catholic calendar not the Anglican.\n"
                + "You could use the British Calendar act which gives the same modern dates.\n";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        return 30;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Easter Sunday";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "The first Sunday after the first ecclesiastical full moon after the vernal equinox.";
    }
    /**
     * @inheritDoc
     */
    public int when( int year, boolean shift, int base )
    {
        if ( !isYearValid( year, base ) )
        {
            return BigDate.NULL_ORDINAL;
        }
        // we are using the Catholic church's date for adoption of the
        // Gregorican calendar. Britain, Canada and the USA did not adopt much
        // later in 1752.
        if ( year <= 1582 )
        {
            // use the old Julian method described in
            // Oudin (1940) as quoted in
            // Explanatory Supplement to the Astronomical Almanac, P. Kenneth
            // Seidelmann, editor.
            // picked off the web at
            // http://www.pip.dknet.dk/~pip10160/cal/node3.html
            // calc g golden number - 1;
            int g = year % 19;
            // calc i the number of days from 21 March to the Paschal full moon
            int i = ( ( 19 * g ) + 15 ) % 30;
            // calc j the weekday for the Paschal full moon (0=Sunday, 1=Monday)
            int j = ( year + year / 4 + i ) % 7;
            // calc l the number of days from 21 March to the Sunday
            // on or before the Paschal full moon (a number between -6 and 28)
            int l = i - j;
            // calc Easter month
            int mm = 3 + ( l + 40 ) / 44;
            // calc Easter day of month.
            int dd = l + 28 - 31 * ( mm / 4 );
            int ord = BigDate.toOrdinal( year, mm, dd );
            return shiftSatToFriSunToMon( ord, shift );
        }
        else if ( year >= 1583 )
        {
            // more elaborate Gregorian method.
            int yearIn19YearCycle = ( year % 19 ) + 1;
            int century = year / 100 + 1;
            int x = ( 3 * century ) / 4 - 12;
            int z = ( 8 * century + 5 ) / 25 - 5;
            int d = 5 * year / 4 - x - 10;
            int e = ( 11 * yearIn19YearCycle + 20 + z - x ) % 30;
            if ( ( ( e == 25 ) && ( yearIn19YearCycle > 11 ) ) || ( e == 24 ) )
            {
                e++;
            }
            int n = 44 - e;
            if ( n < 21 )
            {
                n += 30;
            }
            n += 7 - ( ( d + n ) % 7 );
            int ord;
            if ( n <= 31 )
            {
                ord = BigDate.toOrdinal( year, 3, n );
            }
            else
            {
                ord = BigDate.toOrdinal( year, 4, n - 31 );
            }
            return shiftSatToFriSunToMon( ord, shift );
        }
        return BigDate.NULL_ORDINAL;
    } // end when.
}

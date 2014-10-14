package ca.uwaterloo.iss4e.common.holiday;

/*
 * [CanadaDay.java]
 *
 * Summary: calculate when Canada day occurs.
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
 * calculate when Canada day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class CanadaDay extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "Canada Holidays Act";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        return 1867;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Canada Day";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "July 1, or July 2 when July 1 is a Sunday.";
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
        BigDate d = new BigDate( year, 7, 1 );
        int ord = d.getOrdinal();
        if ( d.getDayOfWeek() == 0 )
        {
            ord++;/* adjust to Monday */
        }
        return shiftSatToFriSunToMon( ord, shift );
    } // end when.
}

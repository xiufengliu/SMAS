package ca.uwaterloo.iss4e.common.holiday;

/*
 * [RemembranceDay.java]
 *
 * Summary: calculate when Remembrance day occurs.
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
 * calculate when Remembrance day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class RemembranceDay extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "Canadian Encyclopedia";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        switch ( base )
        {
            default:
            case OBSERVED:
                return 1918;
            case PROCLAIMED:
                return 1931;
        }
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Remembrance Day";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "Always November 11. From 1923 to 1931 it was called Armistice Day\n"
                + "and merged with Canadian Thanksgiving.\n"
                + "In 1931, it was moved to November 11, and renamed.";
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
        // 1931 actually had two celebrations, we return the Nov 11 one.
        if ( 1923 <= year && year <= 1930 )
        {
            return BigDate.ordinalOfnthXXXDay( 2, BigDate.MONDAY, year, BigDate.NOV );
        }
        return shiftSatToFriSunToMon( BigDate.toOrdinal( year, BigDate.NOV, 11 ),
                shift );
    } // end when.
}

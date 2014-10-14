package ca.uwaterloo.iss4e.common.holiday;

/*
 * [NewYearsDay.java]
 *
 * Summary: calculate when New Years day occurs.
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
 * calculate when New Years day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class NewYearsDay extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        return -153;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "New Year\u2019s Day";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "Always on January 1";
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
        return shiftSatToFriSunToMon( BigDate.toOrdinal( year, 1, 1 ), shift );
    } // end when.
}

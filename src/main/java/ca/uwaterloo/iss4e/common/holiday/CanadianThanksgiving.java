package ca.uwaterloo.iss4e.common.holiday;

/*
 * [CanadianThanksgiving.java]
 *
 * Summary: calculate when Canadian Thanksgiving day occurs.
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
 * calculate when Canadian Thanksgiving day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class CanadianThanksgiving extends HolInfo
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
        return 1700;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Canadian Thanksgiving";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "Second Monday in October";
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
        return BigDate.ordinalOfnthXXXDay( 2/* second */, BigDate.MONDAY, year, BigDate.OCTOBER );
    } // end when.
}

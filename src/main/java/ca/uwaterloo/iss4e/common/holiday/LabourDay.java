package ca.uwaterloo.iss4e.common.holiday;
/*
 * [LabourDay.java]
 *
 * Summary: calculate when Canadian Labour day occurs.
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
 * calculate when Canadian Labour day occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class LabourDay extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "http://www.dol.gov/opa/aboutdol/laborday.htm";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        return 1884;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Canadian Labour Day";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "First Monday in September.";
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
        return BigDate.ordinalOfnthXXXDay( 1, BigDate.MONDAY, year, BigDate.SEP );
    } // end when.
}

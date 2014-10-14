package ca.uwaterloo.iss4e.common.holiday;

/*
 * [VictoriaDay.java]
 *
 * Summary: Calculate when Victoria day occurs.
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
 * Calculate when Victoria day occurs.
 * <p/>
 * Specific holiday calculator Info from http://www.pch.gc.ca/ceremonial-symb/english/day_vic.html May 24, Queen
 * Victoria's birthday, was declared a holiday by the Legislature of the Province of Canada in 1845. After
 * Confederation, the Queen's birthday was celebrated every year on May 24 unless that date was a Sunday, in which case
 * a proclamation was issued providing for the celebration on May 25. After the death of Queen Victoria in 1901, an Act
 * was passed by the Parliament of Canada establishing a legal holiday on May 24 in each year (or May 25 if May 24 fell
 * on a Sunday) under the name Victoria Day. Canada continued to observe Victoria Day. An amendment to the Statutes of
 * Canada in 1952 established the celebration of Victoria Day on the Monday preceding May 25.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class VictoriaDay extends HolInfo
{
    /**
     * @inheritDoc
     */
    public String getAuthority()
    {
        return "http://www.pch.gc.ca/ceremonial-symb/english/day_vic.html";
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
                return 1837;
            case PROCLAIMED:
                return 1845;
        }
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Victoria Day";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "The Sovereign\u2019s birthday has been celebrated in Canada\n"
                + "since the reign of Queen Victoria (1837-1901).\n"
                + "May 24, Queen Victoria\u2019s birthday, was declared a holiday\n"
                + "by the Legislature of the Province of Canada in 1845.\n"
                + "After Confederation, the Queen\u2019s birthday was celebrated\n"
                + "every year on May 24 unless that date was a Sunday,\n"
                + "in which case a proclamation was issued providing\n"
                + "for the celebration on May 25.\n"
                + "After the death of Queen Victoria in 1901,\n"
                + "an Act was passed by the Parliament of Canada\n"
                + "establishing a legal holiday on May 24 in each year\n"
                + "(or May 25 if May 24 fell on a Sunday) under the name Victoria Day.\n"
                + "In 1952, the rule was changed to the Monday preceding May 25.";
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
        if ( year >= 1952 )
        {
            int ord = BigDate.toOrdinal( year, 5, 25 );
            switch ( BigDate.dayOfWeek( ord ) )
            {
                // Monday preceding May 25
                case 0:
                    ord += -6;
                    break;
                case 1:/* Monday */
                    ord += -7;
                    break;
                case 2:/* Tuesday */
                    ord += -1;
                    break;
                case 3:/* Wednesday */
                    ord += -2;
                    break;
                case 4:/* Thursday */
                    ord += -3;
                    break;
                case 5:/* Friday */
                    ord += -4;
                    break;
                case 6:/* Saturday */
                    ord += -5;
                    break;
            }
            return ord;
        }
        else
        {// old rule
            int ord = BigDate.toOrdinal( year, 5, 24 );
            if ( BigDate.dayOfWeek( ord ) == 0/* sunday */ )
            {
                ord++;
            }
            return ord;
        }
    } // end when.
}

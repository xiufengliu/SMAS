package ca.uwaterloo.iss4e.common.holiday;

/*
 * [EasterMonday.java]
 *
 * Summary: calculate when Easter Monday occurs.
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
 * calculate when Easter Monday occurs.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.2 2008-12-03 add World AIDS day
 * @since 1999
 */
public final class EasterMonday extends HolInfo
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
                + "    Pontifice Maximo Restituti Explicatio. Romae, MDCIII.;\n";
    }
    /**
     * @inheritDoc
     */
    public int getFirstYear( int base )
    {
        return 1583;
    }
    /**
     * @inheritDoc
     */
    public String getName()
    {
        return "Easter Monday";
    }
    /**
     * @inheritDoc
     */
    public String getRule()
    {
        return "Monday after Easter.";
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
        return new EasterSunday().when( year, false ) + 1;
    } // end when.
}

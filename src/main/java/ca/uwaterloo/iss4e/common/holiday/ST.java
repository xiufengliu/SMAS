package ca.uwaterloo.iss4e.common.holiday;

/**
 * Created by xiliu on 26/05/14.
 */
/*
 * [ST.java]
 *
 * Summary: Miscellaneous static methods for dealing with Strings in JDK 1.1+.
 *
 * Copyright: (c) 1997-2014 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.7+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.5 2005-07-14 split off from Misc, allow for compilation with old compiler.
 *  1.6 2006-01-01
 *  1.7 2006-03-04 format with IntelliJ and prepare Javadoc
 *  1.8 2006-10-15 add condense method.
 *  1.9 2008-03-10 add ST.firstWord
 *  2.0 2008-04-01
 *  2.1 2008-12-16 new methods in BigDate
 *  2.2 2009-04-19 add countLeading, countTrailing, TrimLeading, TrimTrailing that take multiple trim chars.
 *  2.3 2009-04-29 add countInstances( String page, char lookFor )
 *  2.4 2009-04-30 fix but in countLeading ( String text, String leads ).
 *                 add pruneExcessBlankLines
 *  2.5 2009-11-14 add haveCommonChar, isLetter, isDigit
 *  2.6 2010-02-11 removed removeHead (redundant - same as chopLeadingString).
 *                 renamed chopLeading to chopLeadingString to clarify does not trim a variety of chars.
 *  2.7 2010-12-09 BigDate parsing now more relaxed
 *  2.8 2011-11-13 add ST.nullSafeStringCompare and ST.nullSafeStringCompareIgnoreCase.
 *  2.9 2012-11-06 fix bug in nullSafeStringCompare. It was ignoring case.
 *  3.0 2014-04-29 add quoteForReplace to quote Regex replacement Strings,
 *                 stripNaughtyCharacters, reorderLetters, deDupLetters
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import static java.lang.System.out;

/**
 * Miscellaneous static methods for dealing with Strings in JDK 1.1+.
 * <p/>
 * Augmented by com.mindprod.common17.ST for JDK 1.5+.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.0 2014-04-29 add quoteForReplace to quote Regex replacement Strings,
 *          stripNaughtyCharacters, reorderLetters, deDupLetters
 * @noinspection WeakerAccess
 * @since 2003-05-15
 */
public class ST {
    // declarations
    /**
     * true if you want extra debugging output and test code
     */
    private static final boolean DEBUGGING = false;
    /**
     * used to efficiently generate Strings of spaces of varying length
     */
    private static final String SOMESPACES = "                                                                      ";
    /**
     * track which chars in range 0..017F are vowels. Lookup table takes only 48 bytes.
     */
    private static final BitSet vt = new BitSet(0x0180);

    // end declarations
    static {
        // initialize vt Vowel Table
        vt.set('A');
        vt.set('E');
        vt.set('I');
        vt.set('O');
        vt.set('U');
        vt.set('a');
        vt.set('e');
        vt.set('i');
        vt.set('o');
        vt.set('u');
        vt.set('\u00c0', '\u00c6');
        vt.set('\u00c8', '\u00cf');
        vt.set('\u00d2', '\u00d6');
        vt.set('\u00d8', '\u00dc');
        vt.set('\u00e0', '\u00e6');
        vt.set('\u00e8', '\u00ef');
        vt.set('\u00f2', '\u00f6');
        vt.set('\u00f8', '\u00fc');
        vt.set('\u0100', '\u0105');
        vt.set('\u0112', '\u011b');
        vt.set('\u0128', '\u012f');
        vt.set('\u0130');
        vt.set('\u0132', '\u0133');
        vt.set('\u014c', '\u014f');
        vt.set('\u0150', '\u0153');
        vt.set('\u0168', '\u016f');
        vt.set('\u0170', '\u0173');
    }

    /**
     * Dummy constructor
     * ST contains only static methods.
     */
    protected ST() {
    }
    // methods

    /**
     * makeshift system beep if awt.Toolkit.beep is not available. Works also in JDK 1.02.
     */
    public static void beep() {
        out.print("\007");
        out.flush();
    }// end method

    /**
     * Convert String to canonical standard form. null -> "". Trims lead trail blanks.  Never null.
     *
     * @param s String to be converted.
     * @return String in canonical form.
     */
    public static String canonical(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim();
        }
    }// end method

    /**
     * remove leading string if present
     *
     * @param text   text with possible leading string, possibly empty or null.
     * @param toChop the leading string of interest. Not a list of possible chars to chop, order matters.
     * @return string with to toChop string removed if the text starts with it,
     * otherwise the original string unmodified.
     * @see #trimLeading(String, String)
     * @see #chopTrailingString(String, String)
     */
    public static String chopLeadingString(String text, String toChop) {
        if (text != null && text.startsWith(toChop)) {
            return text.substring(toChop.length());
        } else {
            return text;
        }
    }// end method

    /**
     * remove trailing string if present
     *
     * @param text   text with possible trailing string, possibly empty, but not null.
     * @param toChop the trailing string of interest.  Not a list of possible chars to chop, order matters.
     * @return string with to toChop string removed if the text ends with it, otherwise the original string unmodified.
     * @see #trimTrailing(String, String)
     * @see #chopLeadingString(String, String)
     */
    public static String chopTrailingString(String text, String toChop) {
        if (text != null && text.endsWith(toChop)) {
            return text.substring(0, text.length() - toChop.length());
        } else {
            return text;
        }
    }// end method

    /**
     * Collapse multiple whitespace in string down to a single space. Remove lead and trailing whitespace.
     * Earlier version collapsed only spaces, not whitespace
     *
     * @param s String to condense whitespace.
     * @return String with all whitespace condensed and lead/trail whitespace removed.
     * @noinspection WeakerAccess, SameParameterValue
     * @see #squish(String)
     * @see com.mindprod.common17.ST#condense(String)
     */
    public static String condense(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        final int len = s.length();
        if (len == 0) {
            return s;
        }
        StringBuilder b = new StringBuilder(len);
        boolean suppressSpaces = false;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                //noinspection StatementWithEmptyBody
                if (suppressSpaces) {
                    // subsequent space
                } else {
                    // first space
                    b.append(' ');
                    suppressSpaces = true;
                }
            } else {
                // was not a space
                b.append(c);
                suppressSpaces = false;
            }
        } // end for
        return b.toString();
    }// end method

    /**
     * Does a give string contain a given character
     * Ideally would be an instance method of String
     * to match String.contains( CharSequence )
     *
     * @param s the string
     * @param c the character
     * @return true if string s contains character c
     * @see java.lang.String#contains(CharSequence)
     */
    public static boolean contains(String s, char c) {
        return s.indexOf(c) >= 0;
    }// end method

    /**
     * Returns true if strings a and b have one or more characters in common, not necessarily at the same offset.
     * If you think of a and b as sets of chars,  returns true if the intersection of those sets in not null.
     * It can also be thought of as like an indexOf that scans for multiple characters at once.
     *
     * @param a first string
     * @param b second string
     * @return true if the strings have one or more characters in common.
     */
    public static boolean containsAnyOf(String a, String b) {
        for (int i = 0; i < b.length(); i++) {
            if (a.indexOf(b.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }// end method

    /**
     * Count how many times a String occurs on a page.
     *
     * @param page    big String to look in.
     * @param lookFor small String to look for and count instances.
     * @return number of times the String appears non-overlapping.
     */
    public static int countInstances(String page, String lookFor) {
        int count = 0;
        for (int start = 0;
             (start = page.indexOf(lookFor, start)) >= 0;
             start += lookFor.length()) {
            count++;
        }
        return count;
    }// end method

    /**
     * Count how many times a char occurs  in a String.
     *
     * @param page    big String to look in.
     * @param lookFor char to lookfor count instances.
     * @return number of times the char appears.
     */
    public static int countInstances(String page, char lookFor) {
        int count = 0;
        for (int i = 0; i < page.length(); i++) {
            if (page.charAt(i) == lookFor) {
                count++;
            }
        }
        return count;
    }// end method

    /**
     * count of how many leading characters there are on a string matching a given character. It does not remove them.
     *
     * @param text text with possible leading characters, possibly empty, but not null.
     * @param c    the leading character of interest, usually ' ' or '\n'
     * @return count of leading matching characters, possibly 0.
     * @noinspection WeakerAccess
     */
    public static int countLeading(String text, char c) {
        int count;
        //noinspection StatementWithEmptyBody
        for (count = 0; count < text.length() && text.charAt(count) == c; count++) {
            // empty loop
        }
        return count;
    }// end method

    /**
     * count of how many leading characters there are on a string matching a given character. It does not remove them.
     *
     * @param text          text with possible leading characters, possibly empty, but not null.
     * @param possibleChars the leading characters of interest, usually ' ' or '\n'
     * @return count of leading matching characters, possibly 0.
     * @noinspection WeakerAccess
     */
    public static int countLeading(String text, String possibleChars) {
        int count;
        //noinspection StatementWithEmptyBody
        for (count = 0; count < text.length() && possibleChars.indexOf(text.charAt(count)) >= 0; count++) {
            // empty loop.
        }
        return count;
    }// end method

    /**
     * count of how many trailing characters there are on a string matching a given character. It does not remove them.
     *
     * @param text text with possible trailing characters, possibly empty, but not null.
     * @param c    the trailing character of interest, usually ' ' or '\n'
     * @return count of trailing matching characters, possibly 0.
     * @noinspection WeakerAccess
     */
    public static int countTrailing(String text, char c) {
        int length = text.length();
        // need defined outside the for loop.
        int count;
        //noinspection StatementWithEmptyBody
        for (count = 0; count < length && text.charAt(length - 1 - count) == c; count++) {
            // empty loop
        }
        return count;
    }// end method

    /**
     * count of how many trailing characters there are on a string matching a given character. It does not remove them.
     *
     * @param text          text with possible trailing characters, possibly empty, but not null.
     * @param possibleChars the trailing characters of interest, usually ' ' or '\n'
     * @return count of trailing matching characters, possibly 0.
     * @noinspection WeakerAccess
     */
    public static int countTrailing(String text, String possibleChars) {
        int length = text.length();
        // need defined outside the for loop.
        int count;
        //noinspection StatementWithEmptyBody
        for (count = 0; count < length && possibleChars.indexOf(text.charAt(length - 1 - count)) >= 0; count++) {
            // empty loop
        }
        return count;
    }// end method

    /**
     * reorder the letters in a String in ascending order, case-sensitively, and then remove dups.
     * e.g. aCBBB" --> "BCa", "DAB" --> "ABD", " "Z X" --> "XZ", "   " --> ""
     *
     * @param field field to sort.
     * @return field with letters composing it sorted in ascending order. Blanks squeezed out, but not dups.
     * @see #reorderLetters(String)
     */
    public static String deDupLetters(String field) {
        final String squished = ST.squish(field);
        if (squished.length() <= 1) {
            return field;
        } else {
            final char[] ca = squished.toCharArray();
            Arrays.sort(ca);  // case-sensitive sort letter by letter
            // most of the time, it will already be deDuped, check just in case.
            int dups = 0;
            for (int i = 1; i < ca.length; i++) {
                if (ca[i] == ca[i - 1]) {
                    dups++;
                }
            }
            if (dups == 0) {
                // no dups found, we are done.
                return String.valueOf(ca);
            }
            // we have to tediously dedup but at least we know the precise size of the final result ahead of time.
            final char[] deDuped = new char[ca.length - dups];
            if (ca.length > 0) {
                // always copy first char if there is one.
                deDuped[0] = ca[0];
                int j = 1;
                for (int i = 1; i < ca.length; i++) {
                    if (ca[i] != ca[i - 1]) {
                        deDuped[j++] = ca[i];
                    }
                }
            }
            return String.valueOf(deDuped);
        }
    }// end method

    /**
     * debugging dump of a string with hex
     *
     * @param s string to dump
     */
    public static void dumpString(String s) {
        final int lineLength = 40;
        final int lines = (s.length() + lineLength + 1) / lineLength;
        int start = 0;
        int end = lineLength;
        for (int line = 0; line < lines; line++) {
            if (end > s.length()) {
                end = s.length();
            }
            final String text = s.substring(start, end);
            start += lineLength;
            end += lineLength;
            for (int i = 0; i < text.length(); i++) {
                out.print("    ");
                final char c = text.charAt(i);
                if (' ' <= c && c <= '~' || '\u00a1' <= c && c <= '\u024f') {
                    out.print(c);
                } else {
                    out.print('.');  // probably unprintable. Don't risk breaking the alignment.
                }
            }
            out.println();
            for (int i = 0; i < text.length(); i++) {
                final int c = text.charAt(i);
                out.print(' ');
                out.print(toLZHexString(c, 4));
            }
            out.println();
        }
    }// end method

    /**
     * gets the first word of a String, delimited by space or the end of the string. \n will not delimit a word.
     * If there are no blanks in the string, the result is the entire string.
     *
     * @param s the input String
     * @return the first word of the String.
     * @see #lastWord(String)
     */
    public static String firstWord(String s) {
        s = s.trim();
        final int place = s.indexOf(' ');
        return (place < 0) ? s : s.substring(0, place);
    }// end method

    /**
     * find the first instance of whitespace (space, \n, \r, \t in a string.
     *
     * @param s string to scan
     * @return -1 if not found, offset relative to start of string where found
     */
    public static int indexOfWhiteSpace(String s) {
        return indexOfWhiteSpace(s, 0);
    }// end method

    /**
     * find the first instance of whitespace (space, \n, \r, \t in a string.
     *
     * @param s           string to scan
     * @param startOffset where in string to start looking
     * @return -1 if not found, offset relative to start of string where found, not relative to startOffset.
     */
    public static int indexOfWhiteSpace(String s, int startOffset) {
        final int length = s.length();
        for (int i = startOffset; i < length; i++) {
            switch (s.charAt(i)) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    return i;
                default:
                    // keep looking
            }
        } // end for
        return -1;
    }// end method

    /**
     * Check if char is plain ASCII digit.
     *
     * @param c char to check.
     * @return true if char is in range 0-9
     * @see Character#isLetter(char)
     * @see #isUnsignedNumeric(String)
     */
    public static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }// end method

    /**
     * Is this string empty? In Java 1.6 + isEmpty is build in. Sun's version being an instance method cannot test for
     * null.
     *
     * @param s String to be tested for emptiness.
     * @return true if the string is null or equal to the "" null string. or just blanks
     * @see Misc#nullToEmpty(String)
     * @see String#isEmpty()
     */
    public static boolean isEmpty(String s) {
        return (s == null) || s.trim().length() == 0;
    }// end method

    /**
     * Ensure the string contains only legal characters.
     *
     * @param candidate  string to test.
     * @param legalChars characters than are legal for candidate.
     * @return true if candidate is formed only of chars from the legal set.
     */
    public static boolean isLegal(String candidate, String legalChars) {
        for (int i = 0; i < candidate.length(); i++) {
            if (legalChars.indexOf(candidate.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }// end method

    /**
     * Ensure the char is only one a set of legal characters.
     *
     * @param candidate  char to test.
     * @param legalChars characters than are legal for candidate.
     * @return true if candidate is one of the legallegal set.
     */
    public static boolean isLegal(char candidate, String legalChars) {
        return legalChars.indexOf(candidate) >= 0;
    }// end method

    /**
     * Check if char is plain ASCII letter lower or upper case.
     *
     * @param c char to check.
     * @return true if char is in range a..z A..Z
     * @see Character#isLowerCase(char)
     * @see Character#isUpperCase(char)
     * @see Character#isDigit(char)
     */
    public static boolean isUnaccentedLetter(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }// end method

    /**
     * Check if char is plain ASCII lower case.
     *
     * @param c char to check.
     * @return true if char is in range a..z.
     * @see Character#isLowerCase(char)
     * @see Character#isLetter(char)
     */
    public static boolean isUnaccentedLowerCase(char c) {
        return 'a' <= c && c <= 'z';
    }// end method

    /**
     * Check if char is plain ASCII upper case.
     *
     * @param c char to check.
     * @return true if char is in range A..Z.
     * @see Character#isUpperCase(char)
     * @see Character#isLetter(char)
     */
    public static boolean isUnaccentedUpperCase(char c) {
        return 'A' <= c && c <= 'Z';
    }// end method

    /**
     * test if a string is numeric.  Integer only. No lead - allowed.
     *
     * @param s String to test
     * @return true if numeric
     * @see #isDigit(char)
     */
    public static boolean isUnsignedNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }// end method

    /**
     * is this character a vowel?
     *
     * @param c the character, any char upper or lower case, punctuation or symbol
     * @return true if char is aeiou or AEIOU, or vowel accented in any way or ligature ae AE oe OE ij IJ
     */
    public static boolean isVowel(char c) {
        return c < 0x0180 && vt.get(c);
    }// end method

    /**
     * join two arrays of Strings into one, faster than FastCat, StringBuilder.
     *
     * @param a first array of Strings
     * @param b second array of Strings
     * @return new String array containing all elements of a and b.
     */
    public static String[] join(String[] a, String[] b) {
        String[] result = new String[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }// end method

    /**
     * gets the last word of a String, delimited by space or the end of the string.
     *
     * @param s the input String
     * @return the last word of the String.
     * @see #firstWord(String)
     */
    public static String lastWord(String s) {
        s = s.trim();
        return s.substring(s.lastIndexOf(' ') + 1);
    }// end method

    /**
     * Pads the string value out to the given length by applying blanks on the right, left justifying the value.
     *
     * @param value  value to be converted to string String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop   true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on right/chopped to the desired length. Spaces are inserted on the right.
     * @see #toLZ
     */
    public static String leftJustified(int value, int newLen, boolean chop) {
        return rightPad(Integer.toString(value), newLen, chop);
    }// end method

    /**
     * Pads the string out to the given length by applying blanks on the left, effectively rightjustifying.
     *
     * @param s      String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop   true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on left/chopped to the desired length. Spaces are inserted on the left.
     * @see #toLZ
     */
    public static String leftPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            } else {
                return s;
            }
        } else {
            return spaces(grow) + s;
        }
    }// end method

    /**
     * Like String.compare except it is not phased
     * by null parameters.
     * null compares before everything else.
     *
     * @param a first string to compare, possibly null.
     * @param b second string to compare, possibly null.
     * @return -1 if a is less than b,
     * 0 if a == b,
     * and +1 if a is
     * greater than b
     * comparing case-sensitively, e.g. all lower case letters come
     * after all upper case letters..
     */
    public static int nullSafeStringCompare(final String a, final String b) {
        // catches 100% of equal if interned, otherwise just some,
        //noinspection StringEquality
        if (a == b) {
            return 0;
        } else if (a == null) {
            // b cannot be null;
            return -1;
        } else if (b == null) {
            // a cannot be null
            return 1;
        } else {
            // a and b cannot be null, case-sensitive
            return a.compareTo(b);
        }
    }// end method

    /**
     * Like String.compareIgnoreCase except it is not phased
     * by null parameters.
     * null compares before everything else.
     *
     * @param a first string to compare, possibly null.
     * @param b second string to compare, possibly null.
     * @return -1 if a is less than b,
     * 0 if a == b,
     * and +1 if a is greater than b
     * comparing case-insensitively, e.g. interleaving upper and
     * lower case letters.
     */
    public static int nullSafeStringCompareIgnoreCase(final String a, final String b) {
        // catches 100% of equal if interned, otherwise just some,
        //noinspection StringEquality
        if (a == b) {
            return 0;
        } else if (a == null) {
            // b cannot be null;
            return -1;
        } else if (b == null) {
            // a cannot be null
            return 1;
        } else {
            // a and b cannot be null
            return a.compareToIgnoreCase(b);
        }
    }// end method

    /**
     * convert a String to a long. The routine is very forgiving. It ignores invalid chars, lead trail, embedded spaces,
     * decimal points etc. Dash is treated as a minus sign.
     *
     * @param numStr String to be parsed.
     * @return long value of String with junk characters stripped.
     * @throws NumberFormatException if the number is too big to fit in a long.
     */
    public static long parseDirtyLong(String numStr) {
        numStr = numStr.trim();
        // strip commas, spaces, decimals + etc
        StringBuilder b = new StringBuilder(numStr.length());
        boolean negative = false;
        for (int i = 0, n = numStr.length(); i < n; i++) {
            char c = numStr.charAt(i);
            if (c == '-') {
                negative = true;
            } else if ('0' <= c && c <= '9') {
                b.append(c);
            }
        } // end for
        numStr = b.toString();
        if (numStr.length() == 0) {
            return 0;
        }
        long num = Long.parseLong(numStr);
        if (negative) {
            num = -num;
        }
        return num;
    }// end method

    /**
     * convert a String into long pennies. It ignores invalid chars, lead trail, embedded spaces. Dash is treated as a
     * minus sign. 0 or 2 decimal places are permitted.
     *
     * @param numStr String to be parsed.
     * @return long pennies.
     * @throws NumberFormatException if the number is too big to fit in a long.
     * @noinspection WeakerAccess
     */
    public static long parseLongPennies(String numStr) {
        numStr = numStr.trim();
        // strip commas, spaces, + etc
        StringBuilder b = new StringBuilder(numStr.length());
        boolean negative = false;
        int decpl = -1;
        for (int i = 0, n = numStr.length(); i < n; i++) {
            char c = numStr.charAt(i);
            switch (c) {
                case '-':
                    negative = true;
                    break;
                case '.':
                    if (decpl == -1) {
                        decpl = 0;
                    } else {
                        throw new NumberFormatException(
                                "more than one decimal point");
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (decpl != -1) {
                        decpl++;
                    }
                    b.append(c);
                    break;
                default:
                    // ignore junk chars
                    break;
            } // end switch
        } // end for
        if (numStr.length() != b.length()) {
            numStr = b.toString();
        }
        if (numStr.length() == 0) {
            return 0;
        }
        long num = Long.parseLong(numStr);
        if (decpl == -1 || decpl == 0) {
            num *= 100;
        } else //noinspection StatementWithEmptyBody
            if (decpl == 2) {/* it is fine as is */
            } else {
                throw new NumberFormatException("wrong number of decimal places.");
            }
        if (negative) {
            num = -num;
        }
        return num;
    }// end method

    /**
     * Print dollar currency, stored internally as scaled int. convert pennies to a string with a decorative decimal
     * point.
     *
     * @param pennies long amount in pennies.
     * @return amount with decorative decimal point, but no lead $.
     * @noinspection WeakerAccess
     */
    public static String penniesToString(long pennies) {
        boolean negative;
        if (pennies < 0) {
            pennies = -pennies;
            negative = true;
        } else {
            negative = false;
        }
        String s = Long.toString(pennies);
        int len = s.length();
        switch (len) {
            case 1:
                s = "0.0" + s;
                break;
            case 2:
                s = "0." + s;
                break;
            default:
                s = s.substring(0, len - 2) + "." + s.substring(len - 2, len);
                break;
        } // end switch
        if (negative) {
            s = "-" + s;
        }
        return s;
    }// end method

    /**
     * Extracts a number from a string, returns 0 if malformed.
     *
     * @param s String containing the integer.
     * @return integer.
     */
    public static int pluck(String s) {
        int result = 0;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // leave result at 0
        }
        return result;
    }// end method

    /**
     * Collapse multiple blank lines down to one.  Discards lead and trail blank lines.
     * Blank lines are lines that when trimmed have length 0.
     *
     * @param lines               array of lines to tidy.
     * @param minBlankLinesToKeep usually 1 meaning 1+ consecutive blank lines become 1, effectively collapsing
     *                            runs of blank lines down to 1.
     *                            if 2, 1 blank line is removed, and 2+ consecutive blanks lines become 1,
     *                            effectively undouble spacing.
     *                            if zero, non-blank lines will be separated by one blank line, even if there was not
     *                            one there to begin with, completely independent of preexisting blank lines,
     *                            effectively double spacing..
     *                            9999 effectively removes all blank lines.
     * @return array of lines with lead and trail blank lines removed, and excess blank lines collapsed down to one
     * or 0.
     * The results are NOT trimmed.
     */
    public static String[] pruneExcessBlankLines(String[] lines, int minBlankLinesToKeep) {
        int firstNonBlankLine = lines.length;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) {
                firstNonBlankLine = i;
                break;
            }
        }
        int lastNonBlankLine = -1;
        for (int i = lines.length - 1; i > 0; i--) {
            if (lines[i].trim().length() > 0) {
                lastNonBlankLine = i;
                break;
            }
        }
        if (firstNonBlankLine > lastNonBlankLine) {
            return new String[0];
        }
        // collapse blank lines in the middle chunk
        ArrayList<String> keep = new ArrayList<>(lastNonBlankLine - firstNonBlankLine + 1);
        int pendingBlankLines = 0;
        for (int i = firstNonBlankLine; i <= lastNonBlankLine; i++) {
            if (lines[i].trim().length() == 0) {
                pendingBlankLines++;
            } else {
                if (pendingBlankLines >= minBlankLinesToKeep) {
                    keep.add("");
                }
                keep.add(lines[i]);  // we don't trim. That is up to caller.
                pendingBlankLines = 0;
            }
        }
        return keep.toArray(new String[keep.size()]);
    }// end method

    /**
     * ST.quoteForReplace is like Regex.quote,
     * but quotes replacement strings which have only two reserved characters $ and \
     * Regex.quote will not work for replacement Strings.
     *
     * @param s string to prepare to insert literally in a replacement regex.
     * @return quoted string  with each  $ -> \$ and \ -> \\
     */
    public static String quoteForReplace(String s) {
        // non regex, all instances, Or so dont quote \ created by $
        return s.replace("\\", "\\\\").replace("$", "\\$");
    }// end method

    /**
     * used to prepare SQL string literals by doubling each embedded ' and wrapping in ' at each end. Further quoting is
     * required to use the results in Java String literals. If you use PreparedStatement, then this method is not
     * needed. The ' quoting is automatically handled for you.
     *
     * @param sql Raw SQL string literal
     * @return sql String literal enclosed in '
     * @noinspection WeakerAccess, SameParameterValue
     */
    public static String quoteSQL(String sql) {
        StringBuilder sb = new StringBuilder(sql.length() + 5);
        sb.append('\'');
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'') {
                sb.append("\'\'");
            } else {
                sb.append(c);
            }
        }
        sb.append('\'');
        return sb.toString();
    }// end method

    /**
     * reorder the letters in a String in ascending order, case-sensitively.
     * e.g. "DAB" --> "ABD", "aCBBB" --> "BBBCa", "Z X" --> "XZ", "   " --> ""
     *
     * @param field field to sort.
     * @return field with letters composing it sorted in ascending order. Blanks squeezed out, but not dups.
     * @see #deDupLetters(String)
     */
    public static String reorderLetters(String field) {
        final String squished = ST.squish(field);
        if (squished.length() <= 1) {
            return field;
        } else {
            final char[] ca = squished.toCharArray();
            Arrays.sort(ca);  // case-sensitive sort letter by letter
            return String.valueOf(ca);
        }
    }// end method

    /**
     * Produce a String of a given repeating character.
     *
     * @param c     the character to repeat
     * @param count the number of times to repeat
     * @return String, e.g. rep('*',4) returns "****"
     * @noinspection WeakerAccess, SameParameterValue
     * @see #spaces(int)
     */
    public static String rep(char c, int count) {
        if (c == ' ' && count <= SOMESPACES.length()) {
            return SOMESPACES.substring(0, count);
        }
        char[] s = new char[count];
        for (int i = 0; i < count; i++) {
            s[i] = c;
        }
        return new String(s).intern();
    }// end method

    /**
     * Pads the string value out to the given length by applying blanks on the left, right justifying the value.
     *
     * @param value  value to be converted to string String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop   true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on left/chopped to the desired length. Spaces are inserted on the left.
     * @see #toLZ
     */
    public static String rightJustified(int value, int newLen, boolean chop) {
        return leftPad(Integer.toString(value), newLen, chop);
    }// end method

    /**
     * Pads the string out to the given length by applying blanks on the right, effectively left-justifying.
     *
     * @param s      String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop   true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on right/chopped to the desired length. Spaces are inserted on the right.
     * @noinspection WeakerAccess, SameParameterValue
     */
    public static String rightPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            } else {
                return s;
            }
        } else {
            return s + spaces(grow);
        }
    }// end method

    /**
     * sort chars in String in case-sensitive alphabetical order
     *
     * @param s String to sort
     * @return sorted String
     */
    public static String sort(String s) {
        final char[] ca = s.toCharArray(); // encoding not relevant here
        Arrays.sort(ca);
        final String sorted = new String(ca);
        if (sorted.equals(s)) {
            return s;
        } else {
            return sorted;
        }
    }// end method

    /**
     * insert spaces between the letters
     *
     * @param s string of letters
     * @return string with a space between each letter.
     */
    public static String spaceOut(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2 - 1);
        for (int i = 0, n = s.length(); i < n; i++) {
            if (i != 0) {
                sb.append(' ');
            }
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }// end method

    /**
     * Generate a string of spaces n chars long.
     *
     * @param n how many spaces long
     * @return a string of spaces n chars long.
     * @see #rep(char, int)
     */
    public static String spaces(int n) {
        if (n <= SOMESPACES.length()) {
            if (n <= 0) {
                return "";
            } else {
                return SOMESPACES.substring(0, n);
            }
        } else {
            return rep(' ', n);
        }
    }// end method

    /**
     * Remove all spaces from a String. Does not touch other whitespace.
     *
     * @param s String to strip of blanks.
     * @return String with all blanks, lead/trail/embedded removed.
     * @see #condense(String)
     */
    public static String squish(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.indexOf(' ') < 0) {
            return s;
        }
        int len = s.length();
        StringBuilder b = new StringBuilder(len - 1);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c != ' ') {
                b.append(c);
            }
        } // end for
        return b.toString();
    }// end method

    /**
     * remove multiple unwanted characters from a String,
     * e.g. could be used to remove , and _ from numeric strings
     * before parsing.
     *
     * @param s       string to strip
     * @param naughty list of characters to strip out
     * @return string witch naughty characters removed.
     */
    public static String stripNaughtyCharacters(String s, String naughty) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            // contains wants a string, not char, so we have to use indexOf
            if (naughty.indexOf(c) < 0) {
                // keep char, it was not naughty
                sb.append(c);
            }
        }
        return sb.toString();
    }// end method

    /**
     * convert to Book Title case, with first letter of each word capitalised. e.g. "handbook to HIGHER consciousness"
     * -> "Handbook to Higher Consciousness" e.g. "THE HISTORY OF THE U.S.A." -> "The History of the U.S.A." e.g. "THE
     * HISTORY OF THE USA" -> "The History of the Usa" (sorry about that.) Don't confuse this with Character.isTitleCase
     * which concerns ligatures.
     *
     * @param s String to convert. May be any mixture of case.
     * @return String with each word capitalised, except embedded words "the" "of" "to"
     * @noinspection WeakerAccess
     */
    public static String toBookTitleCase(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "";
        }
        char[] ca = s.toCharArray();
        // Track if we changed anything so that
        // we can avoid creating a duplicate String
        // object if the String is already in Title case.
        boolean changed = false;
        boolean capitalise = true;
        boolean firstCap = true;
        for (int i = 0; i < ca.length; i++) {
            char oldLetter = ca[i];
            if (oldLetter == '\'') {
                // Might have O'Brian or Molly's
                capitalise = (i > 1 && ca[i - 1] == 'o' || ca[i - 1] == 'O');
            } else if (oldLetter <= '/'
                    || ':' <= oldLetter && oldLetter <= '?'
                    || ']' <= oldLetter && oldLetter <= '`') {
                /* whitespace, control chars or punctuation */
                /* Next normal char should be capitalised */
                /* apostrophe treated as letter so Molly's won't come out Molly'S */
                capitalise = true;
            } else {
                if (capitalise && !firstCap) {
                    // might be the_ of_ or to_
                    capitalise =
                            !(s.substring(i, Math.min(i + 4, s.length()))
                                    .equalsIgnoreCase("the ")
                                    || s.substring(i,
                                    Math.min(i + 3, s.length()))
                                    .equalsIgnoreCase("of ")
                                    || s.substring(i,
                                    Math.min(i + 3, s.length()))
                                    .equalsIgnoreCase("to "));
                } // end if
                char newLetter =
                        capitalise
                                ? Character.toUpperCase(oldLetter)
                                : Character.toLowerCase(oldLetter);
                ca[i] = newLetter;
                changed |= (newLetter != oldLetter);
                capitalise = false;
                firstCap = false;
            } // end if
        } // end for
        if (changed) {
            s = new String(ca);
        }
        return s;
    }// end method

    /**
     * Convert int to 8-char hex with lead zeroes
     *
     * @param h number you want to convert to hex
     * @return 0x followed by unsigned hex 8-digit representation
     * @noinspection WeakerAccess
     * @see #toString(Color)
     * @see Integer#toString(int, int)
     */
    public static String toHexString(int h) {
        String s = Integer.toHexString(h);
        if (s.length() < 8) {// pad on left with zeros
            s = "00000000".substring(0, 8 - s.length()) + s;
        }
        return "0x" + s;
    }// end method

    /**
     * Quick replacement for Character.toLowerCase for use with English-only. It does not deal with accented
     * characters.
     *
     * @param c character to convert
     * @return character converted to lower case
     */
    public static char toLowerCase(char c) {
        return 'A' <= c && c <= 'Z' ? (char) (c + ('a' - 'A')) : c;
    }// end method

    /**
     * Quick replacement for Character.toLowerCase for use with English-only. It does not deal with accented
     * characters.
     *
     * @param s String to convert
     * @return String converted to lower case
     */
    public static String toLowerCase(String s) {
        final char[] ca = s.toCharArray();
        final int length = ca.length;
        boolean changed = false;
        // can't use for:each since we need the index to set.
        for (int i = 0; i < length; i++) {
            final char c = ca[i];
            if ('A' <= c && c <= 'Z') {
                // found a char that needs conversion.
                ca[i] = (char) (c + ('a' - 'A'));
                changed = true;
            }
        }
        // give back same string if unchanged.
        return changed ? new String(ca) : s;
    }// end method

    /**
     * Convert an integer to a String, with left zeroes.
     *
     * @param i   the integer to be converted
     * @param len the length of the resulting string. Warning. It will chop the result on the left if it is too long.
     * @return String representation of the int e.g. 007
     * @see #leftPad
     */
    public static String toLZ(int i, int len) {
        // Since String is final, we could not add this method there.
        String s = Integer.toString(i);
        if (s.length() > len) {/* return rightmost len chars */
            return s.substring(s.length() - len);
        } else if (s.length() < len)
        // pad on left with zeros
        {
            return "000000000000000000000000000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    }// end method

    /**
     * Convert an long to a String, with left zeroes.
     *
     * @param l   the long to be converted
     * @param len the length of the resulting string. Warning. It will chop the result on the left if it is too long.
     * @return String representation of the long e.g. 007
     * @see #leftPad
     */
    public static String toLZ(long l, int len) {
        // Since String is final, we could not add this method there.
        String s = Long.toString(l);
        if (s.length() > len) {/* return rightmost len chars */
            return s.substring(s.length() - len);
        } else if (s.length() < len)
        // pad on left with zeros
        {
            return "000000000000000000000000000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    }// end method

    /**
     * convert an integer value to unsigned hex with leading zeroes.
     *
     * @param value integer to convert.
     * @param len   how many characters you want in the result.
     * @return value in hex, padded to len chars with 0s on the left.
     * @see Integer#toString(int, int)
     */
    public static String toLZHexString(int value, int len) {
        // Since String is final, we could not add this method there.
        final String s = Integer.toHexString(value);
        if (s.length() > len) {/* return rightmost len chars */
            return s.substring(s.length() - len);
        } else if (s.length() < len)
        // pad on left with zeros.  at most 7 will be prepended
        {
            return "0000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    }// end method

    /**
     * convert an integer value to unsigned octal with leading zeroes.
     *
     * @param value integer to convert.
     * @param len   how many characters you want in the result.
     * @return value in octal, padded to len chars with 0s on the left.
     * @see Integer#toString(int, int)
     */
    public static String toLZOctalString(int value, int len) {
        // Since String is final, we could not add this method there.
        final String s = Integer.toOctalString(value);
        if (s.length() > len) {/* return rightmost len chars */
            return s.substring(s.length() - len);
        } else if (s.length() < len)
        // pad on left with zeros.  at most 7 will be prepended
        {
            return "0000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    }// end method

    /**
     * Get #ffffff html hex number for a colour
     *
     * @param c Color object whose html colour number you want as a string
     * @return # followed by 6 hex digits
     * @noinspection WeakerAccess, SameParameterValue
     * @see #toHexString(int)
     * @see Integer#toString(int, int)
     */
    public static String toString(Color c) {
        String s = Integer.toHexString(c.getRGB() & 0xffffff);
        if (s.length() < 6) {// pad on left with zeros
            s = "000000".substring(0, 6 - s.length()) + s;
        }
        return '#' + s;
    }// end method

    /**
     * Quick replacement for Character.toUpperCase for use with English-only. It does not deal with accented
     * characters.
     *
     * @param c character to convert
     * @return character converted to upper case
     */
    public static char toUpperCase(char c) {
        return 'a' <= c && c <= 'z' ? (char) (c + ('A' - 'a')) : c;
    }// end method

    /**
     * Quick replacement for Character.toUpperCase for use with English-only. It does not deal with accented
     * characters.
     *
     * @param s String to convert
     * @return String converted to upper case
     */
    public static String toUpperCase(String s) {
        final char[] ca = s.toCharArray();
        final int length = ca.length;
        boolean changed = false;
        // can't use for:each since we need the index to set.
        for (int i = 0; i < length; i++) {
            final char c = ca[i];
            if ('a' <= c && c <= 'z') {
                // found a char that needs conversion.
                ca[i] = (char) (c + ('A' - 'a'));
                changed = true;
            }
        }
        // give back same string if unchanged.
        return changed ? new String(ca) : s;
    }// end method

    /**
     * Removes white space from beginning this string.
     *
     * @param s String to process. As always the original in unchanged.
     * @return this string, with leading white space removed
     * @noinspection WeakerAccess, WeakerAccess, WeakerAccess, SameParameterValue, WeakerAccess
     * @see #trimLeading(String, char)
     * <p/>
     * All characters that have codes less than or equal to <code>'&#92;u0020'</code> (the space character) are
     * considered to be white space.
     */
    public static String trimLeading(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int st = 0;
        while ((st < len) && (s.charAt(st) <= ' ')) {
            st++;
        }
        return (st > 0) ? s.substring(st, len) : s;
    }// end method

    /**
     * trim leading characters there are on a string matching a given character.
     *
     * @param text text with possible trailing characters, possibly empty, but not null.
     * @param c    the leading character of interest, usually ' ' or '\n'
     * @return string with any of those trailing characters removed.
     * @see #trimLeading(String)
     */
    public static String trimLeading(String text, char c) {
        int count = countLeading(text, c);
        // substring will optimise the 0 case.
        return text.substring(count);
    }// end method

    /**
     * trim leading characters there are on a string matching a given characters
     *
     * @param text        text with possible trailing characters, possibly empty, but not null.
     * @param charsToTrim list of leading characters of interest, usually ' ' or '\n'
     * @return string with any of those leading characters removed.
     * @see #trimTrailing(String)
     * @see #chopLeadingString(String, String)
     */
    public static String trimLeading(String text, String charsToTrim) {
        int count = countLeading(text, charsToTrim);
        // substring will optimise the 0 case.
        return text.substring(count);
    }// end method

    /**
     * Removes white space from end this string.
     *
     * @param s String to process. As always the original in unchanged.
     * @return this string, with trailing white space removed
     * @see #trimTrailing(String, char)
     * <p/>
     * All characters that have codes less than or equal to <code>'&#92;u0020'</code> (the space character) are
     * considered to be white space.
     */
    public static String trimTrailing(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int origLen = len;
        while ((len > 0) && (s.charAt(len - 1) <= ' ')) {
            len--;
        }
        return (len != origLen) ? s.substring(0, len) : s;
    }// end method

    /**
     * trim trailing characters there are on a string matching a given character.
     *
     * @param text text with possible trailing characters, possibly empty, but not null.
     * @param c    the trailing character of interest, usually ' ' or '\n'
     * @return string with any of those trailing characters removed.
     * @see #trimTrailing(String)
     */
    public static String trimTrailing(String text, char c) {
        int count = countTrailing(text, c);
        // substring will optimise the 0 case.
        return text.substring(0, text.length() - count);
    }// end method

    /**
     * trim trailing characters there are on a string matching given characters.
     *
     * @param text        text with possible trailing characters, possibly empty, but not null.
     * @param charsToTrim list of the trailing characters of interest, usually ' ' or '\n'
     * @return string with any of those trailing characters removed. ".com" would not only chop .com,
     * but any combination of those letters e.g. mc.moc
     * @see #trimTrailing(String)
     * @see #chopTrailingString(String, String)
     */
    public static String trimTrailing(String text, String charsToTrim) {
        int count = countTrailing(text, charsToTrim);
        // substring will optimise the 0 case.
        return text.substring(0, text.length() - count);
    }// end method

    /**
     * Test harness, used in debugging
     *
     * @param args not used
     */
    public static void main(String[] args) {
        if (DEBUGGING) {
            out.println(">>condense");
            out.println(condense("   this  is   spaced.  "));
            out.println(">> trimLeading");
            out.println(trimLeading("*****t*r*i*m****", '*'));
            out.println(trimLeading("   trim   "));
            out.println(">> trimTrailing");
            out.println(trimTrailing("   trim   "));
            out.println(trimTrailing("*****t*r*i*m****", '*'));
            out.println(">> chopLeadingString");
            out.println(chopLeadingString("abcdefg", "abc"));
            out.println(">> chopTrailingString");
            out.println(chopTrailingString("say!", "!"));
            out.println(toString(Color.red));
            out.println(">> toHexString");
            out.println(toHexString(-3));
            out.println(toHexString(3));
            out.println(">> countLeading");
            out.println(countLeading("none", ' '));
            out.println(countLeading("*one***", '*'));
            out.println(countLeading(" abc ", " *"));
            out.println(countLeading(" *   * abc ", " *"));
            out.println(countLeading(" *   * abc ", "* "));
            out.println(countLeading("\n\ntw\n\n\no\n\n\n\n", '\n'));
            out.println(">> countTrailing");
            out.println(countTrailing("none", ' '));
            out.println(countTrailing("***one*", '*'));
            out.println(countTrailing("\n\n\n\nt\n\n\n\nwo\n\n", '\n'));
            out.println(countTrailing(" abc *  * ", " *"));
            out.println(countTrailing(" *   * abc  *  * ", " *"));
            out.println(countTrailing(" *   * abc  *  * ", "* "));
            out.println(">> quoteSQL");
            out.println(quoteSQL("Judy's Place"));
            out.println(">> parseLongPennies");
            out.println(parseLongPennies("$5.00"));
            out.println(parseLongPennies("$50"));
            out.println(parseLongPennies("50"));
            out.println(parseLongPennies("$50-"));
            out.println(">> penniesToString");
            out.println(penniesToString(0));
            out.println(penniesToString(-1));
            out.println(penniesToString(20));
            out.println(penniesToString(302));
            out.println(penniesToString(-100000));
            out.println(">> toBookTitleCase");
            out.println(toBookTitleCase("handbook to HIGHER consciousness"));
            out.println(toBookTitleCase("THE HISTORY OF THE U.S.A."));
            out.println(toBookTitleCase("THE HISTORY OF THE USA"));
            out.println(">> rightPad");
            out.println(rightPad("abc", 6, true) + "*");
            out.println(rightPad("abc", 2, true) + "*");
            out.println(rightPad("abc", 2, false) + "*");
            out.println(rightPad("abc", 3, true) + "*");
            out.println(rightPad("abc", 3, false) + "*");
            out.println(rightPad("abc", 0, true) + "*");
            out.println(rightPad("abc", 20, true) + "*");
            out.println(rightPad("abc", 29, true) + "*");
            out.println(rightPad("abc", 30, true) + "*");
            out.println(rightPad("abc", 31, true) + "*");
            out.println(rightPad("abc", 40, true) + "*");
            out.println(">> toUpperCase");
            out.println(toUpperCase('q'));
            out.println(toUpperCase('Q'));
            out.println(toUpperCase("The quick brown fox was 10 feet tall."));
            out.println(toUpperCase("THE QUICK BROWN FOX WAS 10 FEET TALL."));
            out.println(toUpperCase("the quick brown fox was 10 feet tall."));
            out.println(">> toLowerCase");
            out.println(toLowerCase('q'));
            out.println(toLowerCase('Q'));
            out.println(toLowerCase("The quick brown fox was 10 feet tall."));
            out.println(toLowerCase("THE QUICK BROWN FOX WAS 10 FEET TALL."));
            out.println(toLowerCase("the quick brown fox was 10 feet tall."));
            out.println(">> countInstances");
            out.println("count instances should be 4: " + countInstances(" abab abcdefgab", "ab"));
        }
    }// end method
    // end methods
}

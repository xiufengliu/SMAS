package ca.uwaterloo.iss4e.common;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Copyright (c) 2014 Xiufeng Liu ( xiufeng.liu@uwaterloo.ca )
 * <p/>
 * This file is free software: you may copy, redistribute and/or modify it
 * under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 * <p/>
 * This file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
public class Utils {
    protected static Random random = new Random();
    private static final String[] operators = {"!=", "==", ">=", "<=", ">", "<"};

    public static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        return scaled + min;
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDecimals = new DecimalFormat("#.##");
        return Double.valueOf(twoDecimals.format(d));
    }

    public static boolean isWeekend(String dateStr) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(sdf.parse(dateStr));
            int n = cal.get(Calendar.DAY_OF_WEEK);
            return n == Calendar.SATURDAY || n == Calendar.SUNDAY;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int countSpecialCharacter(String s, char ch) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ch) {
                counter++;
            }
        }
        return counter;
    }

    public static String mapNull2Empty(String str){
        return isEmpty(str)?"":str;
    }

    public static boolean isEmpty(String str) {
        return str == null || (str.trim()).isEmpty();
    }

    public static long addTime(int calendarType, long time, int number) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.add(calendarType, number);
        return cal.getTimeInMillis();
    }

    //UTC; America/Toronto
    public static long getTimeByYear(String year, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(year + "-01-01 00:00:00");
            return date.getTime();
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        return 0L;
    }

    public static long getTimeByYearMonth(String yearMonth, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(yearMonth + "-01 00:00:00");
            return date.getTime();
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        return 0L;
    }

    public static long getTimeByYearMonthDay(String yearMonthDay, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(yearMonthDay + " 00:00:00");
            return date.getTime();
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        return 0L;
    }

    public static long getTime(String dateStr, int hour, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(dateStr + " " + hour + ":00:00");
            return date.getTime();
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        return 0L;
    }

    public static long getTime(String timestamp, String timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(timestamp);
            return date.getTime();
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }
        return 0L;
    }

    public static String trim(String str, char ch) {
        if (null == str) return null;
        str = str.trim();
        int count = str.length();
        int len = str.length();
        int st = 0;

        char[] val = str.toCharArray();

        while ((st < len) && (val[st] == ch)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == ch)) {
            len--;
        }
        return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void callMethod(Object receiver,
                                  String methodName, Object... params) {
        if (receiver == null || methodName == null) {
            return;
        }
        Class<?> cls = receiver.getClass();
        Method[] methods = cls.getMethods();
        Method toInvoke = null;
        methodLoop:
        for (Method method : methods) {
            if (!methodName.equals(method.getName())) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (params == null && paramTypes == null) {
                toInvoke = method;
                break;
            } else if (params == null || paramTypes == null
                    || paramTypes.length != params.length) {
                continue;
            }

            for (int i = 0; i < params.length; ++i) {
                if (!paramTypes[i].isAssignableFrom(params[i].getClass())) {
                    continue methodLoop;
                }
            }
            toInvoke = method;
        }
        if (toInvoke != null) {
            try {
                toInvoke.invoke(receiver, params);
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    public static double[] toPrimitiveArray(List<Double> array) {
        double[] primitiveArray = new double[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            primitiveArray[i] = array.get(i).doubleValue();
        }
        return primitiveArray;
    }

    public static double[] toPrimitiveArray(Double[] array) {
        if (array == null) return null;
        double[] primitiveArray = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) return null;
            primitiveArray[i] = array[i].doubleValue();
        }
        return primitiveArray;
    }

    public static int[] toPrimitiveArray(Integer[] array) {
        int[] primitiveArray = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            primitiveArray[i] = array[i].intValue();
        }
        return primitiveArray;
    }

    public static Integer[] toObjectArray(int[] oldArray) {
        Integer[] newArray = new Integer[oldArray.length];
        for (int ctr = 0; ctr < oldArray.length; ctr++) {
            newArray[ctr] = Integer.valueOf(oldArray[ctr]);
        }
        return newArray;
    }

    public static String genColorCode() {
        char[] letters = "0123456789ABCDEF".toCharArray();
        String color = "#";
        for (int i = 0; i < 6; i++) {
            int idx = (int) Math.floor(Math.random() * 16);
            color += letters[idx];
        }
        return color;
    }

    ;

    public static java.sql.Date toSqlDate(String dateStr) throws SMASException {
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            Date date = dateFormat.parse(dateStr);
            return new java.sql.Date(date.getTime());
        } catch (Exception e) {
            throw new SMASException("Failed to parse the date");
        }
    }


    public static java.sql.Date toSqlDate(String dateStr, String format) throws SMASException {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            Date date = dateFormat.parse(dateStr);
            return new java.sql.Date(date.getTime());
        } catch (Exception e) {
            throw new SMASException("Failed to parse the date");
        }
    }

    public static Timestamp toTimestamp(String str) throws SMASException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            Date parsedDate = dateFormat.parse(str);
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            throw new SMASException("Failed to parse the date");
        }
    }


    public static java.sql.Date toSqlDate(long val) throws SMASException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(new Date(val));
        return toSqlDate(dateStr, "yyyy-MM-dd");
    }

    public static java.sql.Timestamp toTimestampOnedayBeforeWithHour(long timeInMillis, int hour) throws SMASException {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeInMillis);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String theDate = inputFormatter.format(cal.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date parsedDate = dateFormat.parse(theDate + " " + hour + ":00");
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            throw new SMASException("Failed to parse the date");
        }
    }


    public static boolean evaluate(double left, String op, double right) {
        switch (op) {
            case "==":
                return left == right;
            case ">":
                return left > right;
            case "<":
                return left < right;
            case "<=":
                return left <= right;
            case ">=":
                return left >= right;
            case "!=":
                return left != right;
            default:
                System.err.println("ERROR: Operator type not recognized.");
                return false;
        }
    }

    public static String toHTMLCode(String s) throws SMASException {
        try {
            StringBuffer buf = new StringBuffer();
            String trimStr = s.trim();
            for (int i = 0; i < trimStr.length(); ++i) {
                char ch = trimStr.charAt(i);
                switch (ch) {
                    case '=':
                        buf.append("&#61;");
                        break;
                    case '>':
                        buf.append("&#62;");
                        break;
                    case '<':
                        buf.append("&#60;");
                        break;
                    case '!':
                        buf.append("&#33;");
                        break;
                    default:
                        buf.append(ch);
                }
            }
            return buf.toString();
        } catch (Exception e) {
            throw new SMASException("Faild to convert to HTML code");
        }
    }

    public static String parseOp(String s) throws SMASException {
        try {
            StringBuffer buf = new StringBuffer();
            String trimStr = s.trim();
            for (int i = 0; i < trimStr.length(); ++i) {
                char ch = trimStr.charAt(i);
                if (!Character.isDigit(ch) && ch != '.') {
                    buf.append(ch);
                }
            }
            return buf.toString();
        } catch (Exception e) {
            throw new SMASException("Failed to logic operator");
        }
    }

    public static double parseDouble(String s) throws SMASException {
        try {
            StringBuffer buf = new StringBuffer();
            String trimStr = s.trim();
            for (int i = 0; i < trimStr.length(); ++i) {
                char ch = trimStr.charAt(i);
                if (Character.isDigit(ch) || ch == '.') {
                    buf.append(ch);
                }
            }
            return Double.parseDouble(buf.toString());
        } catch (Exception e) {
            throw new SMASException("Failed to parse number");
        }
    }

    public static final String[] splitToArray(String stringToSplit, String delimitter, boolean trim) {
        if (stringToSplit == null) {
            return new String[] {};
        }
        if (delimitter == null) {
            throw new IllegalArgumentException();
        }
        StringTokenizer tokenizer = new StringTokenizer(stringToSplit, delimitter, false);
        int count = tokenizer.countTokens();
        String[] splitTokens = new String[count];
        for (int i = 0; i < count; ++i) {
            String token = tokenizer.nextToken();
            if (trim) {
                token = token.trim();
            }
            splitTokens[i] = token;
        }
        return splitTokens;
    }

    public static void main(String[] args) {

    }
}

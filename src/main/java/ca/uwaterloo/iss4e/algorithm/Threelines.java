package ca.uwaterloo.iss4e.algorithm;

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
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import java.util.Map.Entry;


public class Threelines {


    public static List<Double[][]> threel(BigDecimal[] temperatures, BigDecimal[] readings)  {
        List<Double[][]> lines = new ArrayList<Double[][]>();


        TreeMap<Double, List<Double>> map = sort(temperatures, readings);
        int bin = 20;
        TreeMap<Double, Double> tempMap10 = binning(bin, 0.1f, map);
        double[] s = findSeparatorFirstRound(tempMap10);

        lines.add(conLines(tempMap10, s[0], s[1]));

        TreeMap<Double, Double> tempMap50 = binning(bin, 0.5f, map);
        s = findSeparatorFirstRound(tempMap50);
        lines.add(conLines(tempMap50, s[0], s[1]));


        TreeMap<Double, Double> tempMap90 = binning(bin, 0.9f, map);
        s = findSeparatorFirstRound(tempMap90);
        lines.add(conLines(tempMap90, s[0], s[1]));
        return lines;
    }

    public static TreeMap<Double, List<Double>> sort(BigDecimal[] temperatures, BigDecimal[] readings) {
        //build list for sorting
        TreeMap<Double, List<Double>> map = new TreeMap<Double, List<Double>>();
        for (int i = 0; i < temperatures.length; ++i) {
            double roundTemp = Math.round(temperatures[i].doubleValue());
            if (!map.containsKey(roundTemp))
                map.put(roundTemp, new LinkedList<Double>());
            map.get(roundTemp).add(readings[i].doubleValue());
        }
        return map;
    }

    public static TreeMap<Double, Double> binning(int i, double percentile, TreeMap<Double, List<Double>> map) {
        TreeMap<Double, Double> tempMap = new TreeMap<Double, Double>();

        for (Entry<Double, List<Double>> entry : map.entrySet()) {
            Double key = entry.getKey();
            List<Double> value = entry.getValue();
            //System.out.println("before"+value);
            // Collections.sort(value); sorting would be done in Percentile method
            //System.out.println("after"+value);
            if (value.size() >= i) {
                double percent = Percentile(percentile, value);
                //double median= Median(value);
                tempMap.put(key, percent);
            }

        }
        return tempMap;
    }

    public static double Percentile(double percentile, List<Double> value) {
        Collections.sort(value);
        double boundary = value.size() * percentile;
        if (Math.round(boundary) != boundary) {

            return (Double) value.get((Math.round((float) boundary)) - 1);
        } else {
            double lower = (Double) value.get(Math.round((float) boundary) - 1);
            double upper = (Double) value.get(Math.round((float) boundary));
            return (double) ((lower + upper) / 2.0);
        }
    }

    public static double Median(List<Double> value) {
        Collections.sort(value);

        if (value.size() % 2 == 1)
            return (Double) value.get((value.size() + 1) / 2 - 1);
        else {
            double lower = (Double) value.get(value.size() / 2 - 1);
            double upper = (Double) value.get(value.size() / 2);

            return (double) ((lower + upper) / 2.0);
        }
    }

    public static double[] linearRegression(SortedMap<Double, Double> treeValues) {

        int n = 0;

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;

        for (Entry<Double, Double> entry : treeValues.entrySet()) {
            sumx += entry.getKey();
            sumx2 += entry.getKey() * entry.getKey();
            sumy += entry.getValue();
            n++;
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (Entry<Double, Double> entry : treeValues.entrySet()) {
            xxbar += (entry.getKey() - xbar) * (entry.getKey() - xbar);
            yybar += (entry.getValue() - ybar) * (entry.getValue() - ybar);
            xybar += (entry.getKey() - xbar) * (entry.getValue() - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        //System.out.println("y   = " + beta1 + " * x + " + beta0);

        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        double mse = 0.0;      // mean square error
        for (Entry<Double, Double> entry : treeValues.entrySet()) {
            double fit = beta1 * entry.getKey() + beta0;
            rss += (fit - entry.getValue()) * (fit - entry.getValue());
            ssr += (fit - ybar) * (fit - ybar);
            mse += Math.pow(entry.getValue() - beta0 - entry.getKey() * beta1, 2);
        }
        double R2 = ssr / yybar;
        double svar = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar / n + xbar * xbar * svar1;
        double mmse = mse / n;
        double rmse = Math.sqrt(mmse);
        double[] results = new double[3];
        results[0] = (double) beta0;
        results[1] = (double) beta1;
        results[2] = (double) mmse;
        return results;


    }


    public  static double[] findSeparatorFirstRound(TreeMap<Double, Double> data) {

        SortedMap<Double, Double> data1, data2, data3;
        // Optimal separator temperature
        double os1 = 0;
        double os2 = 0;
        // Optimal separator energy

        // optimal slopes
        double oslope1 = 0;
        double oslope2 = 0;
        double oslope3 = 0;

        // Corresponding errors
        double OptErr = Double.MAX_VALUE;
        // coefficients and errors of lines
        double[] cos1 = new double[3];
        double[] cos2 = new double[3];
        double[] cos3 = new double[3];

        for (double s1 = 10; s1 <= 20; s1++) {
            double s1Err = 0, s2Err = 0, s3Err = 0;
            // 2. linear regression on temp <= s1 for first line
            data1 = data.headMap(s1, true);
            cos1 = linearRegression(data1);
            //3. get 2 coefficients
            // 4. get S1 MMSE


            for (double s2 = 5; s2 <= 10; s2++) {
                // 4. linear regression on temp>= s1 and temp<=(s1+s2) for second line
                data2 = data.subMap(s1, true, s1 + s2, true);
                cos2 = linearRegression(data2);
                //5. get 2 coefficients
                // 6. get S2 MMSE

                //7. linear regression where temp>= s1+s2
                //8. get two coefficients for s3
                //9. get MMSE s3
                data3 = data.tailMap(s1 + s2, true);
                cos3 = linearRegression(data3);


                // total all MMSE
                double TotalErr = cos1[2] + cos2[2] + cos3[2];
                // store MMSE and coefficients
                if (TotalErr < OptErr) {
                    os1 = s1;
                    os2 = s2;
                    oslope1 = cos1[1];
                    oslope2 = cos2[1];
                    oslope3 = cos3[1];
                    OptErr = TotalErr;
                    //System.out.println(s1+ " "+s2+" "+TotalErr+ " "+slope1+" "+slope2+" "+slope3);
                }

            }
        }
        return new double[]{os1, os2};
    }

    public static Double[][] conLines(TreeMap<Double, Double> data, double s1, double s2) {
        // linear regression where temp <=s1
        // get coefficients

        Double[][] points = new Double[4][];

        points[0] = new Double[]{data.firstEntry().getKey(), data.firstEntry().getValue()};
        points[3] = new Double[]{data.lastEntry().getKey(), data.lastEntry().getValue()};

        SortedMap<Double, Double> data1 = data.headMap(s1, true);
        double[] coS1 = linearRegression(data1);

        // linear regression where temp <=s1+s2 and temp>=s1
        // get coefficients
        SortedMap<Double, Double> data2 = data.subMap(s1, true, s1 + s2, true);
        double[] coS2 = linearRegression(data2);

        // linear regression where temp >=s1+s2
        // get coefficients
        SortedMap<Double, Double> data3 = data.tailMap(s1 + s2, true);
        double[] coS3 = linearRegression(data3);


        s2 = s1 + s2 - 1;

        double tcpA = s1;
        double lcpA1 = coS1[0] + coS1[1] * tcpA;
        double lcpA2 = coS2[0] + coS2[1] * tcpA;
        double lcpA = (lcpA1 + lcpA2) / 2;

        double tcpB = s2;
        double lcpB1 = coS2[0] + coS2[1] * tcpB;
        double lcpB2 = coS3[0] + coS3[1] * tcpB;
        double lcpB = (lcpB1 + lcpB2) / 2;

        HashSet<Point> searchSpace1 = new HashSet<Point>();
        HashSet<Point> searchSpace2 = new HashSet<Point>();

        double deltaT = (double) 0.5;
        double deltaL1 = Math.abs(lcpA1 - lcpA2) / 4;
        double deltaL2 = Math.abs(lcpB1 - lcpB2) / 4;


        searchSpace1.add(new Point(tcpA - deltaT, lcpA + deltaL1));
        searchSpace1.add(new Point(tcpA, lcpA + deltaL1));
        searchSpace1.add(new Point(tcpA + deltaT, lcpA + deltaL1));
        searchSpace1.add(new Point(tcpA - deltaT, lcpA));
        searchSpace1.add(new Point(tcpA, lcpA));
        searchSpace1.add(new Point(tcpA + deltaT, lcpA));
        searchSpace1.add(new Point(tcpA - deltaT, lcpA - deltaL1));
        searchSpace1.add(new Point(tcpA, lcpA - deltaL1));
        searchSpace1.add(new Point(tcpA + deltaT, lcpA - deltaL1));

        searchSpace2.add(new Point(tcpB - deltaT, lcpB + deltaL2));
        searchSpace2.add(new Point(tcpB, lcpB + deltaL2));
        searchSpace2.add(new Point(tcpB + deltaT, lcpB + deltaL2));
        searchSpace2.add(new Point(tcpB - deltaT, lcpB));
        searchSpace2.add(new Point(tcpB, lcpB));
        searchSpace2.add(new Point(tcpB + deltaT, lcpB));
        searchSpace2.add(new Point(tcpB - deltaT, lcpB - deltaL1));
        searchSpace2.add(new Point(tcpB, lcpB - deltaL1));
        searchSpace2.add(new Point(tcpB + deltaT, lcpB - deltaL1));


        double s1beginx = data.firstKey();
        double s1beginy = coS1[0] + coS1[1] * s1beginx;
        double s3endx = data.lastKey();
        double s3endy = coS3[0] + coS3[1] * s3endx;
        double min_RMSE = 10000;
        double s1_RMSE = 0, s2_RMSE = 0, s3_RMSE = 0;


        Point end_s1;
        Point end_s2;
        // optimal connecting points
        Point con_s1_s2 = null;
        Point con_s2_s3 = null;
        // optimal slopes
        double oslope1 = 0;
        double oslope2 = 0;
        double oslope3 = 0;


        for (Point p1 : searchSpace1) {

            end_s1 = p1;

            coS1[1] = (end_s1.y - s1beginy) / (end_s1.x - s1beginx);
            coS1[0] = s1beginy - coS1[0] * s1beginx;
            for (double f1 : data1.keySet()) {
                s1_RMSE += Math.pow(data1.get(f1) - coS1[0] - f1 * coS1[1], 2);

            }

            s1_RMSE = (double) Math.sqrt(s1_RMSE / data1.size());

            for (Point p2 : searchSpace2) {

                end_s2 = p2;

                coS2[1] = (end_s2.y - end_s1.y) / (end_s2.x - end_s1.x);
                coS2[0] = end_s1.y - coS2[1] * end_s1.x;

                for (double f2 : data2.keySet()) {
                    s2_RMSE += Math.pow(data2.get(f2) - coS2[0] - f2 * coS2[1], 2);

                }

                s2_RMSE = (double) Math.sqrt(s2_RMSE / data2.size());

                coS3[1] = (s3endy - end_s2.y) / (s3endx - end_s2.x);
                coS3[0] = end_s2.y - coS3[1] * end_s2.x;

                for (double f3 : data3.keySet()) {
                    s3_RMSE += Math.pow(data3.get(f3) - coS3[0] - f3 * coS3[1], 2);

                }

                s3_RMSE = (double) Math.sqrt(s3_RMSE / data3.size());

                double rmse = s1_RMSE + s2_RMSE + s3_RMSE;
                if (rmse < min_RMSE) {
                    min_RMSE = rmse;
                    con_s1_s2 = p1;
                    con_s2_s3 = p2;
                    oslope1 = coS1[1];
                    oslope2 = coS2[1];
                    oslope3 = coS3[1];
                    //System.out.println(s1+ " "+s2+" "+rmse+ " "+slope1+" "+slope2+" "+slope3);


                }
            }
        }
        points[1] = new Double[]{con_s1_s2.x, con_s1_s2.y};
        points[2] = new Double[]{con_s2_s3.x, con_s2_s3.y};
        return points;
    }
}
package ca.uwaterloo.iss4e.algorithm;

import ca.uwaterloo.iss4e.common.SMASException;
import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Properties;

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
public class PARX {




    public static Pair prepareVariable(final BigDecimal[] observations, int currentIndex, int trainingSize, int order, int seasons) throws SMASException {
        int numberOfElem = Math.min(trainingSize, currentIndex+1);
        int size = numberOfElem/seasons;
        if (size<order+1) {
            throw new SMASException("The number of backward elements should be at least " + (order+1)*seasons);
        }
        double[] Y = new double[size];
        double[][] X = new double[size][order];
        for(int i=0; i<size; ++i){
            int idx = currentIndex - i * seasons;
                Y[i] = observations[idx].doubleValue();
                for (int j = 0; j < order; ++j) {
                    X[i][order-1-j] = observations[idx - 1 - j].doubleValue();
                }
        }
        return new Pair(Y, X);
    }


    public static Pair prepareVariable(final double[] observations, int currentIndex, int trainingSize, int order, int seasons) throws SMASException {
        int numberOfElem = Math.min(trainingSize, currentIndex+1);
        int size = numberOfElem/seasons;
        if (size<order+1) {
            throw new SMASException("The number of backward elements should be at least " + (order+1)*seasons);
        }
        double[] Y = new double[size];
        double[][] X = new double[size][order];
        for(int i=0; i<size; ++i){
            int idx = currentIndex - i * seasons;
            Y[i] = observations[idx];
            for (int j = 0; j < order; ++j) {
                X[i][order-1-j] = observations[idx - 1 - j];
            }
        }
        return new Pair(Y, X);
    }


    /*
* Prepare exogenous variable, temperature, XT1, XT2, XT3
* */
    public static double[][] prepareTemperatureExoVariable(final double[]temperatures, int currentIndex, int trainingSize, int seasons, int size){
        double[][] XT = new double[size][3];
        for (int i = 0; i < size; ++i) {
            double t = temperatures[currentIndex  - i * seasons];
            XT[i][0] = t>20?(t-20):0;
            XT[i][1] = (5<=t && t<16)?(16-t):0;
            XT[i][2] = t<5?(5-t):0;
        }
        return XT;
    }



    /*
    * Auto-regression variables, and the exogenous variable, temperature.
    * */
    public static Pair prepareAllVariables(final List<Double>  readings, final List<Double> temperatures,  int currentIndex, int trainingSize, int order, int seasons) throws SMASException {
        int numberOfElem = Math.min(trainingSize, currentIndex+1);
        int size = numberOfElem/seasons;
        if (size<order+1) {
            throw new SMASException("The number of backward elements should be at least " + (order+1)*seasons);
        }

        double[] Y = new double[size];
        double[][] X = new double[size][order+3];
        for (int i=0; i<size; ++i){
            int idx = currentIndex - i * seasons;
            Y[i] = readings.get(idx);
            for (int j=0; j<order; ++j){
                X[i][order-1-j] = readings.get(idx-1-j);
            }
            double t = temperatures.get(idx);
            X[i][order+0] = t>20?(t-20):0;
            X[i][order+1] = (5<=t && t<16)?(16-t):0;
            X[i][order+2] = t<5?(5-t):0;
        }
        return new Pair(Y, X);
    }


    public static double[] computePARModel(final List<Double>  readings, final List<Double>  temperatures, int currentIndex, int trainingSize, int order, int seasons) throws SMASException {
        Pair pair = PARX.prepareAllVariables(readings, temperatures, currentIndex, trainingSize, order, seasons);
        double[] Y = (double[]) pair.getKey();
        double[][] X = (double[][]) pair.getValue();

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);
        regression.newSampleData(Y, X);

        double[] beta = regression.estimateRegressionParameters();
        return beta;
    }

    public static double[] getLoadByTemperature(final List<Double> readings, final  List<Double> temperatures, int order) throws SMASException {
        int seasons = 24;
        Pair pair = PARX.prepareAllVariables(readings, temperatures, readings.size()-1, readings.size(), order, seasons);
        double[] Y = (double[]) pair.getKey();
        double[][] X = (double[][]) pair.getValue();

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);
        regression.newSampleData(Y, X);

        double[] beta = regression.estimateRegressionParameters();
        double[] loadByTemp = new double[Y.length*seasons];
        int startIndex = readings.size() - Y.length*seasons;
        for (int i=0; i<loadByTemp.length; ++i){
           double t = temperatures.get(i+startIndex);
           loadByTemp[i] = beta[order+0]*(t>20?(t-20):0) +
                   beta[order+1]*((5<=t && t<16)?(16-t):0) +
                   beta[order+2]*(t<5?(5-t):0);
        }
        return loadByTemp;
    }




    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost/essex";
        Properties props = new Properties();
        props.setProperty("user", "xiliu");
        props.setProperty("password", "Abcd1234");

        try {
           /* Random generator = new Random();
            BigDecimal[] d = new BigDecimal[40];
            for (int i=0; i<40; ++i){
                d[i] = new BigDecimal(generator.nextInt(10));
                System.out.print(d[i].doubleValue()+"("+i+") |");
            }
            System.out.println("\n ---------------");
            Pair<double[], double[][]> values = PARX.prepareVariable(d, 11, 12, 2, 4);
            double[] Y = values.getKey();
            double X[][] = values.getValue();
            for (int i=0; i<Y.length; ++i){

                for (int j=0; j<X[i].length; ++j) {
                    System.out.print(X[i][j] + " |");
                }
                System.out.print("|" +Y[i]);
                System.out.println();
            }
*/


            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, props);
            PreparedStatement pstmt = conn.prepareStatement("select array_agg(A.reading) from (select reading from meterreading where homeid=? and readdate between ? and ? order by readdate desc, readtime desc) A ");
            pstmt.setInt(1, 19419);
            pstmt.setDate(2, java.sql.Date.valueOf("2011-04-03"));
            pstmt.setDate(3, java.sql.Date.valueOf("2011-09-07"));

            ResultSet rs = pstmt.executeQuery();



            int order = 3;

            int numOfSeasons = 24;
            int intervalOfUpdateModel = 4; //Every four hours
            int startPredict = 6*24;
            int numOfPointsForTraining = 24*5;
            double[] pointsForPredict = new double[order];

            if (rs.next()) {
                BigDecimal[] readings = (BigDecimal[]) (rs.getArray(1).getArray());
                double[][] data = new double[2][readings.length+1];
                int i = 0;
                for (; i<startPredict; ++i){
                    data[0][i] = readings[i].doubleValue();
                    data[1][i] = 0.0;
                }

                double[] beta = null;
                int updateModelCount = 0;
                for (; i<readings.length; ++i){
                  // if (updateModelCount%intervalOfUpdateModel==0) {
                   // beta = PARX.computePARModel(readings, i, i+1, order, numOfSeasons);
                        ++updateModelCount;
                    //}
                    for(int p=0; p<order; ++p){
                        pointsForPredict[order-1-p] = readings[i-p].doubleValue();
                    }
                    data[0][i] = readings[i].doubleValue();
                 //   data[1][i+1] = PARX.predict(pointsForPredict, order, beta);
                }

                for (int j=0; j<readings.length+1; ++j){
                    System.out.println(data[0][j]+", " + data[1][j]);
                }


            }
       } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

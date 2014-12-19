package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.algorithm.Histogram;
import ca.uwaterloo.iss4e.algorithm.PARX;
import ca.uwaterloo.iss4e.algorithm.Threelines;
import ca.uwaterloo.iss4e.chart.Chart;
import ca.uwaterloo.iss4e.chart.Serie;
import ca.uwaterloo.iss4e.chart.XAxis;
import ca.uwaterloo.iss4e.common.RandomStringUtils;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.common.holiday.BigDate;
import ca.uwaterloo.iss4e.common.holiday.HolInfo;
import ca.uwaterloo.iss4e.common.holiday.IsHoliday;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.databases.PowerDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class PowerDAOImpl implements PowerDAO{

    private static final Logger log = Logger.getLogger(PowerDAOImpl.class.getName());

    @Override
    public JSONArray getAllCustomerIDs() throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT meterid FROM smas_power_meter order by 1");
            ResultSet rs = pstmt.executeQuery();

            JSONArray values = new JSONArray();
            while (rs.next()) {
                values.put(rs.getInt("meterid"));
            }
            return values;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public JSONArray getAllAreaTowns() throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("select distinct areatown from smas_power_meter where areatown is not null");
            ResultSet rs = pstmt.executeQuery();

            JSONArray values = new JSONArray();
            while (rs.next()) {
                values.put(rs.getString("areatown"));
            }
            return values;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    //@Override
    public List<Double[][]> getThreelinesByIDMADlib(int meterid) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT * FROM func_threel(?)");
            pstmt.setInt(1, meterid);
            ResultSet rs = pstmt.executeQuery();

            List<Double[][]> lines = new ArrayList<Double[][]>();
            while (rs.next()) {
                Double[][] points = new Double[4][];
                Array value = rs.getArray(1);
                points[0] = (Double[]) (rs.getArray(1).getArray());
                points[1] = (Double[]) (rs.getArray(2).getArray());
                points[2] = (Double[]) (rs.getArray(3).getArray());
                points[3] = (Double[]) (rs.getArray(4).getArray());
                lines.add(points);
            }
            return lines;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getThreelinesByID(int meterid, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT array_agg(temperature), array_agg(reading) FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ?");
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            String[] names = new String[]{"10th", "50th", "90th"};
            if (rs.next()) {
                BigDecimal[] temperatures = (BigDecimal[]) (rs.getArray(1).getArray());
                BigDecimal[] readings = (BigDecimal[]) (rs.getArray(2).getArray());
                List<Double[][]> lines = Threelines.threel(temperatures, readings);
                int n = 0;
                for (Double[][] pointsOnline : lines) {
                    out.put(names[n++], pointsOnline);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SMASException(e);
        } catch (NullPointerException e) {
            throw new SMASException("Cannot find the data, meterid=" + meterid);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getDataPointsByID(int meterid, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT temperature, reading FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ?");
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                points.put(new double[]{rs.getDouble(1), rs.getDouble(2)});
            }
            out.put("points", points);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getSegmentationByDailyAvgLoadProfile(String distance, int ncluster, JSONObject out) throws SMASException {

    }

    @Override
    public void getSegmentationByBaseActivityLoad(String clusterFields, String distance, int ncluster, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            String dist = "madlib." + distance;
            StringBuffer sqlBuf = new StringBuffer();
            String view = RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyz".toCharArray());
            sqlBuf.append("CREATE TEMP VIEW  ")
                    .append(view).append(" AS SELECT smas_power_threel.meterid, Array[").append(clusterFields).append("] AS point, " +
                    "geom_latitude, geom_longitude FROM smas_power_threel, smas_power_meter " +
                    "WHERE smas_power_threel.meterid=smas_power_meter.meterid");
            System.out.println(sqlBuf.toString());
            PreparedStatement pstmt = dbConn.prepareStatement(sqlBuf.toString());
            pstmt.execute();
            pstmt.close();

            JSONArray clusterJSON = new JSONArray();
            List[] clusterOnMap = new List[ncluster];
            Histogram[] centroidHists = new Histogram[ncluster];
            String[] colors = new String[ncluster];
            int nBin = 20;
            for (int i = 0; i < ncluster; ++i) {
                clusterOnMap[i] = new ArrayList<JSONObject>();
                centroidHists[i] = new Histogram("Cluster-" + (i + 1), nBin, 0, 2);
                colors[i] = Utils.genColorCode();
            }
            out.put("colors", colors);

            pstmt = dbConn.prepareStatement("SELECT meterid, geom_latitude, geom_longitude, point, " +
                    "(madlib.closest_column(A.centroids, point)).column_id AS clusterid " +
                    "FROM " + view + ", " +
                    "(SELECT centroids FROM madlib.kmeanspp(?, ?, ?, ?, 'madlib.avg', 20, 0.001)) A ");
            int i = 0;
            pstmt.setString(++i, view);
            pstmt.setString(++i, "point");
            pstmt.setInt(++i, ncluster);
            pstmt.setString(++i, dist);
            ResultSet rs = pstmt.executeQuery();


            StringBuffer buf = new StringBuffer();
            while (rs.next()) {
                int meterid = rs.getInt("meterid");
                Double[] values = (Double[]) (rs.getArray("point").getArray());
                int clusterid = rs.getInt("clusterid");
                double latitude = rs.getDouble("geom_latitude");
                double longtitude = rs.getDouble("geom_longitude");
                JSONObject point = new JSONObject();
                point.put("name", clusterFields);

                buf.setLength(0);
                double value = 0.0;
                for (int j = 0; j < values.length; ++j) {
                    double v = Math.floor(100 * values[j].doubleValue() + 0.5) / 100;
                    value += v;
                    buf.append(v);
                    if (j != values.length - 1) {
                        buf.append(",");
                    }
                }
                point.put("value", buf.toString());
                point.put("x", latitude);
                point.put("y", longtitude);
                point.put("meterid", meterid);
                clusterOnMap[clusterid].add(point);
                centroidHists[clusterid].fill(value);
            }

            JSONArray heightJSON = new JSONArray();
            for (i = 0; i < ncluster; ++i) {
                clusterJSON.put(clusterOnMap[i]);
                JSONObject cluster = new JSONObject();
                cluster.put("name", "Cluster-" + (i + 1));
                cluster.put("type", "spline");
                cluster.put("color", colors[i]);
                cluster.put("data", centroidHists[i].binHeights());
                heightJSON.put(cluster);
            }
            out.put("clusterOnMap", clusterJSON);
            out.put("heights", heightJSON);
            int[] categories = new int[nBin];
            for (i = 0; i < nBin; ++i) {
                categories[i] = i;
            }

            JSONObject xAxis = new JSONObject();
            xAxis.put("categories", centroidHists[0].binLabels());
            JSONObject labels = new JSONObject();
            labels.put("x", -25 * 30 / nBin);
            xAxis.put("labels", labels);
            xAxis.put("title", clusterFields + ", kWh");
            JSONObject style = new JSONObject();
            style.put("fontSize", "20px");
            xAxis.put("style", style);
            out.put("xAxis", xAxis);
            out.put("binSize", centroidHists[0].binWidth());

            pstmt.close();
            pstmt = dbConn.prepareStatement("DROP VIEW " + view + " CASCADE");
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getConsumptionCentroids(String measure, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT clusterid, cent  FROM smas_power_norm_consumptionpattern_centroid WHERE measure=? ORDER BY 1");
            pstmt.setString(1, measure);
            ResultSet rs = pstmt.executeQuery();

            List<String> colors = new ArrayList<String>();
            JSONArray series = new JSONArray();
            double max = 0;
            while (rs.next()) {
                int clusterid = rs.getInt("clusterid");
                Double[] values = (Double[]) (rs.getArray("cent").getArray());
                JSONObject line = new JSONObject();
                line.put("name", "Pattern-" + clusterid);
                String color = Utils.genColorCode();
                line.put("color", color);
                colors.add(color);
                double[] data = new double[24];
                for (int h = 0; h < 24; ++h) {
                    data[h] = values[h]; //Math.floor(100 * values[h] / values[h] + 0.5) / 100;
                    if (max < values[h]) {
                        max = values[h];
                    }
                }
                line.put("data", data);
                series.put(line);
            }
            out.put("series", series);
            out.put("max", max);
            out.put("title", "Centroid ");
            pstmt.close();

            JSONArray clusterOnMap = new JSONArray();
            String sql = "SELECT A."+measure+"_c, " +
                    "                               array_agg(A.meterid) AS meterIDs," +
                    "                               array_agg(B.geom_latitude) AS lats, " +
                    "                               array_agg(B.geom_longitude) AS lons " +
                    "                       FROM smas_power_norm_consumptionpattern A, " +
                    "                            smas_power_meter B " +
                    "                       WHERE A.meterid=B.meterid AND " +
                    "                             B.geom_latitude IS NOT NULL AND B.geom_longitude IS NOT NULL" +
                    "                       GROUP BY 1 ORDER BY 1";
            pstmt = dbConn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()){
                int patternID = rs.getInt(1);
                List<JSONObject>points = new ArrayList<JSONObject>();
                Integer[] meterIDs = (Integer[])(rs.getArray("meterIDs").getArray());
                BigDecimal[] lats = (BigDecimal[]) (rs.getArray("lats").getArray());
                BigDecimal[] lons = (BigDecimal[]) (rs.getArray("lons").getArray());
                for (int i=0; i<meterIDs.length; ++i){
                    JSONObject point = new JSONObject();
                    point.put("x", lats[i].doubleValue());
                    point.put("y", lons[i].doubleValue());
                    point.put("name", "Pattern-"+patternID+"<br>meterID:"+meterIDs[i].intValue());
                    points.add(point);
                }
                clusterOnMap.put(points);
            }
            pstmt.close();
            out.put("colors", colors);
            out.put("clusterOnMap", clusterOnMap);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }



    @Override
    public void getWidthHistogram(int meterid, int nbuckets, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("select array_agg(reading ORDER BY reading), min(reading), max(reading) from smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ?");
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                BigDecimal[] points = (BigDecimal[]) (rs.getArray(1).getArray());
                double min = rs.getDouble(2);
                double max = rs.getDouble(3);

                final int[] data = new int[nbuckets];

                final double binSize = (max - min) / nbuckets;

                for (BigDecimal bd : points) {
                    double d = bd.doubleValue();
                    int bin = (int) ((d - min) / binSize);
                    if (bin < 0) { /* this data is smaller than min */ } else if (bin >= nbuckets) { /* this data point is bigger than max */ } else {
                        data[bin] += 1;
                    }
                }

                JSONArray categories = new JSONArray();
                for (int i = 0; i < nbuckets; ++i) {
                    double low = Utils.round(min + binSize * i, 2);
                    double up = Utils.round(low + binSize, 2);
                    categories.put("" + low);
                }

                JSONObject xAxis = new JSONObject();
                xAxis.put("categories", categories);

                JSONObject labels = new JSONObject();
                labels.put("x", -10 * 30 / nbuckets);
                xAxis.put("labels", labels);
                xAxis.put("title", "Energy consumption");

                out.put("xAxis", xAxis);
                out.put("binSize", Utils.round(binSize, 2));

                JSONArray series = new JSONArray();
                JSONObject binSerie = new JSONObject();
                JSONObject spineSerie = new JSONObject();
                JSONObject areasplineSerie = new JSONObject();
                series.put(binSerie);
                series.put(spineSerie);
                series.put(areasplineSerie);
                out.put("series", series);

                binSerie.put("name", "Bins");
                binSerie.put("visible", true);
                binSerie.put("data", data);

                spineSerie.put("name", "Curve");
                spineSerie.put("type", "spline");
                spineSerie.put("visible", false);
                spineSerie.put("data", data);

                areasplineSerie.put("name", "Filled Curve");
                areasplineSerie.put("type", "areaspline");
                areasplineSerie.put("visible", false);
                areasplineSerie.put("data", data);
            }
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    public Chart getWidthHistogramMADlib(int meterid, int nbuckets) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            Chart chart = new Chart();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT madlib.cmsketch_width_histogram(madlib.cmsketch(floor(reading)::int8), " +
                            "min(floor(reading)::int8), " +
                            "max(floor(reading)::int8), ?) " +
                            "FROM smas_power_hourlyreading WHERE meterid=?");
            pstmt.setInt(1, nbuckets);
            pstmt.setInt(2, meterid);
            ResultSet rs = pstmt.executeQuery();


            if (rs.next()) {
                String str = rs.getString(1);
                String[] result = str.replaceAll("[\\[L \\]]", "").split(",");
                int size = result.length / 3;

                log.log(Level.INFO, ">>>>meterid=" + meterid + "; nbuckets=" + nbuckets + "; size=" + size);
                List<Integer> data = new ArrayList<Integer>();
                List<String> categories = new ArrayList<String>();
                XAxis xAxis = new XAxis();
                xAxis.setTitle("Energy consumption");
                for (int i = 0; i < size; ++i) {
                    categories.add(result[i * 3] + "<=X<" + (Integer.parseInt(result[i * 3]) + 1));
                    data.add(Integer.parseInt(result[i * 3 + 2]));
                }
                xAxis.setCategories(categories);
                chart.setxAxis(xAxis);

                Serie<List<Integer>> serie1 = new Serie<List<Integer>>();
                serie1.setData(data);
                serie1.setName("Bins");
                serie1.setVisible(true);

                Serie<List<Integer>> serie2 = new Serie<List<Integer>>();
                serie2.setData(data);
                serie2.setName("Curve");
                serie2.setType("spline");
                serie2.setVisible(false);

                Serie<List<Integer>> serie3 = new Serie<List<Integer>>();
                serie3.setData(data);
                serie3.setName("Filled Curve");
                serie3.setType("areaspline");
                serie3.setVisible(false);

                List<Serie> series = new ArrayList<Serie>();
                series.add(serie1);
                series.add(serie2);
                series.add(serie3);
                chart.setSeries(series);
            }
            return chart;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getAvgHourlyActivityLoad(int meterid, int order, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            int seasons = 24;
            PreparedStatement pstmt = dbConn.prepareStatement(
                            "SELECT " +
                                    "B.isWeekday, " +
                                    "extract(hour from readtime) as hour, " +
                                    "AVG(GREATEST(A.reading-(B.coef[4]*(CASE WHEN A.temperature>20 THEN A.temperature-20 ELSE 0 END) " +
                                    "+B.coef[5]*(CASE WHEN A.temperature<16 AND A.temperature>=5 THEN 16-A.temperature ELSE 0 END) " +
                                    "+B.coef[6]*(CASE WHEN A.temperature<5 THEN 5-A.temperature ELSE 0 END) " +
                                    "), 0.0)) AS avgActivity " +
                                    "from smas_power_hourlyreading A, smas_power_parx_model B " +
                                    "where A.meterid=? AND " +
                                    "B.meterid=? AND " +
                                    "A.readtime::date between ? and ? AND " +
                                    "extract(hour from A.readtime) = B.season " +
                                    "GROUP BY A.meterid, B.isWeekday, extract(hour from A.readtime) " +
                                    "ORDER BY 1,2"

            );

            pstmt.setInt(1, meterid);
            pstmt.setInt(2, meterid);
            pstmt.setDate(3, startDate);
            pstmt.setDate(4, endDate);
            ResultSet rs = pstmt.executeQuery();


            double[] hourlyAvgOnWeekday = new double[24];
            double[] hourlyAvgOnHoliday = new double[24];
            while (rs.next()) {
                int isWeekday = rs.getInt("isWeekday");
                int h = rs.getInt("hour");
                double avgActivity = rs.getDouble("avgActivity");
               // double roundAvgActivity = Math.floor(100 * avgActivity / avgActivity + 0.5) / 100;
                if (isWeekday==1){
                    hourlyAvgOnWeekday[h] = avgActivity;
                } else {
                    hourlyAvgOnHoliday[h] = avgActivity;
                }
            }


            JSONArray series = new JSONArray();
            JSONObject weekdayActivityLoadJson = new JSONObject();
            weekdayActivityLoadJson.put("name", "Weekday");
            weekdayActivityLoadJson.put("data", hourlyAvgOnWeekday);

            JSONObject holidayActivityLoadJSon = new JSONObject();
            holidayActivityLoadJSon.put("name", "Holiday and weekend");
            holidayActivityLoadJSon.put("data", hourlyAvgOnHoliday);

            series.put(weekdayActivityLoadJson);
            series.put(holidayActivityLoadJSon);
            out.put("series", series);

           /* JSONObject startEndDate = new JSONObject();
            startEndDate.put("startdate", dates.get(chopSize));
            startEndDate.put("enddate", dates.get(dates.size() - 1));

            out.put("startenddate", startEndDate);*/
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getGetNeighborhoodWithinRadius(int meterID, double radius, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONArray locations = new JSONArray();
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT meterid, " +
                    "geom_latitude," +
                    "geom_longitude " +
                    "FROM smas_power_meter," +
                    "(select geom_latitude as lat,geom_longitude as lon FROM smas_power_meter where meterid=?) A " +
                    "WHERE earth_box(ll_to_earth(A.lat, A.lon), ?) @> ll_to_earth(geom_latitude, geom_longitude)");
            pstmt.setInt(1, meterID);
            pstmt.setDouble(2, radius * 1000.0);
            ResultSet rs = pstmt.executeQuery();
            JSONArray meterids = new JSONArray();
            while (rs.next()) {
                int meterid = rs.getInt(1);
                meterids.put(meterid);
                locations.put(new double[]{rs.getDouble(2), rs.getDouble(3)});
            }
            out.put("meterids", meterids);
            out.put("locations", locations);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }
    /*
    * ---------------------New added function ---------------------------------
    * */
    @Override
    public void getConsumptionByArea(String areaCode, int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            serie.put("name", "Energy Consumption of area: " + areaCode);
            serie.put("type", "area");
            String sql;
            long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime as time, sum(reading) AS total FROM smas_power_hourlyreading, smas_power_meter WHERE smas_power_hourlyreading.meterid=smas_power_meter.meterid AND areatown=? AND readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime,'yyyy-mm-dd') AS time, sum(reading) AS total FROM smas_power_hourlyreading,smas_power_meter WHERE smas_power_hourlyreading.meterid=smas_power_meter.meterid AND areatown=? AND readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else if (timeType == Calendar.MONTH) {
                minRange = 6 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) AS total FROM smas_power_hourlyreading,smas_power_meter WHERE smas_power_hourlyreading.meterid=smas_power_meter.meterid AND areatown=? AND readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy') AS time, sum(reading) AS total FROM smas_power_hourlyreading, smas_power_meter WHERE smas_power_hourlyreading.meterid=smas_power_meter.meterid AND areatown=? AND readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            }
            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setString(1, areaCode);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(rs.getDouble("total"));
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find the records!");
            serie.put("data", points);
            JSONArray series = new JSONArray();
            series.put(serie);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getConsumptionByAll(int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            serie.put("name", "Energy Consumption of All Customers");
            serie.put("type", "area");
            String sql;
            long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime,'yyyy-mm-dd') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else if (timeType == Calendar.MONTH) {
                minRange = 6 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            } else {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE readtime::date BETWEEN ? AND ? GROUP BY 1 ORDER BY 1";
            }
            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(rs.getDouble("total"));
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find the records!");
            serie.put("data", points);
            JSONArray series = new JSONArray();
            series.put(serie);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getConsumptionByMeterID(int meterid, int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            //serie.put("name", "Energy Consumption of MeterID:" + meterid);
            serie.put("name", "My consumption");
            serie.put("type", "area");
            String sql;
            long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime as time, reading AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? order by 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime,'yyyy-mm-dd') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY 1 order by 1";
            } else if (timeType == Calendar.MONTH) {
                minRange = 6 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY 1 order by 1";
            } else {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY 1 order by 1";
            }
            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(rs.getDouble("total"));
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find the records!");
            serie.put("data", points);
            JSONArray series = new JSONArray();
            series.put(serie);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getAvgConsumption(int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            serie.put("name", "Average consumption of neighbours");
            serie.put("type", "area");
            serie.put("color", "#FFFF70");
            String sql;
            //  long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                //    minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime as time, avg(reading) AS avgreading FROM smas_power_hourlyreading WHERE  readtime::date between ? and ? group by 1 order by 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                //  minRange = 30*24*3600*1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT meterid, readtime AS time, sum(reading) as reading FROM smas_power_hourlyreading  WHERE readtime::date between ? and ? GROUP BY meterid, time) A GROUP BY time ORDER BY time";
            } else if (timeType == Calendar.MONTH) {
                // minRange =  6*30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT meterid, TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as reading FROM smas_power_hourlyreading  WHERE readtime::date between ? and ? GROUP BY meterid, time) A GROUP BY time ORDER BY time";
            } else {
                //minRange = 12* 30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT meterid, TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as reading FROM smas_power_hourlyreading  WHERE readtime::date between ? and ? GROUP BY meterid, time) A GROUP BY time ORDER BY time";
            }
            // out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(Math.floor(100 * rs.getDouble("avgreading") + 0.5) / 100);
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find any records!");
            serie.put("data", points);
            JSONArray series = (JSONArray) out.get("series");
            series.put(serie);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getAvgConsumptionWithinRadius(int meterid, int timeType,  double radius, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            serie.put("name", "Average Consumption of households within " + radius + " kilometer");
            serie.put("type", "area");
            serie.put("color", "#FFFF70");
            String tableName = RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyz".toCharArray());
            String sql = "CREATE  temporary Table " + tableName + " AS "+
                    "SELECT meterid " +
                    "FROM smas_power_meter," +
                    "(select geom_latitude as lat,geom_longitude as lon FROM smas_power_meter where meterid=?) A " +
                    "WHERE earth_box(ll_to_earth(A.lat, A.lon), ?) @> ll_to_earth(smas_power_meter.geom_latitude, smas_power_meter.geom_longitude) and meterid!=?";
            PreparedStatement pstmt1 = dbConn.prepareStatement(sql);
            pstmt1.setInt(1, meterid);
            pstmt1.setDouble(2, radius*1000);
            pstmt1.setInt(3, meterid);
            pstmt1.execute();


            //  long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                //    minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime as time, avg(reading) AS avgreading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? group by 1 order by 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                //  minRange = 30*24*3600*1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime,'yyyy-MM-dd') AS time, sum(reading) as reading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            } else if (timeType == Calendar.MONTH) {
                // minRange =  6*30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as reading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            } else {
                //minRange = 12* 30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as reading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") smas_power_hourlyreading.meterid=t.meterid and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            }
            // out.put("minRange", minRange);
            System.out.println(sql);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(Math.floor(100 * rs.getDouble("avgreading") + 0.5) / 100);
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find the records!");
            serie.put("data", points);
            JSONArray series = (JSONArray) out.get("series");
            series.put(serie);
            pstmt.close();
            pstmt1.close();

            PreparedStatement dropStmt = dbConn.prepareStatement("DROP TABLE "+ tableName);
            dropStmt.execute();
            dropStmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getAvgConsumption(int timeType, String IDs, Date startDate, Date endDate, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject serie = new JSONObject();
            serie.put("name", "Average consumption of selected neighbours");
            serie.put("type", "area");
            serie.put("color", "#FFFF70");
            String tableName = RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyz".toCharArray());
            String sql = "CREATE  temporary Table " + tableName + " AS SELECT unnest(Array" + IDs + ") AS meterid";
            Statement stmt = dbConn.createStatement();
            stmt.execute(sql);


            //  long minRange = 0;
            if (timeType == Calendar.HOUR_OF_DAY) {
                //    minRange = 24 * 3600 * 1000L;
                sql = "SELECT readtime as time, avg(reading) AS avgreading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? group by 1 order by 1";
            } else if (timeType == Calendar.DAY_OF_MONTH) {
                //  minRange = 30*24*3600*1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime, 'yyyy-MM-dd') AS time, sum(reading) as reading FROM smas_power_hourlyreading  WHERE  smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            } else if (timeType == Calendar.MONTH) {
                // minRange =  6*30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as reading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            } else {
                //minRange = 12* 30 * 24 * 3600 * 1000L;
                sql = "SELECT A.time, AvG(reading) as avgreading FROM (SELECT smas_power_hourlyreading.meterid, TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as reading FROM smas_power_hourlyreading WHERE smas_power_hourlyreading.meterid IN (SELECT meterid FROM "+tableName+") and readtime::date between ? and ? GROUP BY smas_power_hourlyreading.meterid, time) A GROUP BY time ORDER BY time";
            }
            // out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeType == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "UTC"));
                } else if (timeType == Calendar.YEAR) {
                    point.put(Utils.getTimeByYear(rs.getString("time"), "UTC"));
                }
                point.put(Math.floor(100 * rs.getDouble("avgreading") + 0.5) / 100);
                points.put(point);
            }
            if (points.length() < 1) throw new SMASException("Cannot find the records!");
            serie.put("data", points);
            JSONArray series = (JSONArray) out.get("series");
            series.put(serie);

            stmt.execute("DROP TABLE " + tableName);
            stmt.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getCentroids(String measure, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT clusterid, cent FROM smas_power_norm_consumptionpattern_centroid WHERE measure=?");
            pstmt.setString(1, measure);
            ResultSet rs = pstmt.executeQuery();
            JSONArray series = new JSONArray();
            List<List<JSONObject>> centroids = new ArrayList<List<JSONObject>>();
            double max = 0;
            while (rs.next()) {
                int clusterid = rs.getInt("clusterid");
                Double[] values = (Double[]) (rs.getArray("cent").getArray());
                JSONObject cent = new JSONObject();
                cent.put("name", "Pattern-" + clusterid);
                double[] data = new double[24];
                for (int h = 0; h < 24; ++h) {
                    data[h] = values[h]; //Math.floor(100 * values[h] / values[h] + 0.5) / 100;
                    if (max < values[h]) {
                        max = values[h];
                    }
                }
                cent.put("data", data);
                List<JSONObject> tmp = new ArrayList<JSONObject>();
                tmp.add(cent);
                centroids.add(tmp);
            }
            out.put("series", centroids);
            out.put("max", max);
            out.put("title", "Centroid ");
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getClusteredHouseHolds(String measure, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT meterid, " + measure + "_c" + "," + measure + "  FROM smas_power_norm_consumptionpattern ORDER BY " + measure + "_c");
            ResultSet rs = pstmt.executeQuery();
            List<List<JSONObject>> data = new ArrayList<List<JSONObject>>();
            //JSONArray series = new JSONArray();
            double max = 0;
            while (rs.next()) {
                int clusterid = rs.getInt(measure + "_c");
                if (data.size() <= clusterid) {
                    data.add(new ArrayList<JSONObject>());
                }
                List<JSONObject> cluster = data.get(clusterid);
                JSONObject line = new JSONObject();
                Double[] values = (Double[]) (rs.getArray(measure).getArray());
                double[] points = new double[24];
                for (int h = 0; h < 24; ++h) {
                    points[h] = values[h];
                    if (max < values[h]) {
                        max = values[h];
                    }
                }
                line.put("name", rs.getInt("meterid"));
                line.put("data", points);
                out.put("title", "Cluster ");
                cluster.add(line);
            }
            out.put("series", data);
            out.put("max", max);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }



    @Override
    public void getForcastByHoltWinters(int meterid, int forcastLevel, int forcastTime, int timeUnit, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject actualSerie = new JSONObject();
            actualSerie.put("name", "Energy Consumption");
            actualSerie.put("color", "#7CB5EC");
            actualSerie.put("type", "line");
            String sql;
            long minRange = 0;
            if (timeUnit == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT  readtime as time, reading AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? order by 1";
            } else if (timeUnit == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime,'yyyy-mm-dd') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY time order by time";
            } else if (timeUnit == Calendar.MONTH) {
                minRange = 6 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ?  GROUP BY time  order by time";
            } else {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY time  order by time";
            }
            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, Date.valueOf("2000-01-01"));
            pstmt.setDate(3, Date.valueOf("2019-12-31"));

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();

            List<Double> loads = new ArrayList<Double>();
            List<Long> times = new ArrayList<Long>();
            long time = 0;
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeUnit == Calendar.HOUR_OF_DAY) { // 11
                    time = Utils.getTime(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.DAY_OF_MONTH) { // 5
                    time = Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.MONTH) { //2
                    time = Utils.getTimeByYearMonth(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.YEAR) { //1
                    time = Utils.getTimeByYear(rs.getString("time"), "UTC");
                }
                double load = rs.getDouble("total");
                point.put(time);
                point.put(load);
                points.put(point);

                loads.add(load);
                times.add(time);
            }
            if (points.length() < 1) {
                throw new SMASException("Cannot find the records!");
            }
            actualSerie.put("data", points);

            for (int i = 0; i < forcastTime; ++i) {
                times.add(Utils.addTime(timeUnit, time, i + 1));
            }


            Rengine engine = DAOUtils.getREngine();
            engine.assign("loads", Utils.toPrimitiveArray(loads));
            engine.eval("loadseries <-ts(loads, start=c(1))");
            engine.eval("loadseriesforecasts <- HoltWinters(loadseries, beta=FALSE, gamma=FALSE)");
            engine.eval("library('forecast')");
            engine.eval("forecastvalues<-forecast.HoltWinters(loadseriesforecasts, h=" + forcastTime + ")");
            REXP results = engine.eval("c(forecastvalues$fitted, forecastvalues$mean)");
            double[] values = results.asDoubleArray();
            JSONArray forecastPoints = new JSONArray();
            for (int i = 0; i < values.length; ++i) {
                double value = values[i];
                //System.out.println(value);
                JSONArray point = new JSONArray();
                point.put(times.get(i));
                point.put(Math.floor(100 * value + 0.5) / 100);
                forecastPoints.put(point);
            }

            JSONObject forecastSerie = new JSONObject();
            forecastSerie.put("name", "Forecast Energy Consumption By HoltWinters");
            forecastSerie.put("color", "#E53BBC");
            forecastSerie.put("type", "line");
            forecastSerie.put("data", forecastPoints);

            JSONArray series = new JSONArray();
            series.put(actualSerie);
            series.put(forecastSerie);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getForcastByARIMA(int meterid, int forcastLevel, int forcastTime, int timeUnit, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JSONObject actualSerie = new JSONObject();
            actualSerie.put("name", "Energy Consumption");
            actualSerie.put("color", "#7CB5EC");
            actualSerie.put("type", "line");
            String sql;
            long minRange = 0;
            if (timeUnit == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT  readtime as time, reading AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? order by 1";
            } else if (timeUnit == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime,'yyyy-mm-dd') AS time, sum(reading) AS total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY time order by time";
            } else if (timeUnit == Calendar.MONTH) {
                minRange = 6 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ?  GROUP BY time  order by time";
            } else {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT TO_CHAR(readtime, 'yyyy') AS time, sum(reading) as total FROM smas_power_hourlyreading WHERE meterid=? and readtime::date between ? and ? GROUP BY time  order by time";
            }
            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, meterid);
            pstmt.setDate(2, Date.valueOf("2000-01-01"));
            pstmt.setDate(3, Date.valueOf("2019-12-31"));

            ResultSet rs = pstmt.executeQuery();
            JSONArray points = new JSONArray();

            List<Double> loads = new ArrayList<Double>();
            List<Long> times = new ArrayList<Long>();
            long time = 0;
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeUnit == Calendar.HOUR_OF_DAY) { // 11
                    time = Utils.getTime(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.DAY_OF_MONTH) { // 5
                    time = Utils.getTimeByYearMonthDay(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.MONTH) { //2
                    time = Utils.getTimeByYearMonth(rs.getString("time"), "UTC");
                } else if (timeUnit == Calendar.YEAR) { //1
                    time = Utils.getTimeByYear(rs.getString("time"), "UTC");
                }
                double load = rs.getDouble("total");
                point.put(time);
                point.put(load);
                points.put(point);

                loads.add(load);
                times.add(time);
            }
            if (points.length() < 1) {
                throw new SMASException("Cannot find the records!");
            }
            actualSerie.put("data", points);

            for (int i = 0; i < forcastTime; ++i) {
                times.add(Utils.addTime(timeUnit, time, i + 1));
            }


            Rengine engine = DAOUtils.getREngine();
            engine.assign("loads", Utils.toPrimitiveArray(loads));
            engine.eval("loadseries <-ts(loads, start=c(1))");
            engine.eval("loadseriesarima <- arima(loadseries, order=c(2,0,0))");
            engine.eval("library('forecast')");
            engine.eval("forecastvalues<-forecast.Arima(loadseriesarima, h=" + forcastTime + ")");
            REXP results = engine.eval("c(forecastvalues$fitted, forecastvalues$mean)");
            double[] values = results.asDoubleArray();
            JSONArray forecastPoints = new JSONArray();
            for (int i = 0; i < values.length; ++i) {
                double value = values[i];
                //System.out.println(value);
                JSONArray point = new JSONArray();
                point.put(times.get(i));
                point.put(Math.floor(100 * value + 0.5) / 100);
                forecastPoints.put(point);
            }

            JSONObject forecastSerie = new JSONObject();
            forecastSerie.put("name", "Forecast Energy Consumption by ARIMA");
            forecastSerie.put("color", "#E53BBC");
            forecastSerie.put("type", "line");
            forecastSerie.put("data", forecastPoints);

            JSONArray series = new JSONArray();
            series.put(actualSerie);
            series.put(forecastSerie);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

}

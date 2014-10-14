package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.databases.WaterDAO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
public class WaterDAOImpl implements WaterDAO {
    private static final Logger log = Logger.getLogger(WaterDAOImpl.class.getName());

    //////////////////////// Query Water Data //////////////////////////////////////////////////////////
    @Override
    public void getWaterCustomers(JSONObject out, int startID) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT custid, accountno FROM smas_water_customer WHERE custid between ? AND ?");
            pstmt.setInt(1, startID);
            pstmt.setInt(2, startID + 9999);
            ResultSet rs = pstmt.executeQuery();
            JSONArray customers = new JSONArray();
            while (rs.next()) {
                int custid = rs.getInt("custid");
                int accountno = rs.getInt("accountno");
                customers.put(new int[]{custid, accountno});
            }

            customers.put(new int[]{startID + 10000, -1});
            out.put("customers", customers);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    private Map<Integer, String> getWaterCustomerTypeMap() throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement(" select subtypeid,subtypename from smas_water_type where size>10 order by size desc");
            ResultSet rs = pstmt.executeQuery();
            Map<Integer, String> custTypeMap = new HashMap<Integer, String>();
            while (rs.next()) {
                custTypeMap.put(rs.getInt("subtypeid"), rs.getString("subtypename"));
            }
            return custTypeMap;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getWaterCustomerTypes(JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement("select subtypeid,typename,subtypename from smas_water_type where size>10 order by 2");
            ResultSet rs = pstmt.executeQuery();
            Map<String, List<JSONArray>> types = new HashMap<String, List<JSONArray>>();
            List<String> typeNames = new ArrayList<String>();
            while (rs.next()) {
                String typeName = rs.getString("typename");
                if (!typeNames.contains(typeName)) {
                    typeNames.add(typeName);
                }
                if (!types.containsKey(typeName)) {
                    types.put(typeName, new ArrayList<JSONArray>());
                }
                List<JSONArray> subtypes = types.get(typeName);
                JSONArray subtype = new JSONArray();
                subtype.put(rs.getInt("subtypeid"));
                subtype.put(rs.getString("subtypename"));
                subtypes.add(subtype);
            }
            out.put("typenames", typeNames);
            out.put("types", types);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void getWaterLoadProfileByCustType(JSONObject out, String[] typeNames, int[] subTypeIDs, int timeLevel) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            StringBuilder sqlBld = new StringBuilder("SELECT  ");
            sqlBld.append(subTypeIDs == null ? "typename" : "subtypename").append(" AS name,");
            long minRange = 0;
            if (timeLevel == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sqlBld.append("readtime AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype A, smas_water_type B WHERE A.subtypeid=B.subtypeid AND (");
            } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sqlBld.append("readtime::date AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype A, smas_water_type B WHERE A.subtypeid=B.subtypeid AND (");
            } else if (timeLevel == Calendar.MONTH) {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sqlBld.append("TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype A, smas_water_type B WHERE A.subtypeid=B.subtypeid AND (");
            }
            out.put("minRange", minRange);

            for (int i = 0; i < typeNames.length; ++i) {
                sqlBld.append("typename=? ");
                if (i != typeNames.length - 1) {
                    sqlBld.append("OR ");
                }
            }
            sqlBld.append(")");

            if (subTypeIDs != null) {
                sqlBld.append(" AND (");
                for (int i = 0; i < subTypeIDs.length; ++i) {
                    sqlBld.append("B.subtypeid=? ");
                    if (i != subTypeIDs.length - 1) {
                        sqlBld.append("OR ");
                    }
                }
                sqlBld.append(")");
            }

            sqlBld.append(" GROUP BY 1,2 ORDER BY 1,2");
            PreparedStatement pstmt = dbConn.prepareStatement(sqlBld.toString());
            System.out.println(sqlBld.toString());
            int idx = 0;
            for (int i = 0; i < typeNames.length; ++i) {
                pstmt.setString(++idx, typeNames[i]);
                System.out.println(typeNames[i]);
            }
            for (int i = 0; subTypeIDs != null && i < subTypeIDs.length; ++i) {
                pstmt.setInt(++idx, subTypeIDs[i]);
                System.out.println(subTypeIDs[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            JSONObject line;
            JSONArray points = null;
            String preType = "-1";
            String curType = preType;
            JSONArray series = new JSONArray();
            while (rs.next()) {
                curType = rs.getString("name");
                if (!curType.equals(preType)) {
                    line = new JSONObject();
                    line.put("type", "area");
                    line.put("name", "Type:" + curType);
                    points = new JSONArray();
                    line.put("data", points);
                    series.put(line);
                    preType = curType;
                }
                JSONArray point = new JSONArray();
                if (timeLevel == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "America/Toronto"));
                }
                point.put(rs.getDouble("reading"));
                points.put(point);
            }
            out.put("title", "Water Consumption Time-series");
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getWaterLoadProfileByCust(JSONObject out, int custId, int timeLevel) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            Map<Integer, String> custTypeMap = getWaterCustomerTypeMap();

            String sql = null;
            long minRange = 0;
            if (timeLevel == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sql = "SELECT subtypeid, meterid, readtime as time, reading FROM smas_water_hourlyreading WHERE custid=? ORDER BY 1,2,3";
            } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sql = "SELECT subtypeid, meterid, readdate AS time, reading FROM smas_water_dailyreading WHERE custid=? ORDER BY 1,2,3";
            } else if (timeLevel == Calendar.MONTH) {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sql = "SELECT subtypeid, meterid, TO_CHAR(readdate, 'yyyy-MM') AS time, sum(reading) AS reading FROM smas_water_dailyreading WHERE custid=? GROUP BY 1,2,3 ORDER BY 1,2,3";
            }

            out.put("minRange", minRange);
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, custId);

            ResultSet rs = pstmt.executeQuery();
            JSONObject line;
            JSONArray points = null;
            int typeId = -1;
            int preMeterId = -1;
            int curMeterId = preMeterId;
            JSONArray series = new JSONArray();
            while (rs.next()) {
                typeId = rs.getInt("subtypeid");
                curMeterId = rs.getInt("meterid");
                if (curMeterId != preMeterId) {
                    line = new JSONObject();
                    line.put("type", "area");
                    line.put("name", "MeterID:" + curMeterId);
                    points = new JSONArray();
                    line.put("data", points);
                    series.put(line);
                    preMeterId = curMeterId;
                }
                JSONArray point = new JSONArray();
                if (timeLevel == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "America/Toronto"));
                }
                point.put(rs.getDouble("reading"));
                points.put(point);
            }
            out.put("title", "Water Consumption Time-series, Customer Type=" + custTypeMap.get(typeId));
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getWaterLoadProfile(JSONObject out, int timeLevel) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            StringBuilder sqlBld = new StringBuilder("SELECT ");
            long minRange = 0;
            if (timeLevel == Calendar.HOUR_OF_DAY) {
                minRange = 24 * 3600 * 1000L;
                sqlBld.append("readtime AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype ");
            } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                minRange = 30 * 24 * 3600 * 1000L;
                sqlBld.append("readtime::date AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype ");
            } else if (timeLevel == Calendar.MONTH) {
                minRange = 12 * 30 * 24 * 3600 * 1000L;
                sqlBld.append("TO_CHAR(readtime, 'yyyy-MM') AS time, sum(reading) AS reading FROM smas_water_hourlyreadingbytype ");
            }
            out.put("minRange", minRange);
            sqlBld.append("GROUP BY 1 ORDER BY 1");

            PreparedStatement pstmt = dbConn.prepareStatement(sqlBld.toString());
            ResultSet rs = pstmt.executeQuery();
            JSONArray series = new JSONArray();
            JSONObject line = new JSONObject();
            line.put("type", "area");
            JSONArray points = new JSONArray();
            line.put("data", points);
            series.put(line);
            while (rs.next()) {
                JSONArray point = new JSONArray();
                if (timeLevel == Calendar.HOUR_OF_DAY) {
                    point.put(Utils.getTime(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.DAY_OF_MONTH) {
                    point.put(Utils.getTimeByYearMonthDay(rs.getString("time"), "America/Toronto"));
                } else if (timeLevel == Calendar.MONTH) {
                    point.put(Utils.getTimeByYearMonth(rs.getString("time"), "America/Toronto"));
                }
                point.put(rs.getDouble("reading"));
                points.put(point);
            }
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getWaterDailyAvgLoadByExoVariable(JSONObject out, String exoVar, String[] typeNames, int[] subTypeIDs) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            StringBuilder sqlBld = new StringBuilder("select ");
            sqlBld.append(subTypeIDs == null ? "typename" : "subtypename").append(" AS name,").append(exoVar).append(", sum(reading)/sum(size) AS avgreading FROM smas_water_dailyreadingbytype A, smas_water_type B WHERE A.subtypeid=B.subtypeid AND B.size>10 AND (");
            for (int i = 0; i < typeNames.length; ++i) {
                sqlBld.append("B.typename=?");
                if (i != typeNames.length - 1) {
                    sqlBld.append(" OR ");
                }
            }
            sqlBld.append(")");
            if (subTypeIDs != null) {
                sqlBld.append(" AND (");
                for (int i = 0; i < subTypeIDs.length; ++i) {
                    sqlBld.append("B.subtypeid=?");
                    if (i != subTypeIDs.length - 1) {
                        sqlBld.append(" OR ");
                    }
                }
                sqlBld.append(")");
            }
            sqlBld.append(" AND (readdate!='2013-02-17'::date AND readdate!='2013-03-10'::date AND readdate!='2013-03-31'::date AND readdate!='2013-07-28'::date)");
            sqlBld.append(" AND readdate between '2012-10-01'::date AND '2013-04-30'::date");
            sqlBld.append(" GROUP BY 1,2 ORDER BY 1, 2 asc");


            PreparedStatement pstmt = dbConn.prepareStatement(sqlBld.toString());
            System.out.println(sqlBld.toString());
            int idx = 0;
            for (int i = 0; i < typeNames.length; ++i) {
                pstmt.setString(++idx, typeNames[i]);
            }
            for (int i = 0; subTypeIDs != null && i < subTypeIDs.length; ++i) {
                pstmt.setInt(++idx, subTypeIDs[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            String preType = "-1";
            JSONArray series = new JSONArray();
            JSONObject serie = null;
            JSONArray data = null;
            while (rs.next()) {
                String curType = rs.getString("name");
                if (!curType.equals(preType)) {
                    serie = new JSONObject();
                    data = new JSONArray();
                    serie.put("name", curType);
                    serie.put("data", data);
                    series.put(serie);
                    preType = curType;
                }
                data.put(new double[]{rs.getDouble(exoVar), rs.getDouble("avgreading")});
            }

            out.put("xTitle", exoVar.endsWith("maxtemp") ? "Maximum temperature of a day, C" : "Precipitation of a day, mm");
            out.put("xRange", new Double[]{exoVar.endsWith("maxtemp") ? -5.0 : 0.0, 35.0});
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void getWaterLoadDisaggregaionByPARX(JSONObject out, String typeName) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            out.put("minRange", 30 * 24 * 3600 * 1000L);
            PreparedStatement pstmt = dbConn.prepareStatement("SELECT " +
                    "readdate, " +
                    "reading/B.cnt, " +
                    "predict/B.cnt, " +
                    "readingwithprecip/B.cnt, " +
                    "readingwithtemp/B.cnt, " +
                    "baseload/B.cnt " +
                    "FROM smas_water_dailyreadingbytype_parx, (select sum(size) as cnt from smas_water_type where typename=? and size>10) B WHERE typename=? ORDER BY 1");
            pstmt.setString(1, typeName);
            pstmt.setString(2, typeName);

            ResultSet rs = pstmt.executeQuery();

            JSONArray series = new JSONArray();
            String[] names = new String[]{"Actual load", "Predict load", "Load without temperature effect", "Load without rainfall effect", "Load without temperature and rainfall effect"};

            JSONObject[] lines = new JSONObject[names.length];
            JSONArray[] points = new JSONArray[names.length];
            for (int i = 0; i < names.length; ++i) {
                lines[i] = new JSONObject();
                lines[i].put("type", "line");
                lines[i].put("name", names[i]);
                points[i] = new JSONArray();
                lines[i].put("data", points[i]);
                series.put(lines[i]);
            }
            while (rs.next()) {
                long x = Utils.getTimeByYearMonthDay(rs.getString("readdate"), "America/Toronto");
                for (int i = 0; i < names.length; ++i) {
                    double y = rs.getDouble(i + 2);
                    JSONArray point = new JSONArray();
                    point.put(x);
                    point.put(y);
                    points[i].put(point);
                }
            }
            out.put("title", "Average daily water consumption of customers, Customer Type=" + typeName);
            out.put("series", series);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }
}
